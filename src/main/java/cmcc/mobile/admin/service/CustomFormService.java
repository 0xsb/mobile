package cmcc.mobile.admin.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.ApprovalMostType;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.vo.ApprovalTableConfigVo;

/**
 *
 * @author renlinggao
 * @Date 2016年5月5日
 */
public interface CustomFormService {
	/**
	 * 添加控件
	 * @param name
	 * @param icon
	 * @param des
	 * @param control
	 * @param userId 
	 * @return
	 */
	public boolean addCustomForm(String companyId,Integer scene,String mostTypeKey,String name, String icon, String des, List<ApprovalTableConfigDetails> control, String userId);
	
	
	/**
	 * 编辑表单
	 * @param id
	 * @param name
	 * @param icon
	 * @param des
	 * @param control
	 * @return
	 */
	public boolean editCustomForm(String mostTypeKey,String id,String name, String icon, String des, List<ApprovalTableConfigDetails> control);
	
	
	/**
	 * 设置表单默认审批人
	 * @param id
	 * @return
	 */
	public boolean setDefApprovalUsers(ApprovalTableConfig approvalTableConfig);
	
	/**
	 * 停用流程
	 * @param id
	 * @return
	 */
	public boolean stopWorkFlow(String id);
	
	
	/**
	 * 根据id获取配置信息
	 * @param id
	 * @return
	 */
	public ApprovalTableConfigVo getDefApprovalUsers(String id);
	
}
