package com.linln.modules.task.repository;

import com.linln.modules.task.domain.OwnTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OwnTaskRepository extends BaseRepository<OwnTask,Long>, JpaSpecificationExecutor<OwnTask> {
    @Modifying
    @Transactional
    @Query("update OwnTask o set o.deleteFlg = 1  where o.id= ?1 ")
    Integer deleteOwnTaskById(Long id);
}

