package com.linln.modules.system.service.impl;

import com.linln.modules.system.domain.TgLinkLog;
import com.linln.modules.system.repository.TgLinkLogRepository;
import com.linln.modules.system.service.TgLinkLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TgLinkLogServiceImpl implements TgLinkLogService {
    @Autowired
    private TgLinkLogRepository tgLinkLogRepository;
    @Override
    public int updateTgLinkLogEffective(byte effective, String tgLink) {
        return tgLinkLogRepository.updateTgLinkSatus(effective,tgLink);
    }

    @Override
    public TgLinkLog  getTgLinkLog(String tgLink) {
        return tgLinkLogRepository.getTgLikLog(tgLink);
    }

    @Override
    public TgLinkLog insertTgLinkLog(TgLinkLog tgLinkLog) {
        return tgLinkLogRepository.save(tgLinkLog);
    }
}
