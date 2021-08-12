package com.linln.modules.task.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.task.domain.Integral;
import com.linln.modules.task.domain.IntegralLogger;
import com.linln.modules.task.repository.IntegralLoggerRepository;
import com.linln.modules.task.repository.IntegralRepository;
import com.linln.modules.task.service.IntegralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class IntegralServiceImpl implements IntegralService {
    @Autowired
    private IntegralRepository integralRepository;

    @Autowired
    private IntegralLoggerRepository integralLoggerRepository;

    @Override
    public Page<Integral> getPageList(Example<Integral> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest(Sort.Direction.ASC);
        return integralRepository.findAll(example, page);
    }

    @Override
    public Integral save(Integral integral) {
        return integralRepository.save(integral);
    }

    @Override
    public boolean repeatByUserName(String userName) {
        return integralRepository.getIntegralByUserName(userName)!=null;
    }

    @Override
    public Integer deleteIntegral(Long id) {
        return integralRepository.deleteIntegral(id);
    }

    @Override
    public Integral getIntegralById(Long id) {
        return integralRepository.getOne(id);
    }

    @Override
    public Integer update(Integral integral) {
        return integralRepository.updatePoint(integral.getUserName(),integral.getPoint());
    }

    @Override
    public Integral getIntegralByUserName(String username) {
        return integralRepository.getIntegralByUserName(username);
    }

    /**
     * 积分增减
     * @param point
     */
    @Override
    public void addIntegral(int point,String userName) {
        integralRepository.addIntegral(point,userName);
    }

    @Override
    public void updateUserIntegral(Integer integral, String userName) {
        //更新成功
        integralRepository.updateUserIntegral(integral,userName);
    }

    @Override
    @Transactional
    public void submitPoint(Integer point, String username) {
        //判断本次扣减的积分是否足够
        Integral integralByUserName = integralRepository.getIntegralByUserName(username);
        if(integralByUserName ==null){
            throw new RuntimeException("积分不够，本次提取不成功");
        }
        if (integralByUserName.getPoint()<point){
            throw new RuntimeException("积分不够，本次提取不成功");
        }
        //2、增加流水
        IntegralLogger integralLogger=new IntegralLogger();
        integralLogger.setOperatorType("积分提现");
        integralLogger.setPoint(point);
        integralLogger.setUserName(username);
        integralLogger.setCreateDate(new Date());
        integralLogger.setDeleteFlg((byte)0);
        integralLogger.setAuditStatus((byte)0);
        integralLoggerRepository.save(integralLogger);
    }
}
