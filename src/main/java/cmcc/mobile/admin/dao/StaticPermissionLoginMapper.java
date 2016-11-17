package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.StaticPermissionLogin;
import cmcc.mobile.admin.vo.UserCompanyVo;

public interface StaticPermissionLoginMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(StaticPermissionLogin record);

	int insertSelective(StaticPermissionLogin record);

	StaticPermissionLogin selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(StaticPermissionLogin record);

	int updateByPrimaryKey(StaticPermissionLogin record);
	
	/**
	 * 静态登录获取公司信息
	 * @param mobile
	 * @return
	 */
	List<UserCompanyVo> getUserCompanyVo(String mobile);
}