package com.linln.admin.system.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
public class RobTask implements Serializable {
    private Long id;
    /**
     * 任务名称
     */
    private Long taskId;

    private String taskName;
    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 创建时间
     */
    private Date createDate;


    private Long cUserId;

    private String cUserName;

    private String cNickName;

    private String wangwangId;

    private String qq;

    private String robTaskStatus;

    private String payPicUrl;

}
