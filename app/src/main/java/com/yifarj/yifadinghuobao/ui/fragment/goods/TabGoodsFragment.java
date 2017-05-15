package com.yifarj.yifadinghuobao.ui.fragment.goods;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.adapter.GoodsListAdapter;
import com.yifarj.yifadinghuobao.ui.fragment.base.BaseFragment;

import butterknife.BindView;

/**
* TabGoodsFragment
* @auther  Czech.Yuan
* @date 2017/5/12 15:07
*/
public class TabGoodsFragment extends BaseFragment{
private GoodsListAdapter mGoodsListAdapter;
    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    ImageView mEmptyView;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_goods;
    }

    @Override
    protected void finishCreateView(Bundle savedInstanceState) {

    }
}
