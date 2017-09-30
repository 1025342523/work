package com.yifarj.yifadinghuobao.database;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel;
import com.yifarj.yifadinghuobao.database.model.ReturnListItemModel_Table;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel;
import com.yifarj.yifadinghuobao.database.model.SaleGoodsItemModel_Table;

/**
 * SQLite
 *
 * @auther Czech.Yuan
 * @date 2017/5/16 17:09
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "YiDingHuoDatabase";

<<<<<<< HEAD
    public static final int VERSION = 7; //2017/9/13 15:24 add Property VERSION=5
=======
    public static final int VERSION = 7; //2017/9/25 17:00 add Property
>>>>>>> cb9657d8803e568c6f434fb52e7f32d5db675e37

    //
    @Migration(version = 6, database = AppDatabase.class)
    public static class Migration_6_SaleGoodsItemModel extends AlterTableMigration<SaleGoodsItemModel> {

        public Migration_6_SaleGoodsItemModel(Class<SaleGoodsItemModel> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            //
            addColumn(SQLiteType.TEXT, SaleGoodsItemModel_Table.Supplier.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, SaleGoodsItemModel_Table.SupplierId.getNameAlias().getNameAsKey());
        }
    }

    @Migration(version = 5, database = AppDatabase.class)
    public static class Migration_5_ReturnListItemModel extends AlterTableMigration<ReturnListItemModel> {

        public Migration_5_ReturnListItemModel(Class<ReturnListItemModel> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, ReturnListItemModel_Table.ParentProperyId1Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.TEXT, ReturnListItemModel_Table.ParentProperyId2Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.TEXT, ReturnListItemModel_Table.ProperyId1Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.TEXT, ReturnListItemModel_Table.ProperyId2Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ProperyId1.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ProperyId2.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ParentProperyId1.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ParentProperyId2.getNameAlias().getNameAsKey());
        }
    }

}
