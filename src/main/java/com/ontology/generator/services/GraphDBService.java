package com.ontology.generator.services;

import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GraphDBService {
    private Logger logger = LoggerFactory.getLogger(GraphDBService.class);
    // Why This Failure marker
    private final Marker WTF_MARKER =
            MarkerFactory.getMarker("WTF");

    // GraphDB
    @Value("${graph.db.server}")
    private String GRAPHDB_SERVER;
    @Value("${graph.db.repository.id}")
    private String REPOSITORY_ID;
    @Value("${graph.db.base.class}")
    private String BASE_CLASS;

    private String strPrefixes = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX :<http://www.semanticweb.org/krysanovs/ontologies/2018/5/gazpromneft_demo#> ";
    private String strQuery = "select * " +
            "where { ?s rdf:type :classFilter. } limit 100";

    private RepositoryConnection getRepositoryConnection() {
        Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
        repository.initialize();
        RepositoryConnection repositoryConnection =
                repository.getConnection();
        return repositoryConnection;
    }

    private String query(
            RepositoryConnection repositoryConnection) {
        TupleQuery tupleQuery = repositoryConnection
                .prepareTupleQuery(QueryLanguage.SPARQL, strQuery);
        TupleQueryResult result = null;
        String strRes = "";
        try {
            result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                strRes += bindingSet.toString() + "\n";
            }
        }
        catch (QueryEvaluationException qee) {
            logger.error(WTF_MARKER,
                    qee.getStackTrace().toString(), qee);
            return qee.toString();
        } finally {
            result.close();
            return strRes;
        }
    }

    public String sendRequest() {
        String result = "";
        RepositoryConnection repositoryConnection = null;
        try {
            repositoryConnection = getRepositoryConnection();

            result = query(repositoryConnection);

        } catch (Throwable t) {
            logger.error(WTF_MARKER, t.getMessage(), t);
        } finally {
            repositoryConnection.close();
        }
        return result;
    }
}
