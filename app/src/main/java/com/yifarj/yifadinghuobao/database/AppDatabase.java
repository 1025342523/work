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

    public static final int VERSION = 3; //2017/9/7 18:24 add Property

    @Migration(version = 3, database = AppDatabase.class)
    public static class Migration_3_SaleGoodsItemModel extends AlterTableMigration<SaleGoodsItemModel> {

        public Migration_3_SaleGoodsItemModel(Class<SaleGoodsItemModel> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, SaleGoodsItemModel_Table.ParentProperyId1Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.TEXT, SaleGoodsItemModel_Table.ParentProperyId2Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, SaleGoodsItemModel_Table.ProperyId1.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, SaleGoodsItemModel_Table.ProperyId2.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, SaleGoodsItemModel_Table.ParentProperyId1.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, SaleGoodsItemModel_Table.ParentProperyId2.getNameAlias().getNameAsKey());
        }
    }

    @Migration(version = 3, database = AppDatabase.class)
    public static class Migration_3_ReturnListItemModel extends AlterTableMigration<ReturnListItemModel> {

        public Migration_3_ReturnListItemModel(Class<ReturnListItemModel> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, ReturnListItemModel_Table.ParentProperyId1Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.TEXT, ReturnListItemModel_Table.ParentProperyId2Name.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ProperyId1.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ProperyId2.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ParentProperyId1.getNameAlias().getNameAsKey());
            addColumn(SQLiteType.INTEGER, ReturnListItemModel_Table.ParentProperyId2.getNameAlias().getNameAsKey());
        }
    }
}
