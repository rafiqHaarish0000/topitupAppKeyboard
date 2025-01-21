package za.co.topitupkeyboard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import za.co.topitupkeyboard.activitySplashScreen;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, activitySplashScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}