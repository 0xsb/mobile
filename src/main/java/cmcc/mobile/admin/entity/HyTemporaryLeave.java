package cmcc.mobile.admin.entity;

import java.math.BigDecimal;

public class HyTemporaryLeave {
    private String id;

    private String leavetype;

    private String leavereason;

    private BigDecimal leaveday;

    private String startdate;

    private String enddata;

    private String approvalTableConfigId;

    private String orgId;

    private String userId;

    private String flowId;

    private String leavedate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getLeavetype() {
        return leavetype;
    }

    public void setLeavetype(String leavetype) {
        this.leavetype = leavetype == null ? null : leavetype.trim();
    }

    public String getLeavereason() {
        return leavereason;
    }

    public void setLeavereason(String leavereason) {
        this.leavereason = leavereason == null ? null : leavereason.trim();
    }

    public BigDecimal getLeaveday() {
        return leaveday;
    }

    public void setLeaveday(BigDecimal leaveday) {
        this.leaveday = leaveday;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate == null ? null : startdate.trim();
    }

    public String getEnddata() {
        return enddata;
    }

    public void setEnddata(String enddata) {
        this.enddata = enddata == null ? null : enddata.trim();
    }

    public String getApprovalTableConfigId() {
        return approvalTableConfigId;
    }

    public void setApprovalTableConfigId(String approvalTableConfigId) {
        this.approvalTableConfigId = approvalTableConfigId == null ? null : approvalTableConfigId.trim();
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

    public String getLeavedate() {
        return leavedate;
    }

    public void setLeavedate(String leavedate) {
        this.leavedate = leavedate == null ? null : leavedate.trim();
    }
}