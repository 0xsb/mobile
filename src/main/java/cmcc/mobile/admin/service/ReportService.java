package cmcc.mobile.admin.service;

import java.util.HashMap;
import java.util.List;

import cmcc.mobile.admin.entity.ApprovalData;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.HyTemporaryLeave;
import cmcc.mobile.admin.entity.HyTemporarybx;
import cmcc.mobile.admin.entity.HyTemporarytrip;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;

public interface ReportService {
	
	Customer getCustomerById(String id);
	
	ApprovalType getApprovalTypeByName(HashMap<String,String> map);
	
	List<Organization> getAllOrg(String companyId);
	
	List<Organization> getOrgByPreId(List<String> list);
	
	List<ApprovalTableConfig> getApprovalTableConfigByTypeId(String typeId);
	
	List<ApprovalData> getApprovalDataByConfId(HashMap<String,String> map);
	
	User getUserById(String id);
	
	List<ApprovalTableConfigDetails> getDetailsByConfigId(String id);
	
	boolean addTrip(HyTemporarytrip trip);
	
	boolean addBx(HyTemporarybx bx);
	
	boolean addLeave(HyTemporaryLeave leave);
	
	boolean deletaAllTrip();
	
	boolean deleteAllBx();
	
	boolean deleteAllLeave();
	
	List<HyTemporarytrip> getTripNumByOrg(HashMap<String,Object> map);
	
	List<HyTemporaryLeave> getLeaveDayByOrg(HashMap<String,Object> map);
	
	List<HyTemporaryLeave> getLeaveNumberByOrg(HashMap<String,Object> map);
	
	List<HyTemporarybx>  getBxMoneyByOrg(HashMap<String,Object> map);
	
	List<HyTemporarybx>  getBxNumberByOrg(HashMap<String,Object> map);
	
}
