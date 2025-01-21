package za.co.topitupkeyboard;

import android.accessibilityservice.AccessibilityService;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import za.co.topitupkeyboard.model.MyApiEndpointInterface;
import za.co.topitupkeyboard.utils.ScreenStateReceiver;
import za.co.topitupkeyboard.utils.Topitup;

public class BaseActivity extends AppCompatActivity implements LogoutListener {


    private Runnable runnable;
    Context mContext;

    private static long TIMEOUT_IN_MILLI;

    LinearLayout pageLoadingWrapper;
    //ProgressBar progressBar;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog,dialogp;

    AppCompatButton btn_paper_load;
    AppCompatButton btn_Paper_ignore_time;
    private AccessibilityService context;
    Boolean isRegistered=false;
    private ScreenStateReceiver mReceiver;
    private Handler refreshHandler,refreshHandlerscreensaver;
    private Runnable runnablescreensaver;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
    Boolean isInBackground;
    Handler handlerprint  = new Handler();

    int bp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        Long TIMEOUT_IN_MILLI = settings.getLong("TIMEOUT_IN_MILLI_ORI", 86400000);

        editor.putLong("TIMEOUT_IN_MILLI", TIMEOUT_IN_MILLI);
        editor.commit();

        ((Topitup) getApplication()).registerSessionListener(this);
        ((Topitup) getApplication()).startUserSession();


        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenStateReceiver();
        registerReceiver(mReceiver, intentFilter);

FullscreenCall();




        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark_spi);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);


        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = ((LinearLayout) dialog.findViewById(R.id.pageLoadingWrapper));
        pop_title = ((TextView) dialog.findViewById(R.id.pop_title));
        pop_content = ((TextView) dialog.findViewById(R.id.pop_content));
        bt_close = ((AppCompatButton) dialog.findViewById(R.id.bt_close));
        btn_paper_load = ((AppCompatButton) dialog.findViewById(R.id.btn_paper_load));
        btn_Paper_ignore_time = ((AppCompatButton) dialog.findViewById(R.id.btn_Paper_ignore_time));



/*////////////////// */

        refreshHandlerscreensaver = new Handler();
        runnablescreensaver = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                final Intent intent = new Intent(Intent.ACTION_MAIN);
                try {
                    // Somnabulator is undocumented--may be removed in a future version...
                    intent.setClassName("com.android.systemui",
                            "com.android.systemui.Somnambulator");
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } catch (Exception e) { /* Do nothing */ }




            }
        };
        startHandler();





    }


    @Override
    protected void onPause() {
        super.onPause();


      //  unregisterReceiver(mReceiver);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        ( (Topitup) getApplication()).onUserInteracted();
        stopHandler();//stop first and then start
        startHandler();
      //  batteryAlert();


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
    public boolean  batteryAlert() {


       bp = ((Topitup) getApplication()).getBatteryPercentage(getApplicationContext());
        boolean connected = ((Topitup) getApplication()).isConnected(getApplicationContext());
       Log.i("bp=","="+bp);

        if (Topitup.DEVICE_TYPE.equals("MOBILE")) {

        }
        else if(!Topitup.DEVICE_TYPE.equals("MOBILE") && bp>10){


           // checkPrintMPOSslip();
        }
        else{
        //int bp=10;
        if (bp <= 10) {







            if (bp > 5) {
                if (!connected) {
                    dialog.setCancelable(true);
                    //showCustomDialog("Please Connect Charger!!!","The Battery is getting low:\nless than "+bp+"% remaning",true);
                    showCustomDialogBattery("Please Connect Charger!!!", "Battery is currently " + bp + "%  Note Device will not print at 5%", true);
                }
                return true;

            } else {
                dialog.setCancelable(false);
                showCustomDialogBattery("Please Connect Charger!!!", "Low Battery unable to print", true);
                return false;
            }
        }
    }
return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }


    @Override
    protected void onResume() {

        batteryAlert();
        FullscreenCall();
        super.onResume();

    }



    public void logout() {
      //  Toast.makeText(BaseActivity.this, "in=baseactivity",Toast.LENGTH_LONG).show();
     /*   AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("This is an alert with no consequence");
        dlgAlert.setTitle("App Title");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();*/
        //this.unregisterReceiver(mReceiver);
       finishAffinity();
        //  finish();
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
    public void onSessionLogout() {
       // Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
        logout();
    }



    private class ScreenReceiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
            SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
            SharedPreferences.Editor editor = settings.edit();


            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                editor.putString("setting_screen_off", "1");
                editor.commit();
                //code
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {


                editor.putString("setting_screen_off", "1");
                editor.commit();
                logout();
            }
        }

    }




    //  AppCompatButton btn_Paper_ignore_always;

    private void showCustomDialogBattery(String sTitle, String sContent, Boolean showClose) {

        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        if (!showClose) {
            bt_close.setVisibility(View.GONE);
            btn_paper_load.setVisibility(View.GONE);
            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.GONE);
        } else {
            pageLoadingWrapper.setVisibility(View.GONE);

                bt_close.setVisibility(View.VISIBLE);
                btn_paper_load.setVisibility(View.GONE);

            btn_Paper_ignore_time.setVisibility(View.GONE);
            // btn_Paper_ignore_always.setVisibility(View.VISIBLE);
        }
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        btn_paper_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        btn_Paper_ignore_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }








    public void stopHandler() {
        refreshHandlerscreensaver.removeCallbacks(runnablescreensaver);
    }
    public void startHandler() {


        refreshHandlerscreensaver.postDelayed(runnablescreensaver, ((Topitup) getApplication()).getScreensavertime()); //for 5 minutes
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












}
