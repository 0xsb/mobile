package cmcc.mobile.admin.entity;

public class ApprovalRunManage {
    private String runId;

    private String arriveDate;

    private String examineDate;

    private String file;

    private String opinion;

    private String pic;

    private Integer runNo;

    private String runStatus;

    private String approvalDataId;

    private String userId;

    private String thirdId;

    private String companyId;
    
    private String wyyId ;

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId == null ? null : runId.trim();
    }

    public String getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(String arriveDate) {
        this.arriveDate = arriveDate == null ? null : arriveDate.trim();
    }

    public String getExamineDate() {
        return examineDate;
    }

    public void setExamineDate(String examineDate) {
        this.examineDate = examineDate == null ? null : examineDate.trim();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file == null ? null : file.trim();
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion == null ? null : opinion.trim();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public Integer getRunNo() {
        return runNo;
    }

    public void setRunNo(Integer runNo) {
        this.runNo = runNo;
    }

    public String getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus == null ? null : runStatus.trim();
    }

    public String getApprovalDataId() {
        return approvalDataId;
    }

    public void setApprovalDataId(String approvalDataId) {
        this.approvalDataId = approvalDataId == null ? null : approvalDataId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getThirdId() {
        return thirdId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId == null ? null : thirdId.trim();
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId == null ? null : companyId.trim();
    }

	public String getWyyId() {
		return wyyId;
	}

	public void setWyyId(String wyyId) {
		this.wyyId = wyyId;
	}
    
    
}