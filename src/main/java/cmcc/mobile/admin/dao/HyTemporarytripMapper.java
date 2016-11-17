package cmcc.mobile.admin.dao;

import java.util.HashMap;
import java.util.List;

import cmcc.mobile.admin.entity.HyTemporarytrip;

public interface HyTemporarytripMapper {
    int deleteByPrimaryKey(String id);

    int insert(HyTemporarytrip record);

    int insertSelective(HyTemporarytrip record);

    HyTemporarytrip selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(HyTemporarytrip record);

    int updateByPrimaryKey(HyTemporarytrip record);
    
    int deleteAll();
    
    List<HyTemporarytrip> selectByDate(HashMap<String, Object> map);
    
    List<HyTemporarytrip> selectByDate2(HashMap<String, Object> map);
    
    List<HyTemporarytrip> selectByDate3(HashMap<String, Object> map);
    
    List<HyTemporarytrip> selectByDate4(HashMap<String, Object> map);
    
    List<HyTemporarytrip> getNumByDate(HashMap<String, Object> map);
    
    List<HyTemporarytrip> getTripNumByOrg(HashMap<String, Object> map);
   
}