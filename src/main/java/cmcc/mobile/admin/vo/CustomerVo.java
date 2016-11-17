package cmcc.mobile.admin.vo;

import java.util.List;

import cmcc.mobile.admin.entity.Customer;


public class CustomerVo extends Customer{

	private List<AppOrganizationVo> orgList;

	public List<AppOrganizationVo> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<AppOrganizationVo> orgList) {
		this.orgList = orgList;
	}
	
	
}
