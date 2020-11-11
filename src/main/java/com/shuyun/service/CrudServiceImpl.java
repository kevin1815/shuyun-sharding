package com.shuyun.service;

import com.shuyun.request.JobInfo;
import com.shuyun.request.MemberInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class CrudServiceImpl implements CrudService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(MemberInfo memberInfo) {
        final String insert = "insert into t_member(tenant_id, name, created) values (?, ?, ?)";
        jdbcTemplate.execute(insert, (PreparedStatementCallback<Integer>) ps -> {
            ps.setString(1, memberInfo.getTenantId());
            ps.setString(2, memberInfo.getName());
            java.util.Date created = null;
            if (memberInfo.getCreated() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    created = dateFormat.parse(memberInfo.getCreated());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (created != null) {
                ps.setDate(3, new Date(created.getTime()));
            }
            return ps.executeUpdate();
        });
    }

    @Override
    public void save(JobInfo jobInfo) {
        final String insert =
                "insert into t_schedule_job(tenant_id, name, job_id, created) values (?, ?, ?, ?)";
        jdbcTemplate.execute(insert, (PreparedStatementCallback<Integer>) ps -> {
            ps.setString(1, jobInfo.getTenantId());
            ps.setString(2, jobInfo.getName());
            ps.setInt(3, jobInfo.getJobId());
            java.util.Date created = null;
            if (jobInfo.getCreated() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    created = dateFormat.parse(jobInfo.getCreated());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (created != null) {
                ps.setDate(4, new Date(created.getTime()));
            }
            return ps.executeUpdate();
        });
    }

}
