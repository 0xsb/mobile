package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.ActivitiRoleGroup;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.vo.ActivitiRoleVo;
import cmcc.mobile.admin.vo.ActivitiUserVo;
import cmcc.mobile.admin.vo.UserVo;

public interface ActivitiRoleGroupMapper {
	int deleteByPrimaryKey(Long id);

	int insert(ActivitiRoleGroup record);

	int insertSelective(ActivitiRoleGroup group);

	int selectByPrimaryKey(ActivitiRoleGroup group);

	int updateByPrimaryKeySelective(ActivitiRoleGroup record);

	int updateByPrimaryKey(ActivitiRoleGroup record);

	int deleteRolePerson(ActivitiRoleGroup roles);

	JsonResult insertSelective(String roleId, List<Map> list);

	ActivitiRoleGroup selectByRoleId(ActivitiRoleVo role);

	List<UserVo> selectByrolePersonList(ActivitiRoleVo role);

	int addGroup(Map<String, Object> map);

	int selectByPersons(Map<String, Object> map);

	List<ActivitiRoleGroup> selectById(ActivitiRoleVo role);

	List<User> selectByUser(Map<String, Object> map2);

	List<ActivitiUserVo> selectByUserId(Map<String, Object> user);

	List<ActivitiUserVo> selectByUserOrg(Map<String, Object> user);

	List<ActivitiRoleGroup> selectByUID(String uId);

	List<ActivitiUserVo> selectByUserIds(Map<String, Object> user);

	List<ActivitiRoleGroup> selectByUserOrgs(Map<String, Object> user);

	List<ActivitiRoleGroup> selectByUserIdParents(Map<String, Object> user);

	List<ActivitiUserVo> selectByUserName(Map<String, Object> user);

	List<ActivitiRoleGroup> selectByUsers(Map<String, Object> map);

	List<ActivitiUserVo> selectByListUser(Map<String, Object> user);

	List<ActivitiUserVo> selectByUserUserId(Map<String, Object> user);

	List<ActivitiUserVo> selectUserByOrg(String text2);

}