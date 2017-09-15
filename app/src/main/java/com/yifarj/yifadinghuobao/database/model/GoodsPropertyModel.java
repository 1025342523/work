package com.yifarj.yifadinghuobao.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.rx2.structure.BaseRXModel;
import com.yifarj.yifadinghuobao.database.AppDatabase;

/**
 * GoodsPropertyModel
 *
 * @auther Czech.Yuan
 * @date 2017/9/15 11:29
 */
@Table(database = AppDatabase.class)
public class GoodsPropertyModel extends BaseRXModel {

    @PrimaryKey(autoincrement = true)
    public int LocalId;

    @Column
    public int Id;

    @Column
    public int ParentId;

    @Column
    public int Level;

    @Column
    public String Name;

    @Column
    public String Path;

    @Column
    public int Ordinal;

    @Column
    public int ProductCount;

    @Column
    public int ProductId;

    @Column
    public int PropertyType;
}
