/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.polaris.PolarisTool;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class PolarisOperation {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final ProductRunData productRunData;
    private final PropertyConfiguration detectConfiguration;
    private final DirectoryManager directoryManager;
    private final EventSystem eventSystem;
    private final StatusEventPublisher statusEventPublisher;
    private final OperationSystem operationSystem;

    public PolarisOperation(ProductRunData productRunData, PropertyConfiguration detectConfiguration, DirectoryManager directoryManager, EventSystem eventSystem, StatusEventPublisher statusEventPublisher, OperationSystem operationSystem) {
        this.productRunData = productRunData;
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
        this.statusEventPublisher = statusEventPublisher;
        this.operationSystem = operationSystem;
    }

    public void execute() {
        PolarisServerConfig polarisServerConfig = productRunData.getPolarisRunData().getPolarisServerConfig();
        DetectableExecutableRunner polarisExecutableRunner = DetectExecutableRunner.newInfo(eventSystem);
        PolarisTool polarisTool = new PolarisTool(statusEventPublisher, directoryManager, polarisExecutableRunner, detectConfiguration, polarisServerConfig, operationSystem);
        polarisTool.runPolaris(new Slf4jIntLogger(logger), directoryManager.getSourceDirectory());
    }
}
