package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.ApprovalMostType;

public interface ApprovalMostTypeMapper {
    int deleteByPrimaryKey(String id);

    int insert(ApprovalMostType record);

    int insertSelective(ApprovalMostType record);

    ApprovalMostType selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ApprovalMostType record);

    int updateByPrimaryKey(ApprovalMostType record);
    
    List<ApprovalMostType> getAll(String wyyId);

	List<ApprovalMostType> selectByWyyId(String wyyId);
}