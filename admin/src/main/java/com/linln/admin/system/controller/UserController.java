package com.linln.admin.system.controller;

import com.google.common.collect.ImmutableSet;
import com.linln.admin.system.validator.UserRegisterValid;
import com.linln.admin.system.validator.UserValid;
import com.linln.common.constant.AdminConst;
import com.linln.common.enums.ResultEnum;
import com.linln.common.enums.StatusEnum;
import com.linln.common.exception.ResultException;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.SpringContextUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.action.StatusAction;
import com.linln.component.actionLog.action.UserAction;
import com.linln.component.actionLog.annotation.ActionLog;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.excel.ExcelUtil;
import com.linln.component.fileUpload.config.properties.UploadProjectProperties;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.Dept;
import com.linln.modules.system.domain.Role;
import com.linln.modules.system.domain.TgLinkLog;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.repository.UserRepository;
import com.linln.modules.system.service.RoleService;
import com.linln.modules.system.service.SysCountService;
import com.linln.modules.system.service.TgLinkLogService;
import com.linln.modules.system.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SysCountService sysCountService;

    @Autowired
    private TgLinkLogService tgLinkLogService;
    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:user:index")
    public String index(Model model, User user) {

        // 获取用户列表
        Page<User> list = userService.getPageList(user);

        list.stream().forEach(t->{
            String tranName = userService.getTranName(t);
            t.setEmail(tranName);
        });

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        model.addAttribute("dept", user.getDept());
        return "/system/user/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:user:add")
    public String toAdd() {
        return "/system/user/add";
    }


    @GetMapping("/register/{pid}/{uuid}")
    public String registerLogin(@PathVariable("pid") Long pid ,@PathVariable("uuid")String uuid, Model model) {
        //判断PID是否存在，不存在不允许注册
        User user = userService.getById(pid);
        if(user ==null){
            model.addAttribute("msg", "系统中不存在推广的用户编码！");
            return "/system/user/regiestError";
        }
        //判断推广码是否有效
        TgLinkLog tgLinkLog = tgLinkLogService.getTgLinkLog(uuid);
        if(tgLinkLog.getEffective()==1){
            model.addAttribute("msg", "推广码已经失效,请联系推广人,重新获取推广码！");
            return "/system/user/regiestError";
        }
        //获取会员编码
        int sysCount = sysCountService.getSysCount();
        model.addAttribute("user", pid);
        model.addAttribute("memberNo", sysCount);
        model.addAttribute("uuid", uuid);
        return "/system/user/register";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     * @param user 实体对象
     */
    @PostMapping("/register")
    @ResponseBody
    public ResultVo register(@Validated UserRegisterValid valid, @EntityParam User user, HttpServletRequest request) {
        ResultVo resultVo=new ResultVo();
        // 验证数据是否合格
        if (user.getId() == null) {
            // 判断密码是否为空
            if (user.getPassword().isEmpty() || "".equals(user.getPassword().trim())) {
                throw new ResultException(ResultEnum.USER_PWD_NULL);
            }
            // 判断两次密码是否一致
            if (!user.getPassword().equals(valid.getConfirm())) {
                throw new ResultException(ResultEnum.USER_INEQUALITY);
            }
            // 对密码进行加密
            String salt = ShiroUtil.getRandomSalt();
            String encrypt = ShiroUtil.encrypt(user.getPassword(), salt);
            user.setPassword(encrypt);
            user.setSalt(salt);
        }
        // 判断wangwangId是否重复
        if (userService.repeatBywangwangId(user)) {
            throw new ResultException(ResultEnum.USER_wangwang_ERROR);
        }



        // 复制保留无需修改的数据
        if (user.getId() != null) {
            // 不允许操作超级管理员数据
            if (user.getId().equals(AdminConst.ADMIN_ID) &&
                    !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)) {
                throw new ResultException(ResultEnum.NO_ADMIN_AUTH);
            }
            User beUser = userService.getById(user.getId());
            String[] fields = {"password", "salt", "picture", "roles"};
            EntityBeanUtil.copyProperties(beUser, user, fields);
        }
        Dept  dept=new Dept();
        dept.setId(1L);
        user.setDept(dept);
        Role role=new Role();
        role.setId(2L);
        user.setRoles(ImmutableSet.of(role));
        user.setStatus((byte)2);
        // 保存数据
        userService.save(user);
        String uuid= UUID.randomUUID().toString();
        String requestURL = request.getRequestURL()+"/"+user.getId()+"/"+uuid;
        //更新推广链接
        userService.updateTgLink(requestURL,user.getId());
        //新增链接日志
        TgLinkLog tgLinkLog=new TgLinkLog();
        tgLinkLog.setTgLink(uuid);
        tgLinkLog.setEffective((byte)0);
        tgLinkLogService.insertTgLinkLog(tgLinkLog);
        //失效旧的链接码
        tgLinkLogService.updateTgLinkLogEffective((byte)1,user.getUuid());
        resultVo.setMsg("注册成功！3秒后进入登陆界面");
        String loginUrl= request.getScheme()+"://"+request.getServerName()+"/login?"+user.getUsername();
        resultVo.setData(loginUrl);
        resultVo.setCode(200);
        return resultVo;
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:user:edit")
    public String toEdit(@PathVariable("id") User user, Model model) {
        model.addAttribute("user", user);
        return "/system/user/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     * @param user 实体对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:user:add", "system:user:edit"})
    @ResponseBody
    @ActionLog(key = UserAction.USER_SAVE, action = UserAction.class)
    public ResultVo save(@Validated UserValid valid, @EntityParam User user) {

        // 验证数据是否合格
        if (user.getId() == null) {

            // 判断密码是否为空
            if (user.getPassword().isEmpty() || "".equals(user.getPassword().trim())) {
                throw new ResultException(ResultEnum.USER_PWD_NULL);
            }

            // 判断两次密码是否一致
            if (!user.getPassword().equals(valid.getConfirm())) {
                throw new ResultException(ResultEnum.USER_INEQUALITY);
            }

            // 对密码进行加密
            String salt = ShiroUtil.getRandomSalt();
            String encrypt = ShiroUtil.encrypt(user.getPassword(), salt);
            user.setPassword(encrypt);
            user.setSalt(salt);
            user.setStatus((byte)2);
        }

        // 判断用户名是否重复
        if (userService.repeatByUsername(user)) {
            throw new ResultException(ResultEnum.USER_EXIST);
        }

        // 复制保留无需修改的数据
        if (user.getId() != null) {
            // 不允许操作超级管理员数据
            if (user.getId().equals(AdminConst.ADMIN_ID) &&
                    !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)) {
                throw new ResultException(ResultEnum.NO_ADMIN_AUTH);
            }

            User beUser = userService.getById(user.getId());
            String[] fields = {"password", "salt", "picture", "roles"};
            EntityBeanUtil.copyProperties(beUser, user, fields);
        }

        // 保存数据
        userService.save(user);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:user:detail")
    public String toDetail(@PathVariable("id") User user, Model model) {
        String [] idCardPics=user.getIdCardPic()==null ? new String[0] :user.getIdCardPic().split(",");
        String [] bankCardPics=user.getBankCardPic()==null?new String[0]:user.getBankCardPic().split(",");
        String [] receveAddressPics=user.getReceveAddressPic()==null?new String[0]:user.getReceveAddressPic().split(",");
        String [] shppingXyPics=user.getShppingXyPic()==null?new String[0]:user.getShppingXyPic().split(",");
        String [] taobaoPics=user.getTaobaoPic()==null?new String[0]:user.getTaobaoPic().split(",");
        String [] payPics=user.getPayPic()==null?new String[0]:user.getPayPic().split(",");
        model.addAttribute("user", user);
        model.addAttribute("idCardPics", idCardPics);
        model.addAttribute("bankCardPics", bankCardPics);
        model.addAttribute("receveAddressPics", receveAddressPics);
        model.addAttribute("shppingXyPics", shppingXyPics);
        model.addAttribute("taobaoPics", taobaoPics);
        model.addAttribute("payPics", payPics);
        return "/system/user/detail";
    }

    /**
     * 跳转到修改密码页面
     */
    @GetMapping("/pwd")
    @RequiresPermissions("system:user:pwd")
    public String toEditPassword(Model model, @RequestParam(value = "ids", required = false) List<Long> ids) {
        model.addAttribute("idList", ids);
        return "/system/user/pwd";
    }

    /**
     * 修改密码
     */
    @PostMapping("/pwd")
    @RequiresPermissions("system:user:pwd")
    @ResponseBody
    @ActionLog(key = UserAction.EDIT_PWD, action = UserAction.class)
    public ResultVo editPassword(String password, String confirm,
                                 @RequestParam(value = "ids", required = false) List<Long> ids,
                                 @RequestParam(value = "ids", required = false) List<User> users) {

        // 判断密码是否为空
        if (password.isEmpty() || "".equals(password.trim())) {
            throw new ResultException(ResultEnum.USER_PWD_NULL);
        }

        // 判断两次密码是否一致
        if (!password.equals(confirm)) {
            throw new ResultException(ResultEnum.USER_INEQUALITY);
        }

        // 不允许操作超级管理员数据
        if (ids.contains(AdminConst.ADMIN_ID) &&
                !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)) {
            throw new ResultException(ResultEnum.NO_ADMIN_AUTH);
        }

        // 修改密码，对密码进行加密
        users.forEach(user -> {
            String salt = ShiroUtil.getRandomSalt();
            String encrypt = ShiroUtil.encrypt(password, salt);
            user.setPassword(encrypt);
            user.setSalt(salt);
        });

        // 保存数据
        userService.save(users);
        return ResultVoUtil.success("修改成功");
    }

    /**
     * 跳转到角色分配页面
     */
    @GetMapping("/role")
    @RequiresPermissions("system:user:role")
    public String toRole(@RequestParam(value = "ids") User user, Model model) {
        // 获取指定用户角色列表
        Set<Role> authRoles = user.getRoles();
        // 获取全部角色列表
        Sort sort = new Sort(Sort.Direction.ASC, "createDate");
        List<Role> list = roleService.getListBySortOk(sort);

        model.addAttribute("id", user.getId());
        model.addAttribute("list", list);
        model.addAttribute("authRoles", authRoles);
        return "/system/user/role";
    }

    /**
     * 保存角色分配信息
     */
    @PostMapping("/role")
    @RequiresPermissions("system:user:role")
    @ResponseBody
    @ActionLog(key = UserAction.EDIT_ROLE, action = UserAction.class)
    public ResultVo auth(
            @RequestParam(value = "id", required = true) User user,
            @RequestParam(value = "roleId", required = false) HashSet<Role> roles) {

        // 不允许操作超级管理员数据
        if (user.getId().equals(AdminConst.ADMIN_ID) &&
                !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)) {
            throw new ResultException(ResultEnum.NO_ADMIN_AUTH);
        }

        // 更新用户角色
        user.setRoles(roles);

        // 保存数据
        userService.save(user);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 获取用户头像
     */
    @GetMapping("/picture")
    public void picture(String p, HttpServletResponse response) throws IOException {
        String defaultPath = "/images/user-picture.jpg";
        if (!(StringUtils.isEmpty(p) || p.equals(defaultPath))) {
            UploadProjectProperties properties = SpringContextUtil.getBean(UploadProjectProperties.class);
            String fuPath = properties.getFilePath();
            String spPath = properties.getStaticPath().replace("*", "");
            File file = new File(fuPath + p.replace(spPath, ""));
            if (file.exists()) {
                FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
                return;
            }
        }
        Resource resource = new ClassPathResource("static" + defaultPath);
        FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
    }

    /**
     * 导出用户数据
     */
    @GetMapping("/export")
    @RequiresPermissions("system:user:export")
    @ResponseBody
    public void exportExcel() {
        UserRepository userRepository = SpringContextUtil.getBean(UserRepository.class);
        ExcelUtil.exportExcel(User.class, userRepository.findAll());
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions({"system:user:status","system:user:audit"})
    @ResponseBody
    @ActionLog(name = "用户状态", action = StatusAction.class)
    public ResultVo updateStatus(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {

        // 不能修改超级管理员状态
        if (ids.contains(AdminConst.ADMIN_ID)) {
            throw new ResultException(ResultEnum.NO_ADMIN_STATUS);
        }

        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (userService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }


    @GetMapping("/userlistinfo")
    public String userlistinfo(Model model, User user) {
        // 获取用户列表
        User sessionUser = ShiroUtil.getSubject();
        if (!Objects.equals(sessionUser.getUsername(),"admin")) {
            user.setUsername(sessionUser.getUsername());
        }
        Page<User> list = userService.getPageList(user);
        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        model.addAttribute("dept", user.getDept());
        return "/integral/user_pop";
    }



}
