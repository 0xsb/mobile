package cmcc.mobile.admin.controller.wubj;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONArray;
import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.AdminRoleMapper;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalTableConfigDetails;
import cmcc.mobile.admin.service.CustomFormService;

/**
 *
 * @author wubj
 * @Date 2016/8/16
 */
@Controller
@RequestMapping("microApp/customForm")
public class CustomFormController extends BaseController {
	@Autowired
	private CustomFormService customFormService;
	@Autowired
	AdminRoleMapper roleMapper ;
	/**
	 * 新建表单插入
	 * 
	 * @param name
	 * @param icon
	 * @param des
	 * @param control
	 * @return
	 */
	@RequestMapping("customform")
	@ResponseBody
	public JsonResult customForm(HttpServletRequest request,String mostTypeKey, String name, String icon, String des, String control,
			@RequestParam(value = "scene", defaultValue = "1") Integer scene) {
		String companyId = request.getSession().getAttribute("companyId").toString() ;
		String userId = request.getSession().getAttribute("userId").toString() ;
		List<ApprovalTableConfigDetails> list = JSONArray.parseArray(control, ApprovalTableConfigDetails.class);
		return new JsonResult(
				customFormService.addCustomForm(companyId, scene, mostTypeKey, name, icon, des, list,userId), "",
				"");
	}

	/**
	 * 修改原来的表单
	 * 
	 * @param id
	 * @param name
	 * @param icon
	 * @param des
	 * @param control
	 * @return
	 */
	@RequestMapping("editFrom")
	@ResponseBody
	public JsonResult editFrom(String id, String mostTypeKey, String name, String icon, String des, String control) {
		List<ApprovalTableConfigDetails> list = JSONArray.parseArray(control, ApprovalTableConfigDetails.class);
		return new JsonResult(customFormService.editCustomForm(mostTypeKey, id, name, icon, des, list), "", "");
	}



	/**
	 * 设置默认审批人
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("setDefApprovalUsers")
	@ResponseBody
	public JsonResult setDefApprovalUsers(String id,
			@RequestParam(value = "defaultApprovalUserIds", defaultValue = "") String defaultApprovalUserIds,
			@RequestParam(value = "lastUserId", defaultValue = "") String lastUserId, String lastDealWay) {
		// 初始化更新数据
		ApprovalTableConfig approvalTableConfig = new ApprovalTableConfig();
		approvalTableConfig.setId(id);
		approvalTableConfig.setDefaultApprovalUserIds(defaultApprovalUserIds);
		approvalTableConfig.setLastUserId(lastUserId);
		approvalTableConfig.setLastDealWay(lastDealWay);
		return new JsonResult(customFormService.setDefApprovalUsers(approvalTableConfig), "", "");
	}

	/**
	 * 启用停用流程
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("stopApprovel")
	@ResponseBody
	public JsonResult stopApprovel(String id) {
		String mess = "";
		boolean isOk = customFormService.stopWorkFlow(id);
		if (!isOk) {
			mess = "该流程不存在";
		}
		return new JsonResult(isOk, mess, "");
	}
	
	/**
	 * 获取表单配置数据
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("getDefApprovalUsers")
	@ResponseBody
	public JsonResult getDefApprovalUsers(String id) {
		return new JsonResult(true, "", customFormService.getDefApprovalUsers(id));
	}

}
