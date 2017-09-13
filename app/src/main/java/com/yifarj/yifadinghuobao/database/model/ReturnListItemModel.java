package com.yifarj.yifadinghuobao.database.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.rx2.structure.BaseRXModel;
import com.yifarj.yifadinghuobao.database.AppDatabase;

/**
 * 退货清单
 *
 * @auther zydx
 * @date 2017/8/2 14:58
 */
@Table(database = AppDatabase.class)
public class ReturnListItemModel extends BaseRXModel {

    @PrimaryKey(autoincrement = true)
    public int Id;

    @Column
    public double CurrentPrice;

    @Column
    public String Path;

    @Column
    public String Code;

    @Column
    public int BillId;

    @Column
    public int SalesType;

    @Column
    public int WarehouseId;

    @Column
    public int LocationId;

    @Column
    public int ProductId;

    @Column
    public String BatchId;

    @Column
    public double Quantity;

    @Column
    public double BasicQuantity;

    @Column
    public String PackString = "";

    @Column
    public int UnitId;

    @Column
    public int BasicUnitId;

    @Column
    public int PriceSystemId;

    @Column
    public double BasicUnitPrice;


    @Column
    public double UnitPrice;


    @Column
    public float Discount;

    @Column
    public double TaxRate;

    @Column
    public String Remark = "";

    @Column
    public double TotalPrice;

    @Column
    public int ReferenceBillId;

    @Column
    public int ReferenceBillTypeId;

    @Column
    public String PrimaryBarcode = "";

    @Column
    public int ReferenceBillItemId;

    @Column
    public double ReferencedQuantity;

    @Column
    public String WarehouseRemark = "";

    @Column
    public double OweQuantity;

    @Column
    public double OweReferencedQuantity;

    @Column
    public int OweStatus;

    @Column
    public String OweRemark = "";

    @Column
    public String AluminumColor = "";

    @Column
    public String GlassColor = "";

    @Column
    public String Pedestal = "";

    @Column
    public String Direction = "";

    @Column
    public String Lengthc = "";

    @Column
    public String width = "";

    @Column
    public String high = "";

    @Column
    public String CAddressAndPhone = "";

    @Column
    public String Specification = "";

    @Column
    public double RebateAmount;

    @Column
    public double VipPoint;

    @Column
    public boolean IsActivity;

    @Column
    public boolean IsVip;

    @Column
    public long BeginTime;

    @Column
    public long EndTime;

    @Column
    public double Price0;

    @Column
    public double Price1;

    @Column
    public double Price2;

    @Column
    public double Price3;

    @Column
    public double Price4;

    @Column
    public double Price5;

    @Column
    public double Price6;

    @Column
    public double Price7;

    @Column
    public double Price8;

    @Column
    public double Price9;

    @Column
    public double Price10;

    @Column
    public double MinSalesQuantity;

    @Column
    public double MaxSalesQuantity;

    @Column
    public double MinSalesPrice;

    @Column
    public double MaxPurchasePrice;

    @Column
    public double MemoryPrice;

    @Column
    public String ProductMenmonic = "";

    @Column
    public String ProductName = "";

    @Column
    public String ProductCode = "";

    @Column
    public String BasicUnitName = "";

    @Column
    public String WarehouseName = "";

    @Column
    public String DefaultLocationName = "";

    @Column
    public String CategoryName = "";

    @Column
    public String BrandName = "";

    @Column
    public String OrderCode = "";

    @Column
    public String PackSpec;

    @Column
    public String SalesStockProductInfo;

    @Column
    public String ProductUnitName = "";

    @Column
    public double MemberPrice;

    @Column
    public double BaseMemberPrice;

    @Column
    public String DiscountValue = "";

    @Column
    public String ParentProperyId1Name = "";

    @Column
    public String ParentProperyId2Name = "";

    @Column
    public int ProperyId1;

    @Column
    public int ProperyId2;

    @Column
    public String ProperyId1Name = "";

    @Column
    public String ProperyId2Name = "";

    @Column
    public int ParentProperyId1;

    @Column
    public int ParentProperyId2;

}
