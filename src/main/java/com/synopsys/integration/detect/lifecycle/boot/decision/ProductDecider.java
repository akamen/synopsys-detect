/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.boot.decision;

/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.builder.BuilderStatus;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;

public class ProductDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PolarisDecision decidePolaris(DetectConfigurationFactory detectConfigurationFactory, File userHome, DetectToolFilter detectToolFilter, BlackDuckDecision blackDuckDecision) {
        if (!detectToolFilter.shouldInclude(DetectTool.POLARIS)) {
            logger.debug("Polaris will NOT run because it is excluded.");
            return PolarisDecision.skip();
        }

        if (blackDuckDecision.scanMode() == BlackduckScanMode.RAPID) {
            logger.debug("Polaris will NOT run because BlackDuck {} scan configured.", BlackduckScanMode.RAPID.name());
            return PolarisDecision.skip();
        }

        PolarisServerConfigBuilder polarisServerConfigBuilder = detectConfigurationFactory.createPolarisServerConfigBuilder(userHome);
        BuilderStatus builderStatus = polarisServerConfigBuilder.validateAndGetBuilderStatus();
        if (!builderStatus.isValid()) {
            String polarisUrl = polarisServerConfigBuilder.getUrl();
            if (StringUtils.isBlank(polarisUrl)) {
                logger.debug("Polaris will NOT run: The Polaris url must be provided.");
            } else {
                logger.debug("Polaris will NOT run: " + builderStatus.getFullErrorMessage());
            }
            return PolarisDecision.skip();
        } else {
            logger.debug("Polaris will run: An access token and url were found.");
            return PolarisDecision.runOnline(polarisServerConfigBuilder.build());
        }
    }

    public BlackDuckDecision decideBlackDuck(BlackDuckConnectionDetails blackDuckConnectionDetails, BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions, BlackduckScanMode scanMode, BdioOptions bdioOptions) {
        boolean offline = blackDuckConnectionDetails.getOffline();
        Optional<String> blackDuckUrl = blackDuckConnectionDetails.getBlackDuckUrl();
        Optional<String> signatureScannerHostUrl = blackDuckSignatureScannerOptions.getUserProvidedScannerInstallUrl();
        Optional<Path> signatureScannerOfflineLocalPath = blackDuckSignatureScannerOptions.getOfflineLocalScannerInstallPath();
        if (offline && scanMode == BlackduckScanMode.RAPID) {
            logger.debug("Black Duck will NOT run: Black Duck offline mode is set to true and Black Duck {} scan is enabled which requires online mode", BlackduckScanMode.RAPID.name());
            return BlackDuckDecision.skip();
        } else if (scanMode == BlackduckScanMode.RAPID && !bdioOptions.isBdio2Enabled()) {
            logger.debug("Black Duck will NOT run: Detect will not generate BDIO2 files and Black Duck {} scan is enabled which requires BDIO2 file generation", scanMode.name());
            return BlackDuckDecision.skip();
        } else if (scanMode == BlackduckScanMode.INTELLIGENT && !bdioOptions.isBdio2Enabled() && !bdioOptions.isLegacyUploadEnabled()) {
            logger.debug("Black Duck will NOT run: Detect will not generate BDIO2 files and Black Duck {} scan is enabled which requires BDIO2 file generation", scanMode.name());
            return BlackDuckDecision.skip();
        } else if (signatureScannerHostUrl.isPresent()) {
            logger.info("A Black Duck signature scanner url was provided, which requires Black Duck offline mode.");
            return BlackDuckDecision.runOffline();
        } else if (signatureScannerOfflineLocalPath.isPresent()) {
            logger.info("A local Black Duck signature scanner path was provided, which requires Black Duck offline mode.");
            return BlackDuckDecision.runOffline();
        } else if (blackDuckUrl.isPresent()) {
            logger.debug("Black Duck will run ONLINE: A Black Duck url was found.");
            return BlackDuckDecision.runOnline(scanMode);
        } else if (offline) {
            logger.debug("Black Duck will run: Black Duck offline mode was set to true.");
            return BlackDuckDecision.runOffline();
        } else {
            logger.debug("Black Duck will NOT run: The Black Duck url must be provided or offline mode must be set to true.");
            return BlackDuckDecision.skip();
        }
    }

}
