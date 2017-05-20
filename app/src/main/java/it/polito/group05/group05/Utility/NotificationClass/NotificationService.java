package it.polito.group05.group05.Utility.NotificationClass;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import it.polito.group05.group05.Group_Activity;
import it.polito.group05.group05.Utility.BaseClasses.Singleton;

/**
 * Created by antonino on 16/05/2017.
 */

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            if (remoteMessage.getCollapseKey() == "newGroup") {
                Singleton.getInstance().setIdCurrentGroup(remoteMessage.getData().get("groupId"));
                Intent i = new Intent(this, Group_Activity.class);
                this.startActivity(i);
            }
        }

    }
}