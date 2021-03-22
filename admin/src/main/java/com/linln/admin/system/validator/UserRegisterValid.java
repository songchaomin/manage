package com.linln.admin.system.validator;

import com.linln.modules.system.domain.Dept;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@Data
public class UserRegisterValid implements Serializable {
    @NotEmpty(message = "用户名不能为空")
    private String username;
    private String confirm;
    @NotEmpty(message = "手机号码不能为空")
    private String phone;
}
