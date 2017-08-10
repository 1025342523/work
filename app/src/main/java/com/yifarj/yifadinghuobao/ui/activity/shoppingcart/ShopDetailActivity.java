package com.yifarj.yifadinghuobao.ui.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.facebook.stetho.common.LogUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel_Table;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.loader.GlideImageLoader;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductMemoryPriceEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.entity.StockInfoForToolTipListEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;
import com.yifarj.yifadinghuobao.utils.NumberUtil;
import com.yifarj.yifadinghuobao.utils.ProductPictureUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanEditDialog;
import com.yifarj.yifadinghuobao.view.NumberAddSubView;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.utils.PriceSystemGenerator;
import com.yifarj.yifadinghuobao.vo.PriceSystem;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
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
    @BindView(R.id.shopDetail_collection)
    ImageView collection;
    @BindView(R.id.shopDetail_tvAddShoppingCart)
    TextView shopDetail_tvAddShoppingCart;

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
    private int basicUnitId;
    private String tempUnitName;
    private int traderId;
    private double basicUnitPrice;
    private int orderCount = 0;
    private boolean isCollection = false;
    private int saleType = 0;

    private static final int REQUEST_REFRESH = 10;

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
        shoppingId = getIntent().getIntExtra("shoppingId", 0);
        saleType = getIntent().getIntExtra("saleType", 0);
        if (DataSaver.getMettingCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else {
            return;
        }

        if (saleType == 1) {
            // 查询退货清单
            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where())
                    .queryList()
                    .subscribe(new Consumer<List<ReturnListItemModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                            orderCount = returnListItemModels.size();
                            if (orderCount > 0) {
                                shopDetail_titleView.setRightIconText(View.VISIBLE, orderCount);
                                LogUtils.e("退货清单中的商品数量为：" + orderCount);
                            } else if (orderCount == 0) {
                                shopDetail_titleView.setRightIconText(View.GONE, 0);
                                LogUtils.e("退货清单中的商品数量为：" + orderCount);
                            }
                            LogUtils.e("returnListItemModels：" + returnListItemModels.size());
                        }
                    });
            // 查询退货清单中是否有当前商品
            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where(ReturnListItemModel_Table.ProductId.eq(shoppingId)))
                    .queryList()
                    .subscribe(new Consumer<List<ReturnListItemModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                            if (returnListItemModels != null && returnListItemModels.size() > 0) {
                                LogUtils.e("退货清单中有此商品：" + returnListItemModels.get(0).ProductName);
                                quantity = returnListItemModels.get(0).Quantity;
                                unitName = returnListItemModels.get(0).ProductUnitName;
                                totalPrice = returnListItemModels.get(0).TotalPrice;
                                unitId = returnListItemModels.get(0).UnitId;
                                unitPrice = returnListItemModels.get(0).UnitPrice;
                                basicUnitName = returnListItemModels.get(0).BasicUnitName;
                                tempUnitName = returnListItemModels.get(0).ProductUnitName;
                                basicUnitPrice = returnListItemModels.get(0).BasicUnitPrice;
                                basicUnitId = returnListItemModels.get(0).BasicUnitId;
                                isExist = true;
                            } else {
                                LogUtils.e("退货清单中没有此商品");
                            }
                        }
                    });
        } else {
            // 查询购物车商品
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where())
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModels) throws Exception {
                            orderCount = saleGoodsItemModels.size();
                            if (orderCount > 0) {
                                shopDetail_titleView.setRightIconText(View.VISIBLE, orderCount);
                                LogUtils.e("购物车中的商品数量为：" + orderCount);
                            } else if (orderCount == 0) {
                                shopDetail_titleView.setRightIconText(View.GONE, 0);
                                LogUtils.e("购物车中的商品数量为：" + orderCount);
                            }
                            LogUtils.e("saleGoodsItemModels：" + saleGoodsItemModels.size());
                        }
                    });
            // 查询购物车中是否有当前商品
            RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where(SaleGoodsItemModel_Table.ProductId.eq(shoppingId)))
                    .queryList()
                    .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                        @Override
                        public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModel) throws Exception {
                            if (saleGoodsItemModel != null && saleGoodsItemModel.size() > 0) {
                                LogUtils.e("购物车有此商品：" + saleGoodsItemModel.get(0).ProductName);
                                quantity = saleGoodsItemModel.get(0).Quantity;
                                unitName = saleGoodsItemModel.get(0).ProductUnitName;
                                totalPrice = saleGoodsItemModel.get(0).TotalPrice;
                                unitId = saleGoodsItemModel.get(0).UnitId;
                                unitPrice = saleGoodsItemModel.get(0).UnitPrice;
                                basicUnitName = saleGoodsItemModel.get(0).BasicUnitName;
                                tempUnitName = saleGoodsItemModel.get(0).ProductUnitName;
                                basicUnitPrice = saleGoodsItemModel.get(0).BasicUnitPrice;
                                basicUnitId = saleGoodsItemModel.get(0).BasicUnitId;
                                isExist = true;
                            } else {
                                LogUtils.e("购物车没有此商品");
                            }
                        }
                    });
        }

        // 查询收藏商品中是否有当前商品
        RXSQLite.rx(SQLite.select().from(CollectionItemModel.class).where(CollectionItemModel_Table.ProductId.eq(shoppingId)))
                .queryList()
                .subscribe(new Consumer<List<CollectionItemModel>>() {
                    @Override
                    public void accept(@NonNull List<CollectionItemModel> collectionItemModel) throws Exception {
                        if (collectionItemModel != null && collectionItemModel.size() > 0) {
                            LogUtils.e("收藏夹中有此商品：" + collectionItemModel.get(0).ProductName);
                            isCollection = true;
                            collection.setSelected(true);
                        } else {
                            LogUtils.e("收藏夹中没有此商品");
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
                                                    shopDetail_Inventory.setText("库存：" + item.SalesQuantityPackString);//可开库存
                                                }
                                            }
                                            if (!chosen) {
                                                //没有找到对应的仓库
                                                shopDetail_Inventory.setText("");
                                                LogUtils.e("没有找到对应的仓库");
                                            }
                                        } else {
                                            //没有找到对应的仓库
                                            shopDetail_Inventory.setText("");
                                            LogUtils.e("没有找到对应的仓库");
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
                            List<String> list = new ArrayList<String>();
                            for (GoodsListEntity.ValueEntity.ProductPictureListEntity unit : goodsBean.ProductPictureList) {
                                list.add(AppInfoUtil.genPicUrl(unit.Path));
                                LogUtils.e(goodsBean.Name + "图片Url：" + AppInfoUtil.genPicUrl(unit.Path));
                            }
                            if (list.size() == 0) {
                                list.add("http://img4.imgtn.bdimg.com/it/u=1007043693,2735869963&fm=26&gp=0.jpg");
                            }
                            banner.setImages(list);
                            //banner设置方法全部调用完毕时最后调用
                            banner.start();

                            banner.setOnBannerListener(new OnBannerListener() {
                                @Override
                                public void OnBannerClick(int position) {
                                    LogUtils.e(goodsBean.Name + "图片position：" + position + ",Url:" + list.get(position));
                                    ProductPictureUtil.createLargeImageDialog(ShopDetailActivity.this, list.get(position));
                                }
                            });

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
                                        basicUnitId = unit.Id;
                                        unitId = unit.Id;
                                        basicUnitPrice = goodsBean.MemoryPrice;
                                        getProductMemoryPrice(unitId, 0);
                                        LogUtils.e(goodsBean.Name + ",unitName:" + unitName + ",unitPrice:" + unitPrice);
                                    }
                                }
                            }
                            LogUtils.e(goodsBean.Name + "：修改单位" + unitName);
                            //货品订购数量
                            shopDetail_orderNum.setValue(quantity);
                            //货品价格
                            shopDetail_Price.setText(basicUnitPrice + "元/" + basicUnitName);
                            //货品总价
                            totalPrice = unitPrice * quantity;
                            shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");

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
                                        getProductMemoryPrice(unitId, select);
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
                                int unitPosition = goodsBean.ProductUnitList.indexOf(valueEntity);
                                tagAdapter.setSelectedList(unitPosition);
                                selectedUnitList.add(goodsBean.ProductUnitList.indexOf(valueEntity));
                                unitName = goodsBean.ProductUnitList.get(unitPosition).Name;
                                unitId = goodsBean.ProductUnitList.get(unitPosition).Id;
                                /*unitPrice = goodsBean.ProductUnitList.get(unitPosition).BasicFactor * basicUnitPrice;
                                totalPrice = quantity * unitPrice;*/
                                getProductMemoryPrice(unitId, unitPosition);
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
            /*unitPrice = goodsBean.ProductUnitList.get(0).BasicFactor * basicUnitPrice;
            totalPrice = quantity * unitPrice;*/
            getProductMemoryPrice(unitId, 0);
            shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
            LogUtils.e(goodsBean.Name + "：修改单位" + unitName);
        }
    }

    /**
     * 获取记忆价格
     */
    private void getProductMemoryPrice(int productUnitId, int select) {
        String b;
        b = "ProductId=" + goodsBean.Id + " and " +
                "TraderId=" + traderId + " and " +
                "ProductUnitId=" + productUnitId;
        //获取当前商品的记忆价格
        RetrofitHelper.getProductMemoryPriceApi()
                .getProductMemoryPrice("TraderProductPrice", b, "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProductMemoryPriceEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ProductMemoryPriceEntity entity) {
                        if (!entity.HasError) {
                            PriceSystem.PriceSystemListEntity selectedPriceSystem = null;
                            boolean selected = false;
                            double memoryPrice = 0;
                            if (entity.Value.ProductId != 0 && entity.Value.ProductUnitId != 0 && entity.Value.Price > 0) {
                                for (PriceSystem.PriceSystemListEntity item :
                                        PriceSystemGenerator.getInstance().PriceSystemList) {
                                    if (entity.Value.PriceSystemId == item.Id) {
                                        selected = true;
                                        selectedPriceSystem = item;
                                        LogUtils.e("selectedPriceSystem" + selectedPriceSystem);
                                    }
                                }
                                //存在默认价格
                                memoryPrice = entity.Value.Price;
                                LogUtils.e(goodsBean.Name + "存在记忆价格,记忆价格为:" + memoryPrice);
                            } else if (DataSaver.getPriceSystemId() != 0) {
                                for (PriceSystem.PriceSystemListEntity item :
                                        PriceSystemGenerator.getInstance().PriceSystemList) {
                                    if (item.Id == DataSaver.getPriceSystemId()) {
                                        selected = true;
                                        selectedPriceSystem = item;
                                        LogUtils.e("selectedPriceSystem" + selectedPriceSystem);
                                    }
                                }
                                //没有记忆价格,但是有默认价格体系
                                LogUtils.e(goodsBean.Name + "不存在记忆价格");
                            }
                            if (!selected || DataSaver.getPriceSystemId() == 0) {
                                selectedPriceSystem = PriceSystemGenerator.getInstance().PriceSystemList.get(0);
                            }
                            goodsBean.PriceSystemList.get(0).Id = selectedPriceSystem.Id;
                            setGoodsItemPriceWithUnitAndPriceSystem(memoryPrice, select, productUnitId);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 根据记忆价格、价格体系和单位设置价格,如果有记忆价格,
     * 则设置价格为记忆价格,如果没有记忆价格,则根据价格体系和单位设置价格
     *
     * @param memoryPrice 记忆价格
     */
    @SuppressWarnings("deprecation")
    private void setGoodsItemPriceWithUnitAndPriceSystem(double memoryPrice, int select, int productUnitId) {
        double price = 0;
        double basicPrice = 0;
        if (memoryPrice > 0) {//有记忆价格
            if (select == 0) {
                basicPrice = memoryPrice;
                price = basicPrice;
            } else {
                price = memoryPrice;
                for (ProductUnitEntity.ValueEntity unitItem :
                        goodsBean.ProductUnitList) {
                    if (unitItem.Id == productUnitId) {
                        basicPrice = price / unitItem.BasicFactor;
                    }
                }
            }
        } else {//没有记忆价格,则通过单位id获取单位价格
            //获取价格体系的价格
            switch (goodsBean.PriceSystemList.get(0).Id) {
                case 1:
                    basicPrice = goodsBean.Price1;
                    break;
                case 2:
                    basicPrice = goodsBean.Price2;
                    break;
                case 3:
                    basicPrice = goodsBean.Price3;
                    break;
                case 4:
                    basicPrice = goodsBean.Price4;
                    break;
                case 5:
                    basicPrice = goodsBean.Price5;
                    break;
                case 6:
                    basicPrice = goodsBean.Price6;
                    break;
                case 7:
                    basicPrice = goodsBean.Price7;
                    break;
                case 8:
                    basicPrice = goodsBean.Price8;
                    break;
                case 9:
                    basicPrice = goodsBean.Price9;
                    break;
                case 10:
                    basicPrice = goodsBean.Price10;
                    break;
            }
            for (ProductUnitEntity.ValueEntity unitItem :
                    goodsBean.ProductUnitList) {
                if (unitItem.Id == productUnitId) {
                    price = basicPrice * unitItem.BasicFactor;
                }
            }
        }
        unitPrice = price;
        basicUnitPrice = basicPrice;
        LogUtil.e("setGoodsItemPriceWithUnitAndPriceSystem basicPrice", basicPrice + "");
        LogUtil.e("setGoodsItemPriceWithUnitAndPriceSystem goodsItem.PriceSystemId", goodsBean.PriceSystemList.get(0).Id + "");
        totalPrice = quantity * unitPrice;
        shopDetail_totalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
    }

    private void init() {
        if (saleType == 1) {
            shopDetail_tvAddShoppingCart.setText("确定退货");
        }

        shopDetail_titleView.setLeftIconClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });
        shopDetail_titleView.setRightIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopDetailActivity.this, ShoppingCartActivity.class);
                intent.putExtra("saleType", saleType);
                startActivityForResult(intent, REQUEST_REFRESH);
            }
        });

        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCollection) {
                    isCollection = false;
                    collection.setSelected(false);
                    deleteCollection();
                    Toast.makeText(ShopDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    isCollection = true;
                    collection.setSelected(true);
                    addCollection(goodsBean);
                    Toast.makeText(ShopDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (saleType == 1) {
            //加入退货清单
            RxView.clicks(shopDetail_addShoppingCart)
                    .compose(bindToLifecycle())
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(@NonNull Object o) throws Exception {
                            if (quantity > 0) {
                                RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                        .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.Id)))
                                        .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                                    @Override
                                    public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                        LogUtils.e("returnListItemModel.size()：" + returnListItemModels.size());
                                        if (returnListItemModels.size() > 0) {
                                            isExist = true;
                                            ReturnListItemModel mItem = returnListItemModels.get(0);
                                            mItem.delete().subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(@NonNull Boolean aBean) throws Exception {
                                                    LogUtils.e(mItem.ProductName + "：删除\n" + aBean);
                                                }
                                            });
                                        } else {
                                            isExist = false;
                                        }

                                    }
                                });
                                RXSQLite.rx(SQLite.select().from(ReturnGoodsUnitModel.class)
                                        .where(ReturnGoodsUnitModel_Table.ProductId.eq(goodsBean.Id)))
                                        .queryList()
                                        .subscribe(new Consumer<List<ReturnGoodsUnitModel>>() {
                                            @Override
                                            public void accept(@NonNull List<ReturnGoodsUnitModel> returnGoodsUnitModels) throws Exception {
                                                if (returnGoodsUnitModels != null && returnGoodsUnitModels.size() > 0) {
                                                    Flowable.fromIterable(returnGoodsUnitModels)
                                                            .forEach(new Consumer<ReturnGoodsUnitModel>() {
                                                                @Override
                                                                public void accept(@NonNull ReturnGoodsUnitModel returnGoodsUnitModel) throws Exception {
                                                                    ReturnGoodsUnitModel mUnitModel = returnGoodsUnitModel;
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
                                addReturnList(goodsBean, quantity, unitName, unitId, unitPrice, totalPrice, basicUnitPrice);
                                if (!isExist) {
                                    orderCount = orderCount + 1;
                                    shopDetail_titleView.setRightIconText(View.VISIBLE, orderCount);
                                    LogUtils.e("退货数量：" + orderCount);
                                }
                            } else {
                                Toast.makeText(ShopDetailActivity.this, "请输入退货数量", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
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
                                            isExist = true;
                                            SaleGoodsItemModel mItem = saleGoodsItemModels.get(0);
                                            mItem.delete().subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(@NonNull Boolean aBean) throws Exception {
                                                    LogUtils.e(mItem.ProductName + "：删除\n" + aBean);
                                                }
                                            });
                                        } else {
                                            isExist = false;
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
                                add(goodsBean, quantity, unitName, unitId, unitPrice, totalPrice, basicUnitPrice);
                                if (!isExist) {
                                    orderCount = orderCount + 1;
                                    shopDetail_titleView.setRightIconText(View.VISIBLE, orderCount);
                                    LogUtils.e("购物车数量：" + orderCount);
                                }
                            } else {
                                Toast.makeText(ShopDetailActivity.this, "请输入订购数量", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void add(GoodsListEntity.ValueEntity goodsBean, int count, String unitName, int unitId, double unitPrice, double totalPrice, double basicUnitPrice) {
        SaleGoodsItemModel itemModel = new SaleGoodsItemModel();
        itemModel.CurrentPrice = totalPrice;
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        }
        itemModel.PriceSystemId = DataSaver.getPriceSystemId();
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Code = goodsBean.Code;
        itemModel.ProductName = goodsBean.Name;
        itemModel.BasicUnitName = basicUnitName;
        itemModel.ProductUnitName = unitName;
        itemModel.BasicUnitPrice = basicUnitPrice;
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

    private void addReturnList(GoodsListEntity.ValueEntity goodsBean, int count, String unitName, int unitId, double unitPrice, double totalPrice, double basicUnitPrice) {
        ReturnListItemModel itemModel = new ReturnListItemModel();
        itemModel.CurrentPrice = totalPrice;
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        }
        itemModel.PriceSystemId = DataSaver.getPriceSystemId();
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Code = goodsBean.Code;
        itemModel.ProductName = goodsBean.Name;
        itemModel.BasicUnitName = basicUnitName;
        itemModel.ProductUnitName = unitName;
        itemModel.BasicUnitPrice = basicUnitPrice;
        itemModel.UnitPrice = unitPrice;
        itemModel.Discount = 1.0f;
        itemModel.SalesType = 2;
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
                        Toast.makeText(ShopDetailActivity.this, "已添加到退货清单", Toast.LENGTH_SHORT).show();
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
                    ReturnGoodsUnitModel goodsUnitModel = new ReturnGoodsUnitModel();
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

    private void addCollection(GoodsListEntity.ValueEntity goodsBean) {
        CollectionItemModel itemModel = new CollectionItemModel();
        itemModel.CurrentPrice = basicUnitPrice;
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        }
        itemModel.PriceSystemId = DataSaver.getPriceSystemId();
        itemModel.PackSpec = goodsBean.PackSpec;
        itemModel.Code = goodsBean.Code;
        itemModel.ProductName = goodsBean.Name;
        itemModel.BasicUnitName = basicUnitName;
        itemModel.ProductUnitName = basicUnitName;
        itemModel.BasicUnitPrice = basicUnitPrice;
        itemModel.UnitPrice = basicUnitPrice;
        itemModel.Discount = 1.0f;
        itemModel.SalesType = 1;
        itemModel.TaxRate = 1.0;
        itemModel.UnitId = basicUnitId;
        itemModel.Quantity = 0;
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
                        LogUtils.e("收藏Item插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                    }
                });
        itemModel.save().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                LogUtils.e("收藏Item保存数据成功");
            }
        });
    }

    public void deleteCollection() {
        RXSQLite.rx(SQLite.select().from(CollectionItemModel.class)
                .where(CollectionItemModel_Table.ProductId.eq(goodsBean.Id)))
                .queryList().subscribe(new Consumer<List<CollectionItemModel>>() {
            @Override
            public void accept(@NonNull List<CollectionItemModel> collectionItemModels) throws Exception {
                LogUtils.e("collectionItemModels.size()：" + collectionItemModels.size());
                if (collectionItemModels.size() > 0) {
                    CollectionItemModel mItem = collectionItemModels.get(0);
                    mItem.delete().subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBean) throws Exception {
                            LogUtils.e(mItem.ProductName + "：从收藏中删除\n" + aBean);
                        }
                    });
                }
            }
        });

    }

    public void showEmptyView() {
        scrollView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        addShopCart.setVisibility(View.GONE);
        emptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        emptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }
}
