/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.help.print;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.help.ArgumentState;
import com.blackducksoftware.integration.hub.detect.help.DetectBaseOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionHelp;

@Component
public class HelpPrinter {


    public void printAppropriateHelpMessage(final PrintStream printStream, final List<DetectBaseOption> allOptions, final ArgumentState state) {
        final HelpTextWriter writer = new HelpTextWriter();

        final List<DetectBaseOption> currentOptions = allOptions.stream().filter(it -> !it.getDetectOptionHelp().isDeprecated).collect(Collectors.toList());
        final List<DetectBaseOption> deprecatedOptions = allOptions.stream().filter(it -> it.getDetectOptionHelp().isDeprecated).collect(Collectors.toList());
        final List<String> allPrintGroups = getPrintGroups(currentOptions);

        if (state.isVerboseHelp) {
            printVerboseOptions(writer, currentOptions, null);
        } else if (state.isDeprecatedHelp) {
            printOptions(writer, deprecatedOptions, "Showing only deprecated properties.");
        } else {
            if (state.parsedValue != null) {
                if (isProperty(currentOptions, state.parsedValue)) {
                    printDetailedHelp(writer, allOptions, state.parsedValue);
                } else if (isPrintGroup(allPrintGroups, state.parsedValue)){
                    printHelpFilteredByPrintGroup(writer, currentOptions, state.parsedValue);
                } else {
                    printHelpFilteredBySearchTerm(writer, currentOptions, state.parsedValue);
                }
            }else {
                printDefaultHelp(writer, currentOptions);
            }
        }

        printStandardFooter(writer, getPrintGroupText(allPrintGroups));

        writer.write(printStream);
    }

    private void printVerboseOptions(final HelpTextWriter writer, final List<DetectBaseOption> options, final String notes) {
        final List<DetectBaseOption> sorted = options.stream().sorted((o1, o2) -> {
            if (o1.getDetectOptionHelp().primaryGroup.equals(o2.getDetectOptionHelp().primaryGroup)) {
                return o1.getKey().compareTo(o2.getKey());
            }else {
                return o1.getDetectOptionHelp().primaryGroup.compareTo(o2.getDetectOptionHelp().primaryGroup);
            }
        }).collect(Collectors.toList());
        printOptions(writer, sorted, notes);
    }

    private void printDetailedHelp(final HelpTextWriter writer, final List<DetectBaseOption> options, final String optionName) {
        final DetectBaseOption option = options.stream()
                .filter(it -> it.getKey().equals(optionName))
                .findFirst().orElse(null);

        if (option == null) {
            writer.println("Could not find option named: " + optionName);
        } else {
            printDetailedOption(writer, option);
        }
    }

    private void printDefaultHelp(final HelpTextWriter writer, final List<DetectBaseOption> options) {
        printHelpFilteredByPrintGroup(writer, options, DetectConfiguration.PRINT_GROUP_DEFAULT);
    }

    private void printHelpFilteredByPrintGroup(final HelpTextWriter writer, final List<DetectBaseOption> options, final String filterGroup) {
        final String notes = "Showing help only for: " + filterGroup;

        final List<DetectBaseOption> filteredOptions = options.stream()
                .filter(it -> it.getDetectOptionHelp().groups.stream().anyMatch(printGroup -> printGroup.equalsIgnoreCase(filterGroup)))
                .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                .collect(Collectors.toList());

        printOptions(writer, filteredOptions, notes);
    }

    private void printHelpFilteredBySearchTerm(final HelpTextWriter writer, final List<DetectBaseOption> options, final String searchTerm) {
        final String notes = "Showing help only for fields that contain: " + searchTerm;

        final List<DetectBaseOption> filteredOptions = options.stream()
                .filter(it -> it.getKey().contains(searchTerm))
                .collect(Collectors.toList());

        printOptions(writer, filteredOptions, notes);
    }

    private boolean isPrintGroup (final List<String> allPrintGroups, final String filterGroup) {
        return allPrintGroups.contains(filterGroup);
    }

    private boolean isProperty (final List<DetectBaseOption> allOptions, final String filterTerm) {
        return allOptions.stream()
                .map(it -> it.getKey())
                .anyMatch(it -> it.equals(filterTerm));
    }

    private List<String> getPrintGroups(final List<DetectBaseOption> options) {
        return options.stream()
                .flatMap(it -> it.getDetectOptionHelp().groups.stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private String getPrintGroupText(final List<String> printGroups) {
        return printGroups.stream().collect(Collectors.joining(","));
    }


    public void printDetailedOption(final HelpTextWriter writer, final DetectBaseOption detectOption) {
       detectOption.printDetailedOption(writer);
    }

    public void printOptions(HelpTextWriter writer, List<DetectBaseOption> options, String notes) {
        writer.printColumns("Property Name", "Default", "Description");
        writer.printSeperator();

        if (notes != null) {
            writer.println(notes);
            writer.println();
        }

        String group = null;
        for (final DetectBaseOption detectOption : options) {
            final String currentGroup = detectOption.getDetectOptionHelp().primaryGroup;
            if (group == null) {
                group = currentGroup;
            } else if (!group.equals(currentGroup)) {
                writer.println();
                group = currentGroup;
            }
           detectOption.printOption(writer);
        }
    }

    public void printStandardFooter(HelpTextWriter writer, String groupText) {
        writer.println();
        writer.println("Usage : ");
        writer.println("\t--<property name>=<value>");
        writer.println();
        writer.println("To see all properties, you may request verbose help log with '-hv'");
        writer.println("To see the hidden deprecated properties, you may request them with '-hd'");
        writer.println();
        writer.println("To get detailed help for a specific property, you may specify the property name with '-h [property]'");
        writer.println();
        writer.println("To print only a subset of options, you may specify one of the following printable groups with '-h [group]': ");
        writer.println("\t" + groupText);
        writer.println();
        writer.println("To search options, you may specify a search term with '-h [term]'");
        writer.println();
    }
}
