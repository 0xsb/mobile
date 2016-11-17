package cmcc.mobile.admin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.ApprovalMostTypeMapper;
import cmcc.mobile.admin.dao.ApprovalTableConfigMapper;
import cmcc.mobile.admin.dao.ApprovalTypeMapper;
import cmcc.mobile.admin.dao.CustomerMapper;
import cmcc.mobile.admin.dao.OrganizationMapper;
import cmcc.mobile.admin.dao.ThirdApprovalDealMapper;
import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.dao.UserApprovalTypeMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.ApprovalMostType;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.UserApprovalType;
import cmcc.mobile.admin.service.ApprovalsService;
import cmcc.mobile.admin.vo.ApprovalTypeResultVo;

@Service
public class ApprovalsServcalImpl implements ApprovalsService{
	
	
	
	@Autowired
	private CustomerMapper customerMpper;
	
	@Autowired
	private ApprovalMostTypeMapper approvalMostTypeMapper;
	
	@Autowired 
	private UserApprovalTypeMapper userApprovalTypeMapper;
	
	@Autowired
	private ApprovalTypeMapper approvalTypeMapper;
	
	@Autowired
	private ApprovalTableConfigMapper approvalTableConfigMapper;
	
	@Autowired
	private ThirdApprovalDealMapper thirdApprovalDealMapper;
	
	@Autowired
	private OrganizationMapper organizationMapper;

	@Override
	public Customer selectByCompanyId(String companyId) {
		
		return customerMpper.selectByCompanyId(companyId);
	}

	@Override
	public List<ApprovalMostType> getAll(String wyyId) {
		
		return approvalMostTypeMapper.getAll(wyyId);
	}

	@Override
	public List<String> selectByUserId(String id) {
		
		return userApprovalTypeMapper.selectByUserId(id);
	}

	@Override
	public int insertSelective(UserApprovalType record) {
		
		return userApprovalTypeMapper.insertSelective(record);
	}

	@Override
	public List<ApprovalType> selectBydefault() {
		
		return approvalTypeMapper.selectBydefault();
	}

	@Override
	public List<ApprovalTypeResultVo> getEnabledFlow(Map<String, String> map) {
		
		return approvalTypeMapper.getEnabledFlow(map);
	}

	@Override
	public int deleteByUserIdAndApprovalType(Map<String, String> map) {
		
		return userApprovalTypeMapper.deleteByUserIdAndApprovalType(map);
	}

	@Override
	public ApprovalType selectByPrimaryKey(String id) {
		
		return approvalTypeMapper.selectByPrimaryKey(id);
	}

	@Override
	public ApprovalTableConfig selectByTypeIdAndDeafult(String id) {
		
		return approvalTableConfigMapper.selectByTypeIdAndDeafult(id);
	}

	@Override
	public Long selectByPrimaryUserId(Map<String, String> map) {
		
		return thirdApprovalDealMapper.selectByPrimaryUserId(map);
	}

	@Override
	public List<ApprovalType> getDefaultProcess(Map<String, Object> map1) {
		
		return approvalTypeMapper.getDefaultProcess(map1);
	}


	public Organization getOrgById(String id) {
		
		return organizationMapper.selectByPrimaryKey(id);
	}

	


	
}
