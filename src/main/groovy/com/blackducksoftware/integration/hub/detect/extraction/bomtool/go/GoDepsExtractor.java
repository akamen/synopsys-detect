package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.parse.GoGodepsParser;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocationFactory;
import com.google.gson.Gson;

@Component
public class GoDepsExtractor extends Extractor<GoDepsContext> {

    @Autowired
    Gson gson;

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Autowired
    protected DetectCodeLocationFactory codeLocationFactory;

    @Override
    public Extraction extract(final GoDepsContext context) {
        try {
            final File goDepsFile = new File(context.goDepsDirectory, "Godeps.json");

            final String text =FileUtils.readFileToString(goDepsFile, StandardCharsets.UTF_8);

            final GoGodepsParser goDepParser = new GoGodepsParser(gson, externalIdFactory);
            final DependencyGraph dependencyGraph = goDepParser.extractProjectDependencies(text);

            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.GOLANG, context.directory.toString());

            final DetectCodeLocation codeLocation = codeLocationFactory.createBomCodeLocation(BomToolType.GO_GODEP, context.directory, externalId, dependencyGraph);
            return new Extraction.Builder().success(codeLocation).build();
        }catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
