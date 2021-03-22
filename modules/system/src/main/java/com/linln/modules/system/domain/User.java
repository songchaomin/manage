package com.linln.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.StatusUtil;
import com.linln.component.excel.annotation.Excel;
import com.linln.component.excel.enums.ExcelType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@Data
@Entity
@Table(name = "sys_user")
@ToString(exclude = {"dept", "roles"})
@EqualsAndHashCode(exclude = {"dept", "roles"})
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "update sys_user" + StatusUtil.SLICE_DELETE)
@Where(clause = StatusUtil.NOT_DELETE)
@Excel("用户数据")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(value = "用户ID", type = ExcelType.EXPORT)
    private Long id;
    @Excel("用户名")
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;
    @Excel("昵称")
    private String nickname;
    private String picture;
    @Excel(value = "性别", dict = "USER_SEX")
    private Byte sex;
    @Excel("手机号码")
    private String phone;
    @Excel("电子邮箱")
    private String email;
    @CreatedDate
    @Excel("创建时间")
    private Date createDate;
    @LastModifiedDate
    @Excel("更新时间")
    private Date updateDate;
    @Excel("备注")
    private String remark;
    @Excel(value = "状态", dict = "DATA_STATUS")
    private Byte status;

    private Long qq;
    private Date birthday;
    private Long age;// '年龄'
    @Column(name="id_card_pic")
    private String idCardPic; // '身份证号码图片',
    private String bankCardPic;// '提现银行卡图片',
    private String payPic;//  '支付宝图片',
    private String wangwangId;//  '旺旺ID',
    private String taobaoPic; //  '我的淘宝页面截图',
    private String receveAddress; //  '常用收货地址',
    private Byte shopInfo; // '店铺信息1：是2：否',
    private String shppingXyPic; //   '买家信誉（买家信誉截图）',
    private Byte marryInfo; // '婚姻状态1：是2：否',
    private String childAgeRange; // '子女年龄区间',
    private String chargeRange; // '月收入区间',
    private String heightRange; //'身高区间',
    private String weightRange; // '体重区间',
    private String xl; //'学历',
    private String phoneBrand; // '手机品牌',
    private Byte carInfo; // '是否有车1：是2：否',
    private String tgLink ;//推广链接
    private Long pid ;//上家ID
    private Long jf;//积分
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    @JsonIgnore
    private Dept dept;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<Role> roles = new HashSet<>(0);
}
