package com.yifarj.yifadinghuobao.ui.fragment.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.stetho.common.LogUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.GoodsListAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.adapter.helper.EndlessRecyclerOnScrollListener;
import com.yifarj.yifadinghuobao.adapter.helper.HeaderViewRecyclerAdapter;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel;
import com.yifarj.yifadinghuobao.database.model.CollectionItemModel_Table;
import com.yifarj.yifadinghuobao.database.model.GoodsPropertyModel;
import com.yifarj.yifadinghuobao.database.model.GoodsPropertyModel_Table;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductMemoryPriceEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductPropertyListEntity;
import com.yifarj.yifadinghuobao.model.entity.ProductUnitEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.PageInfo;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.network.utils.JsonUtils;
import com.yifarj.yifadinghuobao.ui.activity.main.CollectionActivity;
import com.yifarj.yifadinghuobao.ui.activity.main.NewProductActivity;
import com.yifarj.yifadinghuobao.ui.activity.main.PromotionActivity;
import com.yifarj.yifadinghuobao.ui.activity.main.RecommendActivity;
import com.yifarj.yifadinghuobao.ui.activity.shoppingcart.ShopDetailActivity;
import com.yifarj.yifadinghuobao.ui.activity.web.WebActivity;
import com.yifarj.yifadinghuobao.ui.fragment.base.BaseFragment;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.DateUtil;
import com.yifarj.yifadinghuobao.utils.NumberUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.utils.ScreenUtil;
import com.yifarj.yifadinghuobao.view.AutoScrollViewPager;
import com.yifarj.yifadinghuobao.view.CustomEmptyView;
import com.yifarj.yifadinghuobao.view.CzechYuanEditDialog;
import com.yifarj.yifadinghuobao.view.NumberAddSubView;
import com.yifarj.yifadinghuobao.view.TitleView;
import com.yifarj.yifadinghuobao.view.ViewPagerIndicator;
import com.yifarj.yifadinghuobao.view.utils.DividerItemDecoration;
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
 * TabMainFragment
 *
 * @auther Czech.Yuan
 * @date 2017/5/12 15:07
 */
public class TabMainFragment extends BaseFragment {
    private static final int REQUEST_ITEM = 11;

    //    @BindView(R.id.viewpager)
    AutoScrollViewPager viewPager;

    //    @BindView(R.id.indicator)
    ViewPagerIndicator indicator;

    //    @BindView(R.id.rlPagerContainer)
    RelativeLayout rlPagerContainer;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.title)
    TitleView titleView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    //    @BindView(R.id.tvPromotion)
    TextView tvPromotion;
    //    @BindView(R.id.tvCollection)
    TextView tvCollection;
    //    @BindView(R.id.tvNewProduct)
    TextView tvNewProduct;
    //    @BindView(R.id.tvRecommend)
    TextView tvRecommend;

    private boolean mIsRefreshing = false;

    private View loadMoreView;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private List<GoodsListEntity.ValueEntity> goodsList = new ArrayList<>();

    private PageInfo pageInfo = new PageInfo();

    private GoodsListAdapter mGoodsListAdapter;

    private int itemPosition;

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

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void finishCreateView(Bundle savedInstanceState) {
        isPrepared = true;
        priceSystemId = PreferencesUtil.getInt("PriceSystemId", -1);
        if (DataSaver.getMettingCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else {
            traderId = PreferencesUtil.getInt("TraderId", 0);
        }
        lazyLoad();
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared && !isVisible) {
            LogUtils.e("TabGoodsFragment", "lazyLoad（） false");
            return;
        }
        goodsList.clear();
        pageInfo = new PageInfo();
        initRefreshLayout();
        initRecyclerView();
        mRecyclerView.scrollToPosition(0);
        isPrepared = false;

    }

    @Override
    protected void initRefreshLayout() {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        //        mSwipeRefreshLayout.setColorSchemeResources(R.color.light_blue);
        mSwipeRefreshLayout.post(() -> {

            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
//            goodsList.clear();
//            pageInfo.PageIndex = 0;
//            mIsRefreshing = true;
//            loadData();
        });
    }

    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mGoodsListAdapter = new GoodsListAdapter(mRecyclerView, goodsList, true, null, null, 0, 0);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mGoodsListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.recyclerview_divider_goods));
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        setRecycleNoScroll();
        createHeadView();
        createLoadMoreView();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {

            @Override
            public void onLoadMore(int i) {
                loadMoreView.setVisibility(View.VISIBLE);
                mIsRefreshing = true;
                loadData();
            }
        });
        mGoodsListAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder) {
                if (holder != null && position < goodsList.size()) {
                    itemPosition = position;
                    Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                    intent.putExtra("shoppingId", goodsList.get(position).Id);
                    intent.putExtra("saleType", 0);
                    startActivityForResult(intent, REQUEST_ITEM);
                }
            }
        });
        mGoodsListAdapter.setAddShoppingCartClickListener(new GoodsListAdapter.AddShoppingCartClickListener() {

            @Override
            public void onAddShoppingCartClickListener(View view, GoodsListEntity.ValueEntity goodsEntity, int statusIcon, int saleType) {
                if (goodsEntity != null) {
                    showPopupWindow(goodsEntity, statusIcon, saleType);
                }
            }
        });
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
        // 查询购物车中是否有当前商品
        RXSQLite.rx(SQLite.select().from(SaleGoodsItemModel.class).where(SaleGoodsItemModel_Table.ProductId.eq(goodsEntity.Id)))
                .queryList()
                .subscribe(new Consumer<List<SaleGoodsItemModel>>() {
                    @Override
                    public void accept(@NonNull List<SaleGoodsItemModel> saleGoodsItemModel) throws Exception {
                        if (saleGoodsItemModel != null && saleGoodsItemModel.size() > 0) {
                            LogUtils.e("购物车有此商品：" + saleGoodsItemModel.get(0).ProductName);
                            quantity = saleGoodsItemModel.get(0).Quantity;
                            unitName = saleGoodsItemModel.get(0).ProductUnitName;
                            initUnitName = saleGoodsItemModel.get(0).ProductUnitName;
                            totalPrice = saleGoodsItemModel.get(0).TotalPrice;
                            unitId = saleGoodsItemModel.get(0).UnitId;
                            unitPrice = saleGoodsItemModel.get(0).UnitPrice;
                            basicUnitName = saleGoodsItemModel.get(0).BasicUnitName;
                            basicUnitPrice = saleGoodsItemModel.get(0).BasicUnitPrice;
                            basicUnitId = saleGoodsItemModel.get(0).BasicUnitId;
                            isExist = true;
                        } else {
                            LogUtils.e("购物车没有此商品");
                        }
                    }
                });
        List<Integer> selectedUnitList = new ArrayList<>(new HashSet<>());
        List<Integer> selectedProperty1List = new ArrayList<>(new HashSet<>());
        List<Integer> selectedProperty2List = new ArrayList<>(new HashSet<>());
        List<ProductPropertyListEntity.ValueEntity> productPropery1 = new ArrayList<>();
        List<ProductPropertyListEntity.ValueEntity> productPropery2 = new ArrayList<>();
        View view = View.inflate(getActivity(), R.layout.popwindow_add_shoppingcart_view, null);
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
                    Toast.makeText(getActivity(), "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    isCollection = true;
                    ivCollection.setSelected(true);
                    addCollection(goodsEntity);
                    Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
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
            Glide.with(getActivity())
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
        final LayoutInflater mInflater = LayoutInflater.from(getActivity());
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
                    Toast.makeText(getActivity(), "订购数量必须大于0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //订购数量的编辑事件
        addSubView.setOnNumberEditClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CzechYuanEditDialog mDialog = new CzechYuanEditDialog(getActivity(), R.style.CzechYuanDialog);
                mDialog.getEditText().setText(NumberUtil.formatDouble2String(quantity));
                mDialog.getEditText().setSelection(0, NumberUtil.formatDouble2String(quantity).length());
                mDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double count = 1;
                        try {
                            double tempEditText = Double.parseDouble(mDialog.getEditText().getText().toString().trim());
                            if (tempEditText <= 0) {
                                Toast.makeText(getActivity(), "订购数量必须大于0", Toast.LENGTH_SHORT).show();
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
                        closeKeybord(mDialog.getEditText(), getActivity());
                    }
                });
                mDialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeKeybord(mDialog.getEditText(), getActivity());
                    }
                });
                openKeybord(mDialog.getEditText(), getActivity());
            }
        });

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        mPopupWindow = new PopupWindow(view, com.czechyuan.imagepicker.util.Utils.getScreenPix(getActivity()).widthPixels * 2 / 3 + 100, LinearLayout.LayoutParams.MATCH_PARENT, true);
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
                            add(goodsEntity, quantity, unitName, unitId, unitPrice, totalPrice, basicUnitPrice, selectedProperty1List, selectedProperty2List, productPropery1, productPropery2);
                        } else {
                            Toast.makeText(getActivity(), "请输入订购数量", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.MenuAnimationLeftRight);
        mPopupWindow.showAtLocation(titleView, Gravity.RIGHT, 0, 0);
    }

    private void add(GoodsListEntity.ValueEntity goodsBean, double count, String unitName, int unitId, double unitPrice, double totalPrice, double basicUnitPrice, List<Integer> selectedProperty1List, List<Integer> selectedProperty2List, List<ProductPropertyListEntity.ValueEntity> productPropery1, List<ProductPropertyListEntity.ValueEntity> productPropery2) {
        if (productPropery1.size() > 0 && productPropery2.size() > 0 && selectedProperty1List.size() > 0 && selectedProperty2List.size() > 0) {
            for (int property1Position : selectedProperty1List) {
                for (int property2Position : selectedProperty2List) {
                    createSaleGoodsItemModel(goodsBean, count, unitName, unitId, unitPrice, totalPrice, basicUnitPrice, productPropery1.get(property1Position).Id, productPropery2.get(property2Position).Id, productPropery1.get(property1Position).Name, productPropery2.get(property2Position).Name, true, productPropery1, productPropery2);
                }
            }
        } else {
            createSaleGoodsItemModel(goodsBean, count, unitName, unitId, unitPrice, totalPrice, basicUnitPrice, 0, 0, null, null, false, null, null);
        }
        mGoodsListAdapter.notifyDataSetChanged();
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    private void createSaleGoodsItemModel(GoodsListEntity.ValueEntity goodsBean, double count, String unitName, int unitId, double unitPrice, double totalPrice, double basicUnitPrice, int property1Id, int property2Id, String property1IdName, String property2IdName, boolean isProperty, List<ProductPropertyListEntity.ValueEntity> productPropery1, List<ProductPropertyListEntity.ValueEntity> productPropery2) {
        SaleGoodsItemModel itemModel = new SaleGoodsItemModel();
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
                        Toast.makeText(getActivity(), "已添加到购物车", Toast.LENGTH_SHORT).show();
                        LogUtils.e("Item插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                    }
                });
        itemModel.save().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                LogUtils.e("Item保存数据成功");
            }
        });

        if (isProperty) {
            RXSQLite.rx(SQLite.select().from(GoodsPropertyModel.class)
                    .where(GoodsPropertyModel_Table.ProductId.eq(goodsBean.Id), GoodsPropertyModel_Table.ParentId.eq(goodsBean.ProperyId1)))
                    .queryList()
                    .subscribe(new Consumer<List<GoodsPropertyModel>>() {
                        @Override
                        public void accept(@NonNull List<GoodsPropertyModel> goodsPropertyModels) throws Exception {
                            if (goodsPropertyModels == null || goodsPropertyModels.size() == 0) {
                                Flowable.fromIterable(productPropery1)
                                        .forEach(valueEntity -> {
                                            GoodsPropertyModel goodsPropertyModel = new GoodsPropertyModel();
                                            goodsPropertyModel.Id = valueEntity.Id;
                                            goodsPropertyModel.ProductId = goodsBean.Id;
                                            goodsPropertyModel.Name = valueEntity.Name;
                                            goodsPropertyModel.Ordinal = valueEntity.Ordinal;
                                            goodsPropertyModel.ParentId = valueEntity.ParentId;
                                            goodsPropertyModel.Level = valueEntity.Level;
                                            goodsPropertyModel.PropertyType = 1;
                                            goodsPropertyModel.Path = valueEntity.Path;
                                            goodsPropertyModel.ProductCount = valueEntity.ProductCount;
                                            goodsPropertyModel.insert().subscribe(new Consumer<Long>() {
                                                @Override
                                                public void accept(@NonNull Long aLong) throws Exception {
                                                    LogUtils.e("商品属性1插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                                                }
                                            });
                                        });
                            }
                        }
                    });

            RXSQLite.rx(SQLite.select().from(GoodsPropertyModel.class)
                    .where(GoodsPropertyModel_Table.ProductId.eq(goodsBean.Id), GoodsPropertyModel_Table.ParentId.eq(goodsBean.ProperyId2)))
                    .queryList()
                    .subscribe(new Consumer<List<GoodsPropertyModel>>() {
                        @Override
                        public void accept(@NonNull List<GoodsPropertyModel> goodsPropertyModels) throws Exception {
                            if (goodsPropertyModels == null || goodsPropertyModels.size() == 0) {
                                Flowable.fromIterable(productPropery2)
                                        .forEach(valueEntity -> {
                                            GoodsPropertyModel goodsPropertyModel = new GoodsPropertyModel();
                                            goodsPropertyModel.Id = valueEntity.Id;
                                            goodsPropertyModel.ProductId = goodsBean.Id;
                                            goodsPropertyModel.Name = valueEntity.Name;
                                            goodsPropertyModel.Ordinal = valueEntity.Ordinal;
                                            goodsPropertyModel.ParentId = valueEntity.ParentId;
                                            goodsPropertyModel.Level = valueEntity.Level;
                                            goodsPropertyModel.PropertyType = 2;
                                            goodsPropertyModel.Path = valueEntity.Path;
                                            goodsPropertyModel.ProductCount = valueEntity.ProductCount;
                                            goodsPropertyModel.insert().subscribe(new Consumer<Long>() {
                                                @Override
                                                public void accept(@NonNull Long aLong) throws Exception {
                                                    LogUtils.e("商品属性2插入数据成功\n用时：" + DateUtil.getFormatTime(aLong));
                                                }
                                            });
                                        });
                            }
                        }
                    });
        }

        RXSQLite.rx(SQLite.select().from(GoodsUnitModel.class)
                .where(GoodsUnitModel_Table.ProductId.eq(goodsBean.Id)))
                .queryList()
                .subscribe(new Consumer<List<GoodsUnitModel>>() {
                    @Override
                    public void accept(@NonNull List<GoodsUnitModel> goodsUnitModels) throws Exception {
                        if (goodsUnitModels == null || goodsUnitModels.size() == 0) {
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
                    }
                });

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
                                final LayoutInflater mInflater = LayoutInflater.from(getActivity());
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
                                final LayoutInflater mInflater = LayoutInflater.from(getActivity());
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


    @Override
    protected void loadData() {
        LogUtils.e("loadData", "获取商品列表数据");
        if (null == pageInfo) {
            pageInfo = new PageInfo();
        }
        ++pageInfo.PageIndex;
        int traderId = PreferencesUtil.getInt("TraderId", 0);
        RetrofitHelper.getGoodsListAPI()
                .getGoodsList("ProductList", JsonUtils.serialize(pageInfo), "status  not in (4,8)", "[" + traderId + "]", AppInfoUtil.getToken())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(goodsListEntity -> {
                    if (goodsListEntity.PageInfo.PageIndex * goodsListEntity.PageInfo.PageLength >= goodsListEntity.PageInfo.TotalCount) {
                        loadMoreView.setVisibility(View.GONE);
                        mHeaderViewRecyclerAdapter.removeFootView();
                    }
                })
                .subscribe(new Observer<GoodsListEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GoodsListEntity goodsListEntity) {
                        if (!goodsListEntity.HasError) {
                            goodsList.addAll(goodsListEntity.Value);
                            List<GoodsListEntity.ValueEntity.PriceSystemListEntity> priceSystemList = goodsList.get(0).PriceSystemList;
                            PreferencesUtil.putInt("PriceSystemId", -1);
                            for (GoodsListEntity.ValueEntity.PriceSystemListEntity item : priceSystemList) {
                                if (item.IsOrderMeetingPrice) {
                                    PreferencesUtil.putInt("PriceSystemId", item.Id);
                                }
                            }
                            finishTask();
                        } else {
                            ToastUtils.showShortSafe(goodsListEntity.Information == null ? "请求超时" : goodsListEntity.Information);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showEmptyView();
                        loadMoreView.setVisibility(View.GONE);
                        --pageInfo.PageIndex;
                        mIsRefreshing = false;
                    }

                    @Override
                    public void onComplete() {
                        mIsRefreshing = false;
                    }
                });

    }

    @Override
    protected void finishTask() {
        mIsRefreshing = false;
        if (goodsList != null) {
            if (goodsList.size() == 0) {
                showEmptyView();
            } else {
                hideEmptyView();
            }
        }
        loadMoreView.setVisibility(View.GONE);
        if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || !mRecyclerView.isComputingLayout()) { // RecyclerView滑动过程中刷新数据导致的Crash(Android官方的一个Bug)
            if (!mGoodsListAdapter.onbind && goodsList != null && goodsList.size() > 0) {
                mGoodsListAdapter.notifyDataSetChanged();
            }
        }
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void showEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.ic_data_empty);
        mCustomEmptyView.setEmptyText("暂无数据");
    }


    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void createLoadMoreView() {
        loadMoreView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_load_more, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addFooterView(loadMoreView);
        loadMoreView.setVisibility(View.GONE);
    }


    private void createHeadView() {
        View headView = LayoutInflater.from(getActivity())
                .inflate(R.layout.tab_main_head_view, mRecyclerView, false);

        viewPager = (AutoScrollViewPager) headView.findViewById(R.id.viewpager);
        indicator = (ViewPagerIndicator) headView.findViewById(R.id.indicator);
        rlPagerContainer = (RelativeLayout) headView.findViewById(R.id.rlPagerContainer);
        tvPromotion = (TextView) headView.findViewById(R.id.tvPromotion);
        tvCollection = (TextView) headView.findViewById(R.id.tvCollection);
        tvNewProduct = (TextView) headView.findViewById(R.id.tvNewProduct);
        tvRecommend = (TextView) headView.findViewById(R.id.tvRecommend);
        rlPagerContainer.getLayoutParams().height = ScreenUtil.getScreenWidth(getContext()) * 200 / 750;
        viewPager.setCurrentItem(0);
        indicator.setCount(4);
        indicator.setCurrentItem(0);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                ImageView view = new ImageView(getContext());
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                switch (position) {
                    case 0:
                        view.setImageResource(R.drawable.banner_1);
                        break;
                    case 1:
                        view.setImageResource(R.drawable.banner_2);
                        break;
                    case 2:
                        view.setImageResource(R.drawable.banner_3);
                        break;
                    case 3:
                        view.setImageResource(R.drawable.banner_4);
                        break;
                }
                container.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        String url = "";
                        switch (position) {
                            case 0:
                                url = "http://m.yifarj.com/index.php?m=wap";
                                break;
                            case 1:
                                url = "http://m.yifarj.com/index.php?a=lists&typeid=29";
                                break;
                            case 2:
                                url = "http://m.yifarj.com/index.php?a=lists&typeid=19";
                                break;
                            case 3:
                                url = "http://m.yifarj.com/index.php?a=lists&typeid=7";
                                break;
                        }
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (indicator != null) {
                    indicator.setCurrentItem(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.startAutoScroll();

        tvPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PromotionActivity.class);
                startActivity(intent);
            }
        });
        tvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CollectionActivity.class);
                startActivity(intent);
            }
        });
        tvNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewProductActivity.class);
                startActivity(intent);
            }
        });
        tvRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecommendActivity.class);
                startActivity(intent);
            }
        });
        mHeaderViewRecyclerAdapter.addHeaderView(headView);
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoodsListAdapter.notifyItemChanged(itemPosition);
    }
}
