package com.yifarj.yifadinghuobao.view;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2017-09-16.
 */

public class ViewHolder {
    //Map
    private SparseArray<View> views;
    private View convertView;

    private ViewHolder(View view){
        convertView = view;
        views = new SparseArray<>();
    }

    public static ViewHolder create(View view){
        return new ViewHolder(view);
    }

    public <T extends View> T getView(int viewId){
        View view = views.get(viewId);
        if(view == null){
            view = convertView.findViewById(viewId);
            views.put(viewId,view);
        }
        return (T)view;
    }

    public View getConvertView(){
        return convertView;
    }

    public void setText(int viewId,String text){
        TextView textView = getView(viewId);
        textView.setText(text);
    }

    public void setText(int viewId,int textId){
        TextView view = getView(viewId);
        view.setText(textId);
    }

    public void setTextColor(int viewId,int colorId){
        TextView view = getView(viewId);
        view.setTextColor(colorId);
    }

    public void setOnClickListener(int viewId,View.OnClickListener click){
        View view = getView(viewId);
        view.setOnClickListener(click);
    }

    public void setBacegroundResource(int viewId,int resId){
        View view = getView(viewId);
        view.setBackgroundResource(resId);
    }

    public void setBackgroundColor(int viewId,int colorId){
        View view = getView(viewId);
        view.setBackgroundColor(colorId);
    }

}
