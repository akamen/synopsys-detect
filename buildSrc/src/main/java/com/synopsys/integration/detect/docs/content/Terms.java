/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.content;

import java.util.HashMap;
import java.util.Map;

public class Terms {

    private final Map<String, String> termMap = new HashMap<>();

    public Terms() {
        termMap.put("company_name", "Synopsys");
        termMap.put("division_name", "Synopsys SIG");
        termMap.put("binary_repo_type", "Artifactory");
        termMap.put("solution_name", "Synopsys Detect");
        termMap.put("project_name", "synopsys-detect");
        termMap.put("source_repo_organization", "blackducksoftware");
        termMap.put("image_repo_organization", "blackducksoftware");
        termMap.put("blackduck_release_page", "https://github.com/blackducksoftware/hub/releases");
        termMap.put("bash_script_name", "detect.sh");
        termMap.put("powershell_script_name", "detect.ps1");
        termMap.put("binary_repo_url_base", "https://sig-repo.synopsys.com");
        termMap.put("binary_repo_ui_url_base", "https://sig-repo.synopsys.com/webapp/#/artifacts/browse/tree/General");
        termMap.put("binary_repo_repo", "bds-integrations-release");
        termMap.put("binary_repo_pkg_path", "com/synopsys/integration");
        termMap.put("script_repo_url_base", "https://detect.synopsys.com");
        termMap.put("spring_boot_config_doc_url", "https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html");
        termMap.put("blackduck_product_name", "Black Duck");
        termMap.put("coverity_product_name", "Coverity");
        termMap.put("blackduck_signature_scanner_name", "Black Duck Signature Scanner");
        termMap.put("blackduck_signature_scan_act", "Black Duck signature scan");
        termMap.put("blackduck_binary_scan_capability", "Black Duck - Binary Analysis");
        termMap.put("polaris_product_name", "Polaris");
        termMap.put("dockerinspector_name", "Black Duck Docker Inspector");
        termMap.put("blackduck_kb", "Black Duck KnowledgeBase");
        termMap.put("impact_analysis_name", "Vulnerability Impact Analysis Tool");
        termMap.put("professional_services", "Synopsys Software Integrity Group Client Services");
    }

    public String put(String termKey, String replacementString) {
        return termMap.put(termKey, replacementString);
    }

    public Map<String, String> getTerms() {
        return termMap;
    }
}