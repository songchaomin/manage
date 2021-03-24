package com.linln.modules.system.service.impl;

import com.linln.modules.system.repository.SysCountRepository;
import com.linln.modules.system.service.SysCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysCountServiceImpl implements SysCountService {
    @Autowired
    private SysCountRepository sysCountRepository;


    @Override
    public synchronized int getSysCount() {
        int sysCount = sysCountRepository.getSysCount();
        int newCount=sysCount+1;
        updateSysCount(newCount);
        return sysCount;
    }

    @Override
    public int updateSysCount(int count) {
        return sysCountRepository.updateSysCount(count);
    }

}
