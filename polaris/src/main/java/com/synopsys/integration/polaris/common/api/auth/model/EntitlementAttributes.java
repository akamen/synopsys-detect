/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class EntitlementAttributes extends PolarisComponent {
    /**
     * Gets or Sets allowed
     */
    @JsonAdapter(AllowedEnum.Adapter.class)
    public enum AllowedEnum {
        ADMINISTER("ADMINISTER"),

        USERS_READ("USERS_READ"),

        USERS_READ_PRIVATE("USERS_READ_PRIVATE"),

        USERS_WRITE("USERS_WRITE"),

        GROUPS_READ("GROUPS_READ"),

        GROUPS_WRITE("GROUPS_WRITE"),

        PROJECTS_CREATE("PROJECTS_CREATE"),

        PROJECTS_READ("PROJECTS_READ"),

        PROJECTS_WRITE("PROJECTS_WRITE");

        private final String value;

        AllowedEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static AllowedEnum fromValue(final String text) {
            for (final AllowedEnum b : AllowedEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<AllowedEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final AllowedEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public AllowedEnum read(final JsonReader jsonReader) throws IOException {
                final String value = jsonReader.nextString();
                return AllowedEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("allowed")
    private List<AllowedEnum> allowed = new ArrayList<>();

    public EntitlementAttributes addAllowedItem(final AllowedEnum allowedItem) {
        this.allowed.add(allowedItem);
        return this;
    }

    /**
     * Get allowed
     * @return allowed
     */
    public List<AllowedEnum> getAllowed() {
        return allowed;
    }

    public void setAllowed(final List<AllowedEnum> allowed) {
        this.allowed = allowed;
    }

}

