package cmcc.mobile.admin.service;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cmcc.mobile.admin.entity.UserToken;
import cmcc.mobile.admin.vo.UserCompanyVo;

public interface UserTokenService {
	
	String createToken(String mobile, String userId);
	
	UserToken getToken(String mobile, String token, HttpServletRequest request);
	
	List<UserCompanyVo> getUserCompny(UserToken userToken, HttpServletRequest request);
}
