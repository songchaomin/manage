package com.linln.modules.task.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.task.domain.OwnTask;
import com.linln.modules.task.repository.OwnTaskRepository;
import com.linln.modules.task.service.OwnTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class OwnTaskServiceImpl implements OwnTaskService {

    @Autowired
    private OwnTaskRepository ownTaskRepository;

    @Override
    public Page<OwnTask> getPageList(OwnTask ownTask) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest(Sort.Direction.ASC);

        // 使用Specification复杂查询
        return ownTaskRepository.findAll(new Specification<OwnTask>(){

            @Override
            public Predicate toPredicate(Root<OwnTask> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> preList = new ArrayList<>();
                if(ownTask.getTaskName() != null){
                    preList.add(cb.equal(root.get("taskName").as(String.class), ownTask.getTaskName()));
                }

                Predicate[] pres = new Predicate[preList.size()];
                return query.where(preList.toArray(pres)).getRestriction();
            }

        }, page);
    }

    @Override
    public OwnTask save(OwnTask ownTask) {
        return ownTaskRepository.save(ownTask);
    }

    @Override
    public OwnTask getById(Long id) {
        return ownTaskRepository.findById(id).orElse(null);
    }
}
