package cmcc.mobile.admin.controller.wubj;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.CustomerMapper;
import cmcc.mobile.admin.entity.Customer;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.AppUserService;
import cmcc.mobile.admin.vo.CustomerVo;

@Controller
@RequestMapping("/users")
public class AppUserController extends BaseController{
	/**
	 * 根据用户公司id查询组织结构接口
	 * @param id
	 * @return
	 */
	@Autowired
	private AppUserService appUserService ;
	@Autowired
	private CustomerMapper customerMapper ;
	@RequestMapping("/getOrg")
	@ResponseBody
	public JsonResult userOrgCompany(String id) {
		//查询公司ID是否存在
		MultipleDataSource.setDataSourceKey("");
		Customer customer = customerMapper.selectByCompanyId(id) ;
		if(customer!=null){
		MultipleDataSource.setDataSourceKey(customer.getDbname());
	
		if (StringUtils.isNotEmpty(id)) {
			List<CustomerVo> list = appUserService.companyOrgUser(id,customer);
			return new JsonResult(true, "", list);
		}
		}
		return new JsonResult(false, "参数错误！", null);
	}

}
