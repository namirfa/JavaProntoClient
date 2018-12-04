package io.vantiq.prontoClient;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.vantiq.client.Vantiq;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;

/**
 * Servlet implementation class ProntoClientServlet
 */
@WebServlet("/LiveView")
public class LiveViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Parameter strings from all .jsp files
    private static final String PARAM_CATALOG_NAME  = "catalogName";
    private static final String PARAM_EVENT_PATH    = "eventPath";
    private static final String PARAM_EVENT_NAME    = "eventName";
    
    // Global vars
    HashMap<String,Vantiq> vantiqMap = ProntoClientServlet.vantiqMap;
    Gson gson = new Gson();
    public static SubscriptionOutputCallback subCallback = new SubscriptionOutputCallback();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LiveViewServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {           
        // Retrieving all relevant data from view
        String catalogName  = request.getParameter(PARAM_CATALOG_NAME);
        String eventPath    = request.getParameter(PARAM_EVENT_PATH);
        String eventName    = request.getParameter(PARAM_EVENT_NAME);
        
        // Get vantiq instance based on session
        Vantiq vantiq = vantiqMap.get(request.getSession().getId());
        
        
        // View Live button pressed - Subscribing to live events, (first unsubscribe, then subscribe)
        vantiq.unsubscribeAll();
        vantiq.subscribe(Vantiq.SystemResources.TOPICS.value(), eventPath, null, subCallback);
        
        // Displays the live events as they appear
        request.setAttribute("eventName", eventName);
        request.setAttribute("catalogName", catalogName);
        RequestDispatcher view = request.getRequestDispatcher("liveView.jsp");
        view.forward(request, response);
    }
}