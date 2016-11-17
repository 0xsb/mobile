package cmcc.mobile.admin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.OrganizationMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.ThirdUserService;


@Service
public class ThirdUserServiceImpl implements ThirdUserService{
	
	@Autowired
	private OrganizationMapper organizationMapper;
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public List<Organization> selectAllDept() {
		
		return organizationMapper.selectAllDept();
	}

	@Override
	public List<User> selectAllByOrgId(String orgId) {
		
		return userMapper.selectAllByOrgId(orgId);
	}
	
	@Override
	public User selectByPrimaryKey(String id) {
		
		return userMapper.selectByPrimaryKey(id);
	}

	@Override
	public Organization getDeptByFullName(String orgFullname) {
		
		return organizationMapper.getDeptByFullName(orgFullname);
	}

	@Override
	public List<Organization> selectAllDeptByCompanyId(String companyId) {
		
		return organizationMapper.selectAllDeptByCompanyId(companyId);
	}

	@Override
	public List<User> selectAllByOrg(Map<String, Object> map) {
		return userMapper.selectAllByOrg(map);
	}

}
