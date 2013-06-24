package com.seeedstudio.beacon.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 圆角 ListView，通过 Shape 实现。
 * 
 * @author xiaobo
 * 
 */
public class CornerListView extends ListView {

    public CornerListView(Context context) {
        super(context);
    }

    public CornerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CornerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            int itemnum = pointToPosition(x, y);
            if (itemnum == AdapterView.INVALID_POSITION)
                break;
            else {
                // setSelector(R.drawable.listview_corner_round_center);
                // if (itemnum == 0) {
                // if (itemnum == (getAdapter().getCount() - 1)) {
                // // 只有一项
                // setSelector(R.drawable.listview_corner_round);
                // } else {
                // // 第一项
                // setSelector(R.drawable.listview_corner_round_top);
                // }
                // } else if (itemnum == (getAdapter().getCount() - 1))
                // // 最后一项
                // setSelector(R.drawable.listview_corner_round_bottom);
                // else {
                // // 中间项
                // setSelector(R.drawable.listview_corner_round_center);
                // }
            }
            break;
        case MotionEvent.ACTION_UP:
            Toast.makeText(getContext(), getAdapter().getCount(),
                    Toast.LENGTH_SHORT).show();
            break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
