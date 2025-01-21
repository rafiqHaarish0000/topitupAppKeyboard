package za.co.topitupkeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import za.co.topitupkeyboard.utils.ScreenStateReceiver;
import za.co.topitupkeyboard.utils.Topitup;

public class BaseAdminActivity extends AppCompatActivity implements LogoutAdminListener {

    private Handler refreshHandler;
    private Runnable runnable;
    Context mContext;
    private ScreenStateReceiver mReceiver;
    boolean isRegistered = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ( (Topitup) getApplication()).registerSessionListenerAdmin(this);
        ( (Topitup) getApplication()).startUserSessionAdmin();
        FullscreenCall();


        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenStateReceiver();
        registerReceiver(mReceiver, intentFilter);

    }
    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        ( (Topitup) getApplication()).onUserInteractedAdmin();

//      if (isValidLogin()) {
//
//          getSharedPreference().edit().putLong(KEY_SP_LAST_INTERACTION_TIME, System.currentTimeMillis()).apply();
//      }
//  else {
//          AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
//          dlgAlert.setMessage("This is an alert with no consequence");
//          dlgAlert.setTitle("App Title");
//          dlgAlert.setPositiveButton("OK", null);
//          dlgAlert.setCancelable(true);
//          dlgAlert.create().show();
//      logout();
//      }
       // Toast.makeText(BaseActivity.this, "t="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();
    }
    public void logout() {

     /*   AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("This is an alert with no consequence");
        dlgAlert.setTitle("App Title");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();*/
//        this.unregisterReceiver(mReceiver);
        finishAffinity();
startActivity(new Intent(this,activity_login.class));

     //   getSharedPreference().edit().remove(KEY_SP_LAST_INTERACTION_TIME).apply();
        //  Toast.makeText(activity_main.this, "logout",Toast.LENGTH_SHORT).show();
       // Intent myIntent2 = new Intent(mContext, activity_login.class);
     //   myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //  myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     //   finish();

     //   startActivity(myIntent2);
       // Toast.makeText(this, "lst", Toast.LENGTH_SHORT).show();
        // make shared preference null.
    }

    @Override
    protected void onResume() {
        FullscreenCall();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onSessionAdminLogout() {
        logout();
    }

    protected void onStop(){

        super.onStop();

    }


    private void FullscreenCall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //code
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
           logout();
            }
        }
    }
}
