package com.linln.admin.system.controller;

import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
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
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;


@Controller
@RequestMapping("/integralLogger")
public class IntegralLoggerController {
    @Autowired
    private IntegralLoggerService integralLoggerService;
    @Autowired
    private IntegralService integralService;

    @GetMapping("/index")
    @RequiresPermissions("integralLogger:index")
    public String index(Model model, IntegralLogger integralLogger){
        integralLogger.setDeleteFlg((byte)0);
        integralLogger.setAuditStatus(null);
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("shopName", match -> match.contains());
        Example<IntegralLogger> example = Example.of(integralLogger, matcher);
        Page<IntegralLogger> list = integralLoggerService.getPageList(example);
        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/integralLogger/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("integralLoger:add")
    public String toAdd(){
        return "/integral/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("integralLogger:edit")
    public String toEdit(@PathVariable("id") Long id, Model model){
        IntegralLogger oldIntegralLogger = integralLoggerService.getIntegralLoggerById(id);
        model.addAttribute("integral", oldIntegralLogger);
        return "/integral/edit";
    }






    @GetMapping("/delete/{id}")
    @RequiresPermissions("integralLoger:delete")
    @ResponseBody
    public ResultVo delete(@PathVariable("id") Long id, Model model){
       integralLoggerService.deleteIntegralById(id);
        return  ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * B端调用更新积分
     */

    @GetMapping("/auditSubmitIntegral/{id}")
    @ResponseBody
    public ResultVo auditSubmitIntegral(@PathVariable("id") Long id, Model model){
        ResultVo resultVo=new ResultVo();
        IntegralLogger oldIntegralLogger = integralLoggerService.getIntegralLoggerById(id);
        if (oldIntegralLogger==null){
            resultVo.setCode(-1);
            resultVo.setMsg("积分提现申请单不存在，无法审批！");
            return resultVo;
        }

        if(oldIntegralLogger.getAuditStatus()==1){
            resultVo.setCode(-1);
            resultVo.setMsg("已经审核通过，无需再次审核！");
            return resultVo;
        }
        //在次判断积分是否足够
        //判断本次扣减的积分是否足够
        Integral integralByUserName = integralService.getIntegralByUserName(oldIntegralLogger.getUserName());
        if (integralByUserName.getPoint()<oldIntegralLogger.getPoint()){
            resultVo.setCode(-1);
            resultVo.setMsg("积分不够，本次提取不成功");
            return resultVo;
        }
        integralLoggerService.updateLoggerStatus(id);
        integralService.updateUserIntegral(oldIntegralLogger.getPoint()*(-1),oldIntegralLogger.getUserName());
        return  ResultVoUtil.SAVE_SUCCESS;
    }


}
