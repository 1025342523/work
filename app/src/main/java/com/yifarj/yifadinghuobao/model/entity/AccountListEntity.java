package com.yifarj.yifadinghuobao.model.entity;

import java.util.List;

/**
 * 账套号列表
 */
public class AccountListEntity {

    public boolean HasError;
    public String Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;
    public List<ValueEntity> Value;

    public static class ValueEntity {
        public int Id;
        public String Name;
        public String Server;
        public String DbName;
        public boolean Visible;
        public int Sort;
        public int Mode;
        public int Status;
        public int CreatedTime;
        public int Version;
        public String VersionString;
        public String ImagePath;
    }
}
