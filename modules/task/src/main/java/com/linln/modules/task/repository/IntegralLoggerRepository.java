package com.linln.modules.task.repository;

import com.linln.modules.task.domain.IntegralLogger;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IntegralLoggerRepository extends BaseRepository<IntegralLogger,Long>, JpaSpecificationExecutor<IntegralLogger> {



    @Transactional
    @Modifying
    @Query(value = "update integral_logger set audit_status=1 where id=?1",nativeQuery = true)
    void updateLoggerStatus(Long id);
}
