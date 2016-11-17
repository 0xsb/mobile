package cmcc.mobile.admin.entity;

public class ApprovalAuthority {
    private String id;

    private String userids;

    private String roleId;

    private String createTime;

    private String createUserId;

    private String updateTime ;
    
    private String updateUserId ;
    
    private String companyId ;
    
    private String wyyId ;
    
    private String reportUserIds ;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserids() {
        return userids;
    }

    public void setUserids(String userids) {
        this.userids = userids == null ? null : userids.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getWyyId() {
		return wyyId;
	}

	public void setWyyId(String wyyId) {
		this.wyyId = wyyId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getReportUserIds() {
		return reportUserIds;
	}

	public void setReportUserIds(String reportUserIds) {
		this.reportUserIds = reportUserIds;
	}
    
    
}