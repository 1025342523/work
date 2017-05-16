package com.yifarj.yifadinghuobao.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.rx2.structure.BaseRXModel;
import com.yifarj.yifadinghuobao.database.AppDatabase;

/**
 * 货品单位
 */
@Table(database = AppDatabase.class)
public class GoodsUnitModel extends BaseRXModel {

    @Column
    public int Id;

    @Column
    public int ProductId;

    @Column
    public String Name;

    @Column
    public int Factor;

    @Column
    public int BasicFactor = 1;

    @Column
    public boolean IsBasic;//判断是否为最小单位 1为true

    @Column
    public boolean IsDefault;

    @Column
    public boolean BreakupNotify;

    @Column
    public int Ordinal;
}
