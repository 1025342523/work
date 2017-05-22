package com.yifarj.yifadinghuobao.model.entity;

import java.util.List;

/**
 * 销售单列表
 */
public class SaleOrderListEntity {

    public boolean HasError;
    public Object Information;
    public PageInfoEntity PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public List<ValueEntity> Value;

    public static class PageInfoEntity {
        public int TotalCount;
        public int PageIndex;
        public int PageLength;
        public int SortOrder;
        public String SortedColumn;
    }


    public static class ValueEntity {
        public int Id;
        public int BillTypeId;
        public String Code;
        public int Status;
        public long BillDate;
        public double Amount;
        public double UnFinishedAmount;
        public String InvoiceType;
        public String InvoiceNo;
        public long DeliveryDate;
        public String DeliveryAddress;
        public String ReceiveMethod;
        public long ReceiveTime;
        public double FinishedAmount;
        public long CreatedTime;
        public String CreatedDevice;
        public long ModifiedTime;
        public String ModifiedDevice;
        public String ModifiedDescription;
        public long AuditedTime;
        public long FinishedTime;
        public long AccountedTime;
        public long ClosedTime;
        public String ClosedDescription;
        public int PrintedCount;
        public int ReferencedCount;
        public String Remark;
        public String AuditedStatus;
        public String SettledStatus;
        public String ExecutedStatus;
        public String TraderName;
        public String ContactName;
        public String TraderPhone;
        public String DepartmentName;
        public String EmployeeName;
        public String CreatedUserName;
        public String ModifiedUseName;
        public String AuditedUserName;
        public String ClosedUserName;
        public String AccountedUserName;
        public int TraderId;
        public int ContactId;
        public int DepartmentId;
        public int EmployeeId;
        public int ModifiedUserId;
        public int CreatedUserId;
        public int AuditedUserId;
        public int AccountedUserId;
        public int ClosedUserId;
        public String WarehouseRemark;
        public String OutTypeName;
        public String Mnemonic;
        public String CustomStatusName;
        public int CustomStatus;
        public String AreaName;
        public String SalesName;
        public String ExDepartmentIdPath;
        public String SalesTypeName;
        public int SalesOutBillOweStatus;
        public double ReBateAmount;
        public double ReBateAmountBalance;
        public int TraderClassId;
        public int TraderTypeId;
        public String TraderClassName;
        public String TraderTypeName;
    }
}
