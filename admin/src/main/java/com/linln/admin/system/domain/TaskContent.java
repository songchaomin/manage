package com.linln.admin.system.domain;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
public class TaskContent <T> {
    public List<T> content;
    public int totalPages;
    public int totalElements;
    public boolean last;
    public int size;
    public int number;
    public int numberOfElements;
    public boolean first;
    public boolean empty;
    public Pageable pageable;


}
