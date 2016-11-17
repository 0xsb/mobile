package cmcc.mobile.admin.service;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.ApprovalMostType;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.UserApprovalType;
import cmcc.mobile.admin.vo.ApprovalTypeResultVo;

public interface ApprovalsService {
	
	
	Customer selectByCompanyId(String companyId);
	
	List<ApprovalMostType> getAll(String wyyid);
	
	List<String>selectByUserId(String id);
	
	int insertSelective(UserApprovalType record);
	
	List<ApprovalType> selectBydefault();
	
	List<ApprovalTypeResultVo> getEnabledFlow(Map<String, String>map);
	
	int deleteByUserIdAndApprovalType(Map<String, String>map);
	
	ApprovalType selectByPrimaryKey(String id);
	
	ApprovalTableConfig selectByTypeIdAndDeafult(String id);
	
	Long selectByPrimaryUserId(Map<String, String> map);
	
	List<ApprovalType> getDefaultProcess(Map<String, Object> map1);
	
	
	Organization getOrgById(String id);
	
}
