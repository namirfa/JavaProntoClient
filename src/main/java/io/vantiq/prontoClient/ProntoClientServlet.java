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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/**
 * Servlet implementation class ProntoClientServlet
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
    private static final String PARAM_VIEW_CATALOG  = "viewCatalog";
    private static final String PARAM_CATALOG_NAME  = "catalogName";
    private static final String PARAM_VIEW_LIVE     = "viewLive";
    private static final String PARAM_EVENT_PATH    = "eventPath";
    
    // Final vars for VANTIQ SDK
    private static final String VANTIQ_SERVER = "http://localhost:8080";
    
    // Global vars
    Vantiq vantiq;
    Gson gson = new Gson();

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
        // Check if any submit button is pressed
        boolean submitPass = request.getParameter(PARAM_SUBMIT_PASS) != null;
        boolean submitAuth = request.getParameter(PARAM_SUBMIT_AUTH) != null;
        boolean viewCatalog = request.getParameter(PARAM_VIEW_CATALOG) != null;
        boolean viewLive = request.getParameter(PARAM_VIEW_LIVE) != null;
        String catalogName = request.getParameter(PARAM_CATALOG_NAME);
        String eventPath = request.getParameter(PARAM_EVENT_PATH);
        
        // Try to authenticate with VANTIQ if username/password are submitted
        if (submitPass) {
            // Using VANTIQ SDK to authenticate
            String username = request.getParameter(PARAM_USERNAME);
            String password = request.getParameter(PARAM_PASSWORD);
            VantiqResponse vantiqResponse = vantiq.authenticate(username, password);
            
            if (vantiqResponse.isSuccess()) {                
                // Selecting all of the managers for given user
                // Create object to be used as filter for VANTIQ SDK Select
                HashMap<String,String> selectFilter = new HashMap<String,String>();
                selectFilter.put("ars_properties.manager", "true");
                VantiqResponse managerResponse = vantiq.select("system.nodes", null, selectFilter, null);
                
                // Get list of the managers with their meta data
                ArrayList<JsonObject> managerResponseBody = (ArrayList<JsonObject>) managerResponse.getBody();
                
                // Iterating through the JSON representation for each manager and storing namespace names
                ArrayList<String> managerNames = new ArrayList<String>();
                for (int i = 0; i < managerResponseBody.size(); i++) {
                    String managerName = managerResponseBody.get(0).get("name").getAsString();
                    managerNames.add(managerName);
                }            
                
                // Display the appropriate view
                request.setAttribute("managerData", managerNames);
                RequestDispatcher view = request.getRequestDispatcher("allCatalogs.jsp");
                view.forward(request, response);
            } else {
                // Username/password were invalid, display the appropriate view
                request.setAttribute("invalidCreds", true);
                RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
            }
        } else if (submitAuth) {
            // Using VANTIQ SDK to authenticate with access token
            String authToken = request.getParameter(PARAM_AUTHTOKEN);
            vantiq.setAccessToken(authToken);
            
            // Selecting all of the managers for given user
            // Create object to be used as filter for VANTIQ SDK Select
            HashMap<String,String> selectFilter = new HashMap<String,String>();
            selectFilter.put("ars_properties.manager", "true");
            VantiqResponse managerResponse = vantiq.select("system.nodes", null, selectFilter, null);
            
            // Get list of the managers with their meta data
            ArrayList<JsonObject> managerResponseBody = (ArrayList<JsonObject>) managerResponse.getBody();
            
            // Iterating through the JSON representation for each manager and storing namespace names
            ArrayList<String> managerNames = new ArrayList<String>();
            for (int i = 0; i < managerResponseBody.size(); i++) {
                String managerName = managerResponseBody.get(0).get("name").getAsString();
                managerNames.add(managerName);
            }            
            
            // Display the appropriate view
            request.setAttribute("managerData", managerNames);
            RequestDispatcher view = request.getRequestDispatcher("allCatalogs.jsp");
            view.forward(request, response);
        } else if (viewCatalog) {
            // Displaying all the events for a given catalog
            HashMap<String,String> catalogFilter = new HashMap<String,String>();
            catalogFilter.put("managerNode", catalogName);
            VantiqResponse catalogResponse = vantiq.execute("Broker.getAllEvents", catalogFilter);
            JsonArray catalogArray = (JsonArray) catalogResponse.getBody();
            
            // Getting the list of subscribers/publishers for the given namespace
            VantiqResponse subscriberResponse = vantiq.select("ArsEventSubscriber", null, null, null);
            VantiqResponse publisherResponse = vantiq.select("ArsEventPublisher", null, null, null);
            ArrayList<JsonObject> subscriberArray = (ArrayList<JsonObject>) subscriberResponse.getBody();
            ArrayList<JsonObject> publisherArray = (ArrayList<JsonObject>) publisherResponse.getBody();
            
            // Iterate over JsonArray and store in ArrayList (.jsp file can't iterate over JsonArray)
            ArrayList<JsonObject> catalogArrayList = new ArrayList<JsonObject>();
            for (int i = 0; i < catalogArray.size(); i++) {
                JsonObject elem = catalogArray.get(i).getAsJsonObject();
                
                // Merge subscriber information to event types
                for (int j = 0; j < subscriberArray.size(); j++) {
                    if (subscriberArray.get(j).get("name").getAsString().equals(elem.get("name").getAsString())) {
                        elem.addProperty("subscriber", subscriberArray.get(j).get("localName").getAsString());
                    }
                }
                
                // Merge publisher information to event types
                for (int j = 0; j < publisherArray.size(); j++) {
                    if (publisherArray.get(j).get("name").getAsString().equals(elem.get("name").getAsString())) {
                        elem.addProperty("publisher", publisherArray.get(j).get("localEvent").getAsString());
                    }
                }                
                catalogArrayList.add(elem);
            }

            // Display the appropriate view
            request.setAttribute("catalogName", catalogName);
            request.setAttribute("catalogData", catalogArrayList);
            RequestDispatcher view = request.getRequestDispatcher("catalog.jsp");
            view.forward(request, response);
        } else if (viewLive) {
            // Subscribing to live events, (first unsubscribe, then subscribe)
            vantiq.unsubscribeAll();
            vantiq.subscribe(Vantiq.SystemResources.TOPICS.value(), eventPath, null, new SubscriptionOutputCallback());
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
