package cmcc.mobile.admin.vo;

import cmcc.mobile.admin.entity.User;

/**
 *
 * @author renlinggao
 * @Date 2016年8月9日
 */
public class UserInfoVo extends User{
	private String orgName;
	private String orgAllName;
	
	public String getOrgAllName() {
		return orgAllName;
	}

	public void setOrgAllName(String orgAllName) {
		this.orgAllName = orgAllName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	
}
