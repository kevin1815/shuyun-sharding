package com.shuyun.request;

public class MemberInfo {

    private String tenantId;
    private String name;
    private String created;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
                ", name='" + name + '\'' +
                ", created=" + created +
                '}';
    }
}
