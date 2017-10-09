package com.yifarj.yifadinghuobao.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.yifarj.yifadinghuobao.R;

/**
 * Created by ZhangZeZhi on 2017-10-04.
 */

public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {

    private TextView tvValue;

    private ImageView ivArrow;
    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = view.findViewById(R.id.tv_value);
        tvValue.setText(value.text);
        ivArrow = view.findViewById(R.id.iv_arrow);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        if(active){
            ivArrow.setImageResource(R.drawable.ic_right_triangle);
        }else{
            ivArrow.setImageResource(R.drawable.ic_bottom_triangle);
        }
    }

    public static class IconTreeItem{
        public String text;

        public IconTreeItem(String text) {
            this.text = text;
        }
    }
}
