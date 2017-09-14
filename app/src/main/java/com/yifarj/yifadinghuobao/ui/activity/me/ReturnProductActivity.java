package com.yifarj.yifadinghuobao.ui.activity.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.stetho.common.LogUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.GoodsListViewAdapter;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductMemoryPriceEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductPropertyListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.PageInfo;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.ui.activity.productCategory.ProductCategoryActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShopDetailActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShoppingCartActivity;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;
import com.yifarj.yifadinghuobao.utils.NumberUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanEditDialog;
import com.yifarj.yifadinghuobao.view.CzechYuanTitleView;
import com.yifarj.yifadinghuobao.view.NumberAddSubView;
import com.yifarj.yifadinghuobao.view.SearchView;
import com.yifarj.yifadinghuobao.view.utils.PriceSystemGenerator;
import com.yifarj.yifadinghuobao.vo.PriceSystem;
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
 * Created by zydx-pc on 2017/8/4.
 */

public class ReturnProductActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_REFRESH = 10;
    private static final int REQUEST_ITEM = 11;

    @BindView(R.id.lvContent)
    ListView lvContent;

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.titleView)
    CzechYuanTitleView titleView;

    @BindView(R.id.searchView)
    SearchView searchView;

    @BindView(R.id.search_all_layout)
    LinearLayout rlTab1;

    @BindView(R.id.search_name_layout)
    LinearLayout rlTab2;

    @BindView(R.id.search_code_layout)
    LinearLayout rlTab3;

    @BindView(R.id.search_price_layout)
    LinearLayout rlTab4;

    private int orderCount;

    private PageInfo pageInfo = new PageInfo();
    private boolean requesting;
    private boolean morePage = true;
    private GoodsListEntity goodsList;
    private GoodsListViewAdapter goodsListAdapter;

    private PageInfo searchPageInfo = new PageInfo();
    private boolean searchRequesting;
    private boolean searchMorePage = true;
    private GoodsListEntity searchGoodsList;
    private GoodsListViewAdapter searchGoodsListAdapter;

    private int itemPosition, shopId;
    private double shopQuantity = 0;
    private boolean isClearText = false;

    private boolean isExist = false;
    private boolean isCollection = false;
    private double quantity;
    private String unitName;
    private double totalPrice;
    private int unitId;
    private double unitPrice;
    private String basicUnitName;
    private double basicUnitPrice;
    private String initUnitName;
    private int basicUnitId;
    private PopupWindow mPopupWindow;
    private int priceSystemId;
    private int traderId;
    private boolean isSearchList;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_goods;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        priceSystemId = PreferencesUtil.getInt("PriceSystemId", -1);
        if (DataSaver.getMettingCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else {
            traderId = PreferencesUtil.getInt("TraderId", 0);
        }
        loadData();
        init();
    }

    private void init() {
        rlTab1.setOnClickListener(this);
        rlTab2.setOnClickListener(this);
        rlTab3.setOnClickListener(this);
        rlTab4.setOnClickListener(this);

        titleView.setLeftBackVisibility(View.VISIBLE);
        titleView.setLeftBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleView.setRightIconClickListener(view -> {
            Intent intent = new Intent(ReturnProductActivity.this, ShoppingCartActivity.class);
            intent.putExtra("saleType", 1);
            startActivityForResult(intent, REQUEST_REFRESH);
        });
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturnProductActivity.this, ProductCategoryActivity.class);
                intent.putExtra("saleType", 1);
                startActivityForResult(intent, REQUEST_REFRESH);
            }
        });
        titleView.setRlSearchClickListener(view -> {
            titleView.setVisibility(View.GONE);
            searchView.show();
        });
        searchView.setOnSearchClickListener(new SearchView.OnSearchClickListener() {
            @Override
            public void onSearch(String keyword) {
                doSearch(keyword);
            }
        });
        searchView.setOnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleView.setVisibility(View.VISIBLE);
                searchView.getListView().setAdapter(null);
                searchView.getEditText().setText("");
                searchGoodsList = null;
                searchPageInfo.PageIndex = -1;
                searchRequesting = false;
                searchMorePage = true;
            }
        });
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String result = s.toString();
                searchGoodsList = null;
                searchPageInfo.PageIndex = -1;
                searchRequesting = false;
                searchMorePage = true;
                if (!isClearText && StringUtils.isEmpty(result)) {
                    searchView.getListView().setAdapter(null);
                }
                if (!StringUtils.isEmpty(result)) {
                    if (result.length() == 13 || result.length() == 12 || result.length() == 8) {
                        doSearch(result);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setRightIcon(int visibility, int title) {
        titleView.setRightIconText(visibility, title);
    }

    private void showPopupWindow(GoodsListEntity.ValueEntity goodsEntity, int statusIcon, int saleType) {
        isExist = false;
        quantity = 0;
        unitName = "";
        totalPrice = 0;
        unitId = 0;
        unitPrice = 0;
        basicUnitName = "";
        basicUnitPrice = 0;
        initUnitName = "";
        basicUnitId = 0;
        isCollection = false;
        // 查询退货清单
        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<ReturnListItemModel>>() {
                    @Override
                    public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                        orderCount = returnListItemModels.size();
                        if (orderCount > 0) {
                            titleView.setRightIconText(View.VISIBLE, orderCount);
                            LogUtils.e("退货清单中的商品数量为：" + orderCount);
                        } else if (orderCount == 0) {
                            titleView.setRightIconText(View.GONE, 0);
                            LogUtils.e("退货清单中的商品数量为：" + orderCount);
                        }
                        LogUtils.e("returnListItemModels：" + returnListItemModels.size());
                    }
                });
        // 查询退货清单中是否有当前商品
        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where(ReturnListItemModel_Table.ProductId.eq(goodsEntity.Id)))
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
                            initUnitName = returnListItemModels.get(0).ProductUnitName;
                            basicUnitPrice = returnListItemModels.get(0).BasicUnitPrice;
                            basicUnitId = returnListItemModels.get(0).BasicUnitId;
                            isExist = true;
                        } else {
                            LogUtils.e("退货清单中没有此商品");
                        }
                    }
                });
        List<Integer> selectedUnitList = new ArrayList<>(new HashSet<>());
        List<Integer> selectedProperty1List = new ArrayList<>(new HashSet<>());
        List<Integer> selectedProperty2List = new ArrayList<>(new HashSet<>());
        List<ProductPropertyListEntity.ValueEntity> productPropery1 = new ArrayList<>();
        List<ProductPropertyListEntity.ValueEntity> productPropery2 = new ArrayList<>();
        View view = View.inflate(ReturnProductActivity.this, R.layout.popwindow_add_shoppingcart_view, null);
        ImageView itemImg = view.findViewById(R.id.item_img);
        TextView tvIcon = view.findViewById(R.id.tv_icon);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvPackSpec = view.findViewById(R.id.tv_PackSpec);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        TextView tvCode = view.findViewById(R.id.tv_Code);
        LinearLayout llProperty1 = view.findViewById(R.id.llProperty1);
        TextView tvProperty1 = view.findViewById(R.id.tvProperty1);
        TagFlowLayout tagFlProperty1 = view.findViewById(R.id.shopDetail_property1);
        LinearLayout llProperty2 = view.findViewById(R.id.llProperty2);
        TextView tvProperty2 = view.findViewById(R.id.tvProperty2);
        TagFlowLayout tagFlProperty2 = view.findViewById(R.id.shopDetail_property2);
        TagFlowLayout tagFlowLayoutUnit = view.findViewById(R.id.shopDetail_unit);
        NumberAddSubView addSubView = view.findViewById(R.id.shopDetail_orderNum);
        TextView tvTotalPrice = view.findViewById(R.id.shopDetail_totalPrice);
        ImageView ivCollection = view.findViewById(R.id.shopDetail_collection);
        ButtonBarLayout addShoppingCart = view.findViewById(R.id.shopDetail_addShoppingCart);

        // 查询收藏商品中是否有当前商品
        RXSQLite.rx(SQLite.select().from(CollectionItemModel.class).where(CollectionItemModel_Table.ProductId.eq(goodsEntity.Id)))
                .queryList()
                .subscribe(new Consumer<List<CollectionItemModel>>() {
                    @Override
                    public void accept(@NonNull List<CollectionItemModel> collectionItemModel) throws Exception {
                        if (collectionItemModel != null && collectionItemModel.size() > 0) {
                            LogUtils.e("收藏夹中有此商品：" + collectionItemModel.get(0).ProductName);
                            isCollection = true;
                            ivCollection.setSelected(true);
                        } else {
                            LogUtils.e("收藏夹中没有此商品");
                        }
                    }
                });


        ivCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCollection) {
                    isCollection = false;
                    ivCollection.setSelected(false);
                    deleteCollection(goodsEntity);
                    Toast.makeText(ReturnProductActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    isCollection = true;
                    ivCollection.setSelected(true);
                    addCollection(goodsEntity);
                    Toast.makeText(ReturnProductActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvName.setText(goodsEntity.Name);
        tvPackSpec.setText(goodsEntity.PackSpec);
        if (goodsEntity.Code.length() <= 6) {
            tvCode.setText("编号：" + goodsEntity.Code);
        } else {
            tvCode.setText("编号：" + goodsEntity.Code.substring(goodsEntity.Code.length() - 4, goodsEntity.Code.length()));
        }
        itemImg.setImageResource(R.drawable.default_image);
        if (goodsEntity.ProductPictureList != null && goodsEntity.ProductPictureList.size() > 0) {
            Glide.with(ReturnProductActivity.this)
                    .load(AppInfoUtil.genPicUrl(goodsEntity.ProductPictureList.get(0).Path))
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_image)
                    .dontAnimate()
                    .into(itemImg);
        }

        switch (statusIcon) {
            case 0:
                break;
            case 1:
                tvIcon.setVisibility(View.VISIBLE);
                tvIcon.setText("促销");
                break;
            case 2:
                tvIcon.setVisibility(View.VISIBLE);
                tvIcon.setText("新品");
                break;
            case 3:
                tvIcon.setVisibility(View.VISIBLE);
                tvIcon.setText("推荐");
                break;
        }

        //商品最小单位
        if (!isExist) {
            for (ProductUnitEntity.ValueEntity unit : goodsEntity.ProductUnitList) {
                if (unit.IsBasic) {
                    unitName = unit.Name;
                    basicUnitName = unit.Name;
                    basicUnitId = unit.Id;
                    unitId = unit.Id;
                    basicUnitPrice = goodsEntity.MemoryPrice;
                    unitPrice = basicUnitPrice;
                    getSelectedUnitPrice(goodsEntity, unitId, 0, unit.BasicFactor, tvTotalPrice);
                }
            }
        }
        //货品订购数量
        addSubView.setValue(quantity);
        //货品价格
        tvPrice.setText(basicUnitPrice + "元/" + basicUnitName);
        //货品总价
        totalPrice = unitPrice * quantity;
        tvTotalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");

        //商品单位
        final LayoutInflater mInflater = LayoutInflater.from(ReturnProductActivity.this);
        TagAdapter<ProductUnitEntity.ValueEntity> tagAdapterUnit = new TagAdapter<ProductUnitEntity.ValueEntity>(goodsEntity.ProductUnitList) {
            @Override
            public View getView(FlowLayout parent, int position, ProductUnitEntity.ValueEntity productUnitEntity) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv, parent, false);
                tv.setText(productUnitEntity.Name);
                return tv;
            }
        };
        tagFlowLayoutUnit.setAdapter(tagAdapterUnit);

        tagFlowLayoutUnit.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                selectedUnitList.clear();
                if (selectPosSet.size() > 0) {
                    selectedUnitList.addAll(selectPosSet);
                    int select = 0;
                    for (Integer item : selectPosSet) {
                        select = item;
                    }
                    unitName = goodsEntity.ProductUnitList.get(select).Name;
                    unitId = goodsEntity.ProductUnitList.get(select).Id;
                    getSelectedUnitPrice(goodsEntity, unitId, select, goodsEntity.ProductUnitList.get(select).BasicFactor, tvTotalPrice);
                } else {
                    setSelectedUnit(tagAdapterUnit, selectedUnitList, goodsEntity, tvTotalPrice);
                }
            }
        });
        setSelectedUnit(tagAdapterUnit, selectedUnitList, goodsEntity, tvTotalPrice);

        //商品多属性
        if (goodsEntity.ProperyId1 != 0 && goodsEntity.ProperyId2 != 0) {
            tvProperty1.setText(goodsEntity.ProperyId1Name);
            tvProperty2.setText(goodsEntity.ProperyId2Name);
            getPropertyList1(true, llProperty1, llProperty2, tagFlProperty1, tagFlProperty2, goodsEntity, productPropery1, productPropery2, selectedProperty1List, selectedProperty2List);
        } else {
            llProperty1.setVisibility(View.GONE);
            llProperty2.setVisibility(View.GONE);
        }

        //订购数量增加/减少的点击事件
        addSubView.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonAddClick(View view, double value) {
                if (value > 0) {
                    quantity = value;
                    totalPrice = unitPrice * quantity;
                    tvTotalPrice.setText(NumberUtil.formatDouble2String(totalPrice) + "元");
                }
            }

            @Override
            public void onButtonSubClick(View view, double value) {
                if (value > 0) {
                    quantity = value;
                    totalPrice = unitPrice * quantity;
                    tvTotalPrice.setText(NumberUtil.formatDouble2String(totalPrice) + "元");
                } else {
                    Toast.makeText(ReturnProductActivity.this, "订购数量必须大于0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //订购数量的编辑事件
        addSubView.setOnNumberEditClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CzechYuanEditDialog mDialog = new CzechYuanEditDialog(ReturnProductActivity.this, R.style.CzechYuanDialog);
                mDialog.getEditText().setText(NumberUtil.formatDouble2String(quantity));
                mDialog.getEditText().setSelection(0, NumberUtil.formatDouble2String(quantity).length());
                mDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double count = 1;
                        try {
                            double tempEditText = Double.parseDouble(mDialog.getEditText().getText().toString().trim());
                            if (tempEditText <= 0) {
                                Toast.makeText(ReturnProductActivity.this, "订购数量必须大于0", Toast.LENGTH_SHORT).show();
                            } else {
                                count = tempEditText;
                            }
                        } catch (NumberFormatException e) {
                            count = quantity;
                        }
                        if (count != quantity) {
                            quantity = count;
                            totalPrice = unitPrice * count;
                            tvTotalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
                            addSubView.setValue(count);
                        }
                        closeKeybord(mDialog.getEditText(), ReturnProductActivity.this);
                    }
                });
                mDialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeKeybord(mDialog.getEditText(), ReturnProductActivity.this);
                    }
                });
                openKeybord(mDialog.getEditText(), ReturnProductActivity.this);
            }
        });

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        mPopupWindow = new PopupWindow(view, com.czechyuan.imagepicker.util.Utils.getScreenPix(ReturnProductActivity.this).widthPixels * 2 / 3 + 100, LinearLayout.LayoutParams.MATCH_PARENT, true);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
                return false;
            }
        });
        //加入购物车
        RxView.clicks(addShoppingCart)
                .compose(bindToLifecycle())
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (quantity > 0) {
                            addReturnList(goodsEntity, quantity, unitName, unitId, unitPrice, totalPrice, basicUnitPrice, selectedProperty1List, selectedProperty2List, productPropery1, productPropery2);
                            if (!isExist) {
                                orderCount = orderCount + 1;
                                titleView.setRightIconText(View.VISIBLE, orderCount);
                                LogUtils.e("购物车数量：" + orderCount);
                            }
                        } else {
                            Toast.makeText(ReturnProductActivity.this, "请输入订购数量", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.MenuAnimationLeftRight);
        mPopupWindow.showAtLocation(titleView, Gravity.RIGHT, 0, 0);
    }

    public void deleteCollection(GoodsListEntity.ValueEntity goodsBean) {
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

    private void addReturnList(GoodsListEntity.ValueEntity goodsBean, double count, String unitName, int unitId, double unitPrice, double totalPrice, double basicUnitPrice, List<Integer> selectedProperty1List, List<Integer> selectedProperty2List, List<ProductPropertyListEntity.ValueEntity> productPropery1, List<ProductPropertyListEntity.ValueEntity> productPropery2) {
        if (productPropery1.size() > 0 && productPropery2.size() > 0 && selectedProperty1List.size() > 0 && selectedProperty2List.size() > 0) {
            for (int property1Position : selectedProperty1List) {
                for (int property2Position : selectedProperty2List) {
                    createReturnGoodsItem(goodsBean, count, unitName, unitId, unitPrice, totalPrice, basicUnitPrice, productPropery1.get(property1Position).Id, productPropery2.get(property2Position).Id, productPropery1.get(property1Position).Name, productPropery2.get(property2Position).Name, true);
                }
            }
        } else {
            createReturnGoodsItem(goodsBean, count, unitName, unitId, unitPrice, totalPrice, basicUnitPrice, 0, 0, null, null, false);
        }
        searchSQlite(goodsBean.Id);
        if (isSearchList) {
            searchGoodsListAdapter.notifyDataSetChanged();
        } else {
            goodsListAdapter.notifyDataSetChanged();
        }

        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private void createReturnGoodsItem(GoodsListEntity.ValueEntity goodsBean, double count, String unitName, int unitId, double unitPrice, double totalPrice, double basicUnitPrice, int property1Id, int property2Id, String property1IdName, String property2IdName, boolean isProperty) {
        ReturnListItemModel itemModel = new ReturnListItemModel();
        itemModel.CurrentPrice = totalPrice;
        if (goodsBean.ProductPictureList != null && goodsBean.ProductPictureList.size() > 0) {
            itemModel.Path = goodsBean.ProductPictureList.get(0).Path;
        }
        if (isProperty) {
            itemModel.ParentProperyId1Name = goodsBean.ProperyId1Name;
            itemModel.ParentProperyId2Name = goodsBean.ProperyId2Name;
            itemModel.ParentProperyId1 = goodsBean.ProperyId1;
            itemModel.ParentProperyId2 = goodsBean.ProperyId2;
            itemModel.ProperyId1 = property1Id;
            itemModel.ProperyId2 = property2Id;
            itemModel.ProperyId1Name = property1IdName;
            itemModel.ProperyId2Name = property2IdName;
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
                        Toast.makeText(ReturnProductActivity.this, "已添加到退货清单", Toast.LENGTH_SHORT).show();
                        LogUtils.e("Item插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                    }
                });
        itemModel.save().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                LogUtils.e("Item保存数据成功");
            }
        });

        RXSQLite.rx(SQLite.select().from(ReturnGoodsUnitModel.class)
                .where(ReturnGoodsUnitModel_Table.ProductId.eq(goodsBean.Id)))
                .queryList()
                .subscribe(new Consumer<List<ReturnGoodsUnitModel>>() {
                    @Override
                    public void accept(@NonNull List<ReturnGoodsUnitModel> goodsUnitModels) throws Exception {
                        if (goodsUnitModels == null || goodsUnitModels.size() == 0) {
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
                    }
                });
    }


    private void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private void getPropertyList1(boolean isProperty1, LinearLayout llProperty1, LinearLayout llProperty2, TagFlowLayout tagFlProperty1, TagFlowLayout tagFlProperty2, GoodsListEntity.ValueEntity goodsEntity, List<ProductPropertyListEntity.ValueEntity> productPropery1, List<ProductPropertyListEntity.ValueEntity> productPropery2, List<Integer> selectedProperty1List, List<Integer> selectedProperty2List) {
        if (goodsEntity == null) {
            return;
        }
        RetrofitHelper
                .getPropertyListApi()
                .getPropertyList("ProductProperyList", "", "ParentId =" + (isProperty1 ? goodsEntity.ProperyId1 : goodsEntity.ProperyId2), "", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProductPropertyListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ProductPropertyListEntity entity) {
                        if (!entity.HasError && entity.Value != null && entity.Value.size() > 0) {
                            if (isProperty1) {
                                productPropery1.addAll(entity.Value);
                                final LayoutInflater mInflater = LayoutInflater.from(ReturnProductActivity.this);
                                TagAdapter<ProductPropertyListEntity.ValueEntity> tagAdapter = new TagAdapter<ProductPropertyListEntity.ValueEntity>(productPropery1) {
                                    @Override
                                    public View getView(FlowLayout parent, int position, ProductPropertyListEntity.ValueEntity entity) {
                                        TextView tv = (TextView) mInflater.inflate(R.layout.tv, parent, false);
                                        tv.setText(entity.Name);
                                        return tv;
                                    }
                                };
                                tagFlProperty1.setAdapter(tagAdapter);
                                tagFlProperty1.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                                    @Override
                                    public void onSelected(Set<Integer> selectPosSet) {
                                        selectedProperty1List.clear();
                                        if (selectPosSet.size() > 0) {
                                            selectedProperty1List.addAll(selectPosSet);
                                        }
                                    }
                                });
                                tagAdapter.setSelectedList(0);
                                selectedProperty1List.add(0);
                                getPropertyList1(false, llProperty1, llProperty2, tagFlProperty1, tagFlProperty2, goodsEntity, productPropery1, productPropery2, selectedProperty1List, selectedProperty2List);
                            } else {
                                productPropery2.addAll(entity.Value);
                                final LayoutInflater mInflater = LayoutInflater.from(ReturnProductActivity.this);
                                TagAdapter<ProductPropertyListEntity.ValueEntity> tagAdapter = new TagAdapter<ProductPropertyListEntity.ValueEntity>(productPropery2) {
                                    @Override
                                    public View getView(FlowLayout parent, int position, ProductPropertyListEntity.ValueEntity entity) {
                                        TextView tv = (TextView) mInflater.inflate(R.layout.tv, parent, false);
                                        tv.setText(entity.Name);
                                        return tv;
                                    }
                                };
                                tagFlProperty2.setAdapter(tagAdapter);
                                tagFlProperty2.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                                    @Override
                                    public void onSelected(Set<Integer> selectPosSet) {
                                        selectedProperty2List.clear();
                                        if (selectPosSet.size() > 0) {
                                            selectedProperty2List.addAll(selectPosSet);
                                        }
                                    }
                                });
                                tagAdapter.setSelectedList(0);
                                selectedProperty2List.add(0);
                            }
                        } else {
                            llProperty1.setVisibility(View.GONE);
                            llProperty2.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e("获取商品多属性1失败，重试");
                        getPropertyList1(isProperty1, llProperty1, llProperty2, tagFlProperty1, tagFlProperty2, goodsEntity, productPropery1, productPropery2, selectedProperty1List, selectedProperty2List);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 获取记忆价格
     */
    private void getProductMemoryPrice(int productUnitId, int select, GoodsListEntity.ValueEntity goodsBean, TextView tvTotalPrice) {
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
                            setGoodsItemPriceWithUnitAndPriceSystem(memoryPrice, select, productUnitId, goodsBean, tvTotalPrice);
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
    private void setGoodsItemPriceWithUnitAndPriceSystem(double memoryPrice, int select, int productUnitId, GoodsListEntity.ValueEntity goodsBean, TextView tvtvTotalPrice) {
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
        tvtvTotalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
    }

    private void getSelectedUnitPrice(GoodsListEntity.ValueEntity goodsBean, int productUnitId, int select, double basicFactor, TextView tvtvTotalPrice) {
        if (priceSystemId == -1) {
            getProductMemoryPrice(productUnitId, select, goodsBean, tvtvTotalPrice);
        } else {
            switch (priceSystemId) {
                case 1:
                    basicUnitPrice = goodsBean.Price1;
                    break;
                case 2:
                    basicUnitPrice = goodsBean.Price2;
                    break;
                case 3:
                    basicUnitPrice = goodsBean.Price3;
                    break;
                case 4:
                    basicUnitPrice = goodsBean.Price4;
                    break;
                case 5:
                    basicUnitPrice = goodsBean.Price5;
                    break;
                case 6:
                    basicUnitPrice = goodsBean.Price6;
                    break;
                case 7:
                    basicUnitPrice = goodsBean.Price7;
                    break;
                case 8:
                    basicUnitPrice = goodsBean.Price8;
                    break;
                case 9:
                    basicUnitPrice = goodsBean.Price9;
                    break;
                case 10:
                    basicUnitPrice = goodsBean.Price10;
                    break;
            }
            unitPrice = basicUnitPrice * basicFactor;
            totalPrice = quantity * unitPrice;
            tvtvTotalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
        }
    }


    private void setSelectedUnit(final TagAdapter<ProductUnitEntity.ValueEntity> tagAdapter, List<Integer> selectedUnitList, GoodsListEntity.ValueEntity goodsBean, TextView tvTotalPrice) {
        selectedUnitList.clear();
        if (isExist) {
            Flowable.fromIterable(goodsBean.ProductUnitList)
                    .forEach(new Consumer<ProductUnitEntity.ValueEntity>() {
                        @Override
                        public void accept(@NonNull ProductUnitEntity.ValueEntity valueEntity) throws Exception {
                            if (initUnitName.equals(valueEntity.Name)) {
                                int unitPosition = goodsBean.ProductUnitList.indexOf(valueEntity);
                                tagAdapter.setSelectedList(unitPosition);
                                selectedUnitList.add(goodsBean.ProductUnitList.indexOf(valueEntity));
                                unitName = goodsBean.ProductUnitList.get(unitPosition).Name;
                                unitId = goodsBean.ProductUnitList.get(unitPosition).Id;
                                /*unitPrice = goodsBean.ProductUnitList.get(unitPosition).BasicFactor * basicUnitPrice;
                                totalPrice = quantity * unitPrice;*/
//                                getProductMemoryPrice(unitId, unitPosition);
                                getSelectedUnitPrice(goodsBean, unitId, unitPosition, goodsBean.ProductUnitList.get(unitPosition).BasicFactor, tvTotalPrice);
                                tvTotalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
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
//            getProductMemoryPrice(unitId, 0);
            getSelectedUnitPrice(goodsBean, unitId, 0, goodsBean.ProductUnitList.get(0).BasicFactor, tvTotalPrice);
            tvTotalPrice.setText(NumberUtil.formatDoubleToString(totalPrice) + "元");
            LogUtils.e(goodsBean.Name + "：修改单位" + unitName);
        }
    }


    private void doSearch(String keyword) {
        if (searchRequesting) {
            return;
        }
        isSearchList = true;
        searchRequesting = true;
        ++searchPageInfo.PageIndex;
        int traderId = PreferencesUtil.getInt("TraderId", 0);
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(searchPageInfo), "((name like '%" + keyword + "%' or right(Code,4) like '%" + keyword + "%'" + "or Mnemonic like '%" + keyword + "%' or id in (select productid from TB_ProductBarcode where Barcode like '%" + keyword + "%' and len('" + keyword + "')>=8)) and  status not in(4,8))", "[" + traderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity entity) {
                        if (searchGoodsList == null) {
                            if (!entity.HasError) {
                                if (entity.Value != null && entity.Value.size() > 0) {
                                    searchGoodsList = entity;
                                    searchGoodsListAdapter = new GoodsListViewAdapter(searchGoodsList.Value, null, 0, ReturnProductActivity.this, true, 1);
                                    searchView.getListView().setAdapter(searchGoodsListAdapter);
                                    searchView.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            itemPosition = position;
                                            shopId = searchGoodsList.Value.get(position).Id;
                                            Intent intent = new Intent(ReturnProductActivity.this, ShopDetailActivity.class);
                                            intent.putExtra("shoppingId", searchGoodsList.Value.get(position).Id);
                                            intent.putExtra("saleType", 1);
                                            startActivityForResult(intent, REQUEST_ITEM);
                                            isClearText = true;
                                            searchView.clearText();
                                            isClearText = false;
                                        }
                                    });
                                    if (entity.Value.size() == 1) {
                                        itemPosition = 0;
                                        shopId = searchGoodsList.Value.get(0).Id;
                                        Intent intent = new Intent(ReturnProductActivity.this, ShopDetailActivity.class);
                                        intent.putExtra("shoppingId", searchGoodsList.Value.get(0).Id);
                                        intent.putExtra("saleType", 1);
                                        startActivityForResult(intent, REQUEST_ITEM);
                                        isClearText = true;
                                        searchView.clearText();
                                        isClearText = false;
                                    }
                                    searchView.getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                                        }

                                        @Override
                                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                            if ((visibleItemCount + firstVisibleItem == totalItemCount)
                                                    && !searchRequesting && searchMorePage && searchGoodsList != null && !searchGoodsListAdapter.onbind) {
                                                doSearch(keyword);
                                            }
                                        }
                                    });
                                    searchGoodsListAdapter.setAddShoppingCartClickListener(new GoodsListViewAdapter.AddShoppingCartClickListener() {

                                        @Override
                                        public void onAddShoppingCartClickListener(View view, GoodsListEntity.ValueEntity goodsEntity, int statusIcon, int saleType) {
                                            showPopupWindow(goodsEntity, statusIcon, saleType);
                                        }
                                    });
                                } else {
                                    searchView.getListView().setAdapter(null);
                                    ToastUtils.showShortSafe("无结果");
                                }
                            } else {
                                searchView.getListView().setAdapter(null);
                                ToastUtils.showShortSafe(entity.Information == null ? "无结果" : entity.Information);
                            }
                        } else if (entity != null && entity.Value.size() > 0) {
                            if (searchGoodsList != null && searchGoodsListAdapter != null) {
                                searchGoodsList.Value.addAll(entity.Value);
                                searchGoodsListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            searchMorePage = false;
                            ToastUtils.showShortSafe("已全部加载");
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.showShortSafe("当前网络不可用,请检查网络设置");
                        searchRequesting = false;
                        --searchPageInfo.PageIndex;
                    }

                    @Override
                    public void onComplete() {
                        searchRequesting = false;
                    }
                });
    }


    //    @Override
    //    protected void initRefreshLayout() {
    //        if (mSwipeRefreshLayout == null) {
    //            return;
    //        }
    //        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
    ////        mSwipeRefreshLayout.setColorSchemeResources(R.color.light_blue);
    //        mSwipeRefreshLayout.post(() -> {
    //
    //            mSwipeRefreshLayout.setRefreshing(true);
    //            mIsRefreshing = true;
    //            loadData();
    //        });

    //
    //        mSwipeRefreshLayout.setOnRefreshListener(() -> {
    //            try {
    //                pageInfo.PageIndex = 0;
    //                mIsRefreshing = false;
    //                goodsList.clear();
    //                loadData();
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        });
    //    }

    @Override
    public void loadData() {
        // 查询退货清单商品
        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where())
                .queryList()
                .subscribe(new Consumer<List<ReturnListItemModel>>() {
                    @Override
                    public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                        orderCount = returnListItemModels.size();
                        if (orderCount > 0) {
                            titleView.setRightIconText(View.VISIBLE, orderCount);
                            LogUtils.e("orderCount：" + orderCount);
                        } else if (orderCount == 0) {
                            titleView.setRightIconText(View.GONE, 0);
                            LogUtils.e("orderCount：" + orderCount);
                        }
                        LogUtils.e("returnListItemModels：" + returnListItemModels.size());
                    }
                });
        LogUtils.e("loadData", "获取商品列表数据");
        onTab1Click();
        rlTab1.setSelected(true);
    }

    private void getGoodsList() {
        if (requesting) {
            return;
        }
        requesting = true;
        ++pageInfo.PageIndex;
        int traderId = PreferencesUtil.getInt("TraderId", 0);
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(pageInfo), "status  not in (4,8)", "[" + traderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
                        if (goodsList == null) {
                            if (!goodsListEntity.HasError) {
                                if (goodsListEntity.Value != null && goodsListEntity.Value.size() > 0) {
                                    goodsList = goodsListEntity;
                                    goodsListAdapter = new GoodsListViewAdapter(goodsList.Value, null, 0, ReturnProductActivity.this, true, 1);
                                    lvContent.setAdapter(goodsListAdapter);
                                    lvContent.setOnItemClickListener((parent, view, position, id) -> {
                                        if (goodsList != null && goodsList.Value != null && goodsList.Value.size() > 0 && goodsList.Value.get(position) != null) {
                                            itemPosition = position;
                                            shopId = goodsList.Value.get(position).Id;
                                            Intent intent = new Intent(ReturnProductActivity.this, ShopDetailActivity.class);
                                            intent.putExtra("shoppingId", goodsList.Value.get(position).Id);
                                            intent.putExtra("saleType", 1);
                                            startActivityForResult(intent, REQUEST_ITEM);
                                        }
                                    });
                                    goodsListAdapter.setAddShoppingCartClickListener(new GoodsListViewAdapter.AddShoppingCartClickListener() {

                                        @Override
                                        public void onAddShoppingCartClickListener(View view, GoodsListEntity.ValueEntity goodsEntity, int statusIcon, int saleType) {
                                            showPopupWindow(goodsEntity, statusIcon, saleType);
                                        }
                                    });
                                    lvContent.setOnScrollListener(new AbsListView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                                        }

                                        @Override
                                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                            if ((visibleItemCount + firstVisibleItem == totalItemCount)
                                                    && !requesting && morePage && goodsList != null && !goodsListAdapter.onbind) {
                                                getGoodsList();
                                            }
                                        }
                                    });
                                } else {
                                    showEmptyView();
                                }
                            } else {
                                ToastUtils.showShortSafe("请求超时");
                            }
                        } else if (goodsListEntity != null && goodsListEntity.Value.size() > 0) {
                            goodsList.Value.addAll(goodsListEntity.Value);
                            goodsListAdapter.notifyDataSetChanged();
                        } else {
                            morePage = false;
                            ToastUtils.showShortSafe("已全部加载");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e("获取商品列表数据失败", e.getMessage());
                        requesting = false;
                        showEmptyView();
                        --pageInfo.PageIndex;
                    }

                    @Override
                    public void onComplete() {
                        requesting = false;
                    }
                });
    }


    public void showEmptyView() {
        if (mCustomEmptyView == null || lvContent == null) {
            return;
        }
        mCustomEmptyView.setVisibility(View.VISIBLE);
        lvContent.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
    }


    public void hideEmptyView() {
        if (mCustomEmptyView == null || lvContent == null) {
            return;
        }
        mCustomEmptyView.setVisibility(View.GONE);
        lvContent.setVisibility(View.VISIBLE);
    }

    public void searchSQlite(int productId) {
        // 查询退货清单中是否有当前商品
        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class).where(ReturnListItemModel_Table.ProductId.eq(productId)))
                .queryList()
                .subscribe(new Consumer<List<ReturnListItemModel>>() {
                    @Override
                    public void accept(@NonNull List<ReturnListItemModel> returnListItemModel) throws Exception {
                        if (returnListItemModel != null && returnListItemModel.size() > 0) {
                            LogUtils.e("退货清单中有此商品：" + returnListItemModel.get(0).ProductName);
                            shopQuantity = returnListItemModel.get(0).Quantity;
                        } else {
                            shopQuantity = 0;
                            LogUtils.e("退货清单中没有此商品");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH) {
            onTab1Click();
        } else if (requestCode == REQUEST_ITEM) {
            searchSQlite(shopId);
            if (isSearchList) {
                if (itemPosition == 0) {
                    searchGoodsListAdapter.notifyDataSetChanged();
                } else {
                    searchGoodsListAdapter.updataView(itemPosition, shopQuantity, searchView.getListView());
                }
            } else {
                if (itemPosition == 0) {
                    goodsListAdapter.notifyDataSetChanged();
                } else {
                    goodsListAdapter.updataView(itemPosition, shopQuantity, lvContent);
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_all_layout:
                LogUtil.e("全部");
                onTab1Click();
                break;
            case R.id.search_name_layout:
                onTab2Click();
                LogUtil.e("品名");
                break;
            case R.id.search_code_layout:
                LogUtil.e("货号");
                onTab3Click();
                break;
            case R.id.search_price_layout:
                LogUtil.e("价格");
                onTab4Click();
                break;
        }
    }

    private void onTab1Click() {
        setCheckedItem(0);
        goodsList = null;
        pageInfo.PageIndex = -1;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "CreatedTime";
        morePage = true;
        getGoodsList();
    }

    private void onTab2Click() {
        setCheckedItem(1);
        goodsList = null;
        pageInfo.PageIndex = 0;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "Name";
        morePage = true;
        getGoodsList();
    }

    private void onTab3Click() {
        setCheckedItem(2);
        goodsList = null;
        pageInfo.PageIndex = 0;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "Code";
        morePage = true;
        getGoodsList();
    }

    private void onTab4Click() {
        setCheckedItem(3);
        goodsList = null;
        pageInfo.PageIndex = 0;
        pageInfo.SortOrder = 2;
        pageInfo.SortedColumn = "Price0";
        morePage = true;
        getGoodsList();
    }

    public void setCheckedItem(int position) {
        rlTab1.setSelected(false);
        rlTab2.setSelected(false);
        rlTab3.setSelected(false);
        rlTab4.setSelected(false);
        switch (position) {
            case 0:
                rlTab1.setSelected(true);
                break;
            case 1:
                rlTab2.setSelected(true);
                break;
            case 2:
                rlTab3.setSelected(true);
                break;
            case 3:
                rlTab4.setSelected(true);
                break;
        }
    }

}

