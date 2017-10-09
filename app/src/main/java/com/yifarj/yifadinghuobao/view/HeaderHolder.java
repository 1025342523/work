package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.yifarj.yifadinghuobao.R;

/**
 * Created by Administrator on 2017-10-05.
 */

public class HeaderHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {

    private TextView mTvValue;
    private ImageView mIvArrow;

    public HeaderHolder(Context context) {
        super(context);

    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItemHolder.IconTreeItem value) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_icon_node2, null, false);
        mTvValue = view.findViewById(R.id.tv_value);
        mIvArrow = view.findViewById(R.id.iv_arrow);
        mTvValue.setText(value.text);
        return view;
    }

    @Override
    public void toggle(boolean active) {
        if (active) {
            mIvArrow.setImageResource(R.drawable.ic_bottom_triangle);
        } else {
            mIvArrow.setImageResource(R.drawable.ic_right_triangle);
        }
    }
}
