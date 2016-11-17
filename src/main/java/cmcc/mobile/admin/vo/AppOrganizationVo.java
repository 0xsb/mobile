/**
 * 
 */
package cmcc.mobile.admin.vo;

import java.util.List;

import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;



/**
 * @author wubj
 *
 */
public class AppOrganizationVo extends Organization{
	
	private List<User> users;

	private List<AppOrganizationVo> orgs;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<AppOrganizationVo> getOrgs() {
		return orgs;
	}

	public void setOrgs(List<AppOrganizationVo> orgs) {
		this.orgs = orgs;
	}
	
	

}
