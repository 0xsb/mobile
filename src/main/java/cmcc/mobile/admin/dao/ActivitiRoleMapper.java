package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.ActivitiRole;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.vo.ActivitiRoleVo;

public interface ActivitiRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivitiRole record);

    int insertSelective(ActivitiRoleVo role);

    ActivitiRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivitiRoleVo role);

    int updateByPrimaryKey(ActivitiRole record);

	List<ActivitiRole> selectCount(ActivitiRoleVo role);

	List<ActivitiRole> selectByList(ActivitiRoleVo role);

	ActivitiRole selectByListId(ActivitiRoleVo role);

	List<Map> selectUserByCompanyId(String companyId);

	List<Map> selectCompanyInfoByCompanyId(String companyId);

	ActivitiRole selectByRole(Integer roleId);

	Organization selectByOrg(String userId);

	List<ActivitiRole> selectByRoles(Long roleId);

	List<ActivitiRole> selectCompany(String companyId);

	List<ActivitiRole> selectByMap(Map<String, Object> map);

	List<Organization> selectCompanyId(String companyId);

	ActivitiRole selectByRoleId(String id);

}