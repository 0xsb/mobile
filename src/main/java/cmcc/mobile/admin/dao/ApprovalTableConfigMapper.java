package cmcc.mobile.admin.dao;

import java.util.Map;
import java.util.List;import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ThirdApprovalDeal;

public interface ApprovalTableConfigMapper {
    int deleteByPrimaryKey(String id);

    int insert(ApprovalTableConfig record);

    int insertSelective(ApprovalTableConfig record);

    ApprovalTableConfig selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ApprovalTableConfig record);

    int updateByPrimaryKey(ApprovalTableConfig record);

    ApprovalTableConfig selectByTypeIdAndDeafult(String id);
    
    
    List<ApprovalTableConfig> getInfoByTypeId(String approvalTypeId);

	List<ApprovalTableConfig> selectByType(String type);

	List<ApprovalTableConfig> selectByTypeId(Map<String, Object> map);
}