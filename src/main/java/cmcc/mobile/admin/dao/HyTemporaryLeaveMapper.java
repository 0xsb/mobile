package cmcc.mobile.admin.dao;

import java.util.HashMap;
import java.util.List;

import cmcc.mobile.admin.entity.HyTemporaryLeave;
import cmcc.mobile.admin.entity.HyTemporarytrip;

public interface HyTemporaryLeaveMapper {
    int deleteByPrimaryKey(String id);

    int insert(HyTemporaryLeave record);

    int insertSelective(HyTemporaryLeave record);

    HyTemporaryLeave selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(HyTemporaryLeave record);

    int updateByPrimaryKey(HyTemporaryLeave record);
    
    List<HyTemporaryLeave>  getHyTemporaryLeaveList(HashMap<String, Object> map);
    
    List<HyTemporaryLeave>  getHyTemporaryLeaveList2(HashMap<String, Object> map);
    
    List<HyTemporaryLeave>  getHyTemporaryLeaveList3(HashMap<String, Object> map);
    
    List<HyTemporaryLeave>  getHyTemporaryLeaveList4(HashMap<String, Object> map);
    
    int deleteAll();
    
    HyTemporaryLeave getLeaveDays(HashMap<String, Object> map);
    
    HyTemporaryLeave getLeaveDays2(HashMap<String, Object> map);
    
    HyTemporaryLeave getLeaveDays3(HashMap<String, Object> map);
    
    List<HyTemporaryLeave> getLeaveDays4(HashMap<String, Object> map);
    
    HyTemporaryLeave selectNumByDate(HashMap<String, Object> map);
    
    List<HyTemporaryLeave> selectNumByDate2(HashMap<String, Object> map);
    
    List<HyTemporaryLeave> getDayByOrg(HashMap<String, Object> map);
    
    List<HyTemporaryLeave> getNumByOrg(HashMap<String, Object> map);
}