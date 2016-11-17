package cmcc.mobile.admin.service;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.vo.UserCompanyVo;
import cmcc.mobile.admin.vo.UserInfoVo;

public interface UserService {

	String selectByPrimaryId(String id);

	List<TotalUser> selectByMobile(String mobile);

	List<UserCompanyVo> getUserCompanys(Map<String, String> params);

	List<User> selectAllByOrgId(String orgId);

	public UserInfoVo findUserOrgName(String id);

	/**
	 * 静态登录的时候获取公司集合
	 * 
	 * @param mobile
	 * @return
	 */
	public List<UserCompanyVo> getStaticUserCompanyVo(String mobile);

	/**
	 * 通过密码登录
	 * 
	 * @param user
	 * @return
	 */
	List<UserCompanyVo> loginByPassword(TotalUser user);

	/**
	 * 修改密码
	 * 
	 * @param user
	 * @return
	 */
	public int updatePassword(TotalUser user);
	
	/**
	 * 验证老密码和修改成新密码
	 * @param mobile
	 * @param oldPass
	 * @param newPass
	 * @return
	 */
	public int updatePassword(String mobile, String oldPass, String newPass);

}
