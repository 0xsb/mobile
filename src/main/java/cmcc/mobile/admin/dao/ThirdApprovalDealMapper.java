package cmcc.mobile.admin.dao;

import java.util.List;
import java.util.Map;

import cmcc.mobile.admin.entity.ThirdApprovalDeal;
import cmcc.mobile.admin.vo.ProcessInfoVo;

public interface ThirdApprovalDealMapper {
	int deleteByPrimaryKey(String id);

	int insert(ThirdApprovalDeal record);

	int insertSelective(ThirdApprovalDeal record);

	ThirdApprovalDeal selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(ThirdApprovalDeal record);

	int updateByPrimaryKey(ThirdApprovalDeal record);

	List<ThirdApprovalDeal> getDealInfo(ThirdApprovalDeal record);

	/**
	 * 获取我的已办列表
	 * 
	 * @return
	 */
	List<ProcessInfoVo> getApprovalListByParams(ThirdApprovalDeal record);

	/**
	 * 获取代办
	 * 
	 * @param record
	 * @return
	 */
	List<ProcessInfoVo> getApprovalListByParams2(ThirdApprovalDeal record);

	Long selectByPrimaryUserId(Map<String, String> map);

	List<ThirdApprovalDeal> getApprovalByParams(ThirdApprovalDeal record);

	ThirdApprovalDeal getApprovalListByFlowId(ThirdApprovalDeal record);

	List<ThirdApprovalDeal> getAllDealInfoByFlowId(String id);

	int updateByRunIdSelective(ThirdApprovalDeal record);

	List<ThirdApprovalDeal> selectByType(String type);
}