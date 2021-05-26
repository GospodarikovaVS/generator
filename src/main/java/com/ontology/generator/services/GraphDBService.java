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

    private String strInsert;
    private String strQuery = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX :<http://www.semanticweb.org/krysanovs/ontologies/2018/5/gazpromneft_demo#> " +
            "select * " +
            "where { ?s rdf:type :LaborContract. } limit 100";

    private RepositoryConnection getRepositoryConnection() {
        Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
        repository.initialize();
        RepositoryConnection repositoryConnection =
                repository.getConnection();
        return repositoryConnection;
    }

//    private void insert(
//            RepositoryConnection repositoryConnection) {
//
//        repositoryConnection.begin();
//        Update updateOperation = repositoryConnection
//                .prepareUpdate(QueryLanguage.SPARQL, strInsert);
//        updateOperation.execute();
//
//        try {
//            repositoryConnection.commit();
//        } catch (Exception e) {
//            if (repositoryConnection.isActive())
//                repositoryConnection.rollback();
//        }
//    }

    private String query(
            RepositoryConnection repositoryConnection) {
        TupleQuery tupleQuery = repositoryConnection
                .prepareTupleQuery(QueryLanguage.SPARQL, strQuery);
        TupleQueryResult result = null;
        try {
            result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();

                SimpleLiteral name =
                        (SimpleLiteral)bindingSet.getValue("name");
                logger.trace("name = " + name.stringValue());
            }
        }
        catch (QueryEvaluationException qee) {
            logger.error(WTF_MARKER,
                    qee.getStackTrace().toString(), qee);
            return qee.toString();
        } finally {
            result.close();
            return result.toString();
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
