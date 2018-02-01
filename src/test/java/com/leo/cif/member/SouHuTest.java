package com.leo.cif.member;/**
 * Created by lee on 18/2/1.
 */

import org.junit.Test;
import util.NetHelper;

import java.util.HashMap;

/**
 * <p> Description:搜狐助手爬取</p>;
 * <p> Company:上海策道科技信息服务有限公司</p>;
 *
 * @version V1.0   ;
 **/
public class SouHuTest {

    /**
     * <p> Description:获取请求头</p>;
     * <p> Company:</p>;
     *
     * @version V1.0   ;
     **/
    private HashMap getRequestHeader() {
        HashMap map = new HashMap();
        map.put("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_5 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13G36 iphone sohuinfonews2_2_0");
        map.put("method", "POST");
        map.put("authority", "h-ss.sohu.com");
        map.put("scheme", "https");
        map.put("path", "/hotspot/millionHero/getQuestion");
        map.put("pragma", "no-cache");
        map.put("cache-control", "no-cache");
        map.put("accept", "*/*");
        map.put("origin", "https://h-ss.sohu.com");
        map.put("x-requested-with", "XMLHttpRequest");
        map.put("content-type", "application/json;charset=UTF-8");
        map.put("dnt", "1");
        map.put("referer", "https://h-ss.sohu.com/activity/million");
        map.put("accept-encoding", "gzip, deflate, br");
        map.put("accept-language", "en-US,en;q=0.8,de;q=0.6,es;q=0.4,zh-CN;q=0.2,zh-TW;q=0.2");
        map.put("cookie", "hjstat_uv=6801578893205276448|679544; vjuids=8616b640a.15e36151dfc.0.d7181ad43fed9; vjlast=1504145907.1507955365.23; shenhui12=w:1; sohutag=8HsmeSc5NCwmcyc5NCwmYjc5NCwmYSc5NSwmZjc5NCwmZyc5NCwmbjc5NSwmaSc5NCwmdyc5NCwmaCc5NCwmYyc5NCwmZSc5NCwmbSc5NCwmdCc5NH0; IPLOC=CN3100; SUV=1704071406241419; debug_test=sohu_third_cookie; Hm_lvt_32b54343ac4b0930095e3ad0ae71c49e=1516961348; Hm_lpvt_32b54343ac4b0930095e3ad0ae71c49e=1517153164");
        return map;
    }

    @Test
    public void getMillionHero() {
        HashMap dataMap = getRequestHeader();
        String content = NetHelper.postHttps("https://h-ss.sohu.com/hotspot/millionHero/getQuestion", "{\"appType\":1}", dataMap);
    }

    /**
     * <p> Description:获取百万英雄答案</p>;
     * <p> Company:</p>;
     *
     * @version V1.0   ;
     **/
    @Test
    public void getChongDingDahhui() {
        HashMap dataMap = getRequestHeader();
        String content = NetHelper.postHttps("https://h-ss.sohu.com/hotspot/millionHero/getQuestion", "{\"appType\":2}", dataMap);
        System.out.println(content);
    }


    /**
     * <p> Description:获取百万赢家答案</p>;
     * <p> Company:</p>;
     *
     * @version V1.0   ;
     **/
    @Test
    public void getMillionWinner() {
        HashMap dataMap = getRequestHeader();
        String content = NetHelper.postHttps("https://h-ss.sohu.com/hotspot/millionHero/getQuestion", "{\"appType\":3}", dataMap);
    }


    /**
     * <p> Description:获取芝士超人答案</p>;
     * <p> Company:</p>;
     *
     * @version V1.0   ;
     **/
    @Test
    public void getZhishiChaoRen() {
        HashMap dataMap = getRequestHeader();
        String content = NetHelper.postHttps("https://h-ss.sohu.com/hotspot/millionHero/getQuestion", "{\"appType\":4}", dataMap);

    }


}
