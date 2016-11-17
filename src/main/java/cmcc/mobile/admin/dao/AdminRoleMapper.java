package cmcc.mobile.admin.dao;

import java.util.Map;

import cmcc.mobile.admin.entity.AdminRole;

public interface AdminRoleMapper {
    int insert(AdminRole record);

    int insertSelective(AdminRole record);

	AdminRole selectRoleId(Map<String, Object> map1);
}