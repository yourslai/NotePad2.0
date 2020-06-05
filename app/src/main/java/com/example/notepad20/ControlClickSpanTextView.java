package com.example.notepad20;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;


public class ControlClickSpanTextView extends AppCompatTextView {

    private static final String TAG = "AutoLinkTextView";

    private long mTime;
    private boolean mLinkIsResponseLongClick = false;

    public ControlClickSpanTextView(Context context) {
        super(context);
    }

    public ControlClickSpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlClickSpanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isLinkIsResponseLongClick() {
        return mLinkIsResponseLongClick;
    }

    public void setLinkIsResponseLongClick(boolean linkIsResponseLongClick) {
        this.mLinkIsResponseLongClick = linkIsResponseLongClick;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CharSequence text = getText();

        if (text == null) {
            return super.onTouchEvent(event);
        }

        if (!mLinkIsResponseLongClick && text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) text;
            ClickableSpan[] clickableSpans = spannable.getSpans(0, end, ClickableSpan.class);

            if (clickableSpans == null || clickableSpans.length == 0) {
                return super.onTouchEvent(event);
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mTime = System.currentTimeMillis();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - mTime > 500) {
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
    }
}
