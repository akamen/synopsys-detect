/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.request.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class PolarisParamBuilder {
    private static final String OPERATOR_PREFIX = "$";
    private static final String OPERATOR_KEY_INSENSITIVE_PREFIX = "i";

    private ParamType paramType;
    private ParamOperator operator = ParamOperator.NONE;
    private String value;
    private boolean caseSensitive = false;
    private final List<String> additionalProps = new ArrayList<>();

    public static PolarisParamBuilder createIncludeFilter(final String baseType, final String typeToInclude) {
        return new PolarisParamBuilder()
                   .setValue(typeToInclude)
                   .setParamType(ParamType.INCLUDE)
                   .setOperator(ParamOperator.NONE)
                   .addAdditionalProp(baseType)
                   .setCaseSensitive(true);
    }

    public PolarisParamBuilder setParamType(final ParamType paramType) {
        this.paramType = paramType;
        return this;
    }

    public PolarisParamBuilder setOperator(final ParamOperator operator) {
        this.operator = operator;
        return this;
    }

    public PolarisParamBuilder setValue(final String value) {
        this.value = value;
        return this;
    }

    public PolarisParamBuilder setCaseSensitive(final boolean isCaseSensitive) {
        this.caseSensitive = isCaseSensitive;
        return this;
    }

    public PolarisParamBuilder addAdditionalProp(final String additionalProp) {
        this.additionalProps.add(additionalProp);
        return this;
    }

    public Map.Entry<String, String> build() throws IllegalStateException {
        if (StringUtils.isBlank(value)) {
            throwRequiredException("value");
        }

        final StringBuilder keyBuilder = new StringBuilder();
        final String paramTypeKey = paramType.getKey();
        if (StringUtils.isBlank(paramTypeKey)) {
            throwRequiredException("paramType");
        } else {
            keyBuilder.append(paramTypeKey);
        }

        for (final String prop : additionalProps) {
            if (StringUtils.isNotBlank(prop)) {
                keyBuilder.append(getBracketed(prop));
            }
        }

        if (!ParamOperator.NONE.equals(operator)) {
            final String op = OPERATOR_PREFIX + makeCaseInsensitive(operator.getKey());
            keyBuilder.append(getBracketed(op));
        }

        return createEntry(keyBuilder.toString(), value);
    }

    private void throwRequiredException(final String fieldName) {
        throw new IllegalStateException(String.format("The field '%s' is required", fieldName));
    }

    private String makeCaseInsensitive(final String operator) {
        if (!caseSensitive && (ParamOperator.OPERATOR_EQUALS.equalsKey(operator) || ParamOperator.OPERATOR_SUBSTRING.equalsKey(operator))) {
            return OPERATOR_KEY_INSENSITIVE_PREFIX + operator;
        }
        return operator;
    }

    private String getBracketed(final String str) {
        return "[" + str + "]";
    }

    private Map.Entry<String, String> createEntry(final String key, final String value) {
        final HashMap<String, String> map = new HashMap<>();
        map.put(key, value);
        return map.entrySet()
                   .stream()
                   .findFirst()
                   .orElse(null);
    }

}
