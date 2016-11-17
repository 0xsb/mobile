package cmcc.mobile.admin.controller.zhaieryuan;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.entity.LinkResources;
import cmcc.mobile.admin.entity.LinkUser;
import cmcc.mobile.admin.service.LinkResiucesService;
import cmcc.mobile.admin.service.LinkUserService;

@Controller
@RequestMapping("/LinkUser")
public class LinkUserController extends BaseController {
	/**
	 * @author zhaieryuan
	 * @method 个人链接资源控制层
	 * 
	 */
	@Autowired
	private LinkUserService linkUserService;
	@Autowired
	private LinkResiucesService linkResiucesService; 
	/**
	 * @method 添加个人链接资源
	 * @return 
	 */
	@RequestMapping("insertLinkUser")
	@ResponseBody
	public JsonResult insertLinkUser(@RequestParam(value = "resourcesIds[]") String[] resourcesIds){
		JsonResult result = new JsonResult();
		for(String resourcesId : resourcesIds){
			LinkUser linkUser=new LinkUser();
			linkUser.setResourcesId(Long.parseLong(resourcesId));
			linkUser.setFlag(1);
			linkUser=linkUserService.selectSingle(linkUser);
			if(linkUser==null){
				LinkUser linkUser2=new LinkUser();
				String userId=this.getUserId();
				linkUser2.setCreateTime(new Date());
				linkUser2.setResourcesId(Long.parseLong(resourcesId));
				linkUser2.setFlag(1);
				linkUser2.setUserId(userId);
				linkUserService.insert(linkUser2);
			}
		}
		result.setMessage("添加调用成功");
		result.setSuccess(true);
		return result;
	}
	/**
	 * @method 删除个人链接资源
	 * @return
	 */
	@RequestMapping("deleteLinkUser")
	@ResponseBody
	public JsonResult deleteLinkUser(@RequestParam(value = "id") String id){
		JsonResult result = new JsonResult();
		LinkUser linkUser=new LinkUser();
		linkUser.setFlag(1);
		linkUser.setResourcesId(Long.parseLong(id));
		linkUser=linkUserService.selectSingle(linkUser);
		if(linkUser != null){
			linkUser.setFlag(0);
			linkUser.setUpdateTime(new Date());
			linkUserService.updateByPrimaryKeySelective(linkUser);
			result.setMessage("删除调用成功");
			result.setSuccess(true);
		}else {
			result.setMessage("删除调用失败");
			result.setSuccess(false);
		}
		return result;
	}
	/**
	 * @author zhaieryuan
	 * @param  typeId
	 * @method 查詢所有的链接种类
	 */
	@RequestMapping("queryListLinkUser")
	@ResponseBody
	public JsonResult queryListLinkUser(@RequestParam(value = "typeId") String typeId) {
		JsonResult result = new JsonResult();
		LinkUser linkUser=new LinkUser();
		linkUser.setUserId(this.getUserId());
		linkUser.setFlag(1);
		List<LinkUser> linkUsers=linkUserService.selectList(linkUser);
		List<LinkResources> linkResources=new LinkedList<LinkResources>();
		for(LinkUser linkUser2 : linkUsers){
			LinkResources linkRes=new LinkResources();
			linkRes.setId(linkUser2.getResourcesId());
			linkRes.setTypeId(Long.parseLong(typeId));
			linkRes.setFlag(1);
			linkRes=linkResiucesService.selectSingle(linkRes);
			if(linkRes != null){
				linkResources.add(linkRes);
			}
		}
		result.setModel(linkResources);
		result.setMessage("查询个人链接调用用成功");
		result.setSuccess(true);
		return result;
	}
}
