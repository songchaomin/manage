package com.linln.modules.system.service;

import com.linln.modules.system.domain.TgLinkLog;

public interface TgLinkLogService {
    int updateTgLinkLogEffective(byte effective,String tgLink);
    TgLinkLog getTgLinkLog(String tgLink);
    TgLinkLog insertTgLinkLog(TgLinkLog tgLinkLog);
}
