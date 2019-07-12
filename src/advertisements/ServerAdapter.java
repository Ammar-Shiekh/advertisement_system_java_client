/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advertisements;

import advertisements.listeners.AddAdvertisementEventListener;
import advertisements.listeners.RemoveAdvertisementEventListener;
import advertisements.listeners.UpdateAdvertisementEventListener;
import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ammar
 */
public class ServerAdapter {
    
    private static final String PUSHER_APP_KEY = "f064778aaec3a1766707";
    private static final String PUSHER_CLUSTER = "ap2";
    
    public static void Initialize(Authentication auth, AddAdvertisementEventListener addListener,
            RemoveAdvertisementEventListener removeListener,
            UpdateAdvertisementEventListener updateListener) throws Exception {
        Map<String, String> authHeaders = new HashMap();

        authHeaders.put("Authorization", "Bearer " + auth.getToken());

        HttpAuthorizer authorizer = 
                new HttpAuthorizer(Helper.getURL(auth.getHost(), Route.BROADCASTING).toString());
        authorizer.setHeaders(authHeaders);
        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer).setCluster(PUSHER_CLUSTER);
        Pusher pusher = new Pusher(PUSHER_APP_KEY, options);
        String channelName = String.format("private-device.%s", auth.getId());
        PrivateChannel channel = pusher.subscribePrivate(channelName, new PrivateChannelEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                System.out.println(String.format("Received event on channel [%s]", channelName));
            }

            @Override
            public void onSubscriptionSucceeded(String string) {
                System.out.println(String.format("Subscribed to channel [%s]", string));
            }

            @Override
            public void onAuthenticationFailure(String string, Exception excptn) {
                System.out.println(string);
            }
        });
        Gson gson = new Gson();

        channel.bind("App\\Events\\AdvertisementAdded", new PrivateChannelEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                AdvertisementContainer ad = gson.fromJson(data, AdvertisementContainer.class);
                addListener.advertisementAdded(ad.advertisement);
                System.out.println(String.format("Advertisement added %s", ad.advertisement));
            }

            @Override
            public void onSubscriptionSucceeded(String string) {
                System.out.println(String.format("Subscribed to channel [%s]", string));
            }

            @Override
            public void onAuthenticationFailure(String string, Exception excptn) {
                System.out.println(string);
            }
        });

        channel.bind("App\\Events\\AdvertisementUpdated", new PrivateChannelEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                AdvertisementContainer ad = gson.fromJson(data, AdvertisementContainer.class);
                updateListener.advertisementUpdated(ad.advertisement);
                System.out.println(String.format("Advertisement updated %s", ad.advertisement));
            }

            @Override
            public void onSubscriptionSucceeded(String string) {
                System.out.println(String.format("Subscribed to channel [%s]", string));
            }

            @Override
            public void onAuthenticationFailure(String string, Exception excptn) {
                System.out.println(string);
            }
        });

        channel.bind("App\\Events\\AdvertisementRemoved", new PrivateChannelEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                AdvertisementContainer ad = gson.fromJson(data, AdvertisementContainer.class);
                removeListener.advertisementRemoved(ad.advertisement);
                System.out.println(String.format("Advertisement removed %s", ad.advertisement));
            }

            @Override
            public void onSubscriptionSucceeded(String string) {
                System.out.println(String.format("Subscribed to channel [%s]", string));
            }

            @Override
            public void onAuthenticationFailure(String string, Exception excptn) {
                System.out.println(string);
            }
        });

        pusher.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    System.out.println("State changed to " + change.getCurrentState() +
                                       " from " + change.getPreviousState());
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    System.out.println("There was a problem connecting!");
                }
            });
    }
}
