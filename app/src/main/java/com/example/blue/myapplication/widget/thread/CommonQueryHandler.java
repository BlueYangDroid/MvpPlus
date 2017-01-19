package com.example.blue.myapplication.widget.thread;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

/**
 * 异步的查询操作帮助类，可以处理增删改(ContentProvider提供的数据)
 *
 */
public class CommonQueryHandler extends AsyncQueryHandler {

    public CommonQueryHandler(ContentResolver cr) {
        super(cr);
    }

    /**
     * 当查询完成后，回调的方法
     */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

        // 判断传入的cookie是否是CursorAdapter
        /*if(cookie!=null && cookie instanceof CursorAdapter){
            CursorAdapter adapter = (CursorAdapter) cookie;
            //将查询返回的cursor设置给adapter
            adapter.changeCursor(cursor);
        }*/

        //触发监听事件
        if(cursorChangedListener!=null){
            cursorChangedListener.onCursorChanged(token, cookie, cursor);
        }
    }


    public OnCursorChangedListener getCursorChangedListener() {
        return cursorChangedListener;
    }

    public void setOnCursorChangedListener(OnCursorChangedListener cursorChangedListener) {
        this.cursorChangedListener = cursorChangedListener;
    }

    private OnCursorChangedListener cursorChangedListener;

    /**
     * 定义cursor改变时的事件监听
     * @author leo
     *
     */
    public interface OnCursorChangedListener{
        void onCursorChanged(int token, Object cookie, Cursor cursor);
    }
}