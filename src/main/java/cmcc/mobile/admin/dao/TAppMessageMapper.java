package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.TAppMessage;
import cmcc.mobile.admin.vo.AppMessageVo;


public interface TAppMessageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TAppMessage record);

    int insertSelective(TAppMessage record);

    TAppMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TAppMessage record);

    int updateByPrimaryKeyWithBLOBs(TAppMessage record);

    int updateByPrimaryKey(TAppMessage record);

	List<AppMessageVo> selectByHistroyNotice(Map<String, Object> map);

	int selectCountByHistroyNotice(Map<String, Object> map);

	List<TAppMessage> selectByCompanyId(String companyId);

	int updateById(Long id);

	List<TAppMessage> selectAllParams(Map<String, Object> map);

	int selectCountAllParams(Map<String, Object> map);

	int selectCountAllByMap1(Map<String, Object> map);

	List<TAppMessage> selectAllByMap1(Map<String, Object> map);
}