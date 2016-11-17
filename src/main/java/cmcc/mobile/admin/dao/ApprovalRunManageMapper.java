package cmcc.mobile.admin.dao;

import java.util.List;

import cmcc.mobile.admin.entity.ApprovalRunManage;

public interface ApprovalRunManageMapper {
    int deleteByPrimaryKey(String runId);

    int insert(ApprovalRunManage record);

    int insertSelective(ApprovalRunManage record);

    ApprovalRunManage selectByPrimaryKey(String runId);

    int updateByPrimaryKeySelective(ApprovalRunManage record);

    int updateByPrimaryKey(ApprovalRunManage record);

    List<ApprovalRunManage> getRunInfoByFlowId(String flowId);
    
    ApprovalRunManage getRunInofByFlowId(ApprovalRunManage record);
    
    
    
    List<ApprovalRunManage> getRunInfoById(String id);
	
	List<ApprovalRunManage> getRunInfoByRunNO(ApprovalRunManage record);
	
	int getRunNoByFlowId(ApprovalRunManage record);
	
	List<ApprovalRunManage> getAllInfo(String id);
	
	ApprovalRunManage getRunInofByFlowIdAndUserId(ApprovalRunManage record);
}