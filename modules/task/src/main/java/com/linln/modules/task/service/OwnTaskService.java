package com.linln.modules.task.service;

import com.linln.modules.task.domain.OwnTask;
import org.springframework.data.domain.Page;

public interface OwnTaskService {
    /**
     * 个人任务列表
     *
     * @param ownTask
     * @return
     */
    Page<OwnTask> getPageList(OwnTask ownTask);


    /**
     * 保存个人任务
     * @param ownTask
     * @return 个人任务
     */
    OwnTask save(OwnTask ownTask);

    /**
     * 根据ID查询个人任务
     * @param id
     * @return
     */
    OwnTask getById(Long id);
}
