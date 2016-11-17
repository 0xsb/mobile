package cmcc.mobile.admin.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.ApprovalDataMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigDetailsMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigMapper;
import cmcc.mobile.admin.dao.ApprovalTypeMapper;
import cmcc.mobile.admin.dao.CustomerMapper;
import cmcc.mobile.admin.dao.HyTemporaryLeaveMapper;
import cmcc.mobile.admin.dao.HyTemporarybxMapper;
import cmcc.mobile.admin.dao.HyTemporarytripMapper;
import cmcc.mobile.admin.dao.OrganizationMapper;
import cmcc.mobile.admin.dao.UserMapper;
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
import cmcc.mobile.admin.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService{

	@Autowired
	protected CustomerMapper customerMapper;
	
	@Autowired
	protected OrganizationMapper organizationMapper;
	
	@Autowired
	protected ApprovalTypeMapper approvalTypeMapper;
	
	@Autowired
	protected ApprovalTableConfigMapper approvalTableConfigMapper;
	
	@Autowired
	protected ApprovalDataMapper approvalDataMapper;
	
	@Autowired
	protected UserMapper userMapper;
	
	@Autowired
	protected ApprovalTableConfigDetailsMapper approvalTableConfigDetailsMapper;
	
	@Autowired
	protected HyTemporaryLeaveMapper hyTemporaryLeaveMapper;
	
	@Autowired
	protected HyTemporarybxMapper hyTemporarybxMapper;
	
	@Autowired
	protected HyTemporarytripMapper hyTemporarytripMapper;
	
	public Customer getCustomerById(String id) {
		return customerMapper.selectByCompanyId(id);
	}


	public List<Organization> getAllOrg(String companyId) {
		
		return organizationMapper.selectAllDeptByCompanyId(companyId);
	}



	public ApprovalType getApprovalTypeByName(HashMap<String, String> map) {
		return approvalTypeMapper.getApprovalTypeByName(map);
	}


	public List<ApprovalTableConfig> getApprovalTableConfigByTypeId(String typeId) {
		
		return approvalTableConfigMapper.getInfoByTypeId(typeId);
	}



	public List<ApprovalData> getApprovalDataByConfId(HashMap<String, String> map) {
		
		return approvalDataMapper.selectByParams(map);
	}


	
	public User getUserById(String id) {
		
		return userMapper.selectByPrimaryKey(id);
	}


	public List<ApprovalTableConfigDetails> getDetailsByConfigId(String id) {

		return approvalTableConfigDetailsMapper.getApprovalInfoById(id);
	}


	
	public boolean addTrip(HyTemporarytrip trip) {
		if(hyTemporarytripMapper.insertSelective(trip)>0){
			return true;
		}else{
			return false;
		}
	}


	
	public boolean addBx(HyTemporarybx bx) {
	
		if(hyTemporarybxMapper.insertSelective(bx)>0){
			return true;
		}else{
			return false;
		}
	}


	
	public boolean addLeave(HyTemporaryLeave leave) {
		if(hyTemporaryLeaveMapper.insertSelective(leave)>0){
			return true;
		}else{
			return false;
		}
		
	}



	public boolean deletaAllTrip() {
		if(hyTemporarytripMapper.deleteAll()>0){
			return true;
		}else{
			return false;
		}
			
	}



	public boolean deleteAllBx() {
		if(hyTemporarybxMapper.deleteAll()>0){
			return true;
		}else{
			return false;
		}
	}


	
	public boolean deleteAllLeave() {
		if(hyTemporaryLeaveMapper.deleteAll()>0){
			return true;
		}else{
			return false;
		}
	}

	public List<Organization> getOrgByPreId(List<String> list) {
		
		return organizationMapper.getOrgByPreId(list);
	}


	public List<HyTemporarytrip> getTripNumByOrg(HashMap<String, Object> map) {
		
		return hyTemporarytripMapper.getTripNumByOrg(map);
	}


	public List<HyTemporaryLeave> getLeaveDayByOrg(HashMap<String, Object> map) {
	
		return hyTemporaryLeaveMapper.getDayByOrg(map);
	}


	@Override
	public List<HyTemporaryLeave> getLeaveNumberByOrg(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return hyTemporaryLeaveMapper.getNumByOrg(map);
	}


	@Override
	public List<HyTemporarybx> getBxMoneyByOrg(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return hyTemporarybxMapper.getMoneyByOrg(map);
	}


	public List<HyTemporarybx> getBxNumberByOrg(HashMap<String, Object> map) {
		
		return hyTemporarybxMapper.getBxNumByOrg(map);
	}
	
	

}
