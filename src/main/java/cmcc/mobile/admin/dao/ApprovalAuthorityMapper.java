package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.ApprovalAuthority;

public interface ApprovalAuthorityMapper {
    int deleteByPrimaryKey(String id);

    int insert(ApprovalAuthority record);

    int insertSelective(ApprovalAuthority record);

    ApprovalAuthority selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ApprovalAuthority record);

    int updateByPrimaryKey(ApprovalAuthority record);

	List<ApprovalAuthority> selectById(Map<String, Object> map2);

	void selectByUser(Map<String, Object> map);

	int updateByIdy(String id);
}