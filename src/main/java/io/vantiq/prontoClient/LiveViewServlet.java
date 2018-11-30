package io.vantiq.prontoClient;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.vantiq.client.Vantiq;
import java.io.IOException;
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
    public static Vantiq vantiq = ProntoClientServlet.vantiq;
    Gson gson = new Gson();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LiveViewServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {           
        // Retrieving all relevant data from views
        String catalogName      = request.getParameter(PARAM_CATALOG_NAME);
        String eventPath        = request.getParameter(PARAM_EVENT_PATH);
        String eventName        = request.getParameter(PARAM_EVENT_NAME);
        
        
        // View Live button pressed - Subscribing to live events, (first unsubscribe, then subscribe)
        vantiq.unsubscribeAll();
        vantiq.subscribe(Vantiq.SystemResources.TOPICS.value(), eventPath, null, new SubscriptionOutputCallback());
        
        // Displays the live events as they appear
        request.setAttribute("eventName", eventName);
        request.setAttribute("catalogName", catalogName);
        RequestDispatcher view = request.getRequestDispatcher("liveView.jsp");
        view.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }
    
}