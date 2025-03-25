package za.co.topitupkeyboard.utils;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import wangpos.sdk4.libbasebinder.BankCard;
import wangpos.sdk4.libbasebinder.Printer;
import za.co.topitupkeyboard.LogoutAdminListener;
import za.co.topitupkeyboard.LogoutListener;
//import com.iposprinter.iposprinterservice.IPosPrinterCallback;
//import com.iposprinter.iposprinterservice.IPosPrinterService;
//import com.smartdevice.aidl.IZKCService;

//import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDex;
//import com.imagpay.Settings;
//import com.imagpay.SwipeEvent;
//import com.imagpay.SwipeListener;
//import com.imagpay.enums.CardDetected;
//import com.imagpay.enums.EmvStatus;
//import com.imagpay.enums.PrintStatus;
//import com.imagpay.mpos.MposHandler;
//z91 printer
/*import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.imagpay.mpos.MposHandler;
*/

public class Topitup extends Application implements LifecycleObserver {  // implements SwipeListener

    //public static final String SDCARD_IMAGES_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "topitup";

    public static String TIU_LICENSE = "";
    public static String TIU_SERVER = "";
    public static String POSUSER_ID = "0";
    public static String IS_ADMIN = "0";
    public static String CUSTOMER_ID = "0";
    public static String POSUSER_NAME = "";
    public static String RICA_REG = "";
    //public static Account myAccount;

    public static final String AUTHORITY = "za.co.topitup.app";
    public static final String ACCOUNT_TYPE = "za.co.topitup";

    public static String BASE_URL = "";
    public static String BASE_URL1 = "";
    public static String BASE_URL_SYNC = "";
    public static String BASE_URL_UPDATE = "";

    public static String DEVICE_TYPE = "";
    public static int DEVICE_MODEL = 0;

    public static  boolean DEBUG ;       // true to use demo server

    private static Topitup mInstance;

    private static Context context;

    public static OkHttpClient client;

    public static double min_swipe=6;
    public static double max_swipe=10000;
    public static double warning_swipe=5000;

    public static String DEVICE_SLNO="0";
    public static String MERCHANT_ID="0";

//   public static Settings setting;
//   public static MposHandler handler;

    private int checking_paper_status = -1;

    public static String PRINT_BARCODE = "0";

    public static String APP_VERSION = "";


    private LogoutListener listener;
    private LogoutAdminListener listenerAdmin;

    private static Timer timer;
    private static Timer timerAdmin;
    private final long screensavertime=24*60000;//6 min



    final Handler mHandler = new Handler();
    private Thread mUiThread;
    //for WPOS
    private BankCard mCore;
    private Printer mPrinter;

    //for Z91
  //  public static Settings setting;
  //  public static MposHandler handler;
    public static boolean wasInBackground;

    //wpos3
    public static String orderno,card_no,voucher_no,batch_no,refer_no,trans_time,amount,response;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;




        Topitup.context = getApplicationContext();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        SharedPreferences prefs = getSharedPreferences("TIUPREF", MODE_PRIVATE);
        TIU_SERVER = prefs.getString("TIU_SERVER", "DEMO");
     //  Toast.makeText(this, "Selected Server="+TIU_SERVER,Toast.LENGTH_LONG).show();
       // Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
      //  assertEquals("com.tiu.keyboard", appContext.getPackageName());
        if(TIU_SERVER.equals("LIVE"))
             DEBUG=false;
        else
            DEBUG=true;



        if (DEBUG) {
            BASE_URL = "http://demo.topitup.co.za:25812/";
            BASE_URL_SYNC = "http://demo.topitup.co.za:25815/";
            BASE_URL_UPDATE = "http://demo.topitup.co.za/";
        } else {
            BASE_URL = "http://tx.topitup.co.za:25812/";
           // BASE_URL1 = "http://41.203.10.186:25812/";
            BASE_URL_SYNC = "http://sync.topitup.co.za:25815/";
            BASE_URL_UPDATE = "http://topitup.co.za/";
        }

        //File file = new File("/storage/emulated/legacy/DCIM/Camera/");

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                //.directory(file )
                //.name("default.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

//        Timber.plant(new Timber.DebugTree());

        //if (DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
        //}


        try {

            PackageInfo pInfo = mInstance.getPackageManager().getPackageInfo(getPackageName(), 0);
            //String version = pInfo.versionName;
           // APP_VERSION = String.valueOf(pInfo.versionCode);
           // APP_VERSION = String.valueOf("Ver"+pInfo.versionName);
            APP_VERSION = String.valueOf(pInfo.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }



        //Timber.e( Environment.getExternalStorageDirectory().toString());



        //String restoredText = prefs.getString("text", null);
        //if (restoredText != null) {
        //TIU_LICENSE  = prefs.getString("TIU_LICENSE", "DEMO762c-a473-11e3-a836-001e679706de");   // vstream
        //TIU_LICENSE = prefs.getString("TIU_LICENSE", "DEMO5f89-1628-11e8-a0d1-001e6779cd30");
       // TIU_LICENSE = prefs.getString("TIU_LICENSE", "fce666f6-1896-11e9-9181-0cc47ac0400e");
        TIU_LICENSE = prefs.getString("TIU_LICENSE", "");
        //}

        PRINT_BARCODE = prefs.getString("setting_print_barcode","0");
        DEVICE_SLNO=getSerialNumber();

        //String manufacturer = android.os.Build.MANUFACTURER;
        //Timber.i("manufacturer:" + manufacturer);

//        Timber.i("android.os.Build.BRAND :" + android.os.Build.BRAND);
//        Timber.i("android.os.Build.MODEL :" + android.os.Build.MODEL);
//        Timber.i("android.os.Build.ID :" + android.os.Build.ID);
        Timber.i("android.os.Build.BRAND :" + android.os.Build.MANUFACTURER);
        if  (android.os.Build.MODEL.equals("SHOP1") && android.os.Build.ID.equals("N2G47H-76")) {

            DEVICE_TYPE = "QCOM SHOP1";
        }
       else if  (android.os.Build.MODEL.equals("WPOS-3") || android.os.Build.MANUFACTURER.equals("Wiseasy") || android.os.Build.MANUFACTURER.equals("wiseasy")) {

            DEVICE_TYPE = "WPOS";
            new Thread() {
                @Override
                public void run() {
                    mCore = new BankCard(getApplicationContext());
                    mPrinter = new Printer(getApplicationContext());
                    try {
                        mPrinter.setPrintFontType(getAppContext(), "");//fonnts/PraduhhTheGreat.ttf
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }
        else if  (android.os.Build.MANUFACTURER.equals("CHUWI") || android.os.Build.MANUFACTURER.equals("Mobicel") ||  android.os.Build.MANUFACTURER.equals("samsung")  ||  android.os.Build.MANUFACTURER.equals("wiseasy")   ||  android.os.Build.MANUFACTURER.equals("rockchip")) {

            DEVICE_TYPE = "MOBILE";

        }

        else if  (android.os.Build.ID.equals("MRA58K")) {

            DEVICE_TYPE = "Q1";

            //Timber.i("DEVICE: Q1");


            //绑定服务
            Intent intent = new Intent();
            intent.setPackage("com.iposprinter.iposprinterservice");
            intent.setAction("com.iposprinter.iposprinterservice.IPosPrintService");
            //startService(intent);
            bindService(intent, connectService, Context.BIND_AUTO_CREATE);
            //注册打印机状态接收器
            IntentFilter printerStatusFilter = new IntentFilter();
            printerStatusFilter.addAction(PRINTER_NORMAL_ACTION);
            printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION);
            printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION);
            printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION);
            printerStatusFilter.addAction(PRINTER_BUSY_ACTION);
            printerStatusFilter.addAction(GET_CUST_PRINTAPP_PACKAGENAME_ACTION);

//            registerReceiver(IPosPrinterStatusListener, printerStatusFilter);

            mInstance.checking_paper_status = 1;

            q1handler = new HandlerUtils.MyHandler(iHandlerIntent);

//            callback = new IPosPrinterCallback.Stub() {
//
//                @Override
//                public void onRunResult(final boolean isSuccess) throws RemoteException {
//                    Timber.i("result:" + isSuccess + "\n");
//                }
//
//                @Override
//                public void onReturnString(final String value) throws RemoteException {
//                    Timber.i("result:" + value + "\n");
//                }
//            };



//            ThreadPoolManager.getInstance().executeTask(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        mIPosPrinterService.printerInit(callback);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

//            callback = new IPosPrinterCallback.Stub() {
//
//                @Override
//                public void onRunResult(final boolean isSuccess) throws RemoteException {
//                    Log.i(TAG, "result:" + isSuccess + "\n");
//                }
//
//                @Override
//                public void onReturnString(final String value) throws RemoteException {
//                    Log.i(TAG, "result:" + value + "\n");
//                }
//            };


        } else if (android.os.Build.ID.equals("NRD90M")) {

            //bindService();

        } else {
            DEVICE_TYPE = "Z91";

            initSDK();

//                DEVICE_TYPE = "Z91";
//
//                initSDK();
//
//                MposHandler.getInstance(this).addSwipeListener(this);
//
//                Topitup.handler.addSwipeListener(new SwipeListener() {
//
//                    @Override
//                    public void onParseData(SwipeEvent event) {
//
//                        //Timber.i("PRINT: zzzz " + event.getValue() );
//                        // sendMessage("onParseData:" + event.getValue());
//                    }
//
//                    @Override
//                    public void onDisconnected(SwipeEvent event) {
//                    }
//
//                    @Override
//                    public void onConnected(SwipeEvent event) {
//                    }
//
//                    @Override
//                    public void onCardDetect(CardDetected type) {
//                    }
//
//                    @Override
//                    public void onPrintStatus(PrintStatus status) {
//
//                        //Timber.i("PRINT: onPrintStatus yy:" + status.toString() );
//
//                        if (status == PrintStatus.IMAGES) {
//
//                        } else if (status == PrintStatus.EXIT) {
//
//                            mInstance.checking_paper_status = 1;
//
//                            // setting.mPosExitPrint();
//                            // new Thread(new Runnable() {
//                            // @Override
//                            // public void run() {
//                            // // TODO Auto-generated method stub
//                            // setting.prnStatus();
//                            // }
//                            // }).start();
//                        } else if (status == PrintStatus.NO_PAPER) {
//
//                            mInstance.checking_paper_status = 0;
//
//                        } else if (status == PrintStatus.LACK_PAPER) {
//
//                            mInstance.checking_paper_status = 0;
//
//                        } else {
//
//                            mInstance.checking_paper_status = 1;
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onEmvStatus(EmvStatus arg0) {
//                    }
//
//                });


        }







    }




    public static Context getAppContext() {
        return Topitup.context;
    }




    public void bindService() {
        //com.zkc.aidl.all为远程服务的名称，不可更改
        //com.smartdevice.aidl为远程服务声明所在的包名，不可更改，
        // 对应的项目所导入的AIDL文件也应该在该包名下
        Intent intent = new Intent("com.zkc.aidl.all");
        intent.setPackage("com.smartdevice.aidl");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }


//    public static IZKCService mIzkcService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.e("client", "onServiceDisconnected");
//            mIzkcService = null;
            //Toast.makeText(BaseActivity.this, getString(R.string.service_bind_fail), Toast.LENGTH_SHORT).show();
            //发送消息绑定失败 send message to notify bind fail
            //sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_FAIL);

            //Toasty.error(mContext, "SEVICE_BIND_FAIL", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.e("client", "onServiceConnected");
//            mIzkcService = IZKCService.Stub.asInterface(service);
//            if(mIzkcService!=null){
//                try {
//                    //Toast.makeText(BaseActivity.this, getString(R.string.service_bind_success), Toast.LENGTH_SHORT).show();
//                    //获取产品型号 get product model
//                    DEVICE_MODEL = mIzkcService.getDeviceModel();
//                    //设置当前模块 set current function module
//                    mIzkcService.setModuleFlag(8);
//
//                    mIzkcService.sendRAWData("printer", new byte[]{0x1b, 0x4e, 0x04, 0x01});
//
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//                //发送消息绑定成功 send message to notify bind success
//                //sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_SUCCESS);
//
//                //Toasty.success(mContext, "SEVICE_BIND_SUCCESS", Toast.LENGTH_LONG).show();
//
//            }
        }
    };





    /**** SDK ***/
    private boolean initSDK() {

        try {

            // Init SDK,call singleton function,so that you can keeping on the
            // connect in the whole life cycle
            //this.handler = MposHandler.getInstance(this);
            //this.setting = Settings.getInstance(handler);
            // power on the device when you need to read card or print
            //this.setting.mPosPowerOn();

            // for 90,delay 1S and then connect
            // Thread.sleep(1000);
            // connect device via serial port
//            if (!handler.isConnected()) {
//
//                if (handler.connect()) {
//                    Timber.i("PRINT: POS Handler Connected");
//                } else {
//                    Timber.i("PRINT: POS Handler NOT Connected");
//                }
//
//            } else {
//
//                handler.close();
//
//                if (handler.connect()) {
//                    Timber.i("PRINT: POS Handler Connected");
//                } else {
//                    Timber.i("PRINT: POS Handler NOT Connected");
//                }
//
//                //txt_printer_status.setText(String.valueOf(handler.connect()));
//                //Toasty.normal(mContext, "Printer ReConnect:" + handler.connect(), 500).show();
//            }

            return true;

        } catch (Exception e) {

            //Toasty.error(mContext, e.getMessage(), 3000, true).show();
            return false;

        }

        //handler.setShowLog(true);

    }


    public static void resetPaperStatus() {

        if (DEVICE_TYPE.equals("Z91")) {
            mInstance.checking_paper_status = -1;
        }

        if (DEVICE_TYPE.equals("ZKC")) {
            //
        }

//        if (DEVICE_TYPE.equals("Q1")) {
//            mInstance.checking_paper_status = -1;
//        }


    }

    public static int getPaperStatus() {

        if (DEVICE_TYPE.equals("Z91")) {
            return mInstance.checking_paper_status;
        }

        if (DEVICE_TYPE.equals("ZKC")) {

            String status = "";

            try {
                //mIzkcService.getPrinterStatus();
//                status = mIzkcService.getPrinterStatus();
            } catch (Exception e) {
                //
            }

            if (status.equals("no paper")) {
                return 0;
            } else {
                return 1;
            }
        }

        if (DEVICE_TYPE.equals("Q1")) {

            int status = 1;

            try {
                //mIzkcService.getPrinterStatus();
//                status = mInstance.mIPosPrinterService.getPrinterStatus();
            } catch (Exception e) {
                //
            }

            if (status == 1) {
                return 0;
            } else {
                return 1;
            }
            //Timber.i("getPrinterStatus: wtf");
            //return mInstance.checking_paper_status;

        }

        return 1;

    }


    public static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }


        public  boolean isConnected(Context context) {
            Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        }




    public static int getQ1PrinterSts(){
       int status=1;
        if (DEVICE_TYPE.equals("Q1")) {
            try {
                //mIzkcService.getPrinterStatus();
//                status = mInstance.mIPosPrinterService.getPrinterStatus();
            } catch (Exception e) {
                //
            }
//|| status == 4 commented
            if (status == 2 || status == 3  || status == 5) {
                return 0;
            } else {
                return 1;
            }

        }

        return 1;
    }












    public static void checkOutOfPaper() {

//        if (DEVICE_TYPE.equals("Z91")) {
//            mInstance.setting.mPosEnterPrint();
//        }


     //   if (DEVICE_TYPE.equals("Q1")) {


//            try {
//                if(mInstance.mIPosPrinterService.getPrinterStatus() == PRINTER_PAPERLESS) {
//                    mInstance.checking_paper_status = 0;
//                } else {
//                    mInstance.checking_paper_status = 1;
//                }
//                Timber.i("getPrinterStatus: " + mInstance.checking_paper_status);
//            } catch (Exception ex){
//                Timber.i("getPrinterStatus: " + ex.getMessage() );
//                mInstance.checking_paper_status = 1;
//            }

//            try {
//                //mInstance.mIPosPrinterService.printerInit(mInstance.callback);
//                Integer ret =  mInstance.mIPosPrinterService.getPrinterStatus();
//
//
//                Timber.i("getPrinterStatus: " + ret.toString());
//
//            } catch (Exception ex) {
//                //
//            }
    //    }

        //if (Topitup.setting.mPosEnterPrint()) {
        //    Timber.i("PRINT: checkOutOfPaper TRUE ");
        //} else {
        //    Timber.i("PRINT: checkOutOfPaper FALSE ");
        //}
//
//          new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                if (Topitup.setting.mPosEnterPrint()) {
//
//                    Timber.i("PRINT: checkOutOfPaper TRUE ");
//
//
//                } else {
//                    Timber.i("PRINT: checkOutOfPaper FALSE ");
//                }
//            }
//        }).start();

     //   boolean ret = this.handler.mPosEnterPrint();



    }



//    @Override
//    public void onCardDetect(CardDetected arg0) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onConnected(SwipeEvent arg0) {
//        //
//    }
//
//    @Override
//    public void onDisconnected(SwipeEvent arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onEmvStatus(EmvStatus arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onParseData(SwipeEvent arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//
//    @Override
//    public void onPrintStatus(PrintStatus arg0) {
//
//        //Timber.i("PRINT: WTF " + arg0.toString());
//        //
//
//    }
//
//        Timber.i("PRINT: topitup " + arg0.toString());
//
//        Toasty.info(mInstance.getAppContext(),"status : " + arg0.toString(), 2000, true).show();
//
//        mInstance.checking_paper_status = 0;
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
//            //
//        } else if (arg0 == PrintStatus.LACK_PAPER) {
//            //
//        }
//
//    }






// old not in new sdk
//    private void initPrinter()
//    {
//
////        new Thread(new Runnable() {
////
////            @Override
////            public void run() {
////                if (Topitup.setting.mPosEnterPrint()) {
////                    Topitup.setting.mPosPrintLn();
////                    Topitup.setting.mPosExitPrint();
////                }
////            }
////        }).start();
//
//    }

//    @SuppressLint("HandlerLeak")
//    public Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 101:
//                    Toasty.info(mContext,"Printing...", Toast.LENGTH_LONG).show();
//                    break;
//                case 500:
//                    Toasty.error(mContext,"Skipped print!", Toast.LENGTH_LONG).show();
//                    break;
//                default:
//                    break;
//            }
//        };
//    };



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    public static synchronized Topitup getInstance() {
        return mInstance;
    }









/**-------------------------

 /**  Q1

 /**------------------------- */



    private HandlerUtils.MyHandler q1handler;




    private HandlerUtils.IHandlerIntent iHandlerIntent = new HandlerUtils.IHandlerIntent() {
        @Override
        public void handlerIntent(Message msg) {

            //Timber.i("getPrinterStatus : " + msg.what);

            mInstance.checking_paper_status = 1;

            switch (msg.what) {
                case MSG_TEST:
                    break;
                case MSG_IS_NORMAL:

                   // Timber.i("getPrinterStatus : NORMAL");

                    if (getPrinterStatus() == PRINTER_NORMAL) {
                       // mInstance.checking_paper_status = 1;
                    }
                    break;
                case MSG_IS_BUSY:
                   // Toast.makeText(mContext, "printer_is_working", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:

                    loopPrintFlag = DEFAULT_LOOP_PRINT;

                    mInstance.checking_paper_status = 0;
//                    Timber.i("PAPER: OUT OF PAPER");


                   // Toast.makeText(mContext,"out_of_paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:

                    //Timber.i("PAPER: GOT PAPER");
                   // mInstance.checking_paper_status = 1;

                    //Toast.makeText(mContext, "exists_paper", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                  //  Toast.makeText(mContext, "printer_high_temp_alarm", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_MOTOR_HIGH_TEMP:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                   // Toast.makeText(mContext, "motor_high_temp_alarm", Toast.LENGTH_SHORT).show();
                   // handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP_INIT_PRINTER, 180000);  //马达高温报警，等待3分钟后复位打印机
                    break;
                case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                    printerInit();
                    break;
                case MSG_CURRENT_TASK_PRINT_COMPLETE:
                    //Toast.makeText(mContext, "printer_current_task_print_complete", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };




    private BroadcastReceiver IPosPrinterStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Log.d(TAG, "IPosPrinterStatusListener onReceive action = null");
                return;
            }
            Log.d(TAG, "IPosPrinterStatusListener action = " + action);
            if (action.equals(PRINTER_NORMAL_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_IS_NORMAL, 0);
            } else if (action.equals(PRINTER_PAPERLESS_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_PAPER_LESS, 0);
            } else if (action.equals(PRINTER_BUSY_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_IS_BUSY, 0);
            } else if (action.equals(PRINTER_PAPEREXISTS_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_PAPER_EXISTS, 0);
            } else if (action.equals(PRINTER_THP_HIGHTEMP_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_THP_NORMALTEMP_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL, 0);
            } else if (action.equals(PRINTER_MOTOR_HIGHTEMP_ACTION))  //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
                q1handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP, 0);
            } else if (action.equals(PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION)) {
                q1handler.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE, 0);
            } else if (action.equals(GET_CUST_PRINTAPP_PACKAGENAME_ACTION)) {
                String mPackageName = intent.getPackage();
                Log.d(TAG, "*******GET_CUST_PRINTAPP_PACKAGENAME_ACTION：" + action + "*****mPackageName:" + mPackageName);

            } else {
                q1handler.sendEmptyMessageDelayed(MSG_TEST, 0);
            }
        }
    };






    /*定义打印机状态*/
    private final int PRINTER_NORMAL = 0;
    static final int PRINTER_PAPERLESS = 1;
    private final int PRINTER_THP_HIGH_TEMPERATURE = 2;
    private final int PRINTER_MOTOR_HIGH_TEMPERATURE = 3;
    private final int PRINTER_IS_BUSY = 4;
    private final int PRINTER_ERROR_UNKNOWN = 5;
    /*打印机当前状态*/
    private int printerStatus = 0;

    /*定义状态广播*/
    private final String PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservic e.NORMAL_ACTION";
    private final String PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION";
    private final String PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION";
    private final String PRINTER_THP_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION";
    private final String PRINTER_THP_NORMALTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION";
    private final String PRINTER_MOTOR_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION";
    private final String PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION";
    private final String PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION = "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION";
    private final String GET_CUST_PRINTAPP_PACKAGENAME_ACTION = "android.print.action.CUST_PRINTAPP_PACKAGENAME";

    /*定义消息*/
    private final int MSG_TEST = 1;
    private final int MSG_IS_NORMAL = 2;
    private final int MSG_IS_BUSY = 3;
    private final int MSG_PAPER_LESS = 4;
    private final int MSG_PAPER_EXISTS = 5;
    private final int MSG_THP_HIGH_TEMP = 6;
    private final int MSG_THP_TEMP_NORMAL = 7;
    private final int MSG_MOTOR_HIGH_TEMP = 8;
    private final int MSG_MOTOR_HIGH_TEMP_INIT_PRINTER = 9;
    private final int MSG_CURRENT_TASK_PRINT_COMPLETE = 10;

    /*循环打印类型*/
    private final int MULTI_THREAD_LOOP_PRINT = 1;
    private final int INPUT_CONTENT_LOOP_PRINT = 2;
    private final int DEMO_LOOP_PRINT = 3;
    private final int PRINT_DRIVER_ERROR_TEST = 4;
    private final int DEFAULT_LOOP_PRINT = 0;

    //循环打印标志位
    private int loopPrintFlag = DEFAULT_LOOP_PRINT;
    private byte loopContent = 0x00;
    private int printDriverTestCount = 0;


    private static final String TAG = "IPosPrinterTestDemo";

//    public static IPosPrinterService mIPosPrinterService;
//    public static IPosPrinterCallback callback = null;

    public int getPrinterStatus() {

        Log.i(TAG, "***** printerStatus" + printerStatus);
//        try {
////            printerStatus = mIPosPrinterService.getPrinterStatus();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        Log.i(TAG, "#### printerStatus" + printerStatus);
        return printerStatus;
    }

    private ServiceConnection connectService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            mIPosPrinterService = IPosPrinterService.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            mIPosPrinterService = null;
        }
    };


    public void printerInit() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
//                try {
////                    mIPosPrinterService.printerInit(callback);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }



    public void startUserSession(){

        SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        Long TIMEOUT_IN_MILLI=settings.getLong("TIMEOUT_IN_MILLI",86400000);
 //Toast.makeText(Topitup.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();
cancelTimer();

  timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {

                      listener.onSessionLogout();

                        //                    //
                }
                });
            }
        }, TIMEOUT_IN_MILLI);

    }

    private void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();

        }
    }


    public void registerSessionListener(LogoutListener listener){
        // listener.onSessionLogout();

        this.listener=listener;

    }

    private void cancelTimer(){

        //Toast.makeText(Topitup.this, "cancel called"+timer,Toast.LENGTH_SHORT).show();
        if(timer!=null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

public void onUserInteracted(){
    //Toast.makeText(Topitup.this, "user interacted",Toast.LENGTH_SHORT).show();
startUserSession();
}



//Admin

    public void startUserSessionAdmin(){

    /*    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
        Long TIMEOUT_IN_MILLI=settings.getLong("TIMEOUT_IN_MILLI",86400000);
       Toast.makeText(Topitup.this, "in="+TIMEOUT_IN_MILLI,Toast.LENGTH_LONG).show();*/

       long TIMEOUT_IN_MILLI_ADMIN=120000;
        cancelTimerAdmin();
//Toast.makeText(Topitup.this, "in="+TIMEOUT_IN_MILLI_ADMIN, Toast.LENGTH_LONG).show();
        timerAdmin = new Timer();
        timerAdmin.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {

                        listenerAdmin.onSessionAdminLogout();

                        //                    //
                    }
                });
            }
        }, TIMEOUT_IN_MILLI_ADMIN);

    }

    public void registerSessionListenerAdmin(LogoutAdminListener listenerAdmin){
        // listener.onSessionLogout();

        this.listenerAdmin=listenerAdmin;

    }
    private void cancelTimerAdmin(){

        //Toast.makeText(Topitup.this, "cancel called"+timer,Toast.LENGTH_SHORT).show();
        if(timerAdmin!=null) {
            timerAdmin.cancel();
            timerAdmin.purge();
            timerAdmin = null;
        }
    }

    public void onUserInteractedAdmin(){
        //Toast.makeText(Topitup.this, "user interacted",Toast.LENGTH_SHORT).show();
        startUserSessionAdmin();
    }

    public long getScreensavertime() {
        return screensavertime;
    }



    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void created() {
        Log.d(getClass().getSimpleName(), "ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void started() {
        Log.d(getClass().getSimpleName(), "ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumed() {
        Log.d(getClass().getSimpleName(), "ON_RESUME");
        wasInBackground=false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void paused() {
        Log.d(getClass().getSimpleName(), "ON_PAUSE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopped() {
        Log.d(getClass().getSimpleName(), "ON_STOP");

        wasInBackground=true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyed() {
        Log.d(getClass().getSimpleName(), "ON_DESTROY");
    }





    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            // (?) Lenovo Tab (https://stackoverflow.com/a/34819027/1276306)
            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))
                // Samsung Galaxy S5 (SM-G900F) : 6.0.1
                // Samsung Galaxy S6 (SM-G920F) : 7.0
                // Samsung Galaxy Tab 4 (SM-T530) : 5.0.2
                // (?) Samsung Galaxy Tab 2 (https://gist.github.com/jgold6/f46b1c049a1ee94fdb52)
                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Google Nexus 5 : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 5C (NEM-L51) : 7.0
                // Honor 5X (KIW-L21) : 6.0.1
                // Huawei M2 (M2-801w) : 5.1.1
                // (?) HTC Nexus One : 2.3.4 (https://gist.github.com/tetsu-koba/992373)
                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                // (?) Samsung Galaxy Tab 3 (https://stackoverflow.com/a/27274950/1276306)
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 9 Lite (LLD-L31) : 8.0
                // Xiaomi Mi 8 (M1803E1A) : 8.1.0
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }


}