package com.yifarj.yifadinghuobao.ui.activity.me;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.model.entity.OrderSummaryEntity;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ScreenUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.LoadingDialog;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ZhangZeZhi on 2017-09-21.
 */

public class OrderSummaryActivity extends BaseActivity {

    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.order_listView)
    ExpandableListView orderListView;

    @BindView(R.id.no_order_listView)
    ExpandableListView noOrderListView;

    @BindView(R.id.tv_no_order)
    TextView tvNoOrder;

    @BindView(R.id.tv_order)
    TextView tvOrder;

    @BindView(R.id.view_line1)
    View viewLine;

    @BindView(R.id.view_line_3)
    View viewLine3;

    private List<OrderSummaryEntity.ValueEntity.Product> mOrderlist = new ArrayList<>();
    private List<OrderSummaryEntity.ValueEntity.Product> mProductList;
    private List<String> titleList = new ArrayList<>();
    private List<String> noOrdertitleList = new ArrayList<>();
    private Map<String,List<OrderSummaryEntity.ValueEntity.Product>> noOrderMap = new HashMap<>();
    private Map<String,List<OrderSummaryEntity.ValueEntity.Product>> orderMap = new HashMap<>();
    private boolean isOrderOpen = false;
    private boolean isNoOrderOpen = false;
    private LoadingDialog mDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_summary;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        ScreenUtil.init(this);
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mDialog = new LoadingDialog(this, "加载数据中...");
        loadHeadData();
        initView();
    }

    private void initView() {
        tvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(titleList.size() > 0){
                    if (!isOrderOpen) {//打开
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_bottom_triangle,null);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        tvOrder.setCompoundDrawables(drawable, null, null, null);
                        isOrderOpen = true;
                        orderListView.setVisibility(View.VISIBLE);
                        orderListView.setAdapter(new OrderExpandableListViewAdapter(orderMap,
                                titleList,OrderSummaryActivity.this));
                    }else {//关闭
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_right_triangle,null);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        tvOrder.setCompoundDrawables(drawable, null, null, null);
                        isOrderOpen = false;
                        orderListView.setVisibility(View.GONE);
                    }
                }else{
                    ToastUtils.showLong("没有数据哦！");
                }
            }
        });

        tvNoOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNoOrderOpen) {//打开
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_bottom_triangle,null);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvNoOrder.setCompoundDrawables(drawable, null, null, null);
                    isNoOrderOpen = true;
                    viewLine3.setVisibility(View.VISIBLE);
                    noOrderListView.setVisibility(View.VISIBLE);
                    noOrderListView.setAdapter(new NoOrderExpandableListViewAdapter(OrderSummaryActivity.this,noOrdertitleList,noOrderMap,viewLine3));
                }else {//关闭
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_right_triangle,null);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvNoOrder.setCompoundDrawables(drawable, null, null, null);
                    isNoOrderOpen = false;
                    noOrderListView.setVisibility(View.GONE);
                    viewLine3.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadHeadData() {
        // 查询购物车商品
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                    @Override
                    public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                        if (saleGoodsItemModels != null && saleGoodsItemModels.size() > 0) {
                            tvNoOrder.setVisibility(View.VISIBLE);
                            viewLine.setVisibility(View.VISIBLE);
                            mProductList = new ArrayList<>();
                            int saleGoodsSize = saleGoodsItemModels.size();
                            for (int i = 0; i < saleGoodsSize; i++) {
                                OrderSummaryEntity.ValueEntity.Product product = new OrderSummaryEntity.ValueEntity.Product();
                                SaleGoodsItemModel model = saleGoodsItemModels.get(i);
                                if (TextUtils.isEmpty(model.Supplier)) {
                                    model.Supplier = "无供应商";
                                }
                                product.UnitName = model.BasicUnitName;
                                product.TotalPrice = model.CurrentPrice;
                                product.Name = model.Supplier;
                                product.Code = model.Code;
                                product.ProductName = model.ProductName;
                                product.Quantity = model.Quantity;

                                if(noOrderMap.containsKey(model.Supplier)){
                                    mProductList = noOrderMap.get(model.Supplier);
                                }
                                if (!noOrdertitleList.contains(model.Supplier)) {
                                    noOrdertitleList.add(model.Supplier);
                                    List<OrderSummaryEntity.ValueEntity.Product> list = new ArrayList<>();
                                    list.add(product);
                                    noOrderMap.put(model.Supplier,list);
                                }else {
                                    mProductList.add(product);
                                    noOrderMap.put(model.Supplier,mProductList);
                                }
                            }
                        }
                        LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());

                        loadData();

                        Log.e("mHeadList", String.valueOf(saleGoodsItemModels.size()));
                    }
                });
    }

    @Override
    public void loadData() {
        Log.e("loadData:", "loadData");
        String body = "{\"SqlStr\":\"select vsd.ProductName,vsd.Quantity,vsd.UnitName,vsd.TotalPrice,vsd.SalesTypeName,vsd.Code,tt.Name from VS_SalesOutBillDetails vsd " +
                "left join TB_Product tp on vsd.ProductId = tp.Id " +
                "left join TB_Trader tt on tt.Id = tp.DefaultTraderId " +
                "where vsd.SalesTypeName = '售' and tt.Name != '' and vsd.Code != ''\",\"SummaryResult\":\"\"}";
        body = body.replace("\\", "");
        body = ZipUtil.gzip(body);
        RetrofitHelper.getOrderSummaryAPI()
                .getOrderSummary(AppInfoUtil.getToken(), "SummaryView", body, "")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderSummaryEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("onSubscribe", "onSubscribe");
                        mDialog.show();
                    }

                    @Override
                    public void onNext(@NonNull OrderSummaryEntity entity) {

                        Log.e("onNext", String.valueOf(entity.HasError));
                        if (!entity.HasError) {
                            if (entity.Value != null) {
                                Log.e("size", String.valueOf(entity.Value.SummaryResult.size()));
                                int size = entity.Value.SummaryResult.size();

                                for (int i = 0; i < size; i++) {

                                    if(orderMap.get(entity.Value.SummaryResult.get(i).Name) != null){
                                        mOrderlist = orderMap.get(entity.Value.SummaryResult.get(i).Name);
                                    }

                                    if (!titleList.contains(entity.Value.SummaryResult.get(i).Name)) {
                                        titleList.add(entity.Value.SummaryResult.get(i).Name);
                                        List<OrderSummaryEntity.ValueEntity.Product> list = new ArrayList<>();
                                        list.add(entity.Value.SummaryResult.get(i));
                                        orderMap.put(entity.Value.SummaryResult.get(i).Name,list);
                                    }else{
                                        mOrderlist.add(entity.Value.SummaryResult.get(i));
                                        orderMap.put(entity.Value.SummaryResult.get(i).Name,mOrderlist);
                                    }
                                }
                            }
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        mDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    static class OrderExpandableListViewAdapter extends BaseExpandableListAdapter{

        private Map<String,List<OrderSummaryEntity.ValueEntity.Product>> orderMap;
        private List<String> titleList;
        private Context mContext;

        public OrderExpandableListViewAdapter(Map<String,List<OrderSummaryEntity.ValueEntity.Product>> map, List<String> titleList, Context context) {
            orderMap = map;
            this.titleList = titleList;
            mContext = context;
        }

        @Override
        public int getGroupCount() {
            return orderMap.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return orderMap.get(titleList.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return titleList.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return orderMap.get(titleList.get(i)).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return 1;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            GroupViewHolder holder;
            if(view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.item_first_expandable,viewGroup,false);
                holder = new GroupViewHolder(view);
                view.setTag(holder);
            }else{
                holder = (GroupViewHolder) view.getTag();
            }
            holder.tvTitle.setText(titleList.get(i));
            if(!b){
                holder.ivOrderArrow.setImageResource(R.drawable.ic_right_triangle);
                holder.viewLine5.setVisibility(View.GONE);
            }else{
                holder.viewLine5.setVisibility(View.VISIBLE);
                holder.ivOrderArrow.setImageResource(R.drawable.ic_bottom_triangle);
            }
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            ChildViewHolder childHolder;
            if(view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.item_order_summary,viewGroup,false);
                childHolder = new ChildViewHolder(view);
                view.setTag(childHolder);
            }else{
                childHolder = (ChildViewHolder) view.getTag();
            }
            OrderSummaryEntity.ValueEntity.Product product = orderMap.get(titleList.get(i)).get(i1);
            childHolder.tvCode.setText("编号: " + product.Code);
            childHolder.tvProductName.setText(product.ProductName);
            childHolder.tvQuantity.setText("数量: " + (int) product.Quantity );
            childHolder.tvSupplier.setText(product.Name);
            childHolder.tvTotalPrice.setText("金额: " + (int) product.TotalPrice);
            childHolder.tvTotalPrice.setTextColor(Color.RED);
            childHolder.tvUnitname.setText("单位: " + product.UnitName);
            return view;
        }
        //子项是否可以选中  子项点击事件
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }

        static class GroupViewHolder {
            @BindView(R.id.iv_order_arrow)
            ImageView ivOrderArrow;

            @BindView(R.id.tv_first_name)
            TextView tvTitle;

            @BindView(R.id.view_line5)
            View viewLine5;

            public GroupViewHolder(View view){
                ButterKnife.bind(this,view);
            }
        }

        static class ChildViewHolder{
            @BindView(R.id.tv_productName)
            TextView tvProductName;

            @BindView(R.id.tv_supplier)
            TextView tvSupplier;

            @BindView(R.id.tv_unitname)
            TextView tvUnitname;

            @BindView(R.id.tv_totalPrice)
            TextView tvTotalPrice;

            @BindView(R.id.tv_quantity)
            TextView tvQuantity;

            @BindView(R.id.tv_code)
            TextView tvCode;

            public ChildViewHolder(View view){
                ButterKnife.bind(this,view);
            }
        }
    }

    static class NoOrderExpandableListViewAdapter extends BaseExpandableListAdapter{

        private List<String> titleList;
        private Map<String,List<OrderSummaryEntity.ValueEntity.Product>> noOrderMap;
        private Context mContext;
        private View viewLine3;

        public NoOrderExpandableListViewAdapter(Context context,List<String> list,Map<String,List<OrderSummaryEntity.ValueEntity.Product>> noOrderMap,View view){
            titleList = list;
            mContext = context;
            this.noOrderMap = noOrderMap;
            viewLine3 = view;
        }

        @Override
        public int getGroupCount() {
            return noOrderMap.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return noOrderMap.get(titleList.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return noOrderMap.get(titleList.get(i));
        }

        @Override
        public Object getChild(int i, int i1) {
            return noOrderMap.get(titleList.get(i)).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            NoOrderGroupViewHolder holder;
            if(view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.item_first_noorder_expandable,viewGroup,false);
                holder = new NoOrderGroupViewHolder(view);
                view.setTag(holder);
            }else{
                holder = (NoOrderGroupViewHolder) view.getTag();
            }

            holder.tvTitle.setText(titleList.get(i));

            if(!b){
                holder.ivOrderArrow.setImageResource(R.drawable.ic_right_triangle);
                holder.viewLine5.setVisibility(View.GONE);
            }else{
                holder.ivOrderArrow.setImageResource(R.drawable.ic_bottom_triangle);
                holder.viewLine5.setVisibility(View.VISIBLE);
            }

            if(i == noOrderMap.size() - 1 && !b && noOrderMap.size() > 1){
                viewLine3.setVisibility(View.GONE);
            }

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            NoOrderChildViewHolder childHolder;
            if(view == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.item_no_order_summary,viewGroup,false);
                childHolder = new NoOrderChildViewHolder(view);
                view.setTag(childHolder);
            }else{
                childHolder = (NoOrderChildViewHolder) view.getTag();
            }
            OrderSummaryEntity.ValueEntity.Product product = noOrderMap.get(titleList.get(i)).get(i1);
            childHolder.tvCode.setText("编号: " + product.Code);
            childHolder.tvProductName.setText(product.ProductName);
            childHolder.tvQuantity.setText("数量: " + (int) product.Quantity );
            childHolder.tvSupplier.setText(product.Name);
            childHolder.tvTotalPrice.setText("金额: " + (int) product.TotalPrice);
            childHolder.tvTotalPrice.setTextColor(Color.RED);
            childHolder.tvUnitname.setText("单位: " + product.UnitName);

            if(noOrderMap.get(titleList.get(titleList.size() -1)).size() - 1 == i1){
                childHolder.viewLine4.setVisibility(View.GONE);
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }

        static class NoOrderGroupViewHolder {
            @BindView(R.id.iv_order_arrow)
            ImageView ivOrderArrow;

            @BindView(R.id.tv_first_name)
            TextView tvTitle;

            @BindView(R.id.view_line5)
            View viewLine5;

            public NoOrderGroupViewHolder(View view){
                ButterKnife.bind(this,view);
            }
        }

        static class NoOrderChildViewHolder{
            @BindView(R.id.tv_productName)
            TextView tvProductName;

            @BindView(R.id.tv_supplier)
            TextView tvSupplier;

            @BindView(R.id.tv_unitname)
            TextView tvUnitname;

            @BindView(R.id.tv_totalPrice)
            TextView tvTotalPrice;

            @BindView(R.id.tv_quantity)
            TextView tvQuantity;

            @BindView(R.id.tv_code)
            TextView tvCode;

            @BindView(R.id.view_line4)
            View viewLine4;

            public NoOrderChildViewHolder(View view){
                ButterKnife.bind(this,view);
            }
        }
    }
}
