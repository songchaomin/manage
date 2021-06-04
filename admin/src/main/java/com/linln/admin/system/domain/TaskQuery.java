package com.linln.admin.system.domain;

import lombok.Data;

import javax.persistence.Column;

@Data
public class TaskQuery {
    public String taskName;
    public String taskLable;
    private String birthday;
    private Long age;// '年龄'
    private String idCardPic; // '身份证号码图片',
    private String bankCardPic;// '提现银行卡图片',
    private String payPic;//  '支付宝图片',
    private String wangwangId;//  '旺旺ID',
    private String taobaoPic; //  '我的淘宝页面截图',
    private String receveAddress; //  '常用收货地址',
    private String receveAddressPic; //  '常用收货地址图片',
    private Byte shopInfo; // '店铺信息1：是2：否',
    private String shppingXyPic; //   '买家信誉（买家信誉截图）',
    private Byte marryInfo; // '婚姻状态1：是2：否',
    private String childAgeRange; // '子女年龄区间',
    private String chargeRange; // '月收入区间',
    private String heightRange; //'身高区间',
    private String weightRange; // '体重区间',
    private String xl; //'学历',
    private String phoneBrand; // '手机品牌',
    private String constellation; // '星座',
    private Byte carInfo; // '是否有车1：是2：否',
    private String tgLink ;//推广链接
    private Long pid ;//上家ID
    private Long jf;//积分
    public int limit;
    public int page;
}
