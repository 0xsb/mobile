package cmcc.mobile.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.StaticPermissionLoginMapper;
import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.dao.UserMapper;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.service.UserService;
import cmcc.mobile.admin.vo.UserCompanyVo;
import cmcc.mobile.admin.vo.UserInfoVo;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private TotalUserMapper totalUserMapper;

	@Autowired
	private StaticPermissionLoginMapper staticPermissionLoginMapper;
	
	@Override
	public String selectByPrimaryId(String id) {

		return userMapper.selectByPrimaryId(id);
	}

	@Override
	public List<TotalUser> selectByMobile(String mobile) {

		return totalUserMapper.selectByMobile(mobile);
	}

	@Override
	public List<UserCompanyVo> getUserCompanys(Map<String, String> params) {

		return totalUserMapper.getUserCompanys(params);
	}

	public List<User> selectAllByOrgId(String orgId) {

		return userMapper.selectAllByOrgId(orgId);
	}

	public UserInfoVo findUserOrgName(String id) {
		return userMapper.findUserOrgName(id);
	}

	@Override
	public List<UserCompanyVo> getStaticUserCompanyVo(String mobile) {
		return staticPermissionLoginMapper.getUserCompanyVo(mobile);
	}

	@Override
	public List<UserCompanyVo> loginByPassword(TotalUser user) {
		return totalUserMapper.getByPassword(user);
	}

	@Override
	public int updatePassword(TotalUser user) {
		return totalUserMapper.updateByMobileSelective(user);
	}

	@Override
	public int updatePassword(String mobile, String oldPass, String newPass) {
		TotalUser user = new TotalUser();
		user.setMobile(mobile);
		user.setPassword(oldPass);
		user = totalUserMapper.findByOldPass(user);
		if(user == null) throw new RuntimeException("旧密码错误");
		user.setPassword(newPass);
		return totalUserMapper.updateByMobileSelective(user);
	}

}
