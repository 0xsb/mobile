package cmcc.mobile.admin.entity;

public class ApprovalTableConfigDetails {
    private String id;

    private String describeName;

    private String isEffective;

    private String isMerge;

    private String isRequired;

    private String isStorage;

    private String reName;

    private String sequence;

    private String approvalTableConfigId;

    private String controlId;

    private String exp;

    private String value;

    private String previousId;

    private String jsonData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDescribeName() {
        return describeName;
    }

    public void setDescribeName(String describeName) {
        this.describeName = describeName == null ? null : describeName.trim();
    }

    public String getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(String isEffective) {
        this.isEffective = isEffective == null ? null : isEffective.trim();
    }

    public String getIsMerge() {
        return isMerge;
    }

    public void setIsMerge(String isMerge) {
        this.isMerge = isMerge == null ? null : isMerge.trim();
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired == null ? null : isRequired.trim();
    }

    public String getIsStorage() {
        return isStorage;
    }

    public void setIsStorage(String isStorage) {
        this.isStorage = isStorage == null ? null : isStorage.trim();
    }

    public String getReName() {
        return reName;
    }

    public void setReName(String reName) {
        this.reName = reName == null ? null : reName.trim();
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence == null ? null : sequence.trim();
    }

    public String getApprovalTableConfigId() {
        return approvalTableConfigId;
    }

    public void setApprovalTableConfigId(String approvalTableConfigId) {
        this.approvalTableConfigId = approvalTableConfigId == null ? null : approvalTableConfigId.trim();
    }

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId == null ? null : controlId.trim();
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp == null ? null : exp.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public String getPreviousId() {
        return previousId;
    }

    public void setPreviousId(String previousId) {
        this.previousId = previousId == null ? null : previousId.trim();
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData == null ? null : jsonData.trim();
    }
}