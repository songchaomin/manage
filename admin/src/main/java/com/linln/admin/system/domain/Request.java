package com.linln.admin.system.domain;

import lombok.Data;

@Data
public class Request<T> {
    public int limit;
    public int page;
    public T param;
}
