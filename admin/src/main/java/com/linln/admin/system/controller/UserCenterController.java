package com.linln.admin.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.linln.admin.system.config.YamlPropertySourceFactory;
import com.linln.admin.system.domain.*;
import com.linln.admin.system.utils.HttpClientUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.Role;
import com.linln.modules.system.domain.TgLinkLog;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.service.RoleService;
import com.linln.modules.system.service.TgLinkLogService;
import com.linln.modules.system.service.UserService;
import com.linln.modules.task.domain.Integral;
import com.linln.modules.task.service.IntegralService;
import com.linln.modules.task.service.OwnTaskService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/userCenter")
@PropertySource(value = {"classpath:application.yml"},factory = YamlPropertySourceFactory.class)
public class UserCenterController {

    @Value("${taskUrl}")
    private  String taskUrl;

    @Value("${myRobOrder}")
    private  String robTaskUrl;


    @Value("${remoteUrl}")
    private  String remoteUrl;

    @Value("${robOrder}")
    private  String robOrderUrl;

    @Value("${changeRobTaskStatus}")
    private  String changeRobTaskStatus;

    @Autowired
    private UserService userService;

   @Autowired
    private OwnTaskService ownTaskService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TgLinkLogService tgLinkLogService;


    @Autowired
    private IntegralService integralService;



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
            Role roles = roleService.getRoleByUserId(user.getId());
            userCenter.setRoleName(roles.getTitle());
        }
        List<User> users = userService.getPid(user.getId());
        userCenter.setTgNum(users.size());
        //显示第一个培训师的姓名
       String tranName= userService.getTranName(user);
        model.addAttribute("user", userCenter);
        model.addAttribute("tranName", tranName);
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
    @GetMapping("/generateTgLink")
    @ResponseBody
    public String generateTgLink( Model model, HttpServletRequest request) {
        User user = ShiroUtil.getSubject();
        Long id = user.getId();
        String uuid= UUID.randomUUID().toString();
        String tglink= request.getScheme()+"://"+request.getServerName()+"/system/user/register/"+id+"/"+uuid;
        TgLinkLog tgLinkLog=new TgLinkLog();
        tgLinkLog.setTgLink(uuid);
        tgLinkLog.setEffective((byte)0);
        tgLinkLogService.insertTgLinkLog(tgLinkLog);
        userService.updateTgLink(tglink,id);
        return tglink;
    }

    /**
     * 任务大厅界面
     */
    @GetMapping("/ownTask/index")
    @RequiresPermissions("userCenter:ownTask:index")
    public String index(Model model) {
        return "/userCenter/ownTask/index";
    }


    @GetMapping("/ownTask/myRobTask")
    public String myRobTask(Model model) {
        return "/userCenter/ownTask/myRobTask";
    }
    /**
     * 显示已经抢单的任务
     * @param param
     * @return
     */

    @PostMapping("/ownTask/taskList")
    @ResponseBody
    public Response< List<Task> >taskList(@RequestBody Request<TaskQuery> param) {
        // 调用远程接口
        User user = ShiroUtil.getSubject();
        User dbUser = userService.getById(user.getId());
        String result = null;
        try {
            TaskQuery taskQuery = new TaskQuery();
            BeanUtils.copyProperties(taskQuery,dbUser);
            taskQuery.setPage(param.getPage());
            taskQuery.setLimit(param.getLimit());
            result = HttpClientUtil.doPost(taskUrl, JSONObject.toJSONString(taskQuery));
        } catch (Exception e) {
            throw new RuntimeException("远程服务器连接失败！");
        }
        TaskContent task = JSONObject.parseObject(result, TaskContent.class);
        Response<List<Task>> response=new Response<>();
        List<Task> tasks=new ArrayList<>();
        List content = task.getContent();
        for (int i=0;i<content.size();i++){
            JSONObject jsonObject = (JSONObject)content.get(i);
            Task otask = jsonObject.toJavaObject(Task.class);
            otask.setPicUrl(remoteUrl);
            tasks.add(otask);
        }
        response.setCode(0);
        response.setData(tasks);
        response.setCount(task.getTotalElements());
        return response;
    }


    @PostMapping("/ownTask/myRobTaskList")
    @ResponseBody
    public Response< List<RobTask> >myRobTaskList(@RequestBody Request<TaskQuery> param) {
        // 调用远程接口
        User user = ShiroUtil.getSubject();
        User dbUser = userService.getById(user.getId());
        String result = null;
        try {
            TaskQuery taskQuery = new TaskQuery();
            BeanUtils.copyProperties(taskQuery,dbUser);
            taskQuery.setPage(param.getPage());
            taskQuery.setLimit(param.getLimit());
            result = HttpClientUtil.doPost(robTaskUrl, JSONObject.toJSONString(taskQuery));
        } catch (Exception e) {
            throw new RuntimeException("远程服务器连接失败！");
        }
        TaskContent task = JSONObject.parseObject(result, TaskContent.class);
        Response<List<RobTask>> response=new Response<>();
        response.setCode(0);
        response.setData(task.getContent());
        response.setCount(task.getTotalElements());
        return response;
    }

    @PostMapping("/ownTask/rob")
    @ResponseBody
    public  Response robOrder(@RequestBody Task param) {
        // 调用远程接口
        User user = ShiroUtil.getSubject();
        User dbUser = userService.getById(user.getId());
        param.setCUserId(dbUser.getId());
        param.setCUserName(dbUser.getUsername());
        param.setCNickName(dbUser.getNickname());
        param.setWangwangId(dbUser.getWangwangId());
        param.setQq(dbUser.getQq());
        param.setPddId(dbUser.getPddId());
        param.setDyId(dbUser.getDyId());
        param.setJdId(dbUser.getJdId());
        String result = HttpClientUtil.doPost(robOrderUrl, JSONObject.toJSONString(param));
        Response response = JSONObject.parseObject(result, Response.class);
        return response;
    }


    @GetMapping("/ownTask/uploadPay/{id}")
    public  String uploadPay(@PathVariable("id") int  id ,Model model) {
            model.addAttribute("id",id);
            model.addAttribute("remoteUrl",remoteUrl);
            return "/userCenter/ownTask/viewPic";
    }


    @PostMapping("/ownTask/updateRobTask")
    @ResponseBody
    public ResultVo updateRobTask(@RequestBody RobTask robTask) {
        ResultVo resultVo=new ResultVo();
        String result = HttpClientUtil.doPost(changeRobTaskStatus, JSONObject.toJSONString(robTask));
        Response response = JSONObject.parseObject(result, Response.class);
        resultVo.setMsg(response.getMsg());
        resultVo.setCode(response.getCode());
        return resultVo;
    }



    /**
     * 我的积分
     */
    @GetMapping("/getIntegral")
    @RequiresPermissions("userCenter:getIntegral")
    public String getIntegral(Model model) {
        User user = ShiroUtil.getSubject();
        //1、根据用户名查询该用户的积分
        Integral integral = integralService.getIntegralByUserName(user.getUsername());
        if (integral ==null){
            integral=new Integral();
        }
        model.addAttribute("integral", integral);
        return "/userCenter/integral";
    }


/**
 * 显示提取积分界面
 */
    @GetMapping("/submitPoint")
    @RequiresPermissions("userCenter:submitPoint")
    public String sbmitPoint(Model model) {
        User user = ShiroUtil.getSubject();
        //1、根据用户名查询该用户的积分
        Integral integral = integralService.getIntegralByUserName(user.getUsername());
        if (integral==null) integral=new Integral();
        model.addAttribute("integral", integral);
        return "/userCenter/submitPoint_pop";
    }


    /**
     * 提现功能
     */
    @PostMapping("/auditPoint")
    @ResponseBody
    public ResultVo submitPoint(Integer point){
        if (point==null){
            return ResultVoUtil.error("提现的积分不能为空");
        }
        String username = ShiroUtil.getSubject().getUsername();
        try {
            integralService.submitPoint(point,username);
        } catch (Exception e) {
            ResultVo resultVo=new ResultVo();
            resultVo.setCode(-1);
            resultVo.setMsg(e.getMessage());
            return resultVo;
        }
        return ResultVoUtil.success("提取申请成功，等待审核发放！");
    }



}
