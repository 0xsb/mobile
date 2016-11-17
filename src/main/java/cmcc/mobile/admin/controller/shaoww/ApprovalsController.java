package cmcc.mobile.admin.controller.shaoww;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.base.VirtualActorConstant;
import cmcc.mobile.admin.entity.ApprovalMostType;
import cmcc.mobile.admin.entity.ApprovalTableConfig;
import cmcc.mobile.admin.entity.ApprovalType;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.entity.UserApprovalType;
import cmcc.mobile.admin.entity.UserMobileLoginLog;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.ApprovalsService;
import cmcc.mobile.admin.service.UserLoginInfoService;
import cmcc.mobile.admin.service.UserService;
import cmcc.mobile.admin.service.iApprovalService;
import cmcc.mobile.admin.vo.ApprovalTypeResultVo;
import cmcc.mobile.admin.vo.UserCompanyVo;
import cmcc.mobile.admin.vo.UserInfoVo;

@Controller
@RequestMapping("approvals")
public class ApprovalsController extends BaseController {

	@Autowired
	private ApprovalsService approvalsService;

	@Autowired
	private UserService userService;

	@Autowired
	private iApprovalService approvalService;
	// @Autowired
	// private ApprovalMostTypeMapper TypeMapper;

	@Autowired
	private UserLoginInfoService userLoginInfoService;

	/**
	 * 根据手机号码返回集团信息
	 * 
	 * @param mobile
	 * @return
	 */
	@RequestMapping("showCompany")
	@ResponseBody
	public JsonResult companyInfo(String mobile, HttpServletRequest request) {
		boolean isStaticLogin = isStaticLogin();// 如果是静态登录
		JsonResult result = new JsonResult();

		if (StringUtils.isNotEmpty(mobile) && mobile.length() > 0) {

			MultipleDataSource.setDataSourceKey(null);
			if (isStaticLogin) {// 如果是静态的
				List<UserCompanyVo> userCompanyVos = userService.getStaticUserCompanyVo(mobile);
				if (userCompanyVos != null && userCompanyVos.size() > 0) {
					result.setSuccess(true);
					result.setModel(userCompanyVos);
					session.setAttribute(MOBILE, mobile);
				} else {
					result.setSuccess(false);
					result.setMessage("用户不存在或者没有权限静态登录");
				}
				return result;
			}

			List<TotalUser> users = userService.selectByMobile(mobile);
			List<UserCompanyVo> customers = new ArrayList<UserCompanyVo>();
			if (null != users && users.size() != 0) {
				for (TotalUser user : users) {
					UserCompanyVo uCompanyVo = new UserCompanyVo();
					MultipleDataSource.setDataSourceKey(null);
					Customer customer = approvalsService.selectByCompanyId(user.getCompanyId());
					if (customer == null || StringUtils.isEmpty(customer.getId())) {
						result.setMessage("手机号码不存在");
						result.setSuccess(false);
					} else {
						uCompanyVo.setCompanyId(customer.getId());
						uCompanyVo.setCompanyName(customer.getCustomerName());
						uCompanyVo.setUserId(user.getId());
						customers.add(uCompanyVo);
						request.getSession().setAttribute("mobile", user.getMobile());

					}
				}
				result.setModel(customers);
				result.setSuccess(true);
			} else {
				result.setMessage("用户不存在");
				result.setSuccess(false);
				return result;
			}
		} else {
			result.setMessage("手机号码不能为空");
			result.setSuccess(false);
		}

		return result;
	}

	/**
	 * 选择集团
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("selectCompany")
	@ResponseBody
	public JsonResult selectCompany(String userId, HttpServletRequest request, HttpServletResponse response) {
		JsonResult result = new JsonResult();
		if (userId != null && userId.trim().length() > 0) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("userId", userId);
			MultipleDataSource.setDataSourceKey(null);
			List<UserCompanyVo> userCompanyVos = userService.getUserCompanys(params);

			if (userCompanyVos != null && userCompanyVos.size() == 1) {
				request.getSession().setAttribute("DynamicDbName", userCompanyVos.get(0).getDatabaseName());
				request.getSession().setAttribute("companyId", userCompanyVos.get(0).getCompanyId());
				request.getSession().setAttribute("companyName", userCompanyVos.get(0).getCompanyName());
				request.getSession().setAttribute("userId", userId);

				result.setSuccess(true);
				// 记录登录日志
				UserMobileLoginLog slog = (UserMobileLoginLog) request.getSession().getAttribute("log");
				if (slog != null) {
					slog.setUserId(userId);
					slog.setLoginTime(new Date());
					slog.setCompanyId(userCompanyVos.get(0).getCompanyId());
					slog.setStatus(0);
					// 删除登录日志
					request.getSession().removeAttribute("log");
				} else {
					slog = new UserMobileLoginLog();
					slog.setCompanyId(userCompanyVos.get(0).getCompanyId());
					slog.setPhone(getMobile());
					slog.setLoginTime(new Date());
					slog.setUserId(userId);
					slog.setStatus(3);// 测试状态下进入的
				}
				userLoginInfoService.add(slog);

				MultipleDataSource.setDataSourceKey(userCompanyVos.get(0).getDatabaseName());
				UserInfoVo userInfoVo = userService.findUserOrgName(userId);
				if (userInfoVo != null) {
					request.getSession().setAttribute("userName", userInfoVo.getUserName());
					request.getSession().setAttribute("orgName", userInfoVo.getOrgName());
				}

			} else {
				result.setSuccess(false);
				result.setMessage("用户id错误");
			}
		} else {
			result.setSuccess(false);
			result.setMessage("用户id不能为空");
		}
		return result;
	}

	/**
	 * 获取更多页面大类小类
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("maiList")
	@ResponseBody
	public JsonResult mainList(HttpServletRequest request,
			@RequestParam(value = "wyyid", defaultValue = "wyy0001") String wyyid) {
		// 保存微应用id到session
		session.setAttribute("wyyId", wyyid);
		Map<String, Object> map1 = new HashMap<>();
		JsonResult result = new JsonResult();
		String userId = (String) request.getSession().getAttribute("userId");// 从缓存中获取用户Id
		String dbName = (String) request.getSession().getAttribute("DynamicDbName");// 获取数据库
		String companyId = (String) request.getSession().getAttribute("companyId");// 获取公司id
		MultipleDataSource.setDataSourceKey(dbName);
		List<ApprovalMostType> approvalMostTypesList = approvalsService.getAll(wyyid);// 获取所有的大类
		List<String> userApprovalTypesList = approvalsService.selectByUserId(userId);
		if (null == userApprovalTypesList || userApprovalTypesList.size() == 0) {
			userApprovalTypesList = new ArrayList<String>();
			MultipleDataSource.setDataSourceKey(dbName);
			map1.put("companyId", companyId);
			map1.put("wyyId", wyyid);
			List<ApprovalType> approvalTypes = approvalsService.getDefaultProcess(map1);

			for (ApprovalType at : approvalTypes) {
				UserApprovalType record = new UserApprovalType();
				record.setApprovalTypeId(at.getId());
				record.setUserId(userId);
				approvalsService.insertSelective(record);
				userApprovalTypesList.add(at.getId());
			}
		}

		Map<String, String> approvalType_map = new HashMap<String, String>();
		approvalType_map.put("companyId", companyId);
		approvalType_map.put("wyyId", wyyid);
		MultipleDataSource.setDataSourceKey(dbName);
		if (wyyid.equals("wyy0002")) {
			approvalType_map.put("userId", userId);
		}
		List<ApprovalTypeResultVo> approvalTypeLists = approvalsService.getEnabledFlow(approvalType_map);
		for (ApprovalTypeResultVo atr : approvalTypeLists) {
			atr.setIsCollection(userApprovalTypesList.contains(atr.getId()) ? 1 : 0);
		}

		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		for (ApprovalMostType amt : approvalMostTypesList) {
			Map<String, Object> list_map = new HashMap<>();
			List<ApprovalTypeResultVo> list_new = new ArrayList<ApprovalTypeResultVo>();
			for (ApprovalTypeResultVo atr : approvalTypeLists) {
				if (amt.getId().equals(atr.getApprovalMostTypeId())) {
					list_new.add(atr);
				}
			}
			list_map.put("mostType", amt);
			list_map.put("approvalTypeList", list_new);
			list2.add(list_map);
		}
		// List<Map<String, Object>> list = new ArrayList<Map<String,
		// Object>>();
		// for (ApprovalMostType amt : approvalMostTypesList) {
		// Map<String, Object> resultMap = new HashMap<String, Object>();
		// resultMap.put("mostType", amt);
		// Map<String, String> idMap = new HashMap<String, String>();
		// idMap.put("id", amt.getId());
		// idMap.put("companyId", companyId);
		// MultipleDataSource.setDataSourceKey(dbName);
		// List<ApprovalTypeResultVo> approvalTypeList =
		// approvalsService.getEnabledFlow(idMap);
		// for (ApprovalTypeResultVo at : approvalTypeList) {
		// at.setIsCollection(userApprovalTypesList.contains(at.getId()) ? 1 :
		// 0);
		// }
		// resultMap.put("approvalTypeList", approvalTypeList);
		// list.add(resultMap);
		// }
		result.setMessage("ok");
		result.setSuccess(true);
		result.setModel(list2);

		return result;
	}

	/**
	 * 收藏管理
	 * 
	 * @param request
	 * @param approvalTypeId
	 * @return
	 */

	@RequestMapping("collection")
	@ResponseBody
	public JsonResult addCollection(HttpServletRequest request, String approvalTypeId) {

		JsonResult result = new JsonResult();
		if (null == approvalTypeId || approvalTypeId.length() == 0) {
			result.setMessage("类型id不能为空");
			result.setSuccess(false);
			return result;
		}
		String userId = request.getSession().getAttribute("userId").toString();

		String dbName = (String) request.getSession().getAttribute("DynamicDbName");
		MultipleDataSource.setDataSourceKey(dbName);
		List<String> userApprovalTypesList = approvalsService.selectByUserId(userId);
		if (null == userApprovalTypesList || userApprovalTypesList.size() == 0) {
			UserApprovalType record = new UserApprovalType();
			record.setApprovalTypeId(approvalTypeId);
			record.setUserId(userId);
			int m = approvalsService.insertSelective(record);
			if (m == 1) {
				result.setModel("1");
				result.setMessage("收藏成功");
				result.setSuccess(true);
			} else {
				result.setMessage("error");
				result.setSuccess(false);
			}

			return result;
		}
		if (userApprovalTypesList.contains(approvalTypeId)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", userId);
			map.put("approvalTypeId", approvalTypeId);
			int m = approvalsService.deleteByUserIdAndApprovalType(map);
			if (m == 1) {
				result.setMessage("ok");
				result.setSuccess(true);
				result.setModel("0");
			} else {
				result.setMessage("error");
				result.setSuccess(false);
			}
			return result;

		} else {

			UserApprovalType record = new UserApprovalType();
			record.setApprovalTypeId(approvalTypeId);
			record.setUserId(userId);
			int m = approvalsService.insertSelective(record);
			if (m == 1) {
				result.setModel("1");
				result.setMessage("收藏成功");
				result.setSuccess(true);
			} else {
				result.setMessage("error");
				result.setSuccess(false);
			}
		}

		return result;

	}

	/**
	 * 根据typeId来取默认审批人
	 * 
	 * @param id
	 * @return
	 */

	@RequestMapping("defaultapproval")
	@ResponseBody
	public JsonResult getDefautlApproval(String id, HttpServletRequest request) {

		JsonResult result = new JsonResult();
		List<Map<String, String>> approvalTypeList = new ArrayList<Map<String, String>>();
		String user_Id = (String) request.getSession().getAttribute("userId");

		String dbName = (String) request.getSession().getAttribute("DynamicDbName");
		MultipleDataSource.setDataSourceKey(dbName);

		if (null != id && id.trim().length() > 0) {

			ApprovalType approvalType = approvalsService.selectByPrimaryKey(id);
			if (null == approvalType) {
				result.setMessage("id不存在");
				result.setSuccess(false);
				return result;

			}
			ApprovalTableConfig approvalTableConfig = approvalsService
					.selectByTypeIdAndDeafult(approvalType.getApprovalTableConfigId());

			if (approvalTableConfig == null) {
				result.setModel(" ");
				result.setSuccess(true);
				result.setMessage("没有默认审批人");
				return result;
			}

			// 获取 部门领导 和 上级部门领导
			String default_approvals = approvalTableConfig.getDefaultApprovalUserIds();
			String orgUserId = "";
			String preOrgUserId = "";
			if (default_approvals != null && default_approvals.length() > 0) {
				if (default_approvals.indexOf(VirtualActorConstant.DEPT_LEADER_ROLE) != -1) { // 部门领导
					User user = approvalService.getUserById(user_Id);
					String orgId = user.getOrgId();
					Organization org = new Organization();
					org = approvalsService.getOrgById(orgId);
					List<User> userList = new ArrayList<User>();
					userList = userService.selectAllByOrgId(orgId);
					orgUserId = userList.get(0).getId();
					default_approvals = default_approvals.replace(VirtualActorConstant.DEPT_LEADER_ROLE, orgUserId);
				}

				if (default_approvals.indexOf(VirtualActorConstant.SUPERIOR_DEPT_LEADER_ROLE) != -1) {// 上级部门领导
					User user = approvalService.getUserById(user_Id);
					String orgId = user.getOrgId();
					Organization org = new Organization();
					org = approvalsService.getOrgById(orgId);
					String preId = org.getPreviousId();
					if (preId == null || preId.length() <= 0) {
						result.setSuccess(false);
						result.setMessage("没有上级部门领导,请联系管理员修改流程默认审批人配置");
						return result;
					}
					List<User> userList = new ArrayList<User>();
					userList = userService.selectAllByOrgId(preId);
					if (userList == null || userList.size() == 0) {
						result.setSuccess(false);
						result.setMessage("没有上级部门领导,请联系管理员修改流程默认审批人配置");
						return result;
					}
					preOrgUserId = userList.get(0).getId();
					default_approvals = default_approvals.replace(VirtualActorConstant.SUPERIOR_DEPT_LEADER_ROLE, preOrgUserId);
				}

			} else {
				result.setSuccess(true);
				result.setMessage("没有默认审批人");
				return result;
			}

			String[] approvalTypeUserIdArray = null;
			if (StringUtils.isNotEmpty(default_approvals)) {
				// approvalTypeUserIdArray =
				// approvalTableConfig.getDefaultApprovalUserIds().split(",");
				approvalTypeUserIdArray = default_approvals.split(",");
				for (int i = 0; i < approvalTypeUserIdArray.length; i++) {
					Map<String, String> map = new HashMap<String, String>();
					String userId = approvalTypeUserIdArray[i];

					if (VirtualActorConstant.PROMOTER_ROLE.equals(userId)) {
						userId = (String) request.getSession().getAttribute("userId");
					}
					String approvalName = userService.selectByPrimaryId(userId);
					map.put("id", userId);
					map.put("approvalName", approvalName);
					approvalTypeList.add(map);
				}
			} else {
				result.setModel(" ");
				result.setSuccess(true);
				result.setMessage("没有默认审批人");
				return result;
			}

			result.setSuccess(true);
			result.setModel(approvalTypeList);
		} else {
			result.setSuccess(false);
			result.setMessage("类型不能为空");
		}

		return result;

	}

	/**
	 * 待阅数量
	 * 
	 * @param request
	 * @param status
	 * @return
	 */

	@RequestMapping("readCount")
	@ResponseBody
	public JsonResult toReadCount(HttpServletRequest request, String status) {

		JsonResult result = new JsonResult();
		String dbName = (String) request.getSession().getAttribute("DynamicDbName");
		MultipleDataSource.setDataSourceKey(dbName);
		String userId = (String) request.getSession().getAttribute("userId");
		// 测试数据
		// String userId = "test_18621324310";
		if (userId == null || userId.trim().length() == 0) {
			result.setSuccess(false);
			result.setMessage("userId 不能为空");
			return result;
		}
		// 创建参数为useId和参数为status的Map
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("nodeStatus", "3");
		map.put("status", "2");
		MultipleDataSource.setDataSourceKey("business1");
		Long count = approvalsService.selectByPrimaryUserId(map);
		result.setModel(count);
		result.setSuccess(true);
		result.setMessage("ok");
		return result;

	}

}
