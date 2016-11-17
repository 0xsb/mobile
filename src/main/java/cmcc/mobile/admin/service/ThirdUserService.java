package cmcc.mobile.admin.service;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;

public interface ThirdUserService {
	
	List<Organization> selectAllDept();
	
	List<User>selectAllByOrgId(String orgId);
	
	User selectByPrimaryKey(String id);
	
	Organization getDeptByFullName(String orgFullname);
	
	List<Organization> selectAllDeptByCompanyId(String companyId);

	List<User> selectAllByOrg(Map<String, Object> map);
}
