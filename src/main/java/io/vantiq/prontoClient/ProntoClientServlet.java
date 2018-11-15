package io.vantiq.prontoClient;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.vantiq.client.BaseResponseHandler;
import io.vantiq.client.Vantiq;
import io.vantiq.client.VantiqError;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;


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
        if(submitPass) {
            String username = request.getParameter(PARAM_USERNAME);
            String password = request.getParameter(PARAM_PASSWORD);
            
            // Using VANTIQ SDK to authenticate
            vantiq.authenticate(username, password, new BaseResponseHandler() {
                
                @Override public void onSuccess(Object body, Response response) {
                    super.onSuccess(body, response);
                    System.out.println("Password worked!");
                }
                
                @Override public void onError(List<VantiqError> errors, Response response) {
                    super.onError(errors, response);
                    System.out.println("Errors: " + errors);
                }
                
            });
        } else if (submitAuth) {
            String authToken = request.getParameter(PARAM_AUTHTOKEN);
            
            // Using VANTIQ SDK to authenticate
            vantiq.setAccessToken(authToken);
        }
        
        RequestDispatcher view = request.getRequestDispatcher("index.jsp");
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
