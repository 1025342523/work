package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.yifarj.yifadinghuobao.R;

/**
 * Created by Administrator on 2017-10-05.
 */

public class PlaceHolderHolder extends TreeNode.BaseNodeViewHolder<PlaceHolderHolder.PlaceItem> {

    private TextView mTvCode;
    private TextView mTvTotalPrice;
    private TextView mTvQuantity;
    private TextView mTvUnitName;
    private TextView mTvProductName;
    private TextView mTvSupplier;
    private ImageView mIvIcon;
    private FrameLayout mFlIcon;

    public PlaceHolderHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, PlaceItem value) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_order_summary, null, false);
        mTvCode = view.findViewById(R.id.tv_code);
        mTvTotalPrice = view.findViewById(R.id.tv_totalPrice);
        mTvQuantity = view.findViewById(R.id.tv_quantity);
        mTvUnitName = view.findViewById(R.id.tv_unitname);
        mTvProductName = view.findViewById(R.id.tv_productName);
        mTvSupplier = view.findViewById(R.id.tv_supplier);
        mIvIcon = view.findViewById(R.id.iv_icon);
        mFlIcon = view.findViewById(R.id.fl_icon);

        mTvCode.setText("编号：" + value.Code);
        mTvProductName.setText(value.ProductName);
        mTvQuantity.setText("数量：" + value.Quantity);
        mTvSupplier.setText(value.Name);
        mTvTotalPrice.setText("金额：" + value.TotalPrice);
        mTvTotalPrice.setTextColor(Color.RED);
        mTvUnitName.setText("单位：" + value.UnitName);

        return view;
    }

    public static class PlaceItem{
        public String Code;
        public double TotalPrice;
        public int Quantity;
        public String UnitName;
        public String Name;
        public String ProductName;
        public String Url;
        public int FlIcon;

        public PlaceItem(String code, double totalPrice, int quantity, String unitName, String name, String productName, String url, int flIcon) {
            Code = code;
            TotalPrice = totalPrice;
            Quantity = quantity;
            UnitName = unitName;
            Name = name;
            ProductName = productName;
            Url = url;
            FlIcon = flIcon;
        }
    }
}
