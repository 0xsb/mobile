package cmcc.mobile.admin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.ApprovalData;

public interface ApprovalDataMapper {
    int deleteByPrimaryKey(String flowId);

    int insert(ApprovalData record);

    int insertSelective(ApprovalData record);

    ApprovalData selectByPrimaryKey(String flowId);

    int updateByPrimaryKeySelective(ApprovalData record);

    int updateByPrimaryKeyWithBLOBs(ApprovalData record);

    int updateByPrimaryKey(ApprovalData record);
    
    List<ApprovalData> getInfoByApprovalTableConfigId(HashMap<String,Object> map);
    
    List<ApprovalData> selectByParams(HashMap<String,String> map);
    
}