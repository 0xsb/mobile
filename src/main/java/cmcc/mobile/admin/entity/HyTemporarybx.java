package cmcc.mobile.admin.entity;

import java.math.BigDecimal;

public class HyTemporarybx {
    private String id;

    private String bxType;

    private BigDecimal bxMoney;

    private String bxDate;

    private String orgId;

    private String userId;

    private String flowId;

    private String approvalTableConfigId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getBxType() {
        return bxType;
    }

    public void setBxType(String bxType) {
        this.bxType = bxType == null ? null : bxType.trim();
    }

    public BigDecimal getBxMoney() {
        return bxMoney;
    }

    public void setBxMoney(BigDecimal bxMoney) {
        this.bxMoney = bxMoney;
    }

    public String getBxDate() {
        return bxDate;
    }

    public void setBxDate(String bxDate) {
        this.bxDate = bxDate == null ? null : bxDate.trim();
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId == null ? null : flowId.trim();
    }

    public String getApprovalTableConfigId() {
        return approvalTableConfigId;
    }

    public void setApprovalTableConfigId(String approvalTableConfigId) {
        this.approvalTableConfigId = approvalTableConfigId == null ? null : approvalTableConfigId.trim();
    }
}