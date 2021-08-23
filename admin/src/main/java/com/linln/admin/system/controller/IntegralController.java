package com.linln.admin.system.controller;

import com.linln.admin.system.domain.IntegralResquest;
import com.linln.admin.system.validator.IntegralValid;
import com.linln.common.enums.ResultEnum;
import com.linln.common.exception.ResultException;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.action.SaveAction;
import com.linln.component.actionLog.annotation.ActionLog;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.service.UserService;
import com.linln.modules.task.domain.Integral;
import com.linln.modules.task.domain.IntegralLogger;
import com.linln.modules.task.service.IntegralLoggerService;
import com.linln.modules.task.service.IntegralService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/integral")
public class IntegralController {
    @Autowired
    private IntegralService integralService;
    @Autowired
    private IntegralLoggerService integralLoggerService;

    @Autowired
    private UserService userService;


    @GetMapping("/index")
    @RequiresPermissions("integral:index")
    public String index(Model model, Integral integral){
        integral.setDeleteFlg((byte)0);
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("shopName", match -> match.contains());
        Example<Integral> example = Example.of(integral, matcher);
        Page<Integral> list = integralService.getPageList(example);
        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/integral/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("integral:add")
    public String toAdd(){
        return "/integral/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("integral:edit")
    public String toEdit(@PathVariable("id") Long id, Model model){
        Integral integral = integralService.getIntegralById(id);
        model.addAttribute("integral", integral);
        return "/integral/edit";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping({"/add"})
    @RequiresPermissions({"integral:add"})
    @ResponseBody
    @ActionLog(name = "店铺管理", message = "店铺：${userName}", action = SaveAction.class)
    public ResultVo save(@Validated IntegralValid valid, @EntityParam Integral integral){
        //判断是否存在

        //判断是否重复
        if (integralService.repeatByUserName(integral.getUserName())) {
        throw new ResultException(-1,"用户名称重复！");
        }
        integral.setDeleteFlg((byte)0);
        integral.setCreateDate(new Date());
        integral.setUpdateDate(new Date());
        integralService.save(integral);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @ResponseBody
    @PostMapping("/edit")
    @RequiresPermissions("integral:edit")
    public ResultVo toDetail(@Validated IntegralValid valid, @EntityParam Integral integral){
        integral.setUpdateDate(new Date());
        integralService.update(integral);
        return ResultVoUtil.SAVE_SUCCESS;
    }


    @GetMapping("/delete/{id}")
    @RequiresPermissions("integral:delete")
    @ResponseBody
    public ResultVo delete(@PathVariable("id") Long id, Model model){
       integralService.deleteIntegral(id);
        return  ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * B端调用更新积分
     * @param request :积分对象
     */

    @PostMapping("/updateUserIntegral")
    @ResponseBody
    public ResultVo updateUserIntegral(@RequestBody IntegralResquest request){
        //更新C端用户的积分
        integralService.updateUserIntegral(request.getIntegral(),request.getUserName());
        //找到C端用户的上级培训师
        User user=new User();
        user.setId(Long.valueOf(request.getUserName()));
        User tran = userService.getTran(user);
        if (tran!=null){
            //给培训师增加积分
            integralService.updateUserIntegral(request.getManageIntegral(),tran.getUsername());
        }else{
            //给ADMIN增加积分
        }
        addIntegral(request.getUserName(),request.getIntegral(),request.getOperatorName(),"抢单操作c佣金");
        addIntegral(tran.getUsername(),request.getManageIntegral(),request.getOperatorName(),"抢单操作管理佣金");
        return  ResultVoUtil.SAVE_SUCCESS;
    }

    private void addIntegral(String userName,int integral,String operatorName,String type) {
        IntegralLogger integralLogger=new IntegralLogger();
        integralLogger.setBusinessType(type);
        integralLogger.setUserName(userName);
        integralLogger.setOperatorType("增加");
        integralLogger.setPoint(integral);
        integralLogger.setOperatorName(operatorName);
        integralLogger.setCreateDate(new Date());
        integralLogger.setDeleteFlg((byte)0);
        integralLogger.setAuditStatus((byte)1);
        integralLoggerService.addIntegralLogger(integralLogger);
    }


}
