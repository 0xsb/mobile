package cmcc.mobile.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.UserMobileLoginLogMapper;
import cmcc.mobile.admin.entity.UserMobileLoginLog;
import cmcc.mobile.admin.service.UserLoginInfoService;

/**
 *
 * @author renlinggao
 * @Date 2016年9月1日
 */
@Service
public class UserLoginInfoServiceImpl implements UserLoginInfoService {

	@Autowired
	private UserMobileLoginLogMapper userMobileLoginLogMapper;

	@Override
	public void add(UserMobileLoginLog log) {
		userMobileLoginLogMapper.insertSelective(log);
	}

}
