package com.shuyun.request;

public class JobInfo {

    private String tenantId;
    private Integer jobId;
    private String name;
    private String created;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "{" +
                "tenantId='" + tenantId + '\'' +
                ", jobId=" + jobId +
                ", name='" + name + '\'' +
                ", created='" + created + '\'' +
                '}';
    }

}
