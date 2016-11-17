package cmcc.mobile.admin.controller.wubj;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.ActivitiRoleGroupMapper;
import cmcc.mobile.admin.dao.ActivitiRoleMapper;
import cmcc.mobile.admin.dao.OrganizationMapper;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.ActivitiRoleService;
import cmcc.mobile.admin.vo.ActivitiRoleVo;
import cmcc.mobile.admin.vo.GroupVo;



/**
 * 
 * @author wubj
 *
 */
@Controller
@RequestMapping("/role")
public class ActivitiRoleController extends BaseController{
	@Autowired
	private ActivitiRoleService roleService ;
	@Autowired
	private ActivitiRoleMapper roleMapper;
	@Autowired
	private ActivitiRoleGroupMapper roleGroupMapper;
	@Autowired
	private OrganizationMapper organizationMapper ;
	//根据首环节获取下一节点审核人
	@RequestMapping("/getNextNode")
	@ResponseBody
	public JsonResult getNextNode(String userId,String companyId,boolean isBelong){
		userId = "testenterprise_20160803100832100732" ;
		companyId = "testenterprise" ;
		isBelong = true ;
		Map<String, Object> map2 = new HashMap<String, Object>() ;
		List<Map<String,Object>> list = new ArrayList<>() ;
		List<GroupVo> candidateGroups = new ArrayList<GroupVo>() ;
		GroupVo vo = new GroupVo() ;
		vo.setId("testenterprise");
		vo.setName("111");
		candidateGroups.add(0, vo);
		map2.put("__formId", "flowform1469675280373") ;
		map2.put("__writable", false) ;
		map2.put("__success", true) ;
		map2.put("__groupsType", 1) ;
		map2.put("candidateGroups",candidateGroups) ;
		map2.put("candidateUsers", null) ;
		map2.put("userId", userId) ;
		map2.put("id","task1") ;
		list.add(map2) ;
		if(StringUtils.isNotEmpty(userId)){
			return roleService.getNextNode(userId,companyId,isBelong,list) ;
		}
		return new JsonResult(false,"参数错误",null) ;
	}
	
	@RequestMapping("/getRole")
	@ResponseBody
	public JsonResult getRole(HttpServletRequest request){
		String companyId = request.getSession().getAttribute("companyId").toString() ;
		if(StringUtils.isNotEmpty(companyId)){
			return roleService.getRole(companyId) ;
		}
		return new JsonResult(false,"非法请求",null) ;
	}
	
	// 角色页面
	@RequestMapping("/role")
	public String ActivitiRole() {
		return "activitiRole/role";
	}

		
	@RequestMapping("/group")
	public String ActivitiGroup() {
		return "activitiRole/group";
	}
	
	@RequestMapping("/getOrgName")
	@ResponseBody
	public JsonResult getOrgName(String userId){
		// 获取发起人所在的部门
	    userId = getUserId() ;
		Organization org = roleMapper.selectByOrg(userId) ;
		List<Organization> list = new ArrayList<>() ;
		if (org == null) {
			return new JsonResult(false, "该用户没有组织信息,请联系管理员加入相应的组织", org);
		}
		List<Organization> organizations = organizationMapper.selectByOrg(org.getId()) ;
		if(organizations.size()==0){
			return new JsonResult(true,"没有下级部门",org) ;
		}
		list = roleService.getOrg(organizations) ;
		return new JsonResult(true,"操作成功！",list) ;
	}
}
