package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.vo.ActivitiUserVo;
import cmcc.mobile.admin.vo.UserInfoVo;

public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    String selectByPrimaryId(String id);
    
    List<User>selectAllByOrgId(String orgId);

	List<User> selectAllByOrg(Map<String, Object> map);
    
    UserInfoVo findUserOrgName(String id);
    
    List<User> selectUserInfoByOrgId(String orgId);
    
    List<User> selectUser(Map<String, Object> map);

	List<User> selectByCompanyId(String companyId);

	List<User> selectAllByOrgCom(Map<String, Object> map2);
	
	/**
	 * 获取部门领导
	 * @param user
	 * @return
	 */
	User findOrgLeader(User user);
	
	List<User> selectByQueryCriteria(Map<String, Object> map);
	
}
