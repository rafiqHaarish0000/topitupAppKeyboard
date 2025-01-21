package za.co.topitupkeyboard.utils;

import android.content.Context;
import android.os.Build;

import com.balysv.materialripple.BuildConfig;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import za.co.topitupkeyboard.model.MyApiEndpointInterface;


public class API {

    MyApiEndpointInterface apiService = MyApiEndpointInterface.retrofit.create(MyApiEndpointInterface.class);


    public void fetch_server_updates(final Context mContext, final Realm realm, Topitup app) {

        //showWaitDialog();

        //EventBus.getDefault().post(new eventSyncDetail("Refreshing Data", 0));


    }




    public void save_device_information(String technician_id) {

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String app_version = versionName + " (" + String.valueOf(versionCode) + ")";

        Call<String> call = apiService.device_information(
                Topitup.TIU_LICENSE,
                app_version,
                Build.VERSION.RELEASE,
                String.valueOf(android.os.Build.VERSION.SDK_INT),
                android.os.Build.MANUFACTURER,
                android.os.Build.DEVICE,
                android.os.Build.MODEL,
                android.os.Build.PRODUCT
                );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Timber.e("Upload error: " + t.getMessage());
            }
        });


    }





}
