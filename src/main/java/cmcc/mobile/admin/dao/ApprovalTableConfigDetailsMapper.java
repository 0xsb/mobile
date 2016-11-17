package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;

public interface ApprovalTableConfigDetailsMapper {
    int deleteByPrimaryKey(String id);

    int insert(ApprovalTableConfigDetails record);

    int insertSelective(ApprovalTableConfigDetails record);

    ApprovalTableConfigDetails selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ApprovalTableConfigDetails record);

    int updateByPrimaryKeyWithBLOBs(ApprovalTableConfigDetails record);

    int updateByPrimaryKey(ApprovalTableConfigDetails record);
    
    /**
     * 根据confId获取表单详细信息
     * @param approval_table_config_id
     * @return
     */
    List<ApprovalTableConfigDetails> getApprovalInfoById(String approval_table_config_id);
}