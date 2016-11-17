package cmcc.mobile.admin.controller.zhaieryuan;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.LinkResources;
import cmcc.mobile.admin.service.LinkResiucesService;

@Controller
@RequestMapping("/LinkResouces")
public class LinkResoucesController extends BaseController {
	/**
	 * 
	 */
	@Autowired
	private LinkResiucesService linkResiucesService;
	
	/**
	 * 
	 * @param linkResources
	 * @return
	 */
	@RequestMapping("insertLinkResiuces")
	@ResponseBody
	public JsonResult insertLinkResiuces(LinkResources linkResources){
		JsonResult result = new JsonResult();
		linkResources.setCompanyId(this.getCompanyId());
		linkResources.setFlag(1);
		linkResiucesService.insert(linkResources);
		result.setSuccess(true);
		result.setModel(linkResources);
		result.setMessage("添加成功");
		return result;
	}
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("deleteLinkResiuces")
	@ResponseBody
	public JsonResult deleteLinkResiuces(String id){
		JsonResult result = new JsonResult();
		LinkResources linkResources = new LinkResources();
		linkResources.setId(Long.parseLong(id));
		linkResources.setFlag(0);
		linkResiucesService.updateById(linkResources);
		result.setSuccess(true);
		result.setMessage("删除成功");
		return result;
	}
	/**\
	 * 
	 * @param linkResources
	 * @return
	 */
	@RequestMapping("updateLinkResiuces")
	@ResponseBody
	public JsonResult updateLinkResiuces(LinkResources linkResources){
		JsonResult result = new JsonResult();
		linkResiucesService.updateById(linkResources);
		result.setSuccess(true);
		result.setMessage("查询成功");
		return result;
	}
	
	/**
	 * @method 查询当前公司的所有链接资源
	 * @param linkResources
	 * @return
	 */
	@RequestMapping("queryLinkResiuces")
	@ResponseBody
	public JsonResult queryLinkResiuces(LinkResources linkResources){
		JsonResult result = new JsonResult();
		linkResources.setCompanyId(this.getCompanyId());
		linkResources.setFlag(1);
		List<LinkResources> links=linkResiucesService.selectList(linkResources);
		result.setSuccess(true);
		result.setModel(links);
		result.setMessage("获取成功");
		return result;
	}
	
}
