package com.czechyuan.imageedit;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.czechyuan.imagepicker.ImagePicker;
import com.czechyuan.imagepicker.R;
import com.czechyuan.imagepicker.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class ImageSelectorAdapter extends BaseAdapter {

    public static final int KEY_IMAGE = -2016;
    public static final int KEY_SELECTED_VIEW = -20161;

    private ArrayList<String> mList;
    private LinkedHashSet<String> mSelectedSet; // 按添加顺序排列
    private ImagePicker imagePicker;
    private int mImageSize;               //每个条目的大小
    private Activity mActivity;

    public ImageSelectorAdapter(Activity activity, ArrayList<String> list) {
        mList = new ArrayList<String>(list);
        mActivity = activity;
        mSelectedSet = new LinkedHashSet<String>();
        imagePicker = ImagePicker.getInstance();
        mImageSize = Utils.getImageItemWidth(mActivity);
    }

    public void refreshPathList(ArrayList<String> list) {
        mList = new ArrayList<String>(list);
        notifyDataSetChanged();
    }


    public Set<String> getSelectedSet() {
        return mSelectedSet;
    }

    public void addSelected(String path) {
        mSelectedSet.add(path);
    }

    public void removeSelected(String path) {
        mSelectedSet.remove(path);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    int id = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.graffiti_imageselector_item, null);
            holder = new ViewHolder();
            holder.mImage = (ImageView) convertView.findViewById(R.id.image);
            holder.mImageSelected = (ImageView) convertView.findViewById(R.id.image_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(KEY_IMAGE, mList.get(position));
        convertView.setTag(KEY_SELECTED_VIEW, holder.mImageSelected);
        if (mSelectedSet.contains(mList.get(position))) {
            holder.mImageSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mImageSelected.setVisibility(View.GONE);
        }
        imagePicker.getImageLoader().displayImage(mActivity, mList.get(position), holder.mImage, mImageSize, mImageSize); //显示图片

        return convertView;
    }


    private class ViewHolder {
        ImageView mImage;
        ImageView mImageSelected;
    }
}
