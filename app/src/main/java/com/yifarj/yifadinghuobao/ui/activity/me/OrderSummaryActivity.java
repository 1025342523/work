package com.yifarj.yifadinghuobao.ui.activity.me;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.chen.treeview.TreeRecyclerView;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.model.entity.OrderSummaryEntity;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.ZipUtil;
import com.yifarj.yifadinghuobao.view.HeaderHolder;
import com.yifarj.yifadinghuobao.view.IconTreeItemHolder;
import com.yifarj.yifadinghuobao.view.PlaceHolderHolder;
import com.yifarj.yifadinghuobao.view.ProfileHolder;
import com.yifarj.yifadinghuobao.view.TitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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

    @BindView(R.id.treeView)
    TreeRecyclerView treeView;

    @BindView(R.id.rl_container)
    RelativeLayout container;

    private List<OrderSummaryEntity.ValueEntity.Product> mOrderlist = new ArrayList<>();
    private List<OrderSummaryEntity.ValueEntity.Product> mProductList;
    private List<String> titleList = new ArrayList<>();
    private List<String> noOrdertitleList = new ArrayList<>();
    private TreeNode mRoot;
    private TreeNode mNoOrderTreeNode;
    private TreeNode mOrderTreeNode;
    private AndroidTreeView mTView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_summary;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mRoot = TreeNode.root();
        loadHeadData();
    }

    private void addProfileData(TreeNode profile) {
        if (noOrdertitleList.size() > 0) {
            for (int i = 0; i < noOrdertitleList.size(); i++) {
                TreeNode treeNode2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(noOrdertitleList.get(i))).setViewHolder(new HeaderHolder(this));
                for (int j = 0; j < mProductList.size(); j++) {
                    if (noOrdertitleList.get(i).equals(mProductList.get(j).Name)) {
                        TreeNode treeNode3 = new TreeNode(new PlaceHolderHolder.PlaceItem(mProductList.get(j).Code,
                                mProductList.get(j).TotalPrice, (int) mProductList.get(j).Quantity,
                                mProductList.get(j).UnitName, mProductList.get(j).Name,
                                mProductList.get(j).ProductName, null, 0))
                                .setViewHolder(new PlaceHolderHolder(this));
                        treeNode2.addChild(treeNode3);
                    }
                }
                profile.addChild(treeNode2);
            }
        }
    }

    private void addProfileDataOrder(TreeNode profile) {
        if (titleList.size() > 0) {
            for (int i = 0; i < titleList.size(); i++) {
                TreeNode treeNode2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(titleList.get(i))).setViewHolder(new HeaderHolder(this));
                for (int j = 0; j < mOrderlist.size(); j++) {
                    if (titleList.get(i).equals(mOrderlist.get(j).Name)) {
                        TreeNode treeNode3 = new TreeNode(new PlaceHolderHolder.PlaceItem(mOrderlist.get(j).Code, mOrderlist.get(j).TotalPrice,
                                (int) mOrderlist.get(j).Quantity, mOrderlist.get(j).UnitName, mOrderlist.get(j).Name,
                                mOrderlist.get(j).ProductName, null, 0)).setViewHolder(new PlaceHolderHolder(this));
                        treeNode2.addChild(treeNode3);
                    }
                }
                profile.addChild(treeNode2);
            }
        }
    }

    private void loadHeadData() {
        // 查询购物车商品
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                    @Override
                    public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                        if (saleGoodsItemModels != null && saleGoodsItemModels.size() > 0) {
                            mProductList = new ArrayList<>();
                            int saleGoodsSize = saleGoodsItemModels.size();
                            for (int i = 0; i < saleGoodsSize; i++) {
                                OrderSummaryEntity.ValueEntity.Product product = new OrderSummaryEntity.ValueEntity.Product();
                                SaleGoodsItemModel model = saleGoodsItemModels.get(i);
                                if (TextUtils.isEmpty(model.Supplier)) {
                                    model.Supplier = "无供应商";
                                }
                                if (!noOrdertitleList.contains(model.Supplier)) {
                                    noOrdertitleList.add(model.Supplier);
                                }

                                product.UnitName = model.BasicUnitName;
                                product.TotalPrice = model.CurrentPrice;
                                product.Name = model.Supplier;
                                product.Code = model.Code;
                                product.ProductName = model.ProductName;
                                product.Quantity = model.Quantity;
                                mProductList.add(product);
                            }
                            if (mProductList.size() > 0) {
                                mNoOrderTreeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem("未下单")).setViewHolder(new ProfileHolder(OrderSummaryActivity.this));
                                addProfileData(mNoOrderTreeNode);
                                mRoot.addChild(mNoOrderTreeNode);
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
                    }

                    @Override
                    public void onNext(@NonNull OrderSummaryEntity entity) {

                        Log.e("onNext", String.valueOf(entity.HasError));
                        if (!entity.HasError) {
                            if (entity.Value != null) {
                                Log.e("size", String.valueOf(entity.Value.SummaryResult.size()));
                                mOrderlist.addAll(entity.Value.SummaryResult);
                                int size = mOrderlist.size();
                                for (int i = 0; i < size; i++) {
                                    if (!titleList.contains(mOrderlist.get(i).Name)) {
                                        titleList.add(mOrderlist.get(i).Name);
                                    }
                                }
                                Log.e("titleSize", String.valueOf(titleList.size()));
                                if (titleList.size() > 0) {
                                    mOrderTreeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem("已下单")).setViewHolder(new ProfileHolder(OrderSummaryActivity.this));
                                    addProfileDataOrder(mOrderTreeNode);
                                    mRoot.addChild(mOrderTreeNode);
                                }
                            }

                            mTView = new AndroidTreeView(OrderSummaryActivity.this, mRoot);
//                            mTView.setDefaultAnimation(true);
                            mTView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided, true);
                            container.addView(mTView.getView());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
