package cmcc.mobile.admin.entity;

import java.math.BigDecimal;

public class HyTemporarytrip {
    private String id;

    private String userId;

    private String orgId;

    private String flowId;

    private String approvalTableConfigId;

    private BigDecimal tripday;

    private String startdate;

    private String enddate;

    private String triplocale;

    private String appleDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
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

    public BigDecimal getTripday() {
        return tripday;
    }

    public void setTripday(BigDecimal tripday) {
        this.tripday = tripday;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate == null ? null : startdate.trim();
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate == null ? null : enddate.trim();
    }

    public String getTriplocale() {
        return triplocale;
    }

    public void setTriplocale(String triplocale) {
        this.triplocale = triplocale == null ? null : triplocale.trim();
    }

    public String getAppleDate() {
        return appleDate;
    }

    public void setAppleDate(String appleDate) {
        this.appleDate = appleDate == null ? null : appleDate.trim();
    }
}