package com.widiarifki.findtutor.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * Created by widiarifki on 04/07/2017.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)//For backward-compability
public class MyNumberPicker extends NumberPicker {

    public MyNumberPicker(Context context) {
        super(context);
    }

    public MyNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    public MyNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributeSet(attrs);
    }

    private void processAttributeSet(AttributeSet attrs) {
        //This method reads the parameters given in the xml file and sets the properties according to it
        this.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });

        boolean isMinute = attrs.getAttributeBooleanValue(null, "isMinute", false);
        this.setDisplayedValues(null);
        if(isMinute){
            String[] mins = { "00" };
            this.setMinValue(1);
            this.setMaxValue(mins.length);
            this.setDisplayedValues(mins);
        }else{
            this.setMinValue(attrs.getAttributeIntValue(null, "min", 0));
            this.setMaxValue(attrs.getAttributeIntValue(null, "max", 0));
        }
    }
}
