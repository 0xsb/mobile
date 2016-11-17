package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.UserApprovalType;

public interface UserApprovalTypeMapper {
    int insert(UserApprovalType record);

    int insertSelective(UserApprovalType record);
    
    /**
     * 获取用户收藏流程
     * @param map1
     * @return
     */
    List<UserApprovalType> getCollectInfoByUserId(Map<String, Object> map1);
    
    List<String>selectByUserId(String id);
    
    int deleteByUserIdAndApprovalType(Map<String, String>map);
    int deleteByUserIdAndTypeId(String id);
}