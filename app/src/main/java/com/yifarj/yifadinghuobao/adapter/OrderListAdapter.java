package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.model.entity.SaleOrderListEntity;
import com.yifarj.yifadinghuobao.utils.DateUtil;
import com.yifarj.yifadinghuobao.utils.NumberUtil;

import java.util.List;


/**
 * OrderListAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/5/22 15:47
 */
public class OrderListAdapter extends AbsRecyclerViewAdapter {
    public List<SaleOrderListEntity.ValueEntity> data;

    public OrderListAdapter(RecyclerView recyclerView, List<SaleOrderListEntity.ValueEntity> data) {
        super(recyclerView);
        this.data = data;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_sale_order, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            SaleOrderListEntity.ValueEntity goodsBean = data.get(position);
            itemViewHolder.tvOrderCode.setText(goodsBean.Code);
            String time = "时间：" + DateUtil.getFormatDate(+goodsBean.BillDate * 1000);
            String amount = "金额：" + NumberUtil.formatDoubleToString(goodsBean.Amount);
            itemViewHolder.tvTime.setText(time);
            itemViewHolder.tvTotalAmount.setText(amount);
            itemViewHolder.tvOrderStatus.setText(goodsBean.AuditedStatus);
            if (goodsBean.AuditedStatus.equals("未审核")) {
                itemViewHolder.tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.text_desc));
            } else if (goodsBean.AuditedStatus.equals("已审核")) {
                itemViewHolder.tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.main_blue));
            } else if (goodsBean.AuditedStatus.equals("已记账")) {
                itemViewHolder.tvOrderStatus.setTextColor(getContext().getResources().getColor(R.color.main_red));
            }
        }
        super.onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ItemViewHolder extends ClickableViewHolder {

        TextView tvOrderCode;
        TextView tvTime;
        TextView tvTotalAmount;
        TextView tvOrderStatus;

        public ItemViewHolder(View itemView) {

            super(itemView);

            tvOrderCode = $(R.id.tvOrderCode);
            tvTime = $(R.id.tvTime);
            tvTotalAmount = $(R.id.tvTotalAmount);
            tvOrderStatus = $(R.id.tvOrderStatus);
        }
    }
}
