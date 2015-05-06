package org.waag.histograph.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waag.histograph.pg.PGMethods;
import org.waag.histograph.util.HistographTokens;

/**
 * A servlet class handling GET requests at the root path.
 * @author Rutger van Willigen
 * @author Bert Spaan
 */
public class RejectedEdgesServlet extends HttpServlet {
	private static final long serialVersionUID = 2635055832633992138L;
	
	private String TABLE_NAME;
	private Connection pg;
	
	public RejectedEdgesServlet (Connection pg, String tableName) {
		this.pg = pg;
		TABLE_NAME = tableName;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        
        String source = request.getParameter(HistographTokens.General.SOURCEID);
        if (source == null || source.equals("")) {
        	out.println(errorResponse("No source parameter supplied in GET request."));
        	return;
        }
        
        JSONArray array = new JSONArray();
        Map<String, String>[] results = null;
        
        if (source.equals("all")) {
        	try {
				results = PGMethods.getAllRows(pg, TABLE_NAME);
        	} catch (SQLException e) {
				out.println(errorResponse("Error in querying PSQL: " + e.getMessage()));
				return;
			}
        } else {
	        try {
				results = PGMethods.getRowsByKeyValPairs(pg, TABLE_NAME, "rel_source", source);
	        } catch (SQLException e) {
				out.println(errorResponse("Error in querying PSQL: " + e.getMessage()));
				return;
			}
        }
		
        if (results != null) {
	        for (Map<String, String> result : results) {
				JSONObject jsonObject = new JSONObject(result);
				array.put(jsonObject);
			}
        }
			
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put(TABLE_NAME, array);
        
        out.println(jsonResponse);
    }
	
    private String errorResponse (String errorMsg) {
    	JSONObject out = new JSONObject();
    	out.put("message", errorMsg);
    	out.put("status", "error");
    	return out.toString();
    }
}