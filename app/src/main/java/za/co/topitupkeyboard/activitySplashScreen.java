package za.co.topitupkeyboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import za.co.topitupkeyboard.R;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import za.co.topitupkeyboard.model.GetUpdateAll;
import za.co.topitupkeyboard.model.MyApiEndpointInterface;
import za.co.topitupkeyboard.model.fin_balance;
import za.co.topitupkeyboard.model.pos_users;
import za.co.topitupkeyboard.model.service_provider;
import za.co.topitupkeyboard.model.service_provider_item;
import za.co.topitupkeyboard.model.service_provider_item_settings;
import za.co.topitupkeyboard.utils.Topitup;
import za.co.topitupkeyboard.utils.UserException;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;


public class activitySplashScreen extends AppCompatActivity {

    private ImageView mLogo;
    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);

    private static int SPLASH_TIME_OUT = 3000;

    private boolean busy_loading = false;

    Realm realm;
    Context mContext;
String enable_vas;
    String noticeid;
    String customer_id;
    String enable_rpt_monthly_deposit="0",enable_rpt_comm_statement="0";
    String spiver;
    //***
    RealmResults<service_provider_item_settings> service_provider_item_settings;
    int size;
    za.co.topitupkeyboard.model.service_provider_item_settings service_provider_item_setting;
    RealmResults<service_provider_item> service_provider_items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        FullscreenCall();

        setContentView(R.layout.activity_splash_screen);


        // Initialize Realm
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();


        mLogo = (ImageView) findViewById(R.id.logo_assetforce_splash);

        animation1();


        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //AAUpdaterController.init(this,Topitup.BASE_URL_UPDATE + "api/get_android_update");




        if (Topitup.TIU_LICENSE.equals("")) {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(activitySplashScreen.this, activity_login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                   overridePendingTransition(0, R.anim.splashfadeout2);
                }
            }, SPLASH_TIME_OUT);

        } else {



         //   get_update_all();


        //    get_status_full();

         //   get_update_users();

       //     get_balance();

        }


    }






    private void get_status_full() {
     /*   realm.beginTransaction();
        realm.where(service_provider_item_settings.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();*/

        busy_loading = true;

        //Timber.i("APP VER: " + Topitup.APP_VERSION);

        Call<GetUpdateAll> call = apiService.get_status_full(Topitup.TIU_LICENSE,"", Topitup.APP_VERSION , spiver,"");
        call.enqueue(new Callback<GetUpdateAll>() {
            @Override
            public void onResponse(Call<GetUpdateAll> call, Response<GetUpdateAll> response) {


                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                try {

                    GetUpdateAll res = response.body();


                } catch (Exception ex)
                {

                    //Toasty.error(mContext,"Unable to fetch Balance. Check internet connection.", 4000, true).show();

                }
                complete_step_4 = true;
                check_complete();

            }

            @Override
            public void onFailure(Call<GetUpdateAll> call, Throwable t) {

                //Toasty.error(mContext, t.getMessage(), 3000, true).show();
                //Timber.e("GetUpdateAll error: " + t.getMessage());

                complete_step_4 = true;
                check_complete();

            }
        });


    }







    private void get_update_users() {

       // Timber.i("SPLASH get_update_users");

        busy_loading = true;

//        RealmResults<GetUpdateAll> result2 = realm.where(GetUpdateAll.class)
//                .equalTo("customer_id", Topitup.CUSTOMER_ID)
//                .findFirst();


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

                if (res.contains("<error><err>")) {

                    //Toasty.info(mContext, matcher, 8000, true).show();

                } else {        //Response OK


                }

                complete_step_2 = true;
                check_complete();

            }

            @Override
            public void onFailure(Call<List<pos_users>> call, Throwable t) {

                //Timber.i("SPLASH " +  t.getMessage());
                //Toasty.error(mContext, t.getMessage(), 8000, true).show();

                complete_step_2 = true;
                check_complete();

            }

        });


    }



    private void get_update_all() {
        //Timber.i("result-manoj="+"called");
        busy_loading = true;

//        RealmResults<GetUpdateAll> result2 = realm.where(GetUpdateAll.class)
//                .equalTo("customer_id", Topitup.CUSTOMER_ID)
//                .findFirst();

        final GetUpdateAll tiu_settings = realm.where(GetUpdateAll.class).findFirst();



        if(tiu_settings==null){

            spiver="0";
        }else{

            spiver=tiu_settings.spi_ver;
        }
        Call<GetUpdateAll> call = apiService.get_update_all(Topitup.TIU_LICENSE,spiver,"",Topitup.APP_VERSION);
        call.enqueue(new Callback<GetUpdateAll>() {
            @Override
            public void onResponse(Call<GetUpdateAll> call, Response<GetUpdateAll> response) {

                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                    //Toasty.error(mContext, "Could not log in", 3000, true).show();
                try {


                    //Toasty.error(mContext, response.body().toString(), 3000, true).show();

                    if (!response.headers().get("server").equals("TIU")) {
                        throw new UserException("Please check your internet connection!");
                    }

                    if (response.body().toString().toLowerCase().contains("customer_id = null") || response.body().toString().toLowerCase().contains("<error><err>") || response.body().toString().toLowerCase().contains("err:") || response.body().toString().toLowerCase().contains("err=")) {
                        //

                    } else {

                        GetUpdateAll result = response.body();

                        realm.beginTransaction();

                        String service_provider_data = "";

                        byte[] bytes = android.util.Base64.decode(result.published_data, android.util.Base64.DEFAULT);
                        service_provider_data = new String(bytes);



                        result.service_provider_data = service_provider_data;

                        customer_id=result.customer_id;

                        //Toasty.error(mContext,"enable_rpt_comm_statement="+result.enable_rpt_comm_statement, 12000, true).show();

                        realm.copyToRealmOrUpdate(result);
                        realm.commitTransaction();

                        update_spi(service_provider_data);



                    }

                } catch (Exception ex)
                {
                    Toasty.error(mContext, "ERR : " +  ex.getMessage(), 3000, true).show();
                }


                complete_step_1 = true;
                check_complete();

            }

            @Override
            public void onFailure(Call<GetUpdateAll> call, Throwable t) {

              Toasty.error(mContext, t.getMessage(), 100000, true).show();
                Timber.e("GetUpdateAll error: " + t.getMessage());

                complete_step_1 = true;
                check_complete();

            }
        });


    }


    boolean complete_step_1 = false;
    boolean complete_step_2 = false;
    boolean complete_step_3 = false;
    boolean complete_step_4 = false;

    private void check_complete()
    {

        if (complete_step_1 && complete_step_2 && complete_step_3 && complete_step_4) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                   // Toasty.info(mContext, "Saved! Logged out after 3 minutes on inactivity", 20000, true).show();

                    Intent i = new Intent(activitySplashScreen.this, activity_login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                    overridePendingTransition(0, R.anim.splashfadeout2);
                }
            }, SPLASH_TIME_OUT);
        }


    }







    public void get_balance(){

        final Call<fin_balance> call = apiService.get_balance(Topitup.TIU_LICENSE, "1");

        call.enqueue(new Callback<fin_balance>(){

            @Override
            public void onResponse(Call<fin_balance> call, Response<fin_balance> response) {


                if (!response.headers().get("Server").equals("TIU")) {
                    Toasty.error(mContext, "Internet Data Issue, please contact Top it Up.", 8000, true).show();
                    return;
                }

                try {

                    fin_balance res = response.body();

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(res);
                    realm.commitTransaction();

                } catch (Exception ex)
                {

                    //Toasty.error(mContext,"Unable to fetch Balance. Check internet connection.", 4000, true).show();

                }


                complete_step_3 = true;
                check_complete();

            }

            @Override
            public void onFailure(Call<fin_balance> call, Throwable t) {

                complete_step_3 = true;
                check_complete();

            }

        });



    }






    private void update_spi(String service_provider_data){


        realm.beginTransaction();
        realm.where(service_provider.class).findAll().deleteAllFromRealm();
        realm.where(service_provider_item.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();


        BufferedReader bufReader = new BufferedReader(new StringReader(service_provider_data));

        try {

            String line = null;
            Integer last_sp_id = 0;

            while ((line = bufReader.readLine()) != null) {


           // Timber.i(line);

                //System.out.println(line);

                String[] myData = line.split("\\^");
                //for (String s: myData) {

                   //if (s.length() > 0) {

                    if (myData[0].equals("p"))
                    {

                        service_provider new_sp = new service_provider();
                        new_sp.provider_id = Integer.parseInt(myData[1]);

                        last_sp_id = Integer.parseInt(myData[1]);

                       // Timber.i("SPI : " + line );

                        if (myData.length > 2) new_sp.provider_desc = myData[2];
                        if (myData.length > 3) new_sp.provider_message = myData[3];

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(new_sp);
                        realm.commitTransaction();

                    } else {

                        service_provider_item new_spi = new service_provider_item();
                        new_spi.service_provider_item_id = Integer.parseInt(myData[0]);
                        new_spi.service_provider_id = last_sp_id;

//                        Timber.i("SPI : SPI -  " + myData[0] );
//                        if (last_sp_id == 19) {
//                            Timber.i("SPI :  " + String.valueOf(new_spi.service_provider_item_id));
//                        }
//

                        boolean item_show_value = false;
                        if (Integer.parseInt(myData[7]) == 1) item_show_value = true;

                        new_spi.item_desc = myData[1];
                        new_spi.item_btn_desc = myData[2];
                        //new_spi.item_print_desc = myData[3];
                        new_spi.item_print_desc = myData[4];
                        new_spi.item_value = myData[5];
                        new_spi.item_type = Integer.parseInt(myData[6]);
                        new_spi.item_show_value = item_show_value;
                        new_spi.item_position = Integer.parseInt(myData[8]);

                        if(myData.length==10)
                        new_spi.item_barcode = myData[9];

                        new_spi.item_value_int = Double.parseDouble(myData[5]);

                       // Timber.i("SPI :  " + String.valueOf( Integer.parseInt(myData[5])));

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(new_spi);
                        realm.commitTransaction();

                    }


                      //  System.out.println(s);

                   // }

               // }


            }

        } catch (Exception ex) {

            Timber.i("SPI Error: " + ex.getMessage());

        }





/**
 *
 * Service provider items
 *
 */
    /*
        service_provider_items = realm.where(service_provider_item.class).findAll();

        size = service_provider_items.size();

        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("enable", 1).findAll();
        realm.beginTransaction();
        for (int i = 0; i < service_provider_item_settings.size(); i++) {
            service_provider_item_settings.get(i).setEnable(0);
        }

        realm.commitTransaction();
        for (int k = 0;k<size;k++) {
            service_provider_item sp_item = service_provider_items.get(k);
            service_provider_item_setting = realm.where(service_provider_item_settings.class).equalTo("service_provider_item_id", sp_item.service_provider_item_id).findFirst();
            realm.beginTransaction();
            if(service_provider_item_setting==null) {
                //Toasty.info(mContext, "if loop ", 8000, true).show();
                service_provider_item_settings sp_settings=new service_provider_item_settings();
                sp_settings.setService_provider_id(sp_item.service_provider_id);
                sp_settings.setService_provider_item_id(sp_item.service_provider_item_id);
                sp_settings.setItem_desc(sp_item.item_desc);
                sp_settings.setItem_position(sp_item.item_position);
                sp_settings.setVisible_admin(true);
                sp_settings.setVisible_cashier(true);
                realm.insertOrUpdate(sp_settings);

            }else {
                service_provider_item_setting.setEnable(1);
                service_provider_item_setting.setItem_desc(sp_item.item_desc);
            }
            realm.commitTransaction();
        }

*/

//        RealmResults<service_provider_item> service_provider_items;
//        service_provider_items = realm.where(service_provider_item.class)
//                .equalTo("service_provider_id", 19)
//                .findAll();

        //Timber.i("TOTAL STUFF : " + String.valueOf(service_provider_items.size()));


    }




    public void onDestroy() {

        super.onDestroy();

    }


    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(600);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(600);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
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
