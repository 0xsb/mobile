package cmcc.mobile.admin.controller.zhaieryuan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.LinkType;
import cmcc.mobile.admin.service.LinkTypeService;

@Controller
@RequestMapping("/LinkType")
public class LinkTypeController extends BaseController {
	
	/**
	 * @author zhaieryuan
	 * @method 根据当前用户的session companyId查询常用链接大类
	 */
	@Autowired
	private LinkTypeService linkTypeService;
	
	/**
	 * 查询当前公司下所有链接种类
	 * @return
	 * @param companyId session
	 */
	@RequestMapping("queryListLinkTypes")
	@ResponseBody
	public JsonResult queryListLinkTypes(){
		JsonResult result = new JsonResult();
		LinkType linkType=new LinkType();
		linkType.setFlag(1);
		linkType.setCompanyId(this.getCompanyId());
		List<LinkType> linkTypes = linkTypeService.queryListLinkTypes(linkType);
		result.setModel(linkTypes);
		return result;
	}
	
	/**
	 * 添加链接类型
	 */
	@RequestMapping("addLinkTypes")
	@ResponseBody
	public JsonResult addLinkTypes(@RequestParam(value="typeName") String  typeName){
		JsonResult result = new JsonResult();
		String companyId=this.getCompanyId();
		LinkType linkType=new LinkType();
		linkType.setCompanyId(companyId);
		linkType.setFlag(1);
		linkType.setCreateTime(new Date());
		linkType.setTypeName(typeName);
		linkTypeService.insert(linkType);
		result.setMessage("添加成功");
		result.setSuccess(true);
		return result;
	}
	/**
	 * 修改链接内容
	 */
	@RequestMapping("udateLinkTypes")
	@ResponseBody
	public JsonResult udateLinkTypes(@RequestParam(value="id") String  id,
			@RequestParam(value="id") String  typeName){
		JsonResult result=new JsonResult();
		LinkType linkType=new LinkType();
		linkType.setCompanyId(this.getCompanyId());
		linkType.setId(Long.parseLong(id));
		linkType.setUpdateTime(new Date());
		linkType.setTypeName(typeName);
		linkTypeService.updateByPrimaryKeySelective(linkType);
		result.setSuccess(true);
		result.setMessage("修改链接成功！");
		return result;
	}
	/**
	 * 删除常用链接类型
	 * @author zhaieryuan
	 * @param  id
	 */
	@RequestMapping("deleteLinkTypes")
	@ResponseBody
	public JsonResult deleteLinkTypes(@RequestParam(value="id") String  id){
		JsonResult result=new JsonResult();
		LinkType linkType=new LinkType();
		linkType.setId(Long.parseLong(id));
		linkType.setFlag(0);
		linkType.setUpdateTime(new Date());
		linkTypeService.updateByPrimaryKeySelective(linkType);
		result.setSuccess(true);
		result.setMessage("修改链接成功！");
		return result;
	}
}
