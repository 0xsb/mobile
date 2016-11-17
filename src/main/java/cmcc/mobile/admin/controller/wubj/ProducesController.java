package cmcc.mobile.admin.controller.wubj;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.AdminRoleMapper;
import cmcc.mobile.admin.entity.AdminRole;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.ProducesService;

/**
 * 产品化改造
 * 获取首页地址
 * 获取应用列表
 * 获取流程列表
 * @author wubj
 *
 */

@Controller
@RequestMapping("/produce")
public class ProducesController extends BaseController{
	@Autowired
	private ProducesService producesService ;
	@Autowired
	private AdminRoleMapper roleMapper ;
	/**
	 * 获取首页轮播图
	 */
	@RequestMapping("getImage")
	@ResponseBody
	public JsonResult getImage(){
		return producesService.getImage() ;
	}
	/**
	 * 
	 * 获取产品（wyy）
	 * 
	 */
	@RequestMapping("getProduces")
	@ResponseBody
	public JsonResult getProduces(HttpServletRequest request){
		//String userId = getUserId() ;
		String companyId = getCompanyId() ;
		MultipleDataSource.setDataSourceKey("business1");		
		if(StringUtils.isNotEmpty(companyId)){
			return producesService.getProduces(companyId) ;
		}		
		return new JsonResult(false,"非法请求",null) ;
	}
	/**
	 * 首页页面
	 */
	@RequestMapping("/wyy")
	public String getWyy() {
		return "microApp/mobile";
	}
}
