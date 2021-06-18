package com.ontology.generator.service;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GraphDBService {
    private Logger logger = LoggerFactory.getLogger(GraphDBService.class);
    // Why This Failure marker
    private final Marker GRAPH_DB_ERROR_MARKER = MarkerFactory.getMarker("ERROR.WITH.GRAPH.DB");

    // GraphDB configuration
    @Value("${graph.db.server}")
    private String GRAPHDB_SERVER;
    @Value("${graph.db.repository.id}")
    private String REPOSITORY_ID;
    @Value("${graph.db.base.class}")
    private String BASE_CLASS;

    public ArrayList<BindingSet> sendRequest(String query) {
        ArrayList<BindingSet> result = new ArrayList<>();
        RepositoryConnection repositoryConnection = null;
        try {
            repositoryConnection = getRepositoryConnection();
            result = executeQuery(repositoryConnection, query);
        } catch (Throwable t) {
            logger.error(GRAPH_DB_ERROR_MARKER, t.getMessage(), t);
        } finally {
            repositoryConnection.close();
            return result;
        }
    }

    public String replaceBaseClassInRequest(String request) {
        return request.replace("_placeForBaseClass_", BASE_CLASS);
    }

    public String replaceBaseClassInRequest(String request, String placeName) {
        return request.replace(placeName, BASE_CLASS);
    }

    private RepositoryConnection getRepositoryConnection() {
        Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
        repository.initialize();
        RepositoryConnection repositoryConnection =
                repository.getConnection();
        return repositoryConnection;
    }

    private ArrayList<BindingSet> executeQuery(RepositoryConnection repositoryConnection, String query) {
        TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
        TupleQueryResult result = null;
        ArrayList<BindingSet> set = new ArrayList<>();
        try {
            result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                set.add(bindingSet);
            }
        }
        catch (QueryEvaluationException qee) {
            logger.error(GRAPH_DB_ERROR_MARKER,
                    qee.getStackTrace().toString(), qee);
        } finally {
            result.close();
            return set;
        }
    }
}
