package com.shuyun.controller;

import com.shuyun.request.JobInfo;
import com.shuyun.request.MemberInfo;
import com.shuyun.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurdController {

    @Autowired
    private CrudService crudService;

    @PostMapping("/save/member")
    public void saveMember(@RequestBody MemberInfo memberInfo) {
        this.crudService.save(memberInfo);
    }

    @PostMapping("/save/job")
    public void saveJob(@RequestBody JobInfo jobInfo) {
        this.crudService.save(jobInfo);
    }

}
