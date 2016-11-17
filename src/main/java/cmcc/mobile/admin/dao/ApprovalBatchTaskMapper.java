package cmcc.mobile.admin.dao;


import cmcc.mobile.admin.entity.ApprovalBatchTask;

public interface ApprovalBatchTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ApprovalBatchTask record);

    int insertSelective(ApprovalBatchTask record);

    ApprovalBatchTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApprovalBatchTask record);

    int updateByPrimaryKey(ApprovalBatchTask record);
    
}