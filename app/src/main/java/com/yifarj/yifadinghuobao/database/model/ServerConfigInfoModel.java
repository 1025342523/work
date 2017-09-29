package com.yifarj.yifadinghuobao.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.rx2.structure.BaseRXModel;
import com.yifarj.yifadinghuobao.database.AppDatabase;

/**
 * 服务器配置信息
 */
@Table(database = AppDatabase.class)
public class ServerConfigInfoModel extends BaseRXModel {

    @PrimaryKey(autoincrement = true)
    public int LocalId;

    @Column
    public String CompanyKey;

    @Column
    public String Company;

}
