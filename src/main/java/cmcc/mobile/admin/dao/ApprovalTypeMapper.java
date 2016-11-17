package cmcc.mobile.admin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.vo.ApprovalTypeResultVo;
import cmcc.mobile.admin.vo.ApprovalVo;

public interface ApprovalTypeMapper {
    int deleteByPrimaryKey(String id);

    int insert(ApprovalType record);

    int insertSelective(ApprovalType record);

    ApprovalType selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ApprovalType record);

    int updateByPrimaryKey(ApprovalType record);
    
    /**
     * 获取置顶流程
     * @param map1
     * @return
     */
    List<ApprovalType> getTopProcess(Map<String, Object> map1);
    
    
    /**
     * 获取默认流程
     * @param map1
     * @return
     */
    List<ApprovalType> getDefaultProcess(Map<String, Object> map1);
    
    ApprovalType getApprovalTypeById(HashMap<String,String> map);
    
    ApprovalType getApprovalTypeById2(HashMap<String,String> map);
    
    List<ApprovalType> selectBydefault();
    
    List<ApprovalTypeResultVo> getEnabledFlow(Map<String, String> map);
    
    /**
     * 根据名称获取
     * @param map
     * @return
     */
    ApprovalType getApprovalTypeByName(HashMap<String,String> map);
    
    //自定义查询报表
	List<ApprovalType> selectByName(ApprovalVo record);

	List<ApprovalType> getProcess(Map<String, Object> map);

	Set<String> selectIds(Map<String, Object> params);
    
}