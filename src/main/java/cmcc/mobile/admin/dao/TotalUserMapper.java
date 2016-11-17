package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.vo.CompanyVo;
import cmcc.mobile.admin.vo.UserCompanyVo;

public interface TotalUserMapper {
	int deleteByPrimaryKey(String id);

	int insert(TotalUser record);

	int insertSelective(TotalUser record);

	TotalUser selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(TotalUser record);

	int updateByPrimaryKey(TotalUser record);

	List<UserCompanyVo> getUserCompanys(Map<String, String> params);

	List<TotalUser> selectByMobile(String mobile);

	/**
	 * Oa Pc端登录验证
	 * 
	 * @param user
	 * @return
	 */
	List<UserCompanyVo> getByPassword(TotalUser user);
	
	/**
	 * 通过手机号更新存在的用户
	 * @param user
	 * @return
	 */
	int updateByMobileSelective(TotalUser user);

	List<User> selectAllByOrgId(String id);

	List<CompanyVo> selectByMobilel(String mobile);
	
	/**
	 * 通过旧密码查询用户数据
	 * @param user
	 * @return
	 */
	TotalUser findByOldPass(TotalUser user);
}