package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.example.Resource;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.fuseki.embedded.FusekiEmbeddedServer;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.util.UUID;

import static ch.unil.sparql.template.Utils.triple;
import static ch.unil.sparql.template.Vocabulary.EXP_NS;
import static ch.unil.sparql.template.Vocabulary.EXR_NS;
import static org.assertj.core.api.Assertions.assertThat;

// code based on example from http://jena.apache.org/documentation/fuseki2/fuseki-embedded.html#example-1

/**
 * @author gushakov
 */
public class SparqlTemplateFusekiEmbeddedTestIT {

    private static final Logger logger = LoggerFactory.getLogger(SparqlTemplateFusekiEmbeddedTestIT.class);

    @Test
    public void testLoad() throws Exception {

        final Dataset ds = DatasetFactory.createTxnMem();
        final Graph graph = ds.asDatasetGraph().getDefaultGraph();

        loadSampleData(graph);

        // reserved by build-helper-maven-plugin at build-time
        final String portArg = System.getenv().get("fuseki.embedded.http.port");
        final int port;
        if (portArg == null) {
            // for tests from IDE
            port = new ServerSocket(0).getLocalPort();
        } else {
            port = Integer.parseInt(portArg);
        }

        final FusekiEmbeddedServer server = FusekiEmbeddedServer.create()
                .setPort(port)
                .add("/ds", ds)
                .build();
        server.start();

        logger.debug("Started embedded Fuseki server on port " + port);

        try {
            final SparqlTemplate sparqlTemplate = new SparqlTemplate("http://localhost:" + port + "/ds/sparql");
            final Resource resource = sparqlTemplate.load(EXR_NS + "1", Resource.class);
            assertThat(resource.getName()).isEqualTo("resource one");
        } finally {
            server.stop();
        }
    }

    private void loadSampleData(Graph graph) {
        graph.add(triple(EXR_NS + "1", EXP_NS + "name", "resource one", XSDDatatype.XSDstring));
        graph.add(triple(EXR_NS + "1", EXP_NS + "uuid", UUID.randomUUID().toString(), XSDDatatype.XSDstring));
    }
}
