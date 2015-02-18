package org.waag.histograph.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.waag.histograph.queue.NDJSONTokens;

public class CypherGraphMethods {
	
	private ExecutionEngine engine;
	private GraphDatabaseService db;
	
	public CypherGraphMethods (GraphDatabaseService db) {
		this.db = db;
		engine = new ExecutionEngine(db);
	}
	
	public Node getVertex(String hgID) throws IOException {
        ExecutionResult result;
        try (Transaction ignored = db.beginTx()) {
            result = engine.execute( "match (n {hgid: '" + hgID + "'}) return n" );
            Iterator<Node> i = result.columnAs( "n" );
            
            if (!i.hasNext()) return null;
            Node out = i.next();
          	if (i.hasNext()) throw new IOException ("Multiple vertices with hgID '" + hgID + "' found in graph.");
            return out;
        }
	}
	
	public boolean vertexExists(String hgID) throws IOException {
		return (getVertex(hgID) != null);
	}
	
	public boolean vertexAbsent(String hgID) throws IOException {
		return (getVertex(hgID) == null);
	}
	
	public Relationship getEdge(Map<String, String> params) throws IOException {
		ExecutionResult result;
		try (Transaction ignored = db.beginTx()) {
			result = engine.execute( "match (a {hgid: '" + params.get(NDJSONTokens.RelationTokens.FROM) + "'}) -[r:" + params.get(NDJSONTokens.RelationTokens.LABEL) + "]-> (b {hgid: '" + params.get(NDJSONTokens.RelationTokens.TO) + "'}) return r" );
			Iterator<Relationship> i = result.columnAs( "r" );

			if (!i.hasNext()) return null;
			Relationship out = i.next();
			if (i.hasNext()) {
				String edgeName = params.get(NDJSONTokens.RelationTokens.FROM) + " --" + params.get(NDJSONTokens.RelationTokens.LABEL) + "--> " + params.get(NDJSONTokens.RelationTokens.TO);
				throw new IOException ("Multiple edges '" + edgeName + "' found in graph.");
			}
			return out;
		}
	}
	
	public boolean edgeExists(Map<String, String> params) throws IOException {
		return (getEdge(params) != null);
	}
	
	public boolean edgeAbsent(Map<String, String> params) throws IOException {
		return (getEdge(params) == null);
	}
}