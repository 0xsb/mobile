package cmcc.mobile.admin.controller.zhuzy;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.Constants;
import cmcc.mobile.admin.entity.TAppMessage;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.TAppMessageService;

@Controller
@RequestMapping("/moblicApprove")
public class MoblicApproveController extends BaseController {
	@Autowired
	private TAppMessageService messageService;

	/**
	 * 表单类别列表
	 * 
	 * @return
	 */
	@RequestMapping("/toApprovalList")
	public String toApprovalList() {
		return "microApp/mobile/approvalList";
	}

	/**
	 * 表单页
	 * 
	 * @return
	 */
	@RequestMapping("/toForm")
	public String toForm(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "microApp/mobile/form";
	}

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping("/toIndex")
	public String toIndex() {
		return "microApp/mobile/index";
	}
	/**
	 * 公文首页
	 * 
	 * @return
	 */
	@RequestMapping("/toOffice")
	public String toOffice() {
		return "microApp/mobile/office";
	}
	/**
	 * 登录页
	 * 
	 * @return
	 */
	@RequestMapping("/toLogin")
	public String toLogin(HttpServletRequest request,
			@RequestParam(value = "prttype", defaultValue = "0") Integer prttype, String FromUserTelNum) {
		String wyyName = "移动审批";
		switch (prttype) {
		case 1:
			wyyName = "日志日报";
			break;
		case 2:
			wyyName = "福利通";
		case 3:
			wyyName = "任务督办";
			break;
		default:
			break;
		}
		request.setAttribute("wyyName", wyyName);
		request.getSession().setAttribute("wyyName", wyyName);
		request.getSession().setAttribute(BaseController.IS_STATIC_LOGIN, true);// 设置是静态登录
		return "microApp/mobile/login";
	}

	/**
	 * 审批详情页
	 * 
	 * @return
	 */
	@RequestMapping("/toWorkflowDetail")
	public String toWorkflowDetail(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "microApp/mobile/workflowDetail";
	}

	/**
	 * 审批列表页
	 * 
	 * @return
	 */
	@RequestMapping("/toWorkflowList")
	public String toWorkflowList() {
		return "microApp/mobile/workflowList";
	}

	@RequestMapping("/toReport")
	public String toReport() {
		return "microApp/mobile/report";
	}

	@RequestMapping("/toSearchReport")
	public String toSearchReport() {
		return "microApp/mobile/searchReport";
	}

	/**
	 * 没有权限的页面
	 * 
	 * @return
	 */
	@RequestMapping("/toWelcome")
	public String toWelcome() {
		return "noPower/noPower";
	}

	/**
	 * 登录页
	 * 
	 * @return
	 */
	@RequestMapping("/toOldLogin")
	public String toLoginOld() {
		return "microApp/mobile/newLogin";
	}

	@RequestMapping("/toPcFrame")
	public String toPcFrame() {

		return "PCFrame/PCDefult";
	}

	@RequestMapping("/toPcLogin")
	public String toPcLogin() {
		setIsStaticLogin(false);
		return "PCFrame/login";
	}

	@RequestMapping("/toForm-e3")
	public String toFormE3(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "editionThird/form";
	}

	@RequestMapping("/toWorkflowDetail-e3")
	public String toWorkflowDetailE3(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "editionThird/workflowDetail";
	}

	@RequestMapping("/toMyFlowDetail-e3")
	public String toMyFlowDetailE3(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "editionThird/myFlowDetail";
	}

	@RequestMapping("/toWorkflowDetail-e4")
	public String toWorkflowDetailE4(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "PCClient/workflowDetail";
	}

	@RequestMapping("/toMyFlowDetail-e4")
	public String toMyFlowDetailE4(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		request.setAttribute("num", uuid.toString());
		return "PCClient/myFlowDetail";
	}

	// 角色页面
	@RequestMapping("/toWelfale")
	public String welfale() {
		return "microApp/mobile/welfale";
	}

	@RequestMapping("/toTasks")
	public String tasks() {
		return "microApp/mobile/tasks";
	}

	@RequestMapping("/toPublication")
	public String toPublication() {
		return "microApp/tasks/publication";
	}

	@RequestMapping("/toDailys")
	public String dailys() {
		return "microApp/mobile/dailys";
	}

	@RequestMapping("/toApproves")
	public String approves() {
		return "microApp/mobile/approves";
	}

	// 产品首页
	@RequestMapping("/produce")
	public String Produce() {
		return "microApp/mobile/produce";
	}

	// 手机端自定义表单 - 列表
	@RequestMapping("/customform/list")
	public String customformList() {
		return "microApp/customForm/list";
	}

	// 手机端自定义表单 - 编辑
	@RequestMapping("/customform/edit")
	public String customformEdit() {
		return "microApp/customForm/edit";
	}

	// 手机端自定义表单 - 设置
	@RequestMapping("/customform/setting")
	public String customformSetting() {
		return "microApp/customForm/setting";
	}

	// 李三国路由
	@RequestMapping("/PCFrame")
	public String pcframetest() {
		return "PCFrame/PCDefult";
	}

	// 通讯录路由
	@RequestMapping("/addressList")
	public String addressList() {
		return "PCFrame/addressList";
	}

	// 新闻列表
	@RequestMapping("/newsList")
	public String newsList() {

		return "PCFrame/newsList";
	}

	// 新闻详情页
	@RequestMapping("/newsDetail")
	public String newsDetail() {

		return "PCFrame/newsDetail";
	}

	// 流程列表
	@RequestMapping("/folwList")
	public String folwList() {

		return "PCFrame/folwList";
	}

	// 通知公告列表
	@RequestMapping("/noticeList")
	public String noticeList() {
		return "PCFrame/notice";
	}

	/**
	 * 公文流程
	 * 
	 * @return
	 */
	@RequestMapping("toPcForm")
	public String pcCleint() {
		String num = UUID.randomUUID().toString();
		request.setAttribute("num", num);
		return "PCClient/form";
	}

	@RequestMapping("pcDetail")
	public String pcDetail() {
		String num = UUID.randomUUID().toString();
		request.setAttribute("num", num);
		return "PCClient/form";
	}
	@RequestMapping("/flowList")
	public String flowList() {
		return "PCFrame/flowlist";
	}
	@RequestMapping("/setting")
	public String setting() {
		return "PCFrame/setting";
	}
}
