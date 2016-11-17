package cmcc.mobile.admin.dao;

import java.util.HashMap;
import java.util.List;

import cmcc.mobile.admin.entity.ThirdApprovalStart;
import cmcc.mobile.admin.vo.ProcessInfoVo;

public interface ThirdApprovalStartMapper {
    int deleteByPrimaryKey(String id);

    int insert(ThirdApprovalStart record);

    int insertSelective(ThirdApprovalStart record);

    ThirdApprovalStart selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ThirdApprovalStart record);

    int updateByPrimaryKey(ThirdApprovalStart record);
    
    List<ProcessInfoVo> getMeStartByUserId(HashMap<String,String> map);
    
    int updateByRunIdSelective(ThirdApprovalStart record);
    
    
    
}