/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.util.NameVersion;

public class FormattedOutputManager {
    private DetectorToolResult detectorToolResult = null;
    private Set<String> codeLocations = new HashSet<>();
    private NameVersion projectNameVersion = null;
    private final List<Status> statusSummaries = new ArrayList<>();
    private final List<DetectResult> detectResults = new ArrayList<>();
    private final List<DetectIssue> detectIssues = new ArrayList<>();
    private final Map<String, List<File>> unrecognizedPaths = new HashMap<>();
    private SortedMap<String, String> rawMaskedPropertyValues = null;
    private final List<Operation> detectOperations = new LinkedList<>();

    public FormattedOutputManager(EventSystem eventSystem) {
        eventSystem.registerListener(Event.DetectorsComplete, this::detectorsComplete);
        eventSystem.registerListener(Event.StatusSummary, this::addStatusSummary);
        eventSystem.registerListener(Event.Issue, this::addIssue);
        eventSystem.registerListener(Event.ResultProduced, this::addDetectResult);
        eventSystem.registerListener(Event.CodeLocationsCompleted, this::codeLocationsCompleted);
        eventSystem.registerListener(Event.UnrecognizedPaths, this::addUnrecognizedPaths);
        eventSystem.registerListener(Event.ProjectNameVersionChosen, this::projectNameVersionChosen);
        eventSystem.registerListener(Event.RawMaskedPropertyValuesCollected, this::rawMaskedPropertyValuesCollected);
        eventSystem.registerListener(Event.DetectOperation, this::addOperation);
    }

    public FormattedOutput createFormattedOutput(DetectInfo detectInfo) {
        FormattedOutput formattedOutput = new FormattedOutput();
        formattedOutput.formatVersion = "0.5.0";
        formattedOutput.detectVersion = detectInfo.getDetectVersion();

        formattedOutput.results = Bds.of(detectResults)
                                      .map(result -> new FormattedResultOutput(result.getResultLocation(), result.getResultMessage(), result.getResultSubMessages()))
                                      .toList();

        formattedOutput.status = Bds.of(statusSummaries)
                                     .map(status -> new FormattedStatusOutput(status.getDescriptionKey(), status.getStatusType().toString()))
                                     .toList();

        formattedOutput.issues = Bds.of(detectIssues)
                                     .map(issue -> new FormattedIssueOutput(issue.getType().name(), issue.getTitle(), issue.getMessages()))
                                     .toList();
        formattedOutput.operations = Bds.of(detectOperations)
                                         .map(operation -> new FormattedOperationOutput(Operation.formatTimestamp(operation.getStartTime()), Operation.formatTimestamp(operation.getEndTime().orElse(null)), operation.getName(),
                                             operation.getStatusType().name()))
                                         .toList();

        if (detectorToolResult != null) {
            formattedOutput.detectors = Bds.of(detectorToolResult.getRootDetectorEvaluationTree())
                                            .flatMap(DetectorEvaluationTree::allDescendentEvaluations)
                                            .filter(DetectorEvaluation::isApplicable)
                                            .map(this::convertDetector)
                                            .toList();
        }
        if (projectNameVersion != null) {
            formattedOutput.projectName = projectNameVersion.getName();
            formattedOutput.projectVersion = projectNameVersion.getVersion();
        }

        formattedOutput.codeLocations = Bds.of(this.codeLocations)
                                            .map(FormattedCodeLocationOutput::new)
                                            .toList();

        formattedOutput.unrecognizedPaths = new HashMap<>();
        unrecognizedPaths.keySet().forEach(key -> formattedOutput.unrecognizedPaths.put(key, unrecognizedPaths.get(key).stream().map(File::toString).collect(Collectors.toList())));

        formattedOutput.propertyValues = rawMaskedPropertyValues;

        return formattedOutput;
    }

    private FormattedDetectorOutput convertDetector(DetectorEvaluation evaluation) {
        FormattedDetectorOutput detectorOutput = new FormattedDetectorOutput();
        detectorOutput.folder = evaluation.getDetectableEnvironment().getDirectory().toString();
        detectorOutput.descriptiveName = evaluation.getDetectorRule().getDescriptiveName();
        detectorOutput.detectorName = evaluation.getDetectorRule().getName();
        detectorOutput.detectorType = evaluation.getDetectorType().toString();

        detectorOutput.extracted = evaluation.wasExtractionSuccessful();
        detectorOutput.discoverable = evaluation.wasDiscoverySuccessful();
        detectorOutput.status = evaluation.getStatus().name();
        detectorOutput.statusCode = evaluation.getStatusCode();
        detectorOutput.statusReason = evaluation.getStatusReason();
        detectorOutput.explanations = Bds.of(evaluation.getAllExplanations()).map(Explanation::describeSelf).toList();

        if (evaluation.getDiscovery() != null) {
            detectorOutput.discoveryReason = evaluation.getDiscovery().getDescription();
        }
        if (evaluation.getExtraction() != null) {
            detectorOutput.extractedReason = evaluation.getExtraction().getDescription();
            detectorOutput.relevantFiles = Bds.of(evaluation.getExtraction().getRelevantFiles()).map(File::toString).toList();
            detectorOutput.projectName = evaluation.getExtraction().getProjectName();
            detectorOutput.projectVersion = evaluation.getExtraction().getProjectVersion();
            if (evaluation.getExtraction().getCodeLocations() != null) {
                detectorOutput.codeLocationCount = evaluation.getExtraction().getCodeLocations().size();
            }
        }

        return detectorOutput;
    }

    private void detectorsComplete(DetectorToolResult detectorToolResult) {
        this.detectorToolResult = detectorToolResult;
    }

    private void codeLocationsCompleted(Collection<String> codeLocations) {
        this.codeLocations.addAll(codeLocations);
    }

    private void projectNameVersionChosen(NameVersion nameVersion) {
        this.projectNameVersion = nameVersion;
    }

    public void addStatusSummary(Status status) {
        statusSummaries.add(status);
    }

    public void addIssue(DetectIssue issue) {
        detectIssues.add(issue);
    }

    public void addDetectResult(DetectResult detectResult) {
        detectResults.add(detectResult);
    }

    public void addUnrecognizedPaths(UnrecognizedPaths unrecognizedPaths) {
        if (!this.unrecognizedPaths.containsKey(unrecognizedPaths.getGroup())) {
            this.unrecognizedPaths.put(unrecognizedPaths.getGroup(), new ArrayList<>());
        }
        this.unrecognizedPaths.get(unrecognizedPaths.getGroup()).addAll(unrecognizedPaths.getPaths());
    }

    public void addOperation(Operation detectOperation) {
        this.detectOperations.add(detectOperation);
    }

    private void rawMaskedPropertyValuesCollected(SortedMap<String, String> keyValueMap) {
        this.rawMaskedPropertyValues = keyValueMap;
    }
}
