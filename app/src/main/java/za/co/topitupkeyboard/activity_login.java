package za.co.topitupkeyboard;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.text.HtmlCompat;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.topitupkeyboard.R;
import za.co.topitupkeyboard.activitySplashScreen;
import za.co.topitupkeyboard.model.GetStatusFull;
import za.co.topitupkeyboard.model.GetUpdateAll;
import za.co.topitupkeyboard.model.MessageService;
import za.co.topitupkeyboard.model.MyApiEndpointInterface;
import za.co.topitupkeyboard.model.fin_balance;
import za.co.topitupkeyboard.model.pos_user_current;
import za.co.topitupkeyboard.model.pos_users;
import za.co.topitupkeyboard.utils.Topitup;
import za.co.topitupkeyboard.utils.UserException;

//import com.imagpay.SwipeEvent;
//import com.imagpay.SwipeListener;
//import com.imagpay.emv.TransListener;
//import com.imagpay.emv.nfc.NFCEmvHandler;
//import com.imagpay.enums.CardDetected;
//import com.imagpay.enums.EmvStatus;
//import com.imagpay.enums.PrintStatus;
//import com.mobapphome.androidappupdater.tools.AAUpdaterController;


//import com.pubnub.api.PNConfiguration;
//import com.pubnub.api.PubNub;

//import com.pubnub.api.callbacks.SubscribeCallback;
//import com.pubnub.api.models.consumer.PNPublishResult;
//import com.pubnub.api.callbacks.PNCallback;
//import com.pubnub.api.models.consumer.PNStatus;
//import com.pubnub.api.models.consumer.presence.PNSetStateResult;
//import com.pubnub.api.models.consumer.pubsub.PNMessageResult;


//SwipeListener
public class activity_login  extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    //public static final String PREFS_NAME = "AssetForcePrefsFile";


    Realm realm;

    Context mContext;
    protected Topitup app;

    String userEntered;

    boolean keyPadLockedFlag = false;

    TextView[] pinBoxArray;
    String spiver;
    TextView statusView;

    // Animations
    private OvershootInterpolator mAnimationSlideInterpolator = new OvershootInterpolator(1.0f);
    private Animation mAnimSlideIn;
    private Animation mAnimSlideOut;

    //private boolean mDeleteIsShowing = false;
    private boolean mFailedLogin = false;

    public static final int USER_PIN_MAX_CHAR = 4;

    //private EditText mUserAccessCode;
    private EditText mUserAccessCode;
    private ProgressBar mLoginProgress;
    private TextView mOneButton;
    private TextView mTwoButton;
    private TextView mThreeButton;
    private TextView mFourButton;
    private TextView mFiveButton;
    private TextView mSixButton;
    private TextView mSevenButton;
    private TextView mEightButton;
    private TextView mNineButton;
    //private TextView mZeroButton;
   // private TextView mDeleteButton;
    private TextView mIsDemo,textView3,textLastSale;

    TextView btn_show_more_contact;
    TextView txt_app_version;
    String lastSaleData,setting_pockepos_open,setting_screen_off,setting_chk_auto_mpos;
    String notice_data;
    boolean is_admin_mode = false;

    boolean cell_incorrect = true;
  //  WebView webView;
    private RadioGroup radioserver;
    private RadioButton radiolve;
    private RadioButton radiodemo;
    //NFCEmvHandler nfc;
    private String SERVER;
    //PubNub pubnub;
    boolean isRegistered = false;
    private Handler refreshHandler,refreshHandlerscreensaver;
    private Runnable runnablescreensaver;
    String noticeid;
    ImageView newImageView;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);
Dialog dialogp;
    private String cslip,mslip;
    Button btn_merchant_copy,btn_customer_copy,btn_email,bt_merchant_customer_copy;
    Handler mHandler = new Handler();
    WebView wb_slip;
    TextView txtload,last_txn_pos;
    ProgressBar mposprogress;
    LinearLayout lntxwait;
    CountDownTimer cntdwnTimer;
    String stDate,endDate;
    SharedPreferences settings;
    boolean isRunning = false;
    private BroadcastReceiver mReceiver = null;
    public  boolean wasScreenOn;
    boolean stopRunning=false;
    String enable_realtime_swipe="0";
    String asset_serial="NA";
    String merchant_no="NA";
    String terminal_no="NA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        userEntered = "";

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.ding_products.activity_pin_entry_view);
        setContentView(R.layout.activity_login_pad);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Keep the screen on and bright while this kiosk activity is running.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        app = (Topitup) this.getApplication();
        FullscreenCall();
//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
//        if (getIntent().getBooleanExtra("crash", false)) {
//            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
//        }

        // add
        //nfc = NFCEmvHandler.getInstance(this);
        //nfc.addTransListener(mContext);


//        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
//        filter.addCategory(Intent.CATEGORY_HOME);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);


      //  btn_show_more_contact = (TextView) findViewById(R.id.btn_show_more_contact);
        textView3 = (TextView) findViewById(R.id.textView3);
         newImageView=(ImageView)  findViewById(R.id.newImageView);

        textLastSale = (TextView) findViewById(R.id.textLastSale);
       // webView = (WebView) findViewById(R.id.webView);
        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();



        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);



////////**************for realtime diallog

        dialogp = new Dialog(this);
        dialogp.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogp.setContentView(R.layout.dialog_dark_realtime_mpos);
        dialogp.setCancelable(false);
        this.setFinishOnTouchOutside(false);


        // initialize receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);


        btn_merchant_copy = ((AppCompatButton) dialogp.findViewById(R.id.bt_merchant_copy));
        btn_customer_copy = ((AppCompatButton) dialogp.findViewById(R.id.bt_customer_copy));
        bt_closep = ((AppCompatButton) dialogp.findViewById(R.id.bt_close));
        btn_email = ((AppCompatButton) dialogp.findViewById(R.id.bt_email));
        bt_merchant_customer_copy = ((AppCompatButton) dialogp.findViewById(R.id.bt_merchant_customer_copy));
        wb_slip = ((WebView) dialogp.findViewById(R.id.wb_realtime_slip));
        txtload = ((TextView) dialogp.findViewById(R.id.id_loading));
        mposprogress = ((ProgressBar) dialogp.findViewById(R.id.id_pbar));
        lntxwait = ((LinearLayout) dialogp.findViewById(R.id.lntxwait));
        last_txn_pos = ((TextView) dialogp.findViewById(R.id.id_last_txn_pos));

        /* TIU HEADER */
         settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_last_sale = settings.getString("setting_last_sale", "0");


        final TextView tiu_title_outlet = (TextView) findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = (TextView) findViewById(R.id.tiu_title_balance);
        final TextView tiu_title_balance_cash = (TextView) findViewById(R.id.tiu_title_balance_cash);

      update_balance();
        /* END HEADER */

        configureViews();


        txt_app_version = (TextView) findViewById(R.id.txt_app_version);
        txt_app_version.setText(" " + Topitup.APP_VERSION);

        //configureAnimations();
        setEditTextListener();


//        PNConfiguration pnConfiguration = new PNConfiguration();
//        pnConfiguration.setSubscribeKey("sub-c-4e2af884-aeed-11e9-a577-e6e01a51e1d3");
//        pnConfiguration.setPublishKey("pub-c-f5ee63c5-cbc3-441f-aeee-cf169f0033eb");
//
//
//        PubNub pubnub = new PubNub(pnConfiguration);
//
//        pubnub();

        //JsonObject position = new JsonObject();
        //position.addProperty("text", "Hello From Java SDK");
//        pubnub.publish()
//                .message(position)
//                .channel("pubnub_onboarding_channel")
//                .async(new PNCallback<PNPublishResult>() {
//                    @Override
//                    public void onResponse(PNPublishResult result, PNStatus status) {
//                        // handle response
//                    }
//                });





        // Toasty.info(mContext, Topitup.TIU_LICENSE, 8000, true).show();


//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        tech_cell_number = settings.getString("tech_cell_number","");
//
//        cell_incorrect = false;
//        activity_login_cell_number.setText(tech_cell_number);



      //  Print.Initialize();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


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
if(setting_last_sale.equals("1")) {
    get_last_sale();

}
if(Topitup.TIU_LICENSE!="") {
    get_swipe_realtime();
    get_status_full_setup();

}
if(!Topitup.DEBUG) {
    if(Topitup.TIU_LICENSE!="") {
        get_message();
    }
  /*  textView3.setOnClickListener(new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            if(notice_data!=null) {
                showCustomDialog("Top it Up", notice_data, true);
            }
        }
    });*/
}





    }


    private void get_status_full_setup() {

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();



        if(tiu_settings==null){

            spiver="0";
        }else{

            spiver=tiu_settings.spi_ver;
        }

        //Timber.i("APP VER: " + Topitup.APP_VERSION);

        Call<GetStatusFull> call = apiService.get_status_full_flag(Topitup.TIU_LICENSE,"", Topitup.APP_VERSION , spiver,"");
        call.enqueue(new Callback<GetStatusFull>() {
            @Override
            public void onResponse(Call<GetStatusFull> call, Response<GetStatusFull> response) {


                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                try {
                    realm.beginTransaction();
                    realm.where(GetStatusFull.class).findAll().deleteAllFromRealm();
                    realm.commitTransaction();
                    GetStatusFull res = response.body();

                    if (res != null) {
                        res.customer_id=Topitup.CUSTOMER_ID;
                    }


                    //res.enable_realtime_swipe=enable_realtime_swipe;
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(res);
                    realm.commitTransaction();

             //      final GetStatusFull tiu_settings1 = realm.where(GetStatusFull.class).findFirst();
                    //   Toasty.error(mContext,"enable_rpt_comm_statement="+tiu_settings1.enable_rpt_monthly_deposit, 12000, true).show();
                } catch (Exception ex)
                {

                    //Toasty.error(mContext,"Unable to fetch Balance. Check internet connection.", 4000, true).show();

                }


            }

            @Override
            public void onFailure(Call<GetStatusFull> call, Throwable t) {


            }
        });


    }

    private void update_balance() {
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final String setting_terminal_name = settings.getString("setting_terminal_name", "");
        final String setting_balance_cashier = settings.getString("setting_balance_cashier", "0");
        final String setting_balance_login = settings.getString("setting_balance_login", "0");
        final String setting_print_to_screen = settings.getString("setting_print_to_screen", "0");
        final String setting_last_sale = settings.getString("setting_last_sale", "0");


        final TextView tiu_title_outlet = (TextView) findViewById(R.id.tiu_title_outlet);
        final TextView tiu_title_balance = (TextView) findViewById(R.id.tiu_title_balance);
        final TextView tiu_title_balance_cash = (TextView) findViewById(R.id.tiu_title_balance_cash);

        try {

            final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();
            if(tiu_settings.company_name.length()>10){
                tiu_title_outlet.setText(tiu_settings.company_name.substring(0,10) + "... | " + tiu_settings.account_number);
            }
            else {

                tiu_title_outlet.setText(tiu_settings.company_name + " | " + tiu_settings.account_number);
            }

            noticeid=tiu_settings.noticeid;
            Topitup.CUSTOMER_ID=tiu_settings.customer_id;
            final fin_balance tiu_fin_balance = realm.where(fin_balance.class).findFirst();
            if (setting_balance_login.equals("1")) {
                tiu_title_balance.setText("Std R " + tiu_fin_balance.available_balance);
                if (!tiu_fin_balance.balance_cash.equals("0.00"))
                    tiu_title_balance_cash.setText("Bills R " + tiu_fin_balance.balance_cash);
                   // tiu_title_balance.setText("Std R " + tiu_fin_balance.available_balance+" | "+"Bills R " + tiu_fin_balance.balance_cash);
            } else {
                tiu_title_balance.setVisibility(View.GONE);
                tiu_title_balance_cash.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            //
        }

    }

    private void get_last_sale() {

      //  String is_admin = "0";
       // if (Topitup.IS_ADMIN.equals("1")) is_admin = "1";

        lastSaleData="";


        Call<ResponseBody> call = apiService.get_last_voucher_info(Topitup.TIU_LICENSE, Topitup.POSUSER_ID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.headers().get("Server")==null){
                    return;
                }

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                try {


                    String res = "";
                    try {
                        res = response.body().string();
                    } catch (Exception ex)
                    {
                        //
                    }


                    //Timber.e("VOUCHER " + res);

                    if (res.contains("<error><err>") || res.contains("ERR:")) {
                        String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
                        Toasty.error(mContext, matcher, 8000, true).show();
                    } else {

                        String json = res;

                        try {

                            JSONObject obj = new JSONObject(json);

                            //Log.d("My App", obj.toString());
                            //return "{\"result\":\"1\", \"stock_uid\":\"" + String.valueOf(stock_uid) + "\", \"time_diff\":\"" + String.valueOf(time_diff) + "\", \"message\": \"" + return_message + "\" }";

                            if (obj.getString("result").equals("-1")) {


                            } else {
                               // stock_uid = obj.getString("stock_uid");

                             //   txt_last_header.setText(obj.getString("last_header"));
                             //   txt_last_time.setText(obj.getString("last_time"));
                                String[] toSplit1=obj.getString("last_time").split("\\|");
                              //  txt_last_voucher_info.setText(obj.getString("message"));
                                String[] toSplit = obj.getString("message").split("\n");
                               // lastSaleData = "<u>Last Sale</u><br>Voucher : <b>"+toSplit[0];
                            lastSaleData = "Last Sale : <b>"+toSplit[1]+" "+toSplit1[1]+"</b><br>Voucher : <b>"+toSplit[0]+"</b> | Cashier: <b>"+toSplit[2].replace("User","")+"</b>";

                                //Timber.i("REPRINT: (" + String.valueOf(rowCount )+ ")" + tokens[4].toString());
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    textLastSale.setText(Html.fromHtml(lastSaleData, Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    textLastSale.setText(Html.fromHtml(lastSaleData));
                                }


                            }

                        } catch (Throwable t) {
                            //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }



                    }

                } catch (Exception ex)
                {

                    //Timber.e("REPRINT " + ex.getMessage());
                    Toasty.error(mContext, "ERR : " +  ex.getMessage(), 3000, true).show();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    Toasty.error(mContext, "Please check that your internet connection is working.", 8000, true).show();
                }
                else {
                    Toasty.error(mContext, t.getMessage(), 8000, true).show();
                }

                dialog.dismiss();

            }
        });





    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime( fecha );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    private void get_message() {
        //Toasty.error(mContext, "date", 18000, true).show();

        try {

            final MessageService mSrvice = realm.where(MessageService.class).findFirst();

            if(noticeid.equals(mSrvice.notice_id)) {
                notice_data=mSrvice.notice_data;
               // notice_data = notice_data.replace("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><title>N</title><style>html, body { overflow: hidden;cursor:none;} body { margin: 0; padding: 0px; } .notice { margin:10px;padding-left:10px; border-left:2px solid #efefef; height:55px;font-family:Tahoma, Geneva, sans-serif; font-size:16px; color:#6c6c6c;} </style>", "");
                //  Toasty.error(mContext, "Unable to login"+HtmlCompat.fromHtml(notice_data,0), 18000, true).show();
               // notice_data=notice_data.replace("Top it Up:","");

               // DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date2 = new Date();



                WebView wb_notice=(WebView)findViewById(R.id.wb_notice);
                wb_notice.getSettings().setJavaScriptEnabled(false);

                wb_notice.loadDataWithBaseURL("", notice_data, "text/html", "UTF-8", "");



            }
            else{

                realm.beginTransaction();
                realm.where(MessageService.class).findAll().deleteAllFromRealm();
                realm.commitTransaction();

                Call<MessageService> call = apiService.get_notice(Topitup.TIU_LICENSE);
                call.enqueue(new Callback<MessageService>() {
                    @Override
                    public void onResponse(Call<MessageService> call, Response<MessageService> response) {


                        if (!response.headers().get("Server").equals("TIU")) {
                            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                            dialog.dismiss();
                            return;
                        }

                        //Toasty.error(mContext, "Could not log in", 3000, true).show();
                        try {


                            //Toasty.error(mContext, response.body().toString(), 3000, true).show();

                            if (!response.headers().get("server").equals("TIU")) {
                                dialog.dismiss();
                                throw new UserException("Please check your internet connection!");
                            }


                            if (response.body().toString().toLowerCase().contains("notice_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                                //
                            } else {



                                MessageService result = response.body();

                                realm.beginTransaction();

                                // realm.where(MessageService.class).findAll().deleteAllFromRealm();
                                //realm.commitTransaction();



                                byte[] bytes = android.util.Base64.decode(result.notice_data, android.util.Base64.DEFAULT);
                                notice_data = new String(bytes);

                              //  notice_data=notice_data.replace("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><title>N</title><style>html, body { overflow: hidden;cursor:none;} body { margin: 0; padding: 0px; } .notice { margin:10px;padding-left:10px; border-left:2px solid #efefef; height:55px;font-family:Tahoma, Geneva, sans-serif; font-size:16px; color:#6c6c6c;} </style>","");
                              //Toasty.error(mContext, "Unable to login"+HtmlCompat.fromHtml(notice_data,0), 18000, true).show();
                                //notice_data=notice_data.replace("Top it Up:","");
                              //  notice_data=notice_data.replace("</head><body><div class=\"notice\">","");
                             //   notice_data=notice_data.replace("</div></body></html>","");
                             //   textView3.setText(HtmlCompat.fromHtml(notice_data,0));
                               // newImageView.setVisibility(View.VISIBLE);


                                WebView wb_notice=(WebView)findViewById(R.id.wb_notice);
                                wb_notice.getSettings().setJavaScriptEnabled(false);
                                wb_notice.loadDataWithBaseURL("", notice_data, "text/html", "UTF-8", "");
                                //  webView.loadData(notice_data, "text/html; charset=utf-8",null);

                                //
                              //  Log.i("notice_data",notice_data);
                                result.notice_data= HtmlCompat.fromHtml(notice_data,0).toString();
                                result.notice_date=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                //  Toasty.error(mContext, "Unable to login"+result.notice_date, 18000, true).show();
                                realm.copyToRealmOrUpdate(result);
                                realm.commitTransaction();

                                // update_spi(service_provider_data);

                                // stopCustomDialog("Updated","Product catalogue updated.");

                            }

                        } catch (Exception ex)
                        {

                        }


                    }

                    @Override
                    public void onFailure(Call<MessageService> call, Throwable t) {


                    }
                });





            }


        } catch (Exception ex) {
            {

                realm.beginTransaction();
                realm.where(MessageService.class).findAll().deleteAllFromRealm();
                realm.commitTransaction();

                Call<MessageService> call = apiService.get_notice(Topitup.TIU_LICENSE);
                call.enqueue(new Callback<MessageService>() {
                    @Override
                    public void onResponse(Call<MessageService> call, Response<MessageService> response) {



                        if (!response.headers().get("Server").equals("TIU")) {
                            Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                            dialog.dismiss();
                            return;
                        }

                        //Toasty.error(mContext, "Could not log in", 3000, true).show();
                        try {


                            //Toasty.error(mContext, response.body().toString(), 3000, true).show();

                            if (!response.headers().get("server").equals("TIU")) {
                                dialog.dismiss();
                                throw new UserException("Please check your internet connection!");
                            }


                            if (response.body().toString().toLowerCase().contains("notice_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                                //
                            } else {



                                MessageService result = response.body();

                                realm.beginTransaction();

                                // realm.where(MessageService.class).findAll().deleteAllFromRealm();
                                //realm.commitTransaction();



                                byte[] bytes = android.util.Base64.decode(result.notice_data, android.util.Base64.DEFAULT);
                                notice_data = new String(bytes);

                                //notice_data=notice_data.replace("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><title>N</title><style>html, body { overflow: hidden;cursor:none;} body { margin: 0; padding: 0px; } .notice { margin:10px;padding-left:10px; border-left:2px solid #efefef; height:55px;font-family:Tahoma, Geneva, sans-serif; font-size:16px; color:#6c6c6c;} </style>","");
                               // //  Toasty.error(mContext, "Unable to login"+HtmlCompat.fromHtml(notice_data,0), 18000, true).show();
                               // notice_data=notice_data.replace("Top it Up:","");
                             //   notice_data=notice_data.replace("</head><body><div class=\"notice\">","");
                            //    notice_data=notice_data.replace("</div></body></html>","");
                             //   textView3.setText(HtmlCompat.fromHtml(notice_data,0));
                                //newImageView.setVisibility(View.VISIBLE);
                                //  webView.loadData(notice_data, "text/html; charset=utf-8",null);


                                WebView wb_notice=(WebView)findViewById(R.id.wb_notice);
                                wb_notice.getSettings().setJavaScriptEnabled(false);
                                wb_notice.loadDataWithBaseURL("", notice_data, "text/html", "UTF-8", "");





                                //
                                //Log.i("notice_data",notice_data);
                                result.notice_data=HtmlCompat.fromHtml(notice_data,0).toString();
                                result.notice_date=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                //  Toasty.error(mContext, "Unable to login"+result.notice_date, 18000, true).show();
                                realm.copyToRealmOrUpdate(result);
                                realm.commitTransaction();

                                // update_spi(service_provider_data);

                                // stopCustomDialog("Updated","Product catalogue updated.");

                            }

                        } catch (Exception ex)
                        {

                        }


                    }

                    @Override
                    public void onFailure(Call<MessageService> call, Throwable t) {


                    }
                });





            }
        }




    }


    public void stopHandler() {
        refreshHandlerscreensaver.removeCallbacks(runnablescreensaver);
    }
    public void startHandler() {
       // Toast.makeText(activity_login.this, "user is inactive from last 5 minutes"+((Topitup) getApplication()).getScreensavertime(),Toast.LENGTH_SHORT).show();

        refreshHandlerscreensaver.postDelayed(runnablescreensaver, ((Topitup) getApplication()).getScreensavertime()); //for 5 minutes
    }

//    private void pubnub()
//    {
//
//
//        /* Subscribe to the demo_tutorial channel with presence and state */
//        Map<String, Object> state = new HashMap<>();
//        state.put("name", "presence-tutorial-user");
//        state.put("timestamp", (new Date()).toString());
//
//        pubnub.setPresenceState()
//                .channels(Arrays.asList("demo_tutorial"))
//                .state(state)
//                .uuid(pubnub.getUUID())
//                .async(new PNCallback<PNSetStateResult>() {
//                    @Override
//                    public void onResponse(PNSetStateResult result, PNStatus status) {
//
//                    }
//                });
//        try {
//            pubnub.addListener(new SubscribeCallback() {
//                @Override
//                public void message(PubNub pubnub, PNMessageResult message) {
//                    JsonNode msg = message.getMessage();
//                    System.out.println(msg);
//                }
//            });
//            pubnub.subscribe()
//                    .channels(Arrays.asList("demo_tutorial"))
//                    .execute();
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            pubnub.addListener(new SubscribeCallback() {
//                @Override
//                public void message(PubNub pubnub, PNMessageResult message) {
//                    JsonNode msg = message.getMessage();
//                    System.out.println(msg);
//                }
//            });
//
//            pubnub.subscribe()
//                    .channels(Arrays.asList("demo_tutorial"))
//                    .withPresence()
//                    .execute();
//
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//
//    }

    private class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i, final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }


    public void click_show_more(View v) {

        //Toasty.error(mContext, v.toString(), 3000, true).show();

//        Intent intent = new Intent(mContext, activity_web.class);
//        intent.putExtra("URL", Topitup.BASE_URL_SYNC + "info/more_concat_details/?l=" + Topitup.TIU_LICENSE);
//        startActivity(intent);

    }


    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close,bt_closep;
    Dialog dialog;

    private void showCustomDialog(String sTitle, String sContent, Boolean bCloseVisible) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);
        this.setFinishOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = ((LinearLayout) dialog.findViewById(R.id.pageLoadingWrapper));
        pageLoadingWrapper.setVisibility(View.GONE);

        pop_title = ((TextView) dialog.findViewById(R.id.pop_title));
        pop_content = ((TextView) dialog.findViewById(R.id.pop_content));



        pop_title.setText(sTitle);
        pop_content.setText(sContent);

        bt_close = ((AppCompatButton) dialog.findViewById(R.id.bt_close));
        if (!bCloseVisible) bt_close.setVisibility(View.GONE);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullscreenCall();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }





    private void setEditTextListener() {
        this.mUserAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (activity_login.this.mUserAccessCode.getText().length() == USER_PIN_MAX_CHAR) {
                    animateLoginButtonInOut(true);
                }
            }
        });
    }


    private void configureViews(){

        mUserAccessCode = (EditText)findViewById(R.id.activity_login_access_code_value);
        mUserAccessCode.setTransformationMethod(new HiddenPassTransformationMethod());

        mLoginProgress = (ProgressBar)findViewById(R.id.login_button_progress);

        mLoginProgress.getIndeterminateDrawable().setColorFilter(0xFFcc0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        mOneButton = (TextView)findViewById(R.id.one_button);
        mTwoButton = (TextView)findViewById(R.id.two_button);
        mThreeButton = (TextView)findViewById(R.id.three_button);
        mFourButton = (TextView)findViewById(R.id.four_button);
        mFiveButton = (TextView)findViewById(R.id.five_button);
        mSixButton = (TextView)findViewById(R.id.six_button);
        mSevenButton = (TextView)findViewById(R.id.seven_button);
        mEightButton = (TextView)findViewById(R.id.eight_button);
        mNineButton = (TextView)findViewById(R.id.nine_button);
        //mZeroButton = (TextView)findViewById(R.id.zero_button);
     // mDeleteButton = (TextView)findViewById(R.id.activity_login_access_code_delete);

        mIsDemo = (TextView)findViewById(R.id.activity_login_is_demo);
        if (!Topitup.DEBUG) mIsDemo.setVisibility(View.INVISIBLE);


        ImageView mLogo = (ImageView)findViewById(R.id.activity_login_admin);
        mLogo.setOnClickListener(this);

        this.mLoginProgress.setVisibility(View.GONE);

        this.mOneButton.setOnClickListener(this);
        this.mOneButton.setOnTouchListener(this);
        this.mTwoButton.setOnClickListener(this);
        this.mTwoButton.setOnTouchListener(this);
        this.mThreeButton.setOnClickListener(this);
        this.mThreeButton.setOnTouchListener(this);
        this.mFourButton.setOnClickListener(this);
        this.mFourButton.setOnTouchListener(this);
        this.mFiveButton.setOnClickListener(this);
        this.mFiveButton.setOnTouchListener(this);
        this.mSixButton.setOnClickListener(this);
        this.mSixButton.setOnTouchListener(this);
        this.mSevenButton.setOnClickListener(this);
        this.mSevenButton.setOnTouchListener(this);
        this.mEightButton.setOnClickListener(this);
        this.mEightButton.setOnTouchListener(this);
        this.mNineButton.setOnClickListener(this);
        this.mNineButton.setOnTouchListener(this);
        //this.mZeroButton.setOnClickListener(this);
        //this.mZeroButton.setOnTouchListener(this);
       // this.mDeleteButton.setVisibility(this);
      //this.mDeleteButton.setOnClickListener(this);

    }



    @SuppressLint("ResourceAsColor")
    private void do_show_enter_setup()
    {



        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        dialog.setContentView(R.layout.dialog_setup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_cell_number = (TextView) dialog.findViewById(R.id.tv_cell_number);
        final TextView tv_device_id = (TextView) dialog.findViewById(R.id.tv_device_id);
        RadioGroup radioserver = (RadioGroup) dialog.findViewById(R.id.rdpserver);
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        final SharedPreferences.Editor editor = settings.edit();

        RadioButton radiodemo=(RadioButton) dialog.findViewById(R.id.radio_demo);
        RadioButton radiolve=(RadioButton) dialog.findViewById(R.id.radio_demo);
        String DAYDREAM = settings.getString("DAYDREAM","NO");

        Button btndaydream=(Button) dialog.findViewById(R.id.btn_daydream);
        Button btnTIULocker=(Button) dialog.findViewById(R.id.btnTIULocker);

if(DAYDREAM.equals("YES"))
    btndaydream.setBackgroundColor(R.color.color_green);
        btndaydream.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent launchIntent = new Intent(Settings.ACTION_DREAM_SETTINGS);
                if (launchIntent != null) {
                    editor.putString("DAYDREAM", "YES");
                 editor.commit();

                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });


        Button btntimezone=(Button) dialog.findViewById(R.id.btnTimezone);
        String TIMEZONE = settings.getString("TIMEZONE","NO");
        if(TIMEZONE.equals("YES"))
            btntimezone.setBackgroundColor(R.color.color_green);
        btntimezone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent launchIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
                if (launchIntent != null) {
                    editor.putString("TIMEZONE", "YES");
                    editor.commit();
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });

        Button btnkeyboard=(Button) dialog.findViewById(R.id.btnKeyboard);
        String KEYBOARD = settings.getString("KEYBOARD","NO");
        if(KEYBOARD.equals("YES"))
            btnkeyboard.setBackgroundColor(R.color.color_green);
        btnkeyboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imeManager.showInputMethodPicker();
                editor.putString("KEYBOARD", "YES");
                editor.commit();
            }
        });


        btnTIULocker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent launchIntentp = getPackageManager().getLaunchIntentForPackage("com.example.simpleapplocker");
                //  Intent launchIntent = new Intent(Settings.ACTION_DREAM_SETTINGS);
                if (launchIntentp != null) {
                    startActivity(launchIntentp);//null pointer check in case package name was not found
                }
            }
        });

        String license_pin = settings.getString("device_licence_pin","");
        String device_id = settings.getString("device_id","");
        final String SERVERSHARED = settings.getString("TIU_SERVER","DEMO");
        //Toast.makeText(activity_login.this,"Selected="+SERVERSHARED,Toast.LENGTH_SHORT).show();

        if(SERVERSHARED.equals("LIVE")) {

            radioserver.check(R.id.radio_live);
        }
        else {
            radioserver.check(R.id.radio_demo);


        }

        tv_cell_number.setText(license_pin);
        tv_device_id.setText(device_id);




        radioserver.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected



                if(checkedId==R.id.radio_demo)
                {
                    SERVER="DEMO";


                    editor.putString("TIU_SERVER", "DEMO");
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
                    editor.commit();
                   // Toast.makeText(activity_login.this,"DEMO",Toast.LENGTH_SHORT).show();
                }else{
                    SERVER="LIVE";

                    editor.putString("TIU_SERVER", "LIVE");
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
                    editor.commit();
                   // Toast.makeText(activity_login.this,"LIVE",Toast.LENGTH_SHORT).show();
                }
              /*  moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);*/

//                ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
//                Intent intent = new Intent(activity_login.this, activitySplashScreen.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(pos_users.class);
                            realm.delete(GetUpdateAll.class);
                        }
                    });

                } catch (Exception ex) {
                    Toasty.error(mContext, "Could not clear database", 3000, true).show();
                }
                Intent intent = new Intent(mContext, activity_login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
                Intent mStartActivity = new Intent(activity_login.this, activitySplashScreen.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(activity_login.this, mPendingIntentId,   mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager mgr = (AlarmManager)activity_login.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
               System.exit(1);

            }
        });


        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            }
        });


        Button btnSaveNumber = (Button) dialog.findViewById(R.id.btnSaveNumber);
        btnSaveNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (tv_cell_number.getText().toString().length() == 0) {
                    Toasty.error(mContext, "Please enter a license upgrade pin!", 3000, true).show();
                    return;
                }

                if (tv_device_id.getText().toString().length() == 0) {
                    Toasty.error(mContext, "Please enter the device ID!", 3000, true).show();
                    return;
                }


                //Clear old info
                SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("device_licence_pin", "");
                editor.putString("device_id", "");
                editor.putString("TIU_SERVER","");
                editor.putString("TIU_LICENSE", "");
               // editor.putString("TIU_SERVER", "DEMO");
                editor.commit();

                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(pos_users.class);
                            realm.delete(GetUpdateAll.class);
                        }
                    });

                } catch (Exception ex) {
                    Toasty.error(mContext, "Could not clear database", 3000, true).show();
                }

                get_setup_information( tv_cell_number.getText().toString(), tv_device_id.getText().toString(),SERVERSHARED);

                //tech_cell_number = tv_cell_number.getText().toString();

                dialog.dismiss();

            }
        });


//        Button btnGetActive = (Button) dialog.findViewById(R.id.btnGetActive);
//        btnGetActive.setEnabled(false);
//        btnGetActive.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (tv_device_id.getText().toString().length() == 0) {
//                    Toasty.error(mContext, "Please enter the device ID!", 3000, true).show();
//                    return;
//                }
//                get_active_license(tv_device_id.getText().toString());
//                dialog.dismiss();
//            }
//        });

        Button btnCancelJC = (Button) dialog.findViewById(R.id.btnCancelJC);
        btnCancelJC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();


    }


    public void crashMe(View v) {
        throw new NullPointerException();
    }


    public void get_active_license(final String device_id){

//        final Call<ResponseBody> call = apiService.get_active_license(device_id);
//
//        call.enqueue(new Callback<ResponseBody>(){
//
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                //Timber.e(response.body());
//
//                String res = "";
//                try {
//
//                    res = response.body().string();
//
//                } catch (Exception ex)
//                {
//                    //
//                }
//
//                if (res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {
//
//                    //String matcher = StringUtils.substringBetween(res, "<err>", "</err>");
//                    Toasty.error(mContext, res, 3000, true).show();
//                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putString("device_licence_pin", "");
//                    editor.putString("device_id", "");
//                    editor.putString("TIU_LICENSE", "");
//                    editor.commit();
//
//                } else {        //Response OK
//
//                    //   Timber.e(res);
//
//                    Toasty.info(mContext, "ID & License PIN saved.", 3000, true).show();
//                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putString("device_licence_pin", pin);
//                    editor.putString("device_id", device_id);
//
//                    String[] parts = res.split("\\^");
//
//                    editor.putString("TIU_LICENSE", parts[3]);
//
//                    editor.commit();
//
//                    Intent mStartActivity = new Intent(mContext, activitySplashScreen.class);
//                    int mPendingIntentId = 123456;
//                    PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                    AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                    System.exit(0);
//
//                    //tv_response.setTextColor( Color.parseColor("#3c3c3c"));
//
//                }
//
//                //  Toasty.info(mContext, res, 8000, true).show();
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                Toasty.error(mContext, t.getMessage(), 8000, true).show();
//                //progressBar.setVisibility(View.GONE);
//                t.printStackTrace();
//
//            }
//
//        });



    }






    public void get_setup_information(final String pin, final String device_id,final String SERVERSHARED){

        final Call<ResponseBody> call = apiService.get_asset_upgrade(pin, device_id);

        call.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                //Timber.e(response.body());

                String res = "";
                try {

                    res = response.body().string();

                } catch (Exception ex)
                {
                    //
                }

                if (res.contains("<error><err>") || res.contains("ERR:") || res.contains("\"err\"")) {

                    //String matcher = StringUtils.substringBetween(res, "<err>", "</err>");

                    showCustomDialog("Could Not Log In",res , true);
                    //Toasty.error(mContext, res, 3000, true).show();

                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("device_licence_pin", "");
                    editor.putString("device_id", "");
                    editor.putString("TIU_LICENSE", "");
               //   editor.putString("TIU_SERVER", "DEMO");
                    editor.commit();

                } else {        //Response OK

                  // Timber.e(res);

                    Toasty.info(mContext, "ID & License PIN saved.", 3000, true).show();
                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("device_licence_pin", pin);
                    editor.putString("device_id", device_id);
                    editor.putString("TIU_SERVER", SERVERSHARED);






                    //String SERVERSHARED = settings.getString("TIU_SERVER","DEMO");




                   // Toasty.info(mContext, "s=."+SERVERSHARED, 3000, true).show();

                    String[] parts = res.split("\\^");





                    editor.putString("TIU_LICENSE", parts[3]);
                    editor.putString("setting_screen_off", "0");

                    editor.commit();
                    Intent intent = new Intent(mContext, activity_login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finishAffinity();
                    Intent mStartActivity = new Intent(mContext, activitySplashScreen.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);

                    //tv_response.setTextColor( Color.parseColor("#3c3c3c"));

                }

              //  Toasty.info(mContext, res, 8000, true).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                if (t instanceof IOException) {
                    showCustomDialog("No Internet","Please check that your internet connection is working.",true);
                }
                else {
                    showCustomDialog("Could Not Log In",t.getMessage(), true);
                }


                //Toasty.error(mContext, t.getMessage(), 8000, true).show();
                //progressBar.setVisibility(View.GONE);
                //t.printStackTrace();

            }

        });



    }

    @Override
    public void onUserInteraction() {
        stopHandler();//stop first and then start
        startHandler();
        super.onUserInteraction();

    }

    // Hide after some seconds
    Handler handler  = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            is_admin_mode = false;
      //      SharedPreferences prefs = getSharedPreferences("TIUPREF", MODE_PRIVATE);
          //  String  TIU_SERVER = prefs.getString("TIU_SERVER", "DEMO");
            if (Topitup.DEBUG ) {

                mIsDemo.setText("DEMO");
            } else {
                mIsDemo.setVisibility(View.INVISIBLE);
            }


        }


    };












    @Override
    public void onClick(View vIn) {

        if (keyPadLockedFlag) return;

        // Timber.i(String.valueOf(vIn.getId()));

        if (vIn.getId() ==  R.id.activity_login_admin) {


            //AAUpdaterController.testUpdaterDlg(this);


            //AAUpdaterController.testUpdaterDlg(this);


            //Printer.print_data("");
            //Topitup.checkOutOfPaper();
            //if (1==1) return;

           // Printer.print_data("");


            // Toasty.error(mContext,"setting.isPrinting(): " + Topitup.setting.isPrinting(), 3000, true).show();

            //if (getPrinterStatus() == PRINTER_NORMAL) printText();

            is_admin_mode = true;
            mIsDemo.setText("ADMIN");
            mIsDemo.setVisibility(View.VISIBLE);
            handler.postDelayed(runnable, 4000);
            resetPinPad();

            //do_show_enter_setup();
            return;
        }


//        if (!this.mDeleteIsShowing) {
//            crossFade( getResources().getInteger( android.R.integer.config_mediumAnimTime), this.mDeleteButton, "delete");
//            this.mDeleteIsShowing = true;
//        }

//        switch (vIn.getId()) {
//            case R.id.one_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mOneButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.two_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mTwoButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.three_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mThreeButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.four_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mFourButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.five_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mFiveButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.six_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mSixButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.seven_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mSevenButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.eight_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mEightButton.getText());
//                }
//                break;
//            case za.co.topitupkeyboard.R.id.nine_button:
//                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
//                    this.mUserAccessCode.append(this.mNineButton.getText());
//                }
//                break;
////            case za.co.topitupkeyboard.R.id.zero_button:
////                if (this.mUserAccessCode.getText().length() < USER_PIN_MAX_CHAR) {
////                    this.mUserAccessCode.append(this.mZeroButton.getText());
////                }
////                break;
////            case za.co.assetforce.R.id.activity_login_access_code_login:
////                crossFade(
////                        getResources().getInteger(android.R.integer.config_longAnimTime),this.mLoginButton,"Login");
////                this.mLoginButton.setText("");
////                this.mLoginProgress.setVisibility(View.VISIBLE);
////                    attemptLogin(this.mUserAccessCode.getText().toString());
////                break;
////            case za.co.topitupkeyboard.R.id.activity_login_access_code_delete:
////
////                this.mUserAccessCode.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
////                if (isEditTextEmpty(this.mUserAccessCode)) {
////                    crossFade( getResources().getInteger(android.R.integer.config_mediumAnimTime),this.mDeleteButton, null);
////                    //this.mDeleteIsShowing = false;
////                }
////                break;
//        }

        if (is_admin_mode && this.mUserAccessCode.getText().length() == USER_PIN_MAX_CHAR) {

            Calendar cal = Calendar.getInstance();
            String m = StringUtils.leftPad(String.valueOf(cal.get(Calendar.MONTH) + 3), 2, "0");
            String d = StringUtils.leftPad(String.valueOf(cal.get(Calendar.DAY_OF_MONTH) + 2), 2, "0");
            m = m.replace("0", "1");
            d = d.replace("0", "1");



//             Toasty.error(mContext,"pin" + this.mUserAccessCode.getText().toString(), 3000, true).show();
//             if (1==1) return;


            if (this.mUserAccessCode.getText().toString().trim().equals("7777") ) {

                resetPinPad();

                showCustomDialog("Updating","Refreshing User List", false);

                get_update_users();

            } else if (this.mUserAccessCode.getText().toString().equals(m + d) ) {

                resetPinPad();
                do_show_enter_setup();

            } else {

                resetPinPad();
                Toasty.error(mContext, "Incorrect Admin PIN", 2000, true).show();

                is_admin_mode = false;
                if (Topitup.DEBUG) {
                    mIsDemo.setText("DEMO");
                } else {
                    mIsDemo.setVisibility(View.INVISIBLE);
                }
                return;
            }


        } else if (this.mUserAccessCode.getText().length() == USER_PIN_MAX_CHAR) {
            this.mLoginProgress.setVisibility(View.VISIBLE);
            checkLogin(this.mUserAccessCode.getText().toString());
        }



    }



    private void crossFade(int animTimeIn, TextView textViewIn, String valueStringIn) {

        textViewIn.setText(valueStringIn);
        textViewIn.setAlpha(0f);
        textViewIn.setVisibility(View.VISIBLE);

        textViewIn.animate().alpha(1f).setDuration(animTimeIn)
                .setListener(null);
    }


    private static boolean isEditTextEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }



    @Override
    public boolean onTouch(View vIn, MotionEvent eventIn) {
//        switch (vIn.getId()) {
//            case za.co.topitupkeyboardkeyboard.R.id.one_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.two_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.three_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.four_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.five_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.six_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.seven_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.eight_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
//            case za.co.topitupkeyboardkeyboard.R.id.nine_button:
//                toggleNumberColor(vIn, eventIn);
//                break;
////            case za.co.topitupkeyboard.R.id.zero_button:
////                toggleNumberColor(vIn, eventIn);
////                break;
//
//
//        }
        if (vIn.getId() == za.co.topitupkeyboard.R.id.one_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.two_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.three_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.four_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.five_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.six_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.seven_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.eight_button ||
                vIn.getId() == za.co.topitupkeyboard.R.id.nine_button) {
            toggleNumberColor(vIn, eventIn);
        }
        return false;
    }

    private void toggleNumberColor(View viewIn, MotionEvent eventIn) {
        textLastSale.setVisibility(View.GONE);
        if (eventIn.getAction() == MotionEvent.ACTION_DOWN) {
            ((TextView) viewIn).setTextColor(getResources().getColor( R.color.white));
        } else if (eventIn.getAction() == MotionEvent.ACTION_UP) {
            ((TextView) viewIn).setTextColor(Color.parseColor("#811d1d"));
        }
    }



    private void animateLoginButtonInOut(boolean animateIn) {
        if (animateIn) {
            //this.mLoginButton.setVisibility(View.VISIBLE);
            //this.mLoginButton.startAnimation(this.mAnimSlideIn);
        } else {
            this.mLoginProgress.setVisibility(View.GONE);
            //this.mLoginButton.startAnimation(this.mAnimSlideOut);
            //this.mLoginButton.setVisibility(View.GONE);
            //this.mLoginButton.setText("Login");
        }
    }







    private void checkLogin(String password) {


        keyPadLockedFlag = true;

        //RealmResults<pos_users> pos_users;

        pos_users user = realm.where(pos_users.class).equalTo("posuser_pin", password).equalTo("posuser_status", 1).findFirst();

        if (null == user) {

            Toasty.error(mContext, "Invalid PIN #", 3000, true).show();
            resetPinPad();

        } else {

            Topitup.POSUSER_ID = String.valueOf(user.posuser_id);
            Topitup.IS_ADMIN = String.valueOf(user.posuser_isadmin);
            Topitup.POSUSER_NAME = user.posuser_firstname + " " + user.posuser_surname;
Topitup.RICA_REG=user.rica_registered;



            keyPadLockedFlag = false;

        }


    }






    private void get_update_users() {






        final Call<List<pos_users>> call = apiService.get_posuser_list(Topitup.TIU_LICENSE, 1);

        call.enqueue(new Callback<List<pos_users>>(){

            @Override
            public void onResponse(Call<List<pos_users>> call, Response<List<pos_users>> response) {

                //Timber.e(response.body());
                //Toasty.error(mContext, response.toString(), 8000, true).show();

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                String res = "";
                try {

                    List<pos_users> result = response.body();

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(result);
                    realm.commitTransaction();

                    //res = response.body().string();

                } catch (Exception ex)
                {
                    //
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<pos_users>> call, Throwable t) {

                dialog.dismiss();
                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

            }

        });


    }







    private void checkLoginLive(String password) {

        keyPadLockedFlag = true;

        //crossFade(getResources().getInteger(android.R.integer.config_mediumAnimTime),this.mDeleteButton, null);
        //this.mDeleteIsShowing = false;

//        SharedPreferences settings = getSharedPreferences(app.PREFS_NAME, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(app.PREFS_TECH_ID, null);
//        editor.putString(app.PREFS_TECH_NAME, null);
//        editor.commit();

        Call<pos_user_current> call = apiService.login(Topitup.TIU_LICENSE, Topitup.TIU_LICENSE, password);
        call.enqueue(new Callback<pos_user_current>() {

            @Override
            public void onResponse(Call<pos_user_current> call, Response<pos_user_current> response) {

//                Timber.i("LOGIN: Call request" + call.request().toString());
//                Timber.i("LOGIN: Call request header" + call.request().headers().toString());
//                Timber.i("LOGIN: Response raw header" + response.headers().toString());
//                Timber.i("LOGIN: Response raw" + String.valueOf(response.raw().body()));
//                Timber.i("LOGIN: Response code"+ String.valueOf(response.code()));

                //Timber.i("LOGIN: " + response.body().toString());

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }


                try {

//                    if (response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
//                        String matcher = response.body().toString();
//
//                        matcher = matcher.replace( "{", "");
//                        matcher = matcher.replace( "}", "");
//                        matcher = matcher.replace( "err=", "");
//
//                        if (matcher.toLowerCase().contains("<err>")) {
//                            matcher = StringUtils.substringBetween(response.body().toString(), "<err>", "</err>");
//                        }
//                        throw new UserException(matcher);
//                    }


                    //String res = response.body().toString();

                    pos_user_current pu = response.body(); // new Gson().fromJson(res, pos_user_current.class);


                    if (pu.error_code.equals("1"))
                    {

                        Toasty.error(mContext, pu.error_desc, 3000, true).show();
                        resetPinPad();

                    } else if (pu.posuser_id.equals("0")) {

                        Toasty.error(mContext, "Invalid PIN #", 3000, true).show();
                        resetPinPad();

                    } else {

                        Topitup.POSUSER_ID = pu.posuser_id;

                        Topitup.IS_ADMIN = pu.is_admin;
                        Topitup.POSUSER_NAME = pu.posuser_name;



                        keyPadLockedFlag = false;

                    }


                } catch (Exception ex) {

                    showCustomDialog("Could Not Log In",ex.getMessage(), true);
                    resetPinPad();

                }


            }

            @Override
            public void onFailure(Call<pos_user_current> call, Throwable t) {

                if(t instanceof SocketTimeoutException){
                    showCustomDialog("Connection Issue", "Timeout, please check internet connection.", true);
                } else {

                    showCustomDialog("Could Not Log In", t.getMessage(), true);
                }

                //Toasty.error(mContext, "Problem loggin in.", 3000, true).show();
                //Timber.e("Login error: " + t.getMessage());
                //"Invalid pin #"

                resetPinPad();
            }
        });


    }


    private void resetPinPad()
    {
        mFailedLogin = true;
        animateLoginButtonInOut(false);
        mUserAccessCode.setText("");
        keyPadLockedFlag = false;
        new LockKeyPadOperation().execute("");
    }


    @Override
    public void onBackPressed() {



        //App not allowed to go back to Parent activity until correct pin entered.
        return;

        //super.onBackPressed();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_pin_entry_view, menu);
        return true;
    }


    private class LockKeyPadOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for(int i=0;i<2;i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            keyPadLockedFlag = false;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


















//
//
//
//    @Override
//    public void onConnected(SwipeEvent arg0) {
//        // TODO Auto-generated method stub
//        //setting.mposWriteSN("82320180810001");
//
//        Timber.i("PRINT: onConnected SN:" + arg0.toString() );
//
//    }
//
//    @Override
//    public void onDisconnected(SwipeEvent arg0) {
//        // TODO Auto-generated method stub
//
//    }
////
////
////
//    @Override
//    public void onParseData(SwipeEvent event) {
//
//        Timber.i("PRINT: onParseData " + event.getValue() );
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onCardDetect(CardDetected type) {
//        //
//    }
//
//
//
//    @Override
//    public void onPrintStatus(PrintStatus arg0) {
//
//        Timber.i("PRINT: onPrintStatus SN:" + arg0.toString() );
//
//        if (arg0 == PrintStatus.IMAGES) {
//
//        } else if (arg0 == PrintStatus.EXIT) {
//            // setting.mPosExitPrint();
//            // new Thread(new Runnable() {
//            // @Override
//            // public void run() {
//            // // TODO Auto-generated method stub
//            // setting.prnStatus();
//            // }
//            // }).start();
//        } else if (arg0 == PrintStatus.NO_PAPER) {
//
//        } else if (arg0 == PrintStatus.LACK_PAPER) {
//
//        }
//
//    }
//
//
//
//    @Override
//    public void onEmvStatus(EmvStatus arg0) {
//    }


//    public void printerInit() {
//        ThreadPoolManager.getInstance().executeTask(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mIPosPrinterService.printerInit(callback);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }


    @Override
    protected void onPause() {
        // when the screen is about to turn off
        Log.e("MYAPP", "onpause="+wasScreenOn);
        if (wasScreenOn) {
           // wasScreenOn=false;
            // this is the case when onPause() is called by the system due to a screen state change
            Log.e("MYAPP", "SCREEN TURNED OFF");
        } else {
            // this is when onPause() is called when the screen state has not changed
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        //Log.d(TAG, "activity onResume");
        update_balance();
        FullscreenCall();
        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        setting_pockepos_open = settings.getString("setting_pockepos_open", "0");
        setting_screen_off = settings.getString("setting_screen_off", "0");
        editor.putString("setting_dnp", "0");
        editor.commit();
        //Log.e("MYAPP", "="+wasScreenOn);
     /*   setting_chk_auto_mpos=settings.getString("setting_chk_auto_mpos", "0");
            // this is when onResume() is called due to a screen state change
           // Log.e("MYAPP", "SCREEN TURNED ON");
        stopRunning=false;
        if(Topitup.getInstance().checkConnection(getApplicationContext())) {

            Log.e("MYAPP", "onresume="+wasScreenOn);
            if (!wasScreenOn) {
                // this is when onResume() is called due to a screen state change
                Log.e("MYAPP", "SCREEN TURNED ON");

                if(Topitup.wasInBackground == true){
                    checkPrintMPOSslip(false);
                }

                if (Topitup.TIU_LICENSE != "" && (setting_pockepos_open.equals("1"))) {
                    showCustomDialog("Loading Swipe Viewer", "Please Wait",false);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(dialog!=null)
                                dialog.dismiss();
                        }
                    },5000L);
                    checkPrintMPOSslip(true);
                }
            } else {
                // this is when onResume() is called when the screen state has not changed


            }

        }*/
       // else{

          //  showCustomDialog("Could Not Connect","Please check your internet connection and try again",true);

        //}
        super.onResume();




    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.

//        loopPrintFlag = DEFAULT_LOOP_PRINT;
//        unregisterReceiver(IPosPrinterStatusListener);
//        unbindService(connectService);

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














    public void ringtone(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ScreenReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // do whatever you need to do here
                wasScreenOn = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // and do whatever you need to do here
                wasScreenOn = true;
            }
        }

    }


    public void get_swipe_realtime(){

        final Call<ResponseBody> call = apiService.get_swipe_sts(Topitup.TIU_LICENSE);
        call.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    // stopCustomDialog("Internet Data Issue", "please contact Top it Up.");
                    return;
                }

                String res_realtime = "";
                try {
                    res_realtime = response.body().string();
                } catch (Exception ex)
                {
                    //
                }

                if (res_realtime.trim().length() == 0 || res_realtime.contains("<error><err>") || res_realtime.contains("ERR:") || res_realtime.contains("\"err\"")) {

                    String matcher = "";
                    matcher = res_realtime;
                    if (res_realtime.contains("<err>")) {
                        matcher = StringUtils.substringBetween(matcher, "<err>", "</err>");
                    }
                    matcher = matcher.replace("ERR:","");

                    // stopCustomDialog("Problem",matcher);
                   Toasty.error(mContext, "API Error"+matcher, 8000, true).show();

                } else {        //Response OK


                    try {
                        JSONObject reader = new JSONObject(res_realtime);
                        enable_realtime_swipe=reader.getString("realtime_settle");
                        asset_serial=reader.getString("asset_serial");
                        merchant_no=reader.getString("merchant_no");
                        terminal_no=reader.getString("terminal_no");
                        double min_swipe=reader.getDouble("min_swipe");
                        double max_swipe=reader.getDouble("max_swipe");
                        double warning_swipe=reader.getDouble("warning_swipe");
                        //Toasty.error(mContext, "API Error"+min_swipe, 8000, true).show();


                        Topitup.min_swipe=min_swipe;
                        Topitup.max_swipe=max_swipe;
                        Topitup.warning_swipe=warning_swipe;


                        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                        SharedPreferences.Editor editor = settings.edit();


                       // enable_realtime_swipe = res_realtime;

                        editor.putString("enable_realtime_swipe", enable_realtime_swipe);
                        editor.putString("asset_serial", asset_serial);
                        editor.putString("merchant_no", merchant_no);
                        editor.putString("terminal_no", terminal_no);
                        editor.commit();
                    }catch (JSONException e) {
                        Topitup.min_swipe=1.00;
                        Topitup.max_swipe=100000.00;
                        Topitup.warning_swipe=5000.00;
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (t instanceof IOException) {
                    // stopCustomDialog("No Internet","Please check that your internet connection is working.");
                }
                else {
                    //   stopCustomDialog("Problem",t.getMessage());
                }

            }

        });



    }


}