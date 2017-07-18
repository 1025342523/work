package com.yifarj.yifadinghuobao.view.utils;

import com.alibaba.fastjson.JSON;
import com.yifarj.yifadinghuobao.vo.PriceSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成一个价格体系对象
 */
public class PriceSystemGenerator {
    private static String priceSystemStr = "{\n" +
            "    \"PriceSystemList\": [\n" +
            "        {\n" +
            "            \"Id\": 1,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"A\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 1\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 2,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"B\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 2\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 3,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"C\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 3\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 4,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"D\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 4\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 5,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"E\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 5\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 6,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"F\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 6\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 7,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"G\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 7\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 8,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"H\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 8\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 9,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"I\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 9\n" +
            "        },\n" +
            "        {\n" +
            "            \"Id\": 10,\n" +
            "            \"IsOrderMeetingPrice\": false,\n" +
            "            \"Name\": \"J\",\n" +
            "            \"Enable\": true,\n" +
            "            \"Ordinal\": 10\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private static PriceSystem priceSystem;


    public static PriceSystem getInstance() {
        if(priceSystem == null) {
            synchronized (PriceSystemGenerator.class) {
                if(priceSystem == null) {
                    priceSystem = JSON.parseObject(priceSystemStr, PriceSystem.class);
                }
            }
        }
        return priceSystem;
    }

    public static List<String> getPriceSystemName() {
        List<String> systemNames = new ArrayList<>();
        PriceSystem priceSystem = getInstance();
        for (PriceSystem.PriceSystemListEntity item :
                priceSystem.PriceSystemList ) {
            systemNames.add(item.Name);
        }
        return systemNames;
    }
}
