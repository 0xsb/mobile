package cmcc.mobile.admin.service;
import java.util.List;
import java.util.Map;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.Organization;

public interface ActivitiRoleService {


	JsonResult getNextNode(String userId,String companyId,boolean isBelong ,List<Map<String, Object>> list);

	JsonResult getRole(String companyId);

	List<Organization> getOrg(List<Organization> list);

}
