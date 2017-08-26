package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.stetho.common.LogUtil;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.ReturnGoodsUnitModel_Table;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel_Table;
import com.yifarj.yifadinghuobao.model.entity.ProductMemoryPriceEntity;
import com.yifarj.yifadinghuobao.model.helper.DataSaver;
import com.yifarj.yifadinghuobao.network.RetrofitHelper;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.yifarj.yifadinghuobao.utils.NumberUtil;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;
import com.yifarj.yifadinghuobao.view.CzechYuanDialog;
import com.yifarj.yifadinghuobao.view.CzechYuanEditDialog;
import com.yifarj.yifadinghuobao.view.NumberAddSubView;
import com.yifarj.yifadinghuobao.view.utils.PriceSystemGenerator;
import com.yifarj.yifadinghuobao.vo.PriceSystem;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 退货清单Adapter
 *
 * @auther zydx
 * @date 2017/8/3 10:44
 */
public class ReturnListAdapter extends AbsRecyclerViewAdapter {
    private List<ReturnListItemModel> itemData;
    public static Map<Integer, Set<Integer>> selectedMap = new HashMap<Integer, Set<Integer>>();
    private static Map<Integer, List<ReturnGoodsUnitModel>> itemUnit = new HashMap<Integer, List<ReturnGoodsUnitModel>>();
    private int mPriceSystemId;

    public ReturnListAdapter(RecyclerView recyclerView, List<ReturnListItemModel> mItemData, int priceSystemId) {
        super(recyclerView);
        this.itemData = mItemData;
        mPriceSystemId = priceSystemId;
    }


    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.item_shopping_cart_list, parent, false));
    }


    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            ReturnListItemModel goodsBean = itemData.get(position);
            selectedMap.clear();

            RXSQLite.rx(SQLite.select().from(ReturnGoodsUnitModel.class)
                    .where(ReturnGoodsUnitModel_Table.ProductId.eq(goodsBean.ProductId)))
                    .queryList()
                    .subscribe(new Consumer<List<ReturnGoodsUnitModel>>() {
                        @Override
                        public void accept(@NonNull List<ReturnGoodsUnitModel> returnGoodsUnitModels) throws Exception {
                            if (returnGoodsUnitModels != null && returnGoodsUnitModels.size() > 0) {
                                itemUnit.put(position, returnGoodsUnitModels);
                            }
                        }
                    });
            if (goodsBean.Path != null) {
                Glide.with(getContext())
                        .load(AppInfoUtil.genPicUrl(goodsBean.Path))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_image)
                        .dontAnimate()
                        .into(itemViewHolder.itemImg);
            }
            itemViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CzechYuanDialog mDialog = new CzechYuanDialog(getContext(), 200, 100, R.style.CzechYuanDialog);
                    mDialog.setContent("确定删除？");
                    mDialog.setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                    .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                                    .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                                @Override
                                public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                    LogUtils.e("ReturnListItemModels.size()：" + returnListItemModels.size());
                                    if (returnListItemModels.size() > 0) {
                                        ReturnListItemModel mItem = returnListItemModels.get(0);
                                        mItem.delete().subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(@NonNull Boolean aBean) throws Exception {
                                                LogUtils.e(mItem.ProductName + "：删除\n" + aBean);
                                                itemData.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, getItemCount());
                                            }
                                        });
                                    }

                                }
                            });
                            RXSQLite.rx(SQLite.select().from(ReturnGoodsUnitModel.class)
                                    .where(ReturnGoodsUnitModel_Table.ProductId.eq(goodsBean.ProductId)))
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
                                                ToastUtils.showShortSafe("删除成功");
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
            itemViewHolder.numberAddSubView.setOnNumberEditClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CzechYuanEditDialog mDialog = new CzechYuanEditDialog(getContext(), R.style.CzechYuanDialog);
                    mDialog.getEditText().setText(String.valueOf(goodsBean.Quantity));
                    mDialog.getEditText().setSelection(0, String.valueOf(goodsBean.Quantity).length());
                    mDialog.setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int count;
                            try {
                                count = Integer.parseInt(mDialog.getEditText().getText().toString());
                            } catch (NumberFormatException e) {
                                count = goodsBean.Quantity;
                            }
                            if (count != goodsBean.Quantity) {
                                int tempCount = count;
                                goodsBean.Quantity = tempCount;
                                goodsBean.CurrentPrice = goodsBean.UnitPrice * tempCount;
                                notifyDataSetChanged();
                                RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                        .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                                        .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                                    @Override
                                    public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                        LogUtils.e(returnListItemModels + "\n长度" + returnListItemModels.size());
                                        if (returnListItemModels.size() > 0) {
                                            ReturnListItemModel mItem = returnListItemModels.get(0);
                                            mItem.Quantity = goodsBean.Quantity;
                                            mItem.CurrentPrice = goodsBean.CurrentPrice;
                                            mItem.update().subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(@NonNull Boolean aBean) throws Exception {
                                                    LogUtils.e(mItem.ProductName + "：数量修改为" + tempCount);
                                                }
                                            });
                                        }

                                    }
                                });
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
            itemViewHolder.tvPackSpec.setText(goodsBean.PackSpec);
            itemViewHolder.tvBasicPrice.setText(goodsBean.BasicUnitPrice + "元/" + goodsBean.BasicUnitName);
            itemViewHolder.tvCode.setText("编号：" + goodsBean.Code.substring(goodsBean.Code.length() - 4, goodsBean.Code.length()));
            itemViewHolder.tvName.setText(goodsBean.ProductName);
            itemViewHolder.tvPrice.setText("小计：" + NumberUtil.formatDoubleToString(goodsBean.CurrentPrice) + "元");
            itemViewHolder.numberAddSubView.setValue(goodsBean.Quantity);
            itemViewHolder.numberAddSubView.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
                @Override
                public void onButtonAddClick(View view, int value) {
                    if (value != goodsBean.Quantity) {
                        goodsBean.Quantity = value;
                        goodsBean.CurrentPrice = goodsBean.UnitPrice * value;
                        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                                .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                            @Override
                            public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                LogUtils.e(returnListItemModels + "\n长度" + returnListItemModels.size());
                                if (returnListItemModels.size() > 0) {
                                    ReturnListItemModel mItem = returnListItemModels.get(0);
                                    mItem.Quantity = goodsBean.Quantity;
                                    mItem.CurrentPrice = goodsBean.CurrentPrice;
                                    mItem.update().subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(@NonNull Boolean aBean) throws Exception {
                                            LogUtils.e(mItem.ProductName + "：数量修改为" + value);
                                        }
                                    });
                                }

                            }
                        });
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onButtonSubClick(View view, int value) {
                    if (value != goodsBean.Quantity) {
                        goodsBean.Quantity = value;
                        goodsBean.CurrentPrice = goodsBean.UnitPrice * value;
                        RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                                .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                            @Override
                            public void accept(@NonNull List<ReturnListItemModel> returnListItemModels) throws Exception {
                                LogUtils.e(returnListItemModels + "\n长度" + returnListItemModels.size());
                                if (returnListItemModels.size() > 0) {
                                    ReturnListItemModel mItem = returnListItemModels.get(0);
                                    mItem.Quantity = goodsBean.Quantity;
                                    mItem.CurrentPrice = goodsBean.CurrentPrice;
                                    mItem.update().subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(@NonNull Boolean aBean) throws Exception {
                                            LogUtils.e(mItem.ProductName + "：数量修改为" + value);
                                        }
                                    });
                                }

                            }
                        });
                        notifyDataSetChanged();
                    }
                }
            });
            if (itemUnit != null) {
                List<String> unitName = new ArrayList<>();
                Flowable.fromIterable(itemUnit.get(position))
                        .forEach(new Consumer<ReturnGoodsUnitModel>() {
                            @Override
                            public void accept(@NonNull ReturnGoodsUnitModel returnGoodsUnitModel) throws Exception {
                                unitName.add(returnGoodsUnitModel.Name);
                            }
                        });
                final LayoutInflater mInflater = LayoutInflater.from(getContext());
                TagAdapter<String> tagAdapter = new TagAdapter<String>(unitName) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) mInflater.inflate(R.layout.tv, parent, false);
                        tv.setText(s);
                        return tv;
                    }
                };
                itemViewHolder.llFlowLayout.setAdapter(tagAdapter);
                //重置状态
                tagAdapter.setSelectedList(selectedMap.get(position));
                itemViewHolder.llFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                    @Override
                    public void onSelected(Set<Integer> selectPosSet) {
                        selectedMap.put(position, selectPosSet);
                        int select = 0;
                        for (Integer item : selectPosSet) {
                            select = item;
                        }
                        goodsBean.ProductUnitName = unitName.get(select);
                        goodsBean.UnitId = itemUnit.get(position).get(select).Id;
                        LogUtils.e(itemUnit.get(position).get(select).BasicFactor);
                        if (mPriceSystemId == -1) {
                            getProductMemoryPrice(goodsBean, select, itemUnit.get(position).get(select).BasicFactor);
//                        goodsBean.UnitPrice = itemUnit.get(position).get(select).BasicFactor * goodsBean.BasicUnitPrice;
//                        goodsBean.CurrentPrice = goodsBean.Quantity * goodsBean.UnitPrice;
                        } else {
                            switch (mPriceSystemId) {
                                case 1:
                                    goodsBean.BasicUnitPrice = goodsBean.Price1;
                                    break;
                                case 2:
                                    goodsBean.BasicUnitPrice = goodsBean.Price2;
                                    break;
                                case 3:
                                    goodsBean.BasicUnitPrice = goodsBean.Price3;
                                    break;
                                case 4:
                                    goodsBean.BasicUnitPrice = goodsBean.Price4;
                                    break;
                                case 5:
                                    goodsBean.BasicUnitPrice = goodsBean.Price5;
                                    break;
                                case 6:
                                    goodsBean.BasicUnitPrice = goodsBean.Price6;
                                    break;
                                case 7:
                                    goodsBean.BasicUnitPrice = goodsBean.Price7;
                                    break;
                                case 8:
                                    goodsBean.BasicUnitPrice = goodsBean.Price8;
                                    break;
                                case 9:
                                    goodsBean.BasicUnitPrice = goodsBean.Price9;
                                    break;
                                case 10:
                                    goodsBean.BasicUnitPrice = goodsBean.Price10;
                                    break;
                            }
                            goodsBean.UnitPrice = itemUnit.get(position).get(select).BasicFactor * goodsBean.BasicUnitPrice;
                            goodsBean.CurrentPrice = goodsBean.Quantity * goodsBean.UnitPrice;
                            notifyDataSetChanged();
                            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                    .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                                    .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                                @Override
                                public void accept(@NonNull List<ReturnListItemModel> ReturnListItemModels) throws Exception {
                                    LogUtils.e(ReturnListItemModels + "\n长度" + ReturnListItemModels.size());
                                    if (ReturnListItemModels.size() > 0) {
                                        ReturnListItemModel mItem = ReturnListItemModels.get(0);
                                        mItem.ProductUnitName = goodsBean.ProductUnitName;
                                        mItem.UnitId = goodsBean.UnitId;
                                        mItem.UnitPrice = goodsBean.UnitPrice;
                                        mItem.CurrentPrice = goodsBean.CurrentPrice;
                                        mItem.update().subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(@NonNull Boolean aBean) throws Exception {
                                                LogUtils.e(mItem.ProductName + "：修改单位" + mItem.ProductUnitName);
                                            }
                                        });
                                    }

                                }
                            });
                        }
                    }
                });
                Flowable.fromIterable(unitName)
                        .forEach(new Consumer<String>() {
                            @Override
                            public void accept(@NonNull String s) throws Exception {
                                if (goodsBean.ProductUnitName.equals(s)) {
                                    tagAdapter.setSelectedList(unitName.indexOf(s));
                                }
                            }
                        });
            }
        }
        super.onBindViewHolder(holder, position);
    }

    /**
     * 获取记忆价格
     */
    private void getProductMemoryPrice(ReturnListItemModel goodsBean, int select, double factor) {
        String b;
        int traderId;
        if (DataSaver.getMettingCustomerInfo() != null) {
            traderId = DataSaver.getMettingCustomerInfo().TraderId;
        } else {
            traderId = PreferencesUtil.getInt("TraderId", 0);
        }
        b = "ProductId=" + goodsBean.ProductId + " and " +
                "TraderId=" + traderId + " and " +
                "ProductUnitId=" + goodsBean.UnitId;
        //获取当前商品的记忆价格
        RetrofitHelper
                .getProductMemoryPriceApi()
                .getProductMemoryPrice("TraderProductPrice", b, "", AppInfoUtil.getToken())
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
                                LogUtils.e(goodsBean.ProductName + "存在记忆价格,记忆价格为:" + memoryPrice);
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
                                LogUtils.e(goodsBean.ProductName + "不存在记忆价格");
                            }
                            if (!selected || DataSaver.getPriceSystemId() == 0) {
                                selectedPriceSystem = PriceSystemGenerator.getInstance().PriceSystemList.get(0);
                            }
                            goodsBean.PriceSystemId = selectedPriceSystem.Id;
                            /**
                             * 根据记忆价格、价格体系和单位设置价格,如果有记忆价格,
                             * 则设置价格为记忆价格,如果没有记忆价格,则根据价格体系和单位设置价格
                             *
                             */
                            double price = 0;
                            double basicPrice = 0;
                            if (memoryPrice > 0) {//有记忆价格
                                if (select == 0) {
                                    basicPrice = memoryPrice;
                                    price = basicPrice;
                                } else {
                                    price = memoryPrice;
                                    basicPrice = price / factor;
                                }
                            } else {//没有记忆价格,则通过单位id获取单位价格
                                //获取价格体系的价格
                                switch (goodsBean.PriceSystemId) {
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
                                price = basicPrice * factor;
                            }
                            goodsBean.UnitPrice = price;
                            goodsBean.BasicUnitPrice = basicPrice;
                            goodsBean.CurrentPrice = goodsBean.Quantity * price;
                            LogUtil.e("setGoodsItemPriceWithUnitAndPriceSystem basicPrice", basicPrice + "");
                            LogUtil.e("setGoodsItemPriceWithUnitAndPriceSystem goodsItem.PriceSystemId", goodsBean.PriceSystemId + "");

                            notifyDataSetChanged();
                            RXSQLite.rx(SQLite.select().from(ReturnListItemModel.class)
                                    .where(ReturnListItemModel_Table.ProductId.eq(goodsBean.ProductId)))
                                    .queryList().subscribe(new Consumer<List<ReturnListItemModel>>() {
                                @Override
                                public void accept(@NonNull List<ReturnListItemModel> ReturnListItemModels) throws Exception {
                                    LogUtils.e(ReturnListItemModels + "\n长度" + ReturnListItemModels.size());
                                    if (ReturnListItemModels.size() > 0) {
                                        ReturnListItemModel mItem = ReturnListItemModels.get(0);
                                        mItem.ProductUnitName = goodsBean.ProductUnitName;
                                        mItem.UnitId = goodsBean.UnitId;
                                        mItem.UnitPrice = goodsBean.UnitPrice;
                                        mItem.CurrentPrice = goodsBean.CurrentPrice;
                                        mItem.update().subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(@NonNull Boolean aBean) throws Exception {
                                                LogUtils.e(mItem.ProductName + "：修改单位" + mItem.ProductUnitName);
                                            }
                                        });
                                    }

                                }
                            });
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


    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public class ItemViewHolder extends ClickableViewHolder {

        ImageView itemImg;
        ImageView ivDelete;
        TextView tvName;
        TextView tvPrice;
        TextView tvPackSpec;
        TextView tvBasicPrice;
        TextView tvCode;
        TagFlowLayout llFlowLayout;
        NumberAddSubView numberAddSubView;

        public ItemViewHolder(View itemView) {

            super(itemView);

            itemImg = $(R.id.item_img);
            tvName = $(R.id.tv_name);
            tvBasicPrice = $(R.id.tv_basicPrice);
            tvPrice = $(R.id.tv_price);
            tvPackSpec = $(R.id.tv_PackSpec);
            tvCode = $(R.id.tv_Code);
            llFlowLayout = $(R.id.ll_flowLayout);
            numberAddSubView = $(R.id.num_control);
            ivDelete = $(R.id.iv_delete);
        }
    }


}
