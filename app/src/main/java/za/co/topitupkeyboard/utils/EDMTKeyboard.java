package za.co.topitupkeyboard.utils;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;

import za.co.topitupkeyboard.R;

//import androidx.annotation.RequiresApi;

public class EDMTKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private  boolean isCaps = false;

    //Press Ctrl+O

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);
        keyboard = new Keyboard(this,R.xml.numpad);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // Set a reduced height for landscape mode (tablet mode)
//            ViewGroup.LayoutParams layoutParams = kv.getLayoutParams();
//            layoutParams.height = 1000;  // Smaller height in landscape
//            kv.setLayoutParams(layoutParams);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            // Set a larger height for portrait mode (phone mode)
//            ViewGroup.LayoutParams layoutParams = kv.getLayoutParams();
//            layoutParams.height = 500;  // Default height for portrait
//            kv.setLayoutParams(layoutParams);
//        }
    }

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public void onKey(int i, int[] ints) {
        InputConnection ic = getCurrentInputConnection();
        playClick(i);
        switch (i) {
            case Keyboard.KEYCODE_DELETE:
                getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
               // Toast.makeText(EDMTKeyboard.this, "close", Toast.LENGTH_SHORT).show();
                //ic.performEditorAction(EditorInfo.IME_ACTION_GO);
             // kv.closing();
                break;

            case Keyboard.KEYCODE_MODE_CHANGE:
                keyboard = new Keyboard(this, R.xml.qwerty);
                kv.setKeyboard(keyboard);
                kv.setOnKeyboardActionListener(this);
                break;
            case -11:
                keyboard = new Keyboard(this, R.xml.symbols);
                kv.setKeyboard(keyboard);
                kv.setOnKeyboardActionListener(this);
                break;
            case -10:
                keyboard = new Keyboard(this, R.xml.numpad);
                kv.setKeyboard(keyboard);
                kv.setOnKeyboardActionListener(this);
                break;
            default:
                char code = (char) i;
                if (code == 900) {
                    CharSequence text;
                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    if(settings.getString("setting_mpos_disclaimer", "0").equals("1")) {
                        text = settings.getString("setting_mpos_user_pwd", "Wappoint123*").trim();
                    }else{
                        text="";
                    }
              //      CharSequence text = getString(R.string.pospwd);
                    ic.commitText(text, 1);
                   // KeyEvent kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                  //  KeyEvent ku = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER);
                  //  ic.sendKeyEvent(kd);
                  //  ic.sendKeyEvent(ku);
                }
                else if(code == 800) {
                    CharSequence text = "00";
                    ic.commitText(text, 1);
            }
                else {
                    if (Character.isLetter(code) && isCaps)
                        code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code), 1);

                }
        }

    }

    private void playClick(int i) {

        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(i)
        {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }



}
