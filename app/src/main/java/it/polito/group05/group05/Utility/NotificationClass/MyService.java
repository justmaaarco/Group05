package it.polito.group05.group05.Utility.NotificationClass;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.polito.group05.group05.R;
import it.polito.group05.group05.Utility.BaseClasses.CurrentUser;
import it.polito.group05.group05.Utility.BaseClasses.Singleton;
import it.polito.group05.group05.Utility.HelperClasses.DB_Manager;


public class MyService extends IntentService {
    Map<String, String> map = new HashMap<>();
    ChildEventListener childEventListener;
    DatabaseReference db;

    public MyService() {
        super("Myservice");
    }


    @Override
    public void onCreate() {
        super.onCreate();


        //  android.os.Debug.waitForDebugger();

        if (Singleton.getInstance().getCurrentUser() == null)
            DB_Manager.getInstance().getCurrentUser();
        try {

            db = FirebaseDatabase.getInstance().getReference("notifications");
            List<String> list = ((CurrentUser) Singleton.getInstance().getCurrentUser()).getGroups();
            for (String s : list)
                map.put(s, s);
            childEventListener = new ChildEventListener() {


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!dataSnapshot.exists()) return;
                    if (dataSnapshot.child("members").child(Singleton.getInstance().getCurrentUser().getId()).exists())
                        if (dataSnapshot.child("members").child(Singleton.getInstance().getCurrentUser().getId()).getValue().toString().equals("notNotify"))
                            return;
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot tmp = iterator.next();
                        defineNotification(tmp);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    if (!dataSnapshot.exists()) return;
                    if (dataSnapshot.child("members").child(Singleton.getInstance().getCurrentUser().getId()).exists())
                        if (dataSnapshot.child("members").child(Singleton.getInstance().getCurrentUser().getId()).getValue().toString().equals("notNotify"))
                            return;
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot tmp = iterator.next();
                        defineNotification(tmp);
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            db.addChildEventListener(childEventListener);

        } catch (Exception e) {

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // db.removeEventListener(childEventListener);
        Intent intent = new Intent(getApplicationContext(), MyService.class);

        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm_manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 100000, pi);

    }

    private void defineNotification(DataSnapshot tmp) {
        String myId = Singleton.getInstance().getCurrentUser().getId();
        switch (tmp.getKey()) {
            case "members":
                if (!tmp.child(myId).exists()) return;
                String tmp1 = tmp.child(myId).getValue().toString();
                boolean isNotified = tmp1.equals("notified") | tmp1.equals("notNotify");
                if (isNotified) break;
                buildNotification(tmp, 0);
                tmp.child(myId).getRef().setValue("notified");
                break;
            case "expenses":
                Iterator<DataSnapshot> iterator2 = tmp.getChildren().iterator();
                while (iterator2.hasNext()) {
                    DataSnapshot tmp2 = iterator2.next();
                    if (!tmp2.child("members").child(myId).exists()) continue;
                    if (!tmp2.child("owner").getValue().toString().equals(myId))
                        buildNotification(tmp2, 1);
                    if (tmp2.child("members").getChildrenCount() == 1)
                        tmp2.getRef().removeValue();
                    else
                        tmp2.child("members").child(myId).getRef().removeValue();
                }

                break;
            case "chats":
                iterator2 = tmp.getChildren().iterator();
                while (iterator2.hasNext()) {
                    DataSnapshot tmp2 = iterator2.next();
                    if (!tmp2.child("members").child(myId).exists()) continue;
                    if (!tmp2.child("messageUserId").getValue().toString().equals(myId))
                        buildNotification(tmp2, 2);
                    if (tmp2.child("members").getChildrenCount() == 1)
                        tmp2.getRef().removeValue();
                    else
                        tmp2.child("members").child(myId).getRef().removeValue();
                }

                break;
            case "paymentNotification":
                iterator2 = tmp.getChildren().iterator();
                while (iterator2.hasNext()) {
                    DataSnapshot tmp2 = iterator2.next();
                    if (!tmp2.child("members").child(myId).exists()) continue;
                    buildNotification(tmp2, 3);
                    if (tmp2.child("members").getChildrenCount() == 1)
                        tmp2.getRef().removeValue();
                    else
                        tmp2.child("members").child(myId).getRef().removeValue();
                }
                break;

        }


    }

    private void buildNotification(DataSnapshot dataSnapshot, int type) {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;
        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(getPackageName().toString())) isActivityFound = true;

        //if (isActivityFound) return;


        NotificationCompat.Builder nb = new NotificationCompat.Builder(getBaseContext());
        nb.setAutoCancel(true).setSmallIcon(R.drawable.logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(R.drawable.ic_mail_white_24dp, "Pay", null)
                .addAction(R.drawable.ic_mail_white_24dp, "Another Pay", null);
        String s = "default";
        switch (type) {
            case 0:
                s = "new Group";
                break;
            case 1:
                s = "new Expense";
                break;
            case 2:
                s = "new Message";
                break;

        }
        nb.setContentText(s).setContentTitle(s).setTicker(s);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(type, nb.build());

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);


    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}