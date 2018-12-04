package io.vantiq.prontoClient;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/websocket")
public class WSServer {
    
    @OnOpen
    public void onOpen(Session session) {
        LiveViewServlet.subCallback.wsSession = session;
    }
}
