package cmcc.mobile.admin.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.dao.UserTokenMapper;
import cmcc.mobile.admin.entity.UserToken;
import cmcc.mobile.admin.service.UserTokenService;
import cmcc.mobile.admin.vo.UserCompanyVo;

@Service
public class UserTokenServiceImpl implements UserTokenService {

	@Autowired
	private UserTokenMapper userTokenMapper;
	
	@Autowired
	private TotalUserMapper toalUserMapper;
	

	@Override
	public String createToken(String mobile, String userId) {
		// 先切库
		String token = UUID.randomUUID().toString();
		Date date = new Date(new Date().getTime() + 5 * 60 * 1000);// 5分钟过期
		UserToken userToken = new UserToken();
		userToken.setMobile(mobile);
		userToken.setToken(token);
		userToken.setUserId(userId);
		userToken.setExpirationTime(date);
		userTokenMapper.insertSelective(userToken);

		return token;
	}

	@Override
	public UserToken getToken(String mobile, String token, HttpServletRequest request) {
		UserToken token2 = new UserToken();
		token2.setMobile(mobile);
		token2.setToken(token);
		token2.setExpirationTime(new Date());
		UserToken tokens = userTokenMapper.geUserTokenByToken(token2);

		return tokens;
	}

	@Override
	public List<UserCompanyVo> getUserCompny(UserToken tokens, HttpServletRequest request) {
		
		List<UserCompanyVo> users = null;
		if (tokens != null && StringUtils.isNotEmpty(tokens.getUserId())) {
			request.getSession().setAttribute("mobile", tokens.getMobile());
			Map<String, String> params = new HashMap<String, String>();
			params.put("mobile", tokens.getMobile());
			users = toalUserMapper.getUserCompanys(params);
			
		}
		return users;
	}

}
