package com.yifarj.yifadinghuobao.model.entity;

import com.chen.treeview.model.NodeChild;
import com.chen.treeview.model.NodeId;
import com.chen.treeview.model.NodeName;
import com.chen.treeview.model.NodePid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-10-03.
 */

public class OrderSummaryCategory {
    @NodeId
    public String ProductName;

    @NodePid
    public String Name;
    public int level;
    @NodeName
    public String goodsName;
    public String Code;
    public double TotalPrice;
    public int Quantity;
    public String UnitName;

    @NodeChild
    public List<OrderSummaryCategory> ChildList = new ArrayList<>();

}
