package cmcc.mobile.admin.dao;

import java.util.HashMap;
import java.util.List;

import cmcc.mobile.admin.entity.HyTemporarybx;

public interface HyTemporarybxMapper {
    int deleteByPrimaryKey(String id);

    int insert(HyTemporarybx record);

    int insertSelective(HyTemporarybx record);

    HyTemporarybx selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(HyTemporarybx record);

    int updateByPrimaryKey(HyTemporarybx record);
    
    List<HyTemporarybx> selectByDate(HashMap<String, Object> map);
    
    int deleteAll();
    
    List<HyTemporarybx> getMoneyByOrg(HashMap<String, Object> map);
    
    List<HyTemporarybx> getBxNumByOrg(HashMap<String, Object> map);
}