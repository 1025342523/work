package com.yifarj.yifadinghuobao.ui.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.loader.GlideImageLoader;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.entity.StockInfoForToolTipListEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;
import com.yifarj.yifadinghuobao.utils.NumberUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanEditDialog;
import com.yifarj.yifadinghuobao.view.NumberAddSubView;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 商品详情
 * <p>
 * Created by zydx-pc on 2017/6/28.
 */

public class ShopDetailActivity extends BaseActivity {

    @BindView(R.id.shopDetail_TitleView)
    TitleView shopDetail_titleView;
    @BindView(R.id.shopDetail_Name)
    TextView shopDetail_Name;
    @BindView(R.id.shopDetail_Code)
    TextView shopDetail_Code;
    @BindView(R.id.shopDetail_Price)
    TextView shopDetail_Price;
    @BindView(R.id.shopDetail_PackSpec)
    TextView shopDetail_PackSpec;
    @BindView(R.id.shopDetail_Inventory)
    TextView shopDetail_Inventory;
    @BindView(R.id.shopDetail_unit)
    TagFlowLayout shopDetail_unit;
    @BindView(R.id.shopDetail_orderNum)
    NumberAddSubView shopDetail_orderNum;
    @BindView(R.id.shopDetail_totalPrice)
    TextView shopDetail_totalPrice;
    @BindView(R.id.shopDetail_addShoppingCart)
    ButtonBarLayout shopDetail_addShoppingCart;
    @BindView(R.id.empty_view)
    CustomEmptyView emptyView;
    @BindView(R.id.shopDetail_scrollView)
    ScrollView scrollView;
    @BindView(R.id.addShopCart)
    LinearLayout addShopCart;

    private GoodsListEntity.ValueEntity goodsBean;
    private Banner banner;

    private int shoppingId;
    private double totalPrice = 0;
    private int quantity = 0;
    private String unitName;
    private int unitId;
    private double unitPrice;
    private boolean isExist = false;
    private String basicUnitName;
    private String tempUnitName;

    private List<Integer> selectedUnitList = new ArrayList<>(new HashSet<>());

    @Override
    public int getLayoutId() {
        return R.layout.activity_shop_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadData();
        init();
    }

    @Override
    public void loadData() {
        int traderId;
        shoppingId = getIntent().getIntExtra("shoppingId", 0);
        if (DataSaver.getMettingCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else {
            return;
        }

        // 查询购物车里是否有当前商品
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where(SaleGoodsItemModel_Table.ProductId.eq(shoppingId)))
                .queryList()
                .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                    @Override
                    public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModel) throws Exception {
                        if (saleGoodsItemModel != null && saleGoodsItemModel.size() > 0) {
                            LogUtils.e("购物车有此商品" + saleGoodsItemModel.get(0).ProductName);
                            quantity = saleGoodsItemModel.get(0).Quantity;
                            unitName = saleGoodsItemModel.get(0).ProductUnitName;
                            totalPrice = saleGoodsItemModel.get(0).TotalPrice;
                            unitId = saleGoodsItemModel.get(0).UnitId;
                            unitPrice = saleGoodsItemModel.get(0).UnitPrice;
                            basicUnitName = saleGoodsItemModel.get(0).BasicUnitName;
                            tempUnitName = saleGoodsItemModel.get(0).ProductUnitName;
                            isExist = true;
                        }
                    }
                });

        //获取当前商品的详细信息
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", "", "Id =" + shoppingId, "[" + traderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
                        goodsBean = goodsListEntity.Value.get(0);
                        //获取库存
                        RetrofitHelper
                                .getStockInfoListApi()
                                .getStockInfoList("StockInfoForToolTipList", "", "ProductId =" + shoppingId, "", AppInfoUtil.getToken())
                                .compose(bindToLifecycle())
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<StockInfoForToolTipListEntity>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull StockInfoForToolTipListEntity stockInfoForToolTipListEntity) {
                                        if (stockInfoForToolTipListEntity.Value != null && stockInfoForToolTipListEntity.Value.size() > 0) {
                                            LogUtils.e("stockInfoForToolTipListEntitySize:", stockInfoForToolTipListEntity.Value.size());
                                            boolean chosen = false;
                                            for (StockInfoForToolTipListEntity.ValueEntity item :
                                                    stockInfoForToolTipListEntity.Value) {
                                                if (item.WarehouseId == goodsBean.DefaultWarehouseId) {
                                                    LogUtils.e("SalesQuantityPackString", item.SalesQuantityPackString);
                                                    chosen = true;
                                                    shopDetail_Inventory.setText(item.SalesQuantityPackString);//可开库存
                                                }
                                            }
                                            if (!chosen) {
                                                //没有找到对应的仓库
                                                shopDetail_Inventory.setText("");
                                            }
                                        } else {
                                            //没有找到对应的仓库
                                            shopDetail_Inventory.setText("");
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                        LogUtils.e("goodBean", goodsBean.Name);
                        if (!goodsListEntity.HasError) {
                            //-----商品图片
                            banner = (Banner) findViewById(R.id.shopDetail_banner);
                            //设置banner样式
                            banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
                            //设置图片加载器
                            banner.setImageLoader(new GlideImageLoader());
                            //设置banner动画效果
                            banner.setBannerAnimation(Transformer.Default);
                            //设置自动轮播，默认为true
                            banner.isAutoPlay(false);
                            //设置图片集合
                            List<String> list =new ArrayList<String>();
                            for (GoodsListEntity.ValueEntity.ProductPictureListEntity unit : goodsBean.ProductPictureList) {
                                list.add(AppInfoUtil.genPicUrl(unit.Path));
                                LogUtils.e(goodsBean.Name + "图片Url：" + AppInfoUtil.genPicUrl(unit.Path));
                            }
                            banner.setImages(list);
                            //banner设置方法全部调用完毕时最后调用
                            banner.start();

                            //商品名称
                            shopDetail_Name.setText(goodsBean.Name);
                            //商品编号
                            if (goodsBean.Code.length() <= 6) {
                                shopDetail_Code.setText("编号：" + goodsBean.Code);
                            } else {
                                shopDetail_Code.setText("编号：" + goodsBean.Code.substring(goodsBean.Code.length() - 4, goodsBean.Code.length()));
                            }
                            //货品规格
                            shopDetail_PackSpec.setText(goodsBean.PackSpec);
                            //商品最小单位
                            if (!isExist) {
                                for (ProductUnitEntity.ValueEntity unit : goodsBean.ProductUnitList) {
                                    if (unit.IsBasic) {
                                        unitName = unit.Name;
                                        basicUnitName = unit.Name;
                                        unitId = unit.Id;
                                        LogUtils.e(goodsBean.Name + "：" + unitName);
                                    }
                                }
                            }
                            LogUtils.e(goodsBean.Name + "：修改单位" + unitName);
                            //货品订购数量
                            shopDetail_orderNum.setValue(quantity);
                            //货品价格
                            shopDetail_Price.setText(goodsBean.MemoryPrice + "元/" + basicUnitName);
                            //货品总价
                            totalPrice = unitPrice * quantity;
                            shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
                            if (!isExist) {
                                unitPrice = goodsBean.MemoryPrice;
                            }

                            //商品单位
                            final LayoutInflater mInflater = LayoutInflater.from(ShopDetailActivity.this);
                            TagAdapter<ProductUnitEntity.ValueEntity> tagAdapter = new TagAdapter<ProductUnitEntity.ValueEntity>(goodsBean.ProductUnitList) {
                                @Override
                                public View getView(FlowLayout parent, int position, ProductUnitEntity.ValueEntity productUnitEntity) {
                                    TextView tv = (TextView) mInflater.inflate(R.layout.tv, parent, false);
                                    tv.setText(productUnitEntity.Name);
                                    return tv;
                                }
                            };
                            shopDetail_unit.setAdapter(tagAdapter);
                            shopDetail_unit.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                                @Override
                                public void onSelected(Set<Integer> selectPosSet) {
                                    selectedUnitList.clear();
                                    if (selectPosSet.size() > 0) {
                                        selectedUnitList.addAll(selectPosSet);
                                        int select = 0;
                                        for (Integer item : selectPosSet) {
                                            select = item;
                                        }
                                        unitName = goodsBean.ProductUnitList.get(select).Name;
                                        unitId = goodsBean.ProductUnitList.get(select).Id;
                                        unitPrice = goodsBean.ProductUnitList.get(select).BasicFactor * goodsBean.MemoryPrice;
                                        totalPrice = quantity * unitPrice;
                                        shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
                                        LogUtils.e(goodsBean.Name + "：修改单位" + unitName);
                                    } else {
                                        setSelectedUnit(tagAdapter);
                                    }
                                }
                            });
                            setSelectedUnit(tagAdapter);

                            //订购数量增加/减少的点击事件
                            shopDetail_orderNum.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
                                @Override
                                public void onButtonAddClick(View view, int value) {
                                    if (value > 0) {
                                        quantity = value;
                                        totalPrice = unitPrice * quantity;
                                        shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
                                        LogUtils.e(goodsBean.Name + "：数量增加为" + quantity);
                                    }
                                }

                                @Override
                                public void onButtonSubClick(View view, int value) {
                                    if (value > 0) {
                                        quantity = value;
                                        totalPrice = unitPrice * quantity;
                                        shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
                                        LogUtils.e(goodsBean.Name + "：数量减少为" + quantity);
                                    } else {
                                        Toast.makeText(ShopDetailActivity.this, "订购数量必须大于0", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //订购数量的编辑事件
                            shopDetail_orderNum.setOnNumberEditClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CzechYuanEditDialog mDialog = new CzechYuanEditDialog(ShopDetailActivity.this, R.style.CzechYuanDialog);
                                    LogUtils.e(goodsBean.Name + "：数量为" + quantity);
                                    mDialog.getEditText().setText(String.valueOf(quantity));
                                    mDialog.getEditText().setSelection(0, String.valueOf(quantity).length());
                                    mDialog.setConfirmClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            int count = 1;
                                            try {
                                                int tempEditText = Integer.parseInt(mDialog.getEditText().getText().toString());
                                                if (tempEditText <= 0) {
                                                    Toast.makeText(ShopDetailActivity.this, "订购数量必须大于0", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    count = tempEditText;
                                                }
                                            } catch (NumberFormatException e) {
                                                count = quantity;
                                            }
                                            if (count != quantity) {
                                                quantity = count;
                                                totalPrice = unitPrice * count;
                                                shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
                                                LogUtils.e(goodsBean.Name + "：数量修改为" + count);
                                                shopDetail_orderNum.setValue(count);
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            //显示空布局
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showEmptyView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setSelectedUnit(final TagAdapter<ProductUnitEntity.ValueEntity> tagAdapter) {
        selectedUnitList.clear();
        if (isExist) {
            Flowable.fromIterable(goodsBean.ProductUnitList)
                    .forEach(new Consumer<ProductUnitEntity.ValueEntity>() {
                        @Override
                        public void accept(@NonNull ProductUnitEntity.ValueEntity valueEntity) throws Exception {
                            if (tempUnitName.equals(valueEntity.Name)) {
                                int unitPosition=goodsBean.ProductUnitList.indexOf(valueEntity);
                                tagAdapter.setSelectedList(unitPosition);
                                selectedUnitList.add(goodsBean.ProductUnitList.indexOf(valueEntity));
                                unitName = goodsBean.ProductUnitList.get(unitPosition).Name;
                                unitId = goodsBean.ProductUnitList.get(unitPosition).Id;
                                unitPrice = goodsBean.ProductUnitList.get(unitPosition).BasicFactor * goodsBean.MemoryPrice;
                                totalPrice = quantity * unitPrice;
                                shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
                                LogUtils.e(goodsBean.Name + "：修改单位" + unitName);
                            }
                        }
                    });
        } else {
            tagAdapter.setSelectedList(0);
            selectedUnitList.add(0);
            unitName = goodsBean.ProductUnitList.get(0).Name;
            unitId = goodsBean.ProductUnitList.get(0).Id;
            unitPrice = goodsBean.ProductUnitList.get(0).BasicFactor * goodsBean.MemoryPrice;
            totalPrice = quantity * unitPrice;
            shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
            LogUtils.e(goodsBean.Name + "：修改单位" + unitName);
        }
    }

    private void init() {
        shopDetail_titleView.setLeftIconClickListener(view -> {
            finish();
        });
        shopDetail_titleView.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopDetailActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
        });
        //加入购物车
        RxView.clicks(shopDetail_addShoppingCart)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (quantity > 0) {
                            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class)
                                    .where(SaleGoodsItemModel_Table.ProductId.eq(goodsBean.Id)))
                                    .queryList().subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                                @Override
                                public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                                    LogUtils.e("saleGoodsItemModels.size()：" + saleGoodsItemModels.size());
                                    if (saleGoodsItemModels.size() > 0) {
                                        SaleGoodsItemModel mItem = saleGoodsItemModels.get(0);
                                        mItem.delete().subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(@NonNull Boolean aBean) throws Exception {
                                                LogUtils.e(mItem.ProductName + "：删除\n" + aBean);
                                            }
                                        });
                                    }

                                }
                            });
                            RXSQLite.rx(SQLite.select().from(GoodsUnitModel.class)
                                    .where(GoodsUnitModel_Table.ProductId.eq(goodsBean.Id)))
                                    .queryList()
                                    .subscribe(new Consumer<List<GoodsUnitModel>>() {
                                        @Override
                                        public void accept(@NonNull List<GoodsUnitModel> goodsUnitModels) throws Exception {
                                            if (goodsUnitModels != null && goodsUnitModels.size() > 0) {
                                                Flowable.fromIterable(goodsUnitModels)
                                                        .forEach(new Consumer<GoodsUnitModel>() {
                                                            @Override
                                                            public void accept(@NonNull GoodsUnitModel goodsUnitModel) throws Exception {
                                                                GoodsUnitModel mUnitModel = goodsUnitModel;
                                                                mUnitModel.delete().subscribe(new Consumer<Boolean>() {
                                                                    @Override
                                                                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                                                                        LogUtils.e("单位：" + mUnitModel.Name + "删除成功");
                                                                    }
                                                                });
                                                            }
                                                        });
                                                LogUtils.e("删除成功");
                                            }
                                        }
                                    });
                            add(goodsBean, quantity, unitName, unitId, unitPrice, totalPrice);
                        } else {
                            Toast.makeText(ShopDetailActivity.this, "请输入订购数量", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void add(GoodsListEntity.ValueEntity goodsBean, int count, String unitName, int unitId, double unitPrice, double totalPrice) {
        SaleGoodsItemModel itemModel = new SaleGoodsItemModel();
        itemModel.CurrentPrice = totalPrice;
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        }
        itemModel.PriceSystemId = DataSaver.getPriceSystemId();
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Code = goodsBean.Code;
        itemModel.ProductName = goodsBean.Name;
        itemModel.BasicUnitName = unitName;
        itemModel.ProductUnitName = unitName;
        itemModel.BasicUnitPrice = goodsBean.MemoryPrice;
        itemModel.UnitPrice = unitPrice;
        itemModel.Discount = 1.0f;
        itemModel.SalesType = 1;
        itemModel.TaxRate = 1.0;
        itemModel.UnitId = unitId;
        itemModel.Quantity = count;
        itemModel.WarehouseId = goodsBean.DefaultWarehouseId;
        itemModel.ProductId = goodsBean.Id;
        itemModel.LocationId = goodsBean.DefaultLocationId;
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Price0 = goodsBean.Price0;
        itemModel.Price1 = goodsBean.Price1;
        itemModel.Price2 = goodsBean.Price2;
        itemModel.Price3 = goodsBean.Price3;
        itemModel.Price4 = goodsBean.Price4;
        itemModel.Price5 = goodsBean.Price5;
        itemModel.Price6 = goodsBean.Price6;
        itemModel.Price7 = goodsBean.Price7;
        itemModel.Price8 = goodsBean.Price8;
        itemModel.Price9 = goodsBean.Price9;
        itemModel.Price10 = goodsBean.Price10;
        itemModel.MinSalesQuantity = goodsBean.MinSalesQuantity;
        itemModel.MaxSalesQuantity = goodsBean.MaxSalesQuantity;
        itemModel.MinSalesPrice = goodsBean.MinSalesPrice;
        itemModel.MaxPurchasePrice = goodsBean.MaxPurchasePrice;
        itemModel.DefaultLocationName = goodsBean.DefaultLocationName;
        itemModel.OweRemark = goodsBean.Remark;
        itemModel.insert()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Toast.makeText(ShopDetailActivity.this, "已添加到购物车", Toast.LENGTH_SHORT).show();
                        LogUtils.e("Item插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                    }
                });
        itemModel.save().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                LogUtils.e("Item保存数据成功");
            }
        });
        Flowable.fromIterable(goodsBean.ProductUnitList)
                .forEach(valueEntity -> {
                    GoodsUnitModel goodsUnitModel = new GoodsUnitModel();
                    goodsUnitModel.Id = valueEntity.Id;
                    goodsUnitModel.ProductId = valueEntity.ProductId;
                    goodsUnitModel.Name = valueEntity.Name;
                    goodsUnitModel.Factor = valueEntity.Factor;
                    goodsUnitModel.BasicFactor = valueEntity.BasicFactor;
                    goodsUnitModel.IsBasic = valueEntity.IsBasic;
                    goodsUnitModel.IsDefault = valueEntity.IsDefault;
                    goodsUnitModel.BreakupNotify = valueEntity.BreakupNotify;
                    goodsUnitModel.Ordinal = valueEntity.Ordinal;
                    goodsUnitModel.insert().subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            LogUtils.e("Unit插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                        }
                    });
                });
    }

    public void showEmptyView() {
        scrollView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        addShopCart.setVisibility(View.GONE);
        emptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        emptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
    }

}
