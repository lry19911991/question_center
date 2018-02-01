package com.leo.cif.member;/**
 * Created by lee on 18/2/1.
 */

import org.junit.Test;
import util.NetHelper;

import java.util.HashMap;

/**
 * <p> Description:搜狗测试</p>;
 *
 * @author leo;
 * @version V1.0   ;
 **/
public class SouGouTest {


    /**
     * <p> Description:获取请求头</p>;
     * <p> Company:</p>;
     *
     * @author lirenyi;
     * @version V1.0   ;
     **/
    private HashMap getRequestHeader() {
        HashMap map = new HashMap();
        map.put("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_5 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13G36 Sogousearch/Ios/5.9.8");
        map.put("host", "140.143.49.31");
        map.put("referer", "http://nb.sa.sogou.com/cheat-sheet?channel=xigua");
        return map;
    }


    /**
     * 百万英雄
     **/
    @Test
    public void getBaiWantTest() {

        HashMap map = getRequestHeader();
        String content = NetHelper.getHttp("http://140.143.49.31/api/ans2?key=xigua&wdcallback=jQuery1124010749807232059538_1517450409540&_=1517450409541", map);
        System.out.println(content);

    }

    /**
     * 芝士超人
     **/
    @Test
    public void getZhishiChaoRenTest() {
        HashMap map = getRequestHeader();
        String content = NetHelper.getHttp("http://140.143.49.31/api/ans2?key=zscr&wdcallback=jQuery112403605917361564934_1517452643786&_=1517452644239", map);
        System.out.println(content);
    }


    /**
     * 百万赢家
     **/
    @Test
    public void getHuaJiaoTest() {
        HashMap map = getRequestHeader();
        String content = NetHelper.getHttp("http://140.143.49.31/api/ans2?key=huajiao&wdcallback=jQuery112408324612360447645_1517451597343&_=1517451597344", map);
        System.out.println(content);
    }


    /**
     * 冲顶大会
     **/
    @Test
    public void getChonDingRenTest() {
        HashMap map = getRequestHeader();
        String content = NetHelper.getHttp("http://140.143.49.31/api/ans2?key=cddh&wdcallback=jQuery112407869317876175046_1517452198829&_=1517452198835", map);
        System.out.println(content);
    }


}
