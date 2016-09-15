package xyz.chat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message = intent.getStringExtra("Message");
        String name= intent.getStringExtra("Name");


        //Clicking the notification launches the main activity
        Intent launchIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,launchIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        //Declare the notification builder
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Chat")
                .setContentText(name+": "+message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        //Set the pending intent to builder
        builder.setContentIntent(pendingIntent);

        // Sets an ID for the notification
        int mNotificationId =1;

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, builder.build());
    }

}
