package com.linln.modules.system.repository;

import com.linln.modules.system.domain.TgLinkLog;
import com.linln.modules.system.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
public interface TgLinkLogRepository extends JpaRepository<TgLinkLog, Long>, JpaSpecificationExecutor<TgLinkLog> {
    @Transactional
    @Modifying
    @Query(value="update tg_link_log set effective=:effective where tg_link=:tgLink ",nativeQuery =true)
    int updateTgLinkSatus(@Param("effective") byte effective,@Param("tgLink") String tgLink);

    @Query(value="select * from  tg_link_log where tg_link=:tgLink ",nativeQuery =true)
    TgLinkLog getTgLikLog(@Param("tgLink") String tgLink);
}
