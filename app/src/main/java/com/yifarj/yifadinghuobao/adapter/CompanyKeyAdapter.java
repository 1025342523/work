package com.yifarj.yifadinghuobao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.model.ServerConfigInfoModel;

import java.util.List;

/**
 * CompanyKeyAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/9/30 9:32
 */
public class CompanyKeyAdapter extends BaseAdapter {

    private List<ServerConfigInfoModel> mData;
    private Context mContext;

    public CompanyKeyAdapter(Context context, List<ServerConfigInfoModel> data) {
        mData = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData == null ? null : mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        final ServerConfigInfoModel item = mData.get(i);
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_company_key_list, viewGroup, false);
            holder.company = view.findViewById(R.id.company);
            holder.companyKey = view.findViewById(R.id.companyKey);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.companyKey.setText(item.CompanyKey);
        holder.company.setText(item.Company);
        return view;
    }


    static class ViewHolder {
        TextView company;
        TextView companyKey;
    }
}
