package za.co.topitupkeyboard.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import za.co.topitupkeyboard.R;
import za.co.topitupkeyboard.model.service_provider_item_settings;
import za.co.topitupkeyboard.model.service_provider_settings;

public class MultiSelectionSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener
{
    String[] _items = null;
    boolean[] mSelection = null;
int isadmin=0;
    int isproduct=0;
    ArrayAdapter<String> simple_adapter;
    Realm realm;
    RealmResults<service_provider_settings> service_provider_settings;
    RealmResults<service_provider_item_settings> service_provider_item_settings;
    public MultiSelectionSpinner(Context context)
    {
        super(context);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
        // Initialize Realm

    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }
    public void setisAdmin(int isAdmin) {
       isadmin=isAdmin;
    }
    public void setisProduct(int isProduct) {
        isproduct=isProduct;
    }
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            simple_adapter.clear();
          //  simple_adapter.add(buildSelectedItemString());  uncomment
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, this);

        Realm.init(getContext());
        realm = Realm.getDefaultInstance();

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                showCustomDialog();

                List<String>m=getSelectedStrings();
                //Toasty.info(getContext(), "ID " +  isproduct, 8000, true).show();

                if(isproduct==1){

                    handler.postDelayed(runnable, 2000);
                service_provider_settings = realm.where(service_provider_settings.class).findAll();

                int size = service_provider_settings.size();

                for (int j = 0;j<size;j++) {
                    service_provider_settings product = service_provider_settings.get(j);
                    realm.beginTransaction();
                    if(isadmin==1)
                    product.setVisible_admin(false);
                    else
                        product.setVisible_cashier(false);
                    realm.commitTransaction();
                }
                /**/



                for(int i=0;i<m.size();i++) {
        // Toasty.info(getContext(), "ID " +  m.get(i), 8000, true).show();
                    service_provider_settings = realm.where(service_provider_settings.class).equalTo("provider_desc", m.get(i)).findAll();


                    //int size = service_provider.size();
                    size = service_provider_settings.size();
                  //  Toasty.info(getContext(), "size" + size, 8000, true).show();
                 /**/    for (int k = 0;k<size;k++) {
                        service_provider_settings sp = service_provider_settings.get(k);
                        //   Toasty.info(getContext(), "isadmin" +  isadmin, 8000, true).show();
                        realm.beginTransaction();
                        if (isadmin == 1) {
                            sp.setVisible_admin(true);

                        } else {
                            sp.setVisible_cashier(true);

                        }

                        realm.commitTransaction();

                    }
                    }

                }else{

                    handler.postDelayed(runnable, 5000);
                    service_provider_item_settings = realm.where(service_provider_item_settings.class).findAll();

                    int size = service_provider_item_settings.size();

                    for (int j = 0;j<size;j++) {
                        service_provider_item_settings product = service_provider_item_settings.get(j);
                        realm.beginTransaction();
                        if(isadmin==1)
                            product.setVisible_admin(false);
                        else
                            product.setVisible_cashier(false);
                        realm.commitTransaction();
                    }

                    /**/



                    for(int i=0;i<m.size();i++) {
                        // Toasty.info(getContext(), "ID " +  m.get(i), 8000, true).show();
                        service_provider_item_settings = realm.where(service_provider_item_settings.class).equalTo("item_desc", m.get(i)).findAll();


                        //int size = service_provider.size();
                        size = service_provider_item_settings.size();
                        //  Toasty.info(getContext(), "size" + size, 8000, true).show();
                        /**/    for (int k = 0;k<size;k++) {
                            service_provider_item_settings sp = service_provider_item_settings.get(k);
                            //   Toasty.info(getContext(), "isadmin" +  isadmin, 8000, true).show();
                            realm.beginTransaction();
                            if (isadmin == 1) {
                                sp.setVisible_admin(true);

                            } else {
                                sp.setVisible_cashier(true);

                            }
                            realm.commitTransaction();


                        }
                    }









                }


            }

        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                       // dialog.dismiss();
                    }
                });

        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                }
            }
        }
    }


    public void clearSelection() {

            for (int j = 0; j < _items.length; ++j) {

                    mSelection[j] = false;

            }

    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();

        // return "Uncheck Product to Disable";
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }


    // Hide after some seconds
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dialog.dismiss();

        }
    };


    LinearLayout pageLoadingWrapper;
    TextView pop_title;
    TextView pop_content;
    AppCompatButton bt_close;
    Dialog dialog;

    private void showCustomDialog() {

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_dark);
        dialog.setCancelable(false);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity= Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //progressBar = ((ProgressBar) dialog.findViewById(R.id.progressBar));
        pageLoadingWrapper = ((LinearLayout) dialog.findViewById(R.id.pageLoadingWrapper));
        pop_title = ((TextView) dialog.findViewById(R.id.pop_title));
        pop_content = ((TextView) dialog.findViewById(R.id.pop_content));

        pop_title.setText("Updating");
        pop_content.setText("please wait...");

        bt_close = ((AppCompatButton) dialog.findViewById(R.id.bt_close));
        bt_close.setVisibility(View.GONE);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }



}

