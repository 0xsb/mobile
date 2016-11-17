package cmcc.mobile.admin.entity;

public class ApprovalType {
    private String id;

    private String des;

    private String icon;

    private String name;

    private String remark;

    private String approvalTableConfigId;

    private String approvalMostTypeId;

    private String thirdApprovalStartLink;

    private String thirdCompanyId;

    private String isDefault;

    private String isBoutique;

    private String status;

    private String createTime;

    private String thirdConfigLink;

    private String companyId;

    private Integer scene;

    private String createUserId ;
    
    private String processUrl ;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des == null ? null : des.trim();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getApprovalTableConfigId() {
        return approvalTableConfigId;
    }

    public void setApprovalTableConfigId(String approvalTableConfigId) {
        this.approvalTableConfigId = approvalTableConfigId == null ? null : approvalTableConfigId.trim();
    }

    public String getApprovalMostTypeId() {
        return approvalMostTypeId;
    }

    public void setApprovalMostTypeId(String approvalMostTypeId) {
        this.approvalMostTypeId = approvalMostTypeId == null ? null : approvalMostTypeId.trim();
    }

    public String getThirdApprovalStartLink() {
        return thirdApprovalStartLink;
    }

    public void setThirdApprovalStartLink(String thirdApprovalStartLink) {
        this.thirdApprovalStartLink = thirdApprovalStartLink == null ? null : thirdApprovalStartLink.trim();
    }

    public String getThirdCompanyId() {
        return thirdCompanyId;
    }

    public void setThirdCompanyId(String thirdCompanyId) {
        this.thirdCompanyId = thirdCompanyId == null ? null : thirdCompanyId.trim();
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault == null ? null : isDefault.trim();
    }

    public String getIsBoutique() {
        return isBoutique;
    }

    public void setIsBoutique(String isBoutique) {
        this.isBoutique = isBoutique == null ? null : isBoutique.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getThirdConfigLink() {
        return thirdConfigLink;
    }

    public void setThirdConfigLink(String thirdConfigLink) {
        this.thirdConfigLink = thirdConfigLink == null ? null : thirdConfigLink.trim();
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId == null ? null : companyId.trim();
    }

    public Integer getScene() {
        return scene;
    }

    public void setScene(Integer scene) {
        this.scene = scene;
    }

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getProcessUrl() {
		return processUrl;
	}

	public void setProcessUrl(String processUrl) {
		this.processUrl = processUrl;
	}
    
    
}