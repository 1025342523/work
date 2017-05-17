package com.yifarj.yifadinghuobao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.snappingstepper.SnappingStepper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.helper.AbsRecyclerViewAdapter;
import com.yifarj.yifadinghuobao.database.model.GoodsUnitModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.utils.AppInfoUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * ShoppingCartAdapter
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 15:56
 */
public class ShoppingCartAdapter extends AbsRecyclerViewAdapter {
    private List<SaleGoodsItemModel> itemData;
    private List<GoodsUnitModel> unitData;
    private List<String> unitNameList = new ArrayList<>();
    public static Map<Integer, Set<Integer>> selectedMap = new HashMap<Integer, Set<Integer>>();

    public ShoppingCartAdapter(RecyclerView recyclerView, List<SaleGoodsItemModel> mItemData, List<GoodsUnitModel> mUnitData) {
        super(recyclerView);
        this.itemData = mItemData;
        this.unitData = mUnitData;
        Flowable.fromIterable(unitData)
                .forEach(new Consumer<GoodsUnitModel>() {
                    @Override
                    public void accept(@NonNull GoodsUnitModel goodsUnitModel) throws Exception {
                        unitNameList.add(goodsUnitModel.Name);
                    }
                });
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
            SaleGoodsItemModel goodsBean = itemData.get(position);
            if (goodsBean.Path != null) {
                Glide.with(getContext())
                        .load(AppInfoUtil.genPicUrl(goodsBean.Path))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_image)
                        .dontAnimate()
                        .into(itemViewHolder.itemImg);
            }

            itemViewHolder.tvName.setText(goodsBean.ProductName);
            itemViewHolder.tvUnit.setText(goodsBean.UnitName);
            itemViewHolder.tvPrice.setText(String.valueOf(goodsBean.CurrentPrice));

            if (unitNameList != null) {
                final LayoutInflater mInflater = LayoutInflater.from(getContext());
                TagAdapter<String> tagAdapter = new TagAdapter<String>(unitNameList) {
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
                    }
                });
            }
        }

        super.onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public class ItemViewHolder extends ClickableViewHolder {

        ImageView itemImg;
        TextView tvName;
        TextView tvUnit;
        TextView tvPrice;
        TagFlowLayout llFlowLayout;
        SnappingStepper stepperCustom;

        public ItemViewHolder(View itemView) {

            super(itemView);

            itemImg = $(R.id.item_img);
            tvName = $(R.id.tv_name);
            tvUnit = $(R.id.tv_unit);
            tvPrice = $(R.id.tv_price);
            llFlowLayout = $(R.id.ll_flowLayout);
            stepperCustom = $(R.id.stepperCustom);
        }
    }
}
