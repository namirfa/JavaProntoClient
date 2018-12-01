package io.vantiq.prontoClient;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/websocket")
public class WSServer {
    
    public WSServer () {
        System.out.println("WS Constructor");
    }
    
    @OnOpen
    public void onOpen() {
        System.out.println("Opened!");
    }
    
    @OnClose
    public void onClose() {
        System.out.println("Closed!");
    }
    
    @OnMessage
    public String onMessage(String message) {
        System.out.println("Message received at endpoint: " + message);
        return message;
    }
    
    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
