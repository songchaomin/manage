package com.linln.modules.task.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@Table(name = "own_task")
public class OwnTask implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cId;
    private String cUserename;
    private String taskName;
    private String taskContent;
    private String merchantName;
    private String taskLable;
    private String taskStatus;
    private Byte deleteFlg;
    @CreatedDate
    private Date createDate;
    @LastModifiedDate
    private Date updateDate;



}
