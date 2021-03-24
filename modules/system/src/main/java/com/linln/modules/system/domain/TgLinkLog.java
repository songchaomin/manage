package com.linln.modules.system.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tg_link_log")
public class TgLinkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 部门名称 */
    private String tgLink;

    /** 父级编号 */
    private Byte effective;

}
