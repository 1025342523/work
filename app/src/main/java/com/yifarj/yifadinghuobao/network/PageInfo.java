package com.yifarj.yifadinghuobao.network;

/**
 * 分页信息
 */
public class PageInfo {

    public int PageIndex = 0;//page 从0开始,但是用分页的时候会先自增
    public int TotalCount;
    public int PageLength = 10;
    public String SortedColumn = "Id";
    public int SortOrder = 1;
}
