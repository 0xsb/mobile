package cmcc.mobile.admin.controller.shaoww;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.Organization;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.ThirdUserService;

@Controller
@RequestMapping("users")
public class ThirdUserController {
	
	@Autowired
	private ThirdUserService thirdUserService;
	
	@RequestMapping("getAllDept")
	@ResponseBody
	public JsonResult getAllDepts(HttpServletRequest request){
		JsonResult result = new JsonResult();
		MultipleDataSource.setDataSourceKey("business1");
		String companyId = (String)request.getSession().getAttribute("companyId");
		if(StringUtils.isEmpty(companyId)){
			result.setMessage("公司Id不能为空");
			result.setSuccess(false);
		}
		List<Organization> organizations = thirdUserService.selectAllDeptByCompanyId(companyId);
		if (organizations != null && organizations.size() != 0) {
			result.setSuccess(true);
			result.setModel(organizations);
		} else {
			result.setSuccess(false);
			result.setMessage("没有部门");
		}
		
		return result;
	}
	
	/**
	 * 根据部门Id获取部门人员
	 * 
	 * @param deptId
	 *            部门Id
	 * @param selfCompanyId
	 *            调用该接口的第三方企业Id
	 * @param secretKey
	 *            验证密钥
	 * @param companyId
	 *            该流程对应企业/组织Id
	 * @return
	 */

	@RequestMapping("getUserByDeptId")
	@ResponseBody
	public JsonResult getUserByDeptId(String deptId, String selfCompanyId, String secretKey, String companyId) {
		Map<String, Object> map = new HashMap<String, Object>() ;
		JsonResult result = new JsonResult();

		if (deptId == null || deptId.trim().length() == 0) {
			result.setSuccess(false);
			result.setMessage("Id不存在");
			return result;
		}

		MultipleDataSource.setDataSourceKey("business1");
		map.put("orgId", deptId) ;
		map.put("companyId", companyId) ;
		List<User> users = thirdUserService.selectAllByOrg(map);

		if (users != null && users.size() != 0) {
			result.setSuccess(true);
			result.setModel(users);
		} else {
			result.setSuccess(true);
			result.setMessage("部门为空");
			result.setModel(" ");
		}

		return result;
	}
	
	/**
	 * 根据Id获取员工信息
	 * 
	 * @param companyId
	 *            该流程对应企业/组织Id
	 * @param selfCompanyId
	 *            调用该接口的第三方企业Id
	 * @param secretKey
	 *            验证密钥
	 * @param Id
	 *            人员Id
	 * @return
	 */
	@RequestMapping("getUserById")
	@ResponseBody
	public JsonResult getUserById(String companyId, String selfCompanyId, String secretKey, String id) {

		JsonResult result = new JsonResult();
		MultipleDataSource.setDataSourceKey("business1");
		if (id != null && id.trim().length() != 0) {
			User user = thirdUserService.selectByPrimaryKey(id);
			result.setSuccess(true);
			result.setModel(user);
		} else {
			result.setSuccess(false);
			result.setMessage("Id不能为空 ");
		}

		return result;
	}
	
	/**
	 * 根据部门名称定位到具体的部门
	 * 
	 * @param orgFullname
	 * @return
	 */
	@RequestMapping("getDeptByDeptName")
	@ResponseBody
	public JsonResult getDeptByDeptName(String orgFullname) {
		JsonResult result = new JsonResult();
		MultipleDataSource.setDataSourceKey("business1");
		try {
			if (orgFullname != null && orgFullname.trim().length() != 0) {
				orgFullname = new String(orgFullname.getBytes("ISO-8859-1"), "UTF-8");
				Organization organization = thirdUserService.getDeptByFullName(orgFullname);
				result.setModel(organization);
				result.setSuccess(true);
			} else {
				result.setMessage("部门不能为空");
			}

		} catch (UnsupportedEncodingException e) {

		}

		return result;

	}



}
