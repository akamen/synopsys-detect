/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class AggregationContainer extends PolarisComponent {
    @SerializedName("data")
    private Aggregation data = null;

    /**
     * Get data
     * @return data
     */
    public Aggregation getData() {
        return data;
    }

    public void setData(final Aggregation data) {
        this.data = data;
    }

}

