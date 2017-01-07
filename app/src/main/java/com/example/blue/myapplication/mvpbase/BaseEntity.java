package com.example.blue.myapplication.mvpbase;

/**
 * Created by Administrator on 2016/12/13.
 */

public class BaseEntity<T> {
    public BaseEntity() {
    }
    public int rCode;
    public String rMsg;
    public T rContent;
}
