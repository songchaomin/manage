package com.linln.admin.system.controller;

import com.linln.admin.system.domain.UserCenter;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.Role;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.service.RoleService;
import com.linln.modules.system.service.UserService;
import com.linln.modules.task.domain.OwnTask;
import com.linln.modules.task.service.OwnTaskService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/userCenter")
public class UserCenterController {

    @Autowired
    private UserService userService;

   @Autowired
    private OwnTaskService ownTaskService;

    @Autowired
    private RoleService roleService;

    /**
     * 个人信息
     */
    @GetMapping("/userInfo")
    @RequiresPermissions("userCenter:userInfo")
    public String userInfo(Model model) {
        User user = ShiroUtil.getSubject();
        UserCenter userCenter=new UserCenter();
        try {
            BeanUtils.copyProperties(userCenter,user);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if(Objects.equals(user.getUsername(),"admin")){
            userCenter.setRoleName("管理员");
        }else{
            Role roles = roleService.getById(2L);
            userCenter.setRoleName(roles.getTitle());
        }
        List<User> users = userService.getPid(user.getId());
        userCenter.setTgNum(users.size());
        model.addAttribute("user", userCenter);
        return "/userCenter/userInfo";
    }

    /**
     * 推广链接页面
     */
    @GetMapping("/getTgLink")
    @RequiresPermissions("userCenter:getTgLink")
    public String getTgLink(Model model) {
        User user = ShiroUtil.getSubject();
        model.addAttribute("user", user);
        return "/userCenter/tgLink";
    }

    /**
     * 推广链接页面
     */
    @GetMapping("/generateTgLink/{id}")
    @ResponseBody
    public String generateTgLink(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        String tglink= request.getScheme()+"://"+request.getServerName()+":"+request.getLocalPort()+"/system/user/register/"+id;
        userService.updateTgLink(tglink,id);
        return tglink;
    }

    /**
     * 我的任务
     */
    @GetMapping("/ownTask/index")
    @RequiresPermissions("userCenter:ownTask:index")
    public String index(Model model, OwnTask ownTask) {

        // 获取用户列表
        Page<OwnTask> list = ownTaskService.getPageList(ownTask);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/userCenter/ownTask/index";
    }

    /**
     * 我的积分
     */
    @GetMapping("/getIntegral")
    @RequiresPermissions("userCenter:getIntegral")
    public String getIntegral(Model model) {
        User user = ShiroUtil.getSubject();
        model.addAttribute("user", user);
        return "/userCenter/integral";
    }


}
