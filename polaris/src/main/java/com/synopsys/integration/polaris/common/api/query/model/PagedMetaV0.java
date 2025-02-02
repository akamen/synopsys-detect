/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class PagedMetaV0 extends PolarisComponent {
    @SerializedName("offset")
    private Integer offset;

    @SerializedName("limit")
    private Integer limit;

    @SerializedName("total")
    private Integer total;

    /**
     * The page offset of the response.
     * @return offset
     */
    public Integer getOffset() {
        return offset;
    }

    public void setOffset(final Integer offset) {
        this.offset = offset;
    }

    /**
     * The size of the response page.
     * @return limit
     */
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(final Integer limit) {
        this.limit = limit;
    }

    /**
     * The total size of the results available for the query.  &#x60;size&#x60; / &#x60;limit&#x60; &#x3D; &#x60;pages&#x60;.
     * @return total
     */
    public Integer getTotal() {
        return total;
    }

    public void setTotal(final Integer total) {
        this.total = total;
    }

}

