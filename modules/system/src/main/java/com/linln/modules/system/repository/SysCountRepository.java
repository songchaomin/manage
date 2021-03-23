package com.linln.modules.system.repository;

import com.linln.modules.system.domain.Dept;
import com.linln.modules.system.domain.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
public interface SysCountRepository extends BaseRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Transactional
    @Modifying
    @Query(value="update sys_count set member_no=:count ",nativeQuery =true)
    int updateSysCount(@Param("count") int count);

    @Query(value="select member_no from  sys_count ",nativeQuery =true)
    int getSysCount();
}
