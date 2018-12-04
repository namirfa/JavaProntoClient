package io.vantiq.prontoClient;

import java.io.IOException;

import javax.websocket.Session;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import io.vantiq.client.SubscriptionCallback;
import io.vantiq.client.SubscriptionMessage;

public class SubscriptionOutputCallback implements SubscriptionCallback {
    
    // Websocket session used to send messages to client
    Session wsSession;
    Gson gson;
    
    @Override
    public void onConnect() {
        System.out.println("Connected Successfully");
    }

    @Override
    public void onMessage(SubscriptionMessage message) {        
        LinkedTreeMap<?,?> bodyTree = (LinkedTreeMap<?,?>) message.getBody();
        
        if (wsSession.isOpen()) {
          try {
              wsSession.getBasicRemote().sendText(bodyTree.get("value").toString());
          } catch (IOException e) {
              e.printStackTrace();
          }
        }
    }

    @Override
    public void onError(String error) {
        System.out.println("Error: " + error);
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }
}
