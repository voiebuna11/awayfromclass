package com.example.afc.classes;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

public class PolymorphButton extends AppCompatImageButton {

    private Integer first_background;
    private Integer second_background;

    public PolymorphButton(Context context) {
        super(context);
    }
    public PolymorphButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PolymorphButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBackgrounds(Integer first_background, Integer second_background){
        this.first_background = first_background;
        this.second_background = second_background;
        super.setBackground(getResources().getDrawable(first_background));
    }

}
