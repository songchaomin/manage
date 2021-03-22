package com.linln.modules.task.domain;

import com.linln.component.excel.annotation.Excel;
import com.linln.component.excel.enums.ExcelType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "task")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(value = "id", type = ExcelType.EXPORT)
    private Long id;
    @Excel("用户名")
    private String taskName;
    private String taskContent;
    @CreatedDate
    @Excel("创建时间")
    private Date createDate;
    @LastModifiedDate
    @Excel("更新时间")
    private Date updateDate;
    private String merchantName;
    private String taskLable;
    private Byte effective;
    private Byte deleteFlg;

}
