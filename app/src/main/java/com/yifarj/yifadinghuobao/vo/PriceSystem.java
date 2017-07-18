package com.yifarj.yifadinghuobao.vo;

import java.util.List;

/**
 * 价格体系
 */
public class PriceSystem {
    public List<PriceSystemListEntity> PriceSystemList;

    public static class PriceSystemListEntity {
        public int Id;
        public boolean IsOrderMeetingPrice;
        public String Name;
        public boolean Enable;
        public int Ordinal;
    }
}
