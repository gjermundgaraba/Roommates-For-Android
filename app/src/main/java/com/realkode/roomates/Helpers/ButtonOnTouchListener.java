package com.realkode.roomates.Helpers;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ButtonOnTouchListener implements View.OnTouchListener {
    private final TouchActionHandler touchActionHandler;

    public ButtonOnTouchListener(TouchActionHandler touchActionHandler) {
        this.touchActionHandler = touchActionHandler;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view instanceof Button) {
            Button button = (Button) view;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    button.setTextColor(Color.BLACK);
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    button.setTextColor(0xFFFFFFFF);
                    touchActionHandler.performAction();
                    return true;
                }
            }
        }

        return false;
    }

    public interface TouchActionHandler {
        public void performAction();
    }
}

