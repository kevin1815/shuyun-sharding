package com.shuyun.service;

import com.shuyun.request.JobInfo;
import com.shuyun.request.MemberInfo;

public interface CrudService {
    void save(MemberInfo memberInfo);

    void save(JobInfo jobInfo);
}
