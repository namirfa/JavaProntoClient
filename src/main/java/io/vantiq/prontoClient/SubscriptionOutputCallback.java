package io.vantiq.prontoClient;

import io.vantiq.client.SubscriptionCallback;
import io.vantiq.client.SubscriptionMessage;

public class SubscriptionOutputCallback implements SubscriptionCallback {
    
    public WSServer testWS;
    
    public SubscriptionOutputCallback() {
        testWS = new WSServer();
    }
    
    @Override
    public void onConnect() {
        System.out.println("Connected Successfully");
    }

    @Override
    public void onMessage(SubscriptionMessage message) {
        System.out.println("Received Message: " + message);
        testWS.onMessage(message.toString());
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
