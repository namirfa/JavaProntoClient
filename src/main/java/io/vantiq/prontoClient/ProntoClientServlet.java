package io.vantiq.prontoClient;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.vantiq.client.Vantiq;
import io.vantiq.client.VantiqResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Servlet implementation class ReportSummaryServlet
 */
@WebServlet("/ProntoClientServlet")
public class ProntoClientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Parameter strings from index.jsp file
    private static final String PARAM_SUBMIT_PASS   = "submitPass";
    private static final String PARAM_SUBMIT_AUTH   = "submitAuth";
    private static final String PARAM_USERNAME      = "username";
    private static final String PARAM_PASSWORD      = "password";
    private static final String PARAM_AUTHTOKEN     = "authToken";
    
    // Final vars for VANTIQ SDK
    private static final String VANTIQ_SERVER = "http://localhost:8080";
    
    // Global vars
    Vantiq vantiq;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProntoClientServlet() {
        super();
        vantiq = new io.vantiq.client.Vantiq(VANTIQ_SERVER);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {   
        // Check if either submit button is pressed
        boolean submitPass = request.getParameter(PARAM_SUBMIT_PASS) != null;
        boolean submitAuth = request.getParameter(PARAM_SUBMIT_AUTH) != null;
        
        // Try to authenticate with VANTIQ if values are submitted
        if (submitPass) {
            String username = request.getParameter(PARAM_USERNAME);
            String password = request.getParameter(PARAM_PASSWORD);
            
            // Using VANTIQ SDK to authenticate
            VantiqResponse vantiqResponse = vantiq.authenticate(username, password);
            if (vantiqResponse.isSuccess()) {                
                // Selecting all of the managers for given user
                // Create object to be used as filter for VANTIQ SDK Select
                HashMap<String,String> selectFilter = new HashMap<String,String>();
                selectFilter.put("ars_properties.manager", "true");
                VantiqResponse managerResponse = vantiq.select("system.nodes", null, selectFilter, null);
                
                // Get list of the managers with their meta data
                ArrayList managerResponseBody = (ArrayList) managerResponse.getBody();
                
                
                // Iterating through the JSON representation for manager meta data, converting to map,
                // and storing in an ArrayList. Also storing names to display in catalogs.jsp
                ArrayList<Map> managersData = new ArrayList<Map>();
                ArrayList<String> managerNames = new ArrayList<String>();
                Gson gson = new Gson();
                for (int i = 0; i < managerResponseBody.size(); i++) {
                    Map<String,Object> managerMap = new HashMap<String,Object>();
                    String responseBodyString = managerResponseBody.get(i).toString();
                    managerMap = (Map<String,Object>) gson.fromJson(responseBodyString, managerMap.getClass());
                    managersData.add(managerMap);
                    managerNames.add(managerMap.get("name").toString());
                }
                managerNames.add("testName");
                managerNames.add("another");
                managerNames.add("namir");
                managerNames.add("fawaz");
                
                
                // Display the appropriate view
                request.setAttribute("managerData", managerNames);
                RequestDispatcher view = request.getRequestDispatcher("catalogs.jsp");
                view.forward(request, response);
            } else {              
                // Display the appropriate view
                request.setAttribute("invalidCreds", true);
                RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
            }
        } else if (submitAuth) {
            String authToken = request.getParameter(PARAM_AUTHTOKEN);
            
            // Using VANTIQ SDK to authenticate
            vantiq.setAccessToken(authToken);
            
            // Display the appropriate view
            RequestDispatcher view = request.getRequestDispatcher("catalogs.jsp");
            view.forward(request, response);
        } else {
            // Display the appropriate view
            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
            view.forward(request, response);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
