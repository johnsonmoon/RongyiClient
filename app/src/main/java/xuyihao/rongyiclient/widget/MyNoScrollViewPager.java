package xuyihao.rongyiclient.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by XuYihao on 2016/3/4.
 */
public class MyNoScrollViewPager extends ViewPager {
    private boolean noScroll = false;

    public MyNoScrollViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public MyNoScrollViewPager(Context context){
        super(context);
    }

    public void setNoScroll(boolean noScroll){
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(noScroll){
            return false;
        }
        else{
            return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(noScroll){
            return false;
        }
        else{
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }



}
