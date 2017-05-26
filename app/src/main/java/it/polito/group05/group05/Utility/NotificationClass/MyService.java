package it.polito.group05.group05.Utility.NotificationClass;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import it.polito.group05.group05.Utility.HelperClasses.DB_Manager;


public class MyService extends IntentService {
    String b, eid, gid, uid;

    public MyService() {
        super("Myservice");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (b.equals("true"))
            DB_Manager.getInstance().payDone(gid, eid, uid);
        else
            DB_Manager.getInstance().payUnDone(gid, eid, uid);
        stopSelf();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        b = intent.getStringExtra("action");
        eid = intent.getStringExtra("expenseId");
        uid = intent.getStringExtra("requestFromId");
        gid = intent.getStringExtra("groupId");
    }
}
