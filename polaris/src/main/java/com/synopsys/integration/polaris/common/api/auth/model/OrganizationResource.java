/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class OrganizationResource extends PolarisComponent {
    @SerializedName("data")
    private Organization data = null;

    /**
     * Get data
     * @return data
     */
    public Organization getData() {
        return data;
    }

    public void setData(final Organization data) {
        this.data = data;
    }

}

