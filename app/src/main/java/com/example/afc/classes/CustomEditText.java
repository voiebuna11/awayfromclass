package com.example.afc.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;

public class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            InputMethodManager mgr = (InputMethodManager)

                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            // TODO: Hide your view as you do it in your activity
        }
        return false;
    }
}