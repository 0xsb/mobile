package cmcc.mobile.admin.entity;

public class ApprovalData {
    private String flowId;

    private String draftDate;

    private Integer num;

    private String status;

    private String approvalTableConfigId;

    private String userId;

    private String isDefinition;

    private String thirdId;

    private String defaultStartUsers;

    private String companyId;

    private String jsonData;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId == null ? null : flowId.trim();
    }

    public String getDraftDate() {
        return draftDate;
    }

    public void setDraftDate(String draftDate) {
        this.draftDate = draftDate == null ? null : draftDate.trim();
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getApprovalTableConfigId() {
        return approvalTableConfigId;
    }

    public void setApprovalTableConfigId(String approvalTableConfigId) {
        this.approvalTableConfigId = approvalTableConfigId == null ? null : approvalTableConfigId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getIsDefinition() {
        return isDefinition;
    }

    public void setIsDefinition(String isDefinition) {
        this.isDefinition = isDefinition == null ? null : isDefinition.trim();
    }

    public String getThirdId() {
        return thirdId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId == null ? null : thirdId.trim();
    }

    public String getDefaultStartUsers() {
        return defaultStartUsers;
    }

    public void setDefaultStartUsers(String defaultStartUsers) {
        this.defaultStartUsers = defaultStartUsers == null ? null : defaultStartUsers.trim();
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId == null ? null : companyId.trim();
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData == null ? null : jsonData.trim();
    }
}