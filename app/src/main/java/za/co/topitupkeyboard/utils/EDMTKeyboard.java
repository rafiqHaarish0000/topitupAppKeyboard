package za.co.topitupkeyboard.utils;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import za.co.topitupkeyboard.R;

public class EDMTKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private boolean isCaps = false;

    // Sound variables
    private SoundPool soundPool;
    private int clickSoundId;
    private boolean soundLoaded = false;
    private int currentStreamId = 0; // Track currently playing sound

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        // Set listener to know when sound is loaded
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    soundLoaded = true;
                }
            }
        });

        // Load the click sound (key_clic.mp3 from raw folder)
        clickSoundId = soundPool.load(this, R.raw.key_click, 1);
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);

        // Get the screen height based on API level
        int screenHeight = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getSystemService(WindowManager.class).getCurrentWindowMetrics();
            screenHeight = windowMetrics.getBounds().height();
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (windowManager != null) {
                windowManager.getDefaultDisplay().getMetrics(metrics);
                screenHeight = metrics.heightPixels;
            }
        }

        int keyboardHeight = screenHeight / 4;
        kv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight));

        keyboard = new Keyboard(this, R.xml.numpad);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);

        return kv;
    }

    @Override
    public void onStartInputView(EditorInfo editorInfo, boolean restarting) {
        super.onStartInputView(editorInfo, restarting);
        int inputType = editorInfo.inputType;

        if ((inputType & InputType.TYPE_CLASS_TEXT) == InputType.TYPE_CLASS_TEXT) {
            switchToAlphabetKeyboard();
        }
        if ((inputType & InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER) {
            switchToNumberKeyboard();
        }
    }

    private void switchToNumberKeyboard() {
        keyboard = new Keyboard(this, R.xml.numpad);
        kv.setKeyboard(keyboard);
    }

    private void switchToAlphabetKeyboard() {
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

        // Play click sound
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

            case -100: // Custom KeyCode for "Clear All"
                CharSequence currentText = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
                if (currentText != null) {
                    ic.deleteSurroundingText(currentText.length(), 0);
                }
                break;

            default:
                char code = (char) i;
                if (code == 900) {
                    CharSequence text;
                    SharedPreferences settings = getSharedPreferences("TIUPREF", 0);
                    if(settings.getString("setting_mpos_disclaimer", "0").equals("1")) {
                        text = settings.getString("setting_mpos_user_pwd", "Wappoint123*").trim();
                    } else {
                        text = "";
                    }
                    ic.commitText(text, 1);
                } else if(code == 800) {
                    CharSequence text = "00";
                    ic.commitText(text, 1);
                } else {
                    if (Character.isLetter(code) && isCaps)
                        code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code), 1);
                }
        }
    }

    private void playClick(int i) {
        // Stop any currently playing sound first
        if (soundPool != null && currentStreamId != 0) {
            soundPool.stop(currentStreamId);
        }

        // Play custom sound if loaded
        if (soundPool != null && soundLoaded) {
            currentStreamId = soundPool.play(clickSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Release SoundPool resources
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}