package cmcc.mobile.admin.entity;

import java.util.Date;

public class ApprovalBatchTask {
    private Long id;

    private String taskName;

    private String createUserId;

    private Date createTime;

    private Integer initdtUsers;

    private Integer tocreatetaskUsers;

    private Integer createdtaskUsers;

    private Integer donetaskUsers;

    private Integer completetaskUsers;

    private Integer undotaskUsers;

    private Integer status;

    private String todo1;

    private String todo2;

    private String approvalTypeId;

    private String companyId;

    private Date startTime;

    private String startUserId;

    private Date revokeTime;

    private Date cancelTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getInitdtUsers() {
        return initdtUsers;
    }

    public void setInitdtUsers(Integer initdtUsers) {
        this.initdtUsers = initdtUsers;
    }

    public Integer getTocreatetaskUsers() {
        return tocreatetaskUsers;
    }

    public void setTocreatetaskUsers(Integer tocreatetaskUsers) {
        this.tocreatetaskUsers = tocreatetaskUsers;
    }

    public Integer getCreatedtaskUsers() {
        return createdtaskUsers;
    }

    public void setCreatedtaskUsers(Integer createdtaskUsers) {
        this.createdtaskUsers = createdtaskUsers;
    }

    public Integer getDonetaskUsers() {
        return donetaskUsers;
    }

    public void setDonetaskUsers(Integer donetaskUsers) {
        this.donetaskUsers = donetaskUsers;
    }

    public Integer getCompletetaskUsers() {
        return completetaskUsers;
    }

    public void setCompletetaskUsers(Integer completetaskUsers) {
        this.completetaskUsers = completetaskUsers;
    }

    public Integer getUndotaskUsers() {
        return undotaskUsers;
    }

    public void setUndotaskUsers(Integer undotaskUsers) {
        this.undotaskUsers = undotaskUsers;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTodo1() {
        return todo1;
    }

    public void setTodo1(String todo1) {
        this.todo1 = todo1 == null ? null : todo1.trim();
    }

    public String getTodo2() {
        return todo2;
    }

    public void setTodo2(String todo2) {
        this.todo2 = todo2 == null ? null : todo2.trim();
    }

    public String getApprovalTypeId() {
        return approvalTypeId;
    }

    public void setApprovalTypeId(String approvalTypeId) {
        this.approvalTypeId = approvalTypeId == null ? null : approvalTypeId.trim();
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId == null ? null : companyId.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId == null ? null : startUserId.trim();
    }

    public Date getRevokeTime() {
        return revokeTime;
    }

    public void setRevokeTime(Date revokeTime) {
        this.revokeTime = revokeTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }
}