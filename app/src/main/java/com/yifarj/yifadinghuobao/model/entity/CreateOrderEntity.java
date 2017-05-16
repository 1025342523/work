package com.yifarj.yifadinghuobao.model.entity;

import java.util.List;

/**
 * 开单接口数据对象
 */
public class CreateOrderEntity {


    public ValueEntity Value;

    public boolean HasError;
    public Object Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public static class ValueEntity {
        public int Id;
        public boolean hasReferenecBill;
        public int SysMode;
        public String Code = "";
        public int BillTypeId;
        public int OutTypeId;
        public int Status;
        public int CustomStatus;
        public long BillDate;
        public int AccMonthId;
        public int TraderId;
        public int ContactId;
        public int DepartmentId;
        public int EmployeeId;
        public double Amount;
        public String InvoiceType = "";
        public String InvoiceNo = "";
        public long DeliveryDate;
        public String DeliveryAddress;
        public String ReceiveMethod;
        public long ReceiveTime;
        public int FinishedAmount;
        public int CreatedUserId;
        public long CreatedTime;
        public String CreatedDevice = "";
        public int ModifiedUserId;
        public long ModifiedTime;
        public String ModifiedDevice = "";
        public String ModifiedDescription = "";
        public int AuditedUserId;
        public long AuditedTime;
        public long FinishedTime;
        public int AccountedUserId;
        public long AccountedTime;
        public int ClosedUserId;
        public long ClosedTime;
        public String ClosedDescription = "";
        public int PrintedCount;
        public int ReferencedCount;
        public String Remark = "";
        public String WarehouseRemark = "";
        public double TotalAmount;
        public float Discount;
        public double DiscountAmount;
        public float ReBateAmount;
        public String WarehouseName = "";
        public String LocationName = "";
        public int PriceSystemId;
        public int BasicUnitId;
        public int WarehouseId;
        public int ProductId;
        public int LocationId;
        public String BatchId = "";
        public String SalesOutTypeName = "";
        public int UnFinishedAmount;
        public String TraderName = "";
        public String TraderContactName = "";
        public String TraderPhone = "";
        public String TraderFax = "";
        public String DepartmentName = "";
        public String EmployeeName = "";
        public String CustomStatusName = "";
        public List<SaleGoodsItem.ValueEntity> SalesOutBillItemList;
    }
}
