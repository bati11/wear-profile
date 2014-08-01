package info.bati11.wearprofile.views;

import android.content.Context;
import android.support.wearable.view.GridViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CardGridViewPager extends GridViewPager {
    private boolean isSwipable = false;
    public CardGridViewPager(Context context) {
        super(context);
    }
    public CardGridViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CardGridViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSwipable(boolean swipable) {
        this.isSwipable = swipable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSwipable) return super.onTouchEvent(event);
        else            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)  {
        if (isSwipable) return super.onInterceptTouchEvent(event);
        else            return false;
    }
}
