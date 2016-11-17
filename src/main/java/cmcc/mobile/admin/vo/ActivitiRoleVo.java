package cmcc.mobile.admin.vo;

import java.util.List;

import cmcc.mobile.admin.entity.ActivitiRoleGroup;
import net.sf.jsqlparser.statement.create.index.CreateIndex;

public class ActivitiRoleVo {

		private Long id;

	    private String roleName;

	    private String companyId;

	    private Integer type;

	    private Integer parentId;

	    private Integer status;

	    private String createTime;

	    private String userId ;

	    private String text1;

	    private String text2;

	    private String text3;
	    
	    private String createId ;
	    public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getRoleName() {
			return roleName;
		}

		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}


		public String getCompanyId() {
			return companyId;
		}

		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getParentId() {
			return parentId;
		}

		public void setParentId(Integer parentId) {
			this.parentId = parentId;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getText1() {
			return text1;
		}

		public void setText1(String text1) {
			this.text1 = text1;
		}

		public String getText2() {
			return text2;
		}

		public void setText2(String text2) {
			this.text2 = text2;
		}

		public String getText3() {
			return text3;
		}

		public void setText3(String text3) {
			this.text3 = text3;
		}

		public String getCreateId() {
			return createId;
		}

		public void setCreateId(String createId) {
			this.createId = createId;
		}




	
	    

}
