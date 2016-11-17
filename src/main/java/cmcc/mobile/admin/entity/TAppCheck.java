package cmcc.mobile.admin.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "t_app_check")
public class TAppCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 0:无密级，1：内部消息，2：普通商密，3：核心商密
     */
    private Integer confidentialityLevel;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 审批标题
     */
    private String title;

    /**
     * 审批内容
     */
    private String message;

    /**
     * 附件地址
     */
    private String accessoryUrl;

    /**
     * 审批编号
     */
    private String code;

    /**
     * 部门名字
     */
    private String departmentName;

    /**
     * 部门id
     */
    private String departmentId;

    /**
     * 下一审核人id
     */
    private String nextAssigneeId;

    private String nextAssigneeName;

    /**
     * 处理人名字
     */
    private String assigneeName;

    /**
     * 处理人id
     */
    private String assigneeId;

    /**
     * 审核意见
     */
    private String checkMessage;

    /**
     * 是否通过
     */
    private Boolean isPass;

    /**
     * 审批发起人
     */
    private String initiator;

    private Long PMId;

    private String procInstId;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取0:无密级，1：内部消息，2：普通商密，3：核心商密
     *
     * @return confidentialityLevel - 0:无密级，1：内部消息，2：普通商密，3：核心商密
     */
    public Integer getConfidentialityLevel() {
        return confidentialityLevel;
    }

    /**
     * 设置0:无密级，1：内部消息，2：普通商密，3：核心商密
     *
     * @param confidentialityLevel 0:无密级，1：内部消息，2：普通商密，3：核心商密
     */
    public void setConfidentialityLevel(Integer confidentialityLevel) {
        this.confidentialityLevel = confidentialityLevel;
    }

    /**
     * 获取创建时间
     *
     * @return createTime - 创建时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取结束时间
     *
     * @return endTime - 结束时间
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     *
     * @param endTime 结束时间
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取审批标题
     *
     * @return title - 审批标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置审批标题
     *
     * @param title 审批标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取审批内容
     *
     * @return message - 审批内容
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置审批内容
     *
     * @param message 审批内容
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取附件地址
     *
     * @return accessoryUrl - 附件地址
     */
    public String getAccessoryUrl() {
        return accessoryUrl;
    }

    /**
     * 设置附件地址
     *
     * @param accessoryUrl 附件地址
     */
    public void setAccessoryUrl(String accessoryUrl) {
        this.accessoryUrl = accessoryUrl;
    }

    /**
     * 获取审批编号
     *
     * @return code - 审批编号
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置审批编号
     *
     * @param code 审批编号
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取部门名字
     *
     * @return departmentName - 部门名字
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * 设置部门名字
     *
     * @param departmentName 部门名字
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * 获取部门id
     *
     * @return departmentId - 部门id
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置部门id
     *
     * @param departmentId 部门id
     */
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * 获取下一审核人id
     *
     * @return nextAssigneeId - 下一审核人id
     */
    public String getNextAssigneeId() {
        return nextAssigneeId;
    }

    /**
     * 设置下一审核人id
     *
     * @param nextAssigneeId 下一审核人id
     */
    public void setNextAssigneeId(String nextAssigneeId) {
        this.nextAssigneeId = nextAssigneeId;
    }

    /**
     * @return nextAssigneeName
     */
    public String getNextAssigneeName() {
        return nextAssigneeName;
    }

    /**
     * @param nextAssigneeName
     */
    public void setNextAssigneeName(String nextAssigneeName) {
        this.nextAssigneeName = nextAssigneeName;
    }

    /**
     * 获取处理人名字
     *
     * @return assigneeName - 处理人名字
     */
    public String getAssigneeName() {
        return assigneeName;
    }

    /**
     * 设置处理人名字
     *
     * @param assigneeName 处理人名字
     */
    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    /**
     * 获取处理人id
     *
     * @return assigneeId - 处理人id
     */
    public String getAssigneeId() {
        return assigneeId;
    }

    /**
     * 设置处理人id
     *
     * @param assigneeId 处理人id
     */
    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    /**
     * 获取审核意见
     *
     * @return checkMessage - 审核意见
     */
    public String getCheckMessage() {
        return checkMessage;
    }

    /**
     * 设置审核意见
     *
     * @param checkMessage 审核意见
     */
    public void setCheckMessage(String checkMessage) {
        this.checkMessage = checkMessage;
    }

    /**
     * 获取是否通过
     *
     * @return isPass - 是否通过
     */
    public Boolean getIsPass() {
        return isPass;
    }

    /**
     * 设置是否通过
     *
     * @param isPass 是否通过
     */
    public void setIsPass(Boolean isPass) {
        this.isPass = isPass;
    }

    /**
     * 获取审批发起人
     *
     * @return initiator - 审批发起人
     */
    public String getInitiator() {
        return initiator;
    }

    /**
     * 设置审批发起人
     *
     * @param initiator 审批发起人
     */
    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    /**
     * @return PMId
     */
    public Long getPMId() {
        return PMId;
    }

    /**
     * @param PMId
     */
    public void setPMId(Long PMId) {
        this.PMId = PMId;
    }

    /**
     * @return procInstId
     */
    public String getProcInstId() {
        return procInstId;
    }

    /**
     * @param procInstId
     */
    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }
}