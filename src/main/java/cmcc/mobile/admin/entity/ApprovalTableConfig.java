package cmcc.mobile.admin.entity;

public class ApprovalTableConfig {
    private String id;

    private String date;

    private String defaultApprovalUserIds;

    private String status;

    private String approvalTypeId;

    private String lastUserId;

    private String lastDealWay;

    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public String getDefaultApprovalUserIds() {
        return defaultApprovalUserIds;
    }

    public void setDefaultApprovalUserIds(String defaultApprovalUserIds) {
        this.defaultApprovalUserIds = defaultApprovalUserIds == null ? null : defaultApprovalUserIds.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getApprovalTypeId() {
        return approvalTypeId;
    }

    public void setApprovalTypeId(String approvalTypeId) {
        this.approvalTypeId = approvalTypeId == null ? null : approvalTypeId.trim();
    }

    public String getLastUserId() {
        return lastUserId;
    }

    public void setLastUserId(String lastUserId) {
        this.lastUserId = lastUserId == null ? null : lastUserId.trim();
    }

    public String getLastDealWay() {
        return lastDealWay;
    }

    public void setLastDealWay(String lastDealWay) {
        this.lastDealWay = lastDealWay == null ? null : lastDealWay.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }
}