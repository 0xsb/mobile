package cmcc.mobile.admin.service;

import java.util.List;

import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.vo.AppOrganizationVo;
import cmcc.mobile.admin.vo.CustomerVo;

public interface AppUserService {

	List<CustomerVo> companyOrgUser(String id, Customer customer);

	List<AppOrganizationVo> selectByCompanyId(String companyId);

}
