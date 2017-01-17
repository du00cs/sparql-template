package ch.unil.sparql.template.query;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.shared.PrefixMapping;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// based on code from http://www.baeldung.com/httpclient-ssl

/**
 * @author gushakov
 */
public class SparqlQueryService {

    private String endpoint;

    private CloseableHttpClient httpClient;

    public SparqlQueryService(String endpoint, boolean sslTrustAll) {
        this.endpoint = endpoint;
        final HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (sslTrustAll) {
            try {
                httpClientBuilder.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build())
                        .setSSLHostnameVerifier(new NoopHostnameVerifier());
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                // should not happen
            }
        }
        this.httpClient = httpClientBuilder.build();
    }

    public Collection<Triple> query(String subjectIri, PrefixMapping prefixMap) {
        List<Triple> triples = new ArrayList<>();
        try (final QueryExecution queryExec = QueryExecutionFactory.sparqlService(endpoint,
                "SELECT * WHERE { <" + prefixMap.expandPrefix(subjectIri) + "> ?p ?o }", httpClient)) {
            final ResultSet resultSet = queryExec.execSelect();
            while (resultSet.hasNext()) {
                final QuerySolution querySolution = resultSet.next();
                triples.add(Triple.create(NodeFactory.createURI(subjectIri),
                        querySolution.get("p").asNode(), querySolution.get("o").asNode()));
            }
        }
        return triples;
    }

    public void shutdown() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
