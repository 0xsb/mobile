package cmcc.mobile.admin.controller.wubj;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.Constants;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.EmailConfigMapper;
import cmcc.mobile.admin.entity.EmailConfig;
import cmcc.mobile.admin.entity.File;
import cmcc.mobile.admin.entity.TAppMessage;
import cmcc.mobile.admin.entity.TAppSendMessage;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.FileService;
import cmcc.mobile.admin.service.MsgService;
import cmcc.mobile.admin.service.TAppMessageService;
import cmcc.mobile.admin.util.CheckoutUtil;
import cmcc.mobile.admin.util.PropertiesUtil;
import cmcc.mobile.admin.vo.AppMessageVo;
import cmcc.mobile.admin.vo.PageVo;


/**
 * 通知公告（PC端）
 * 
 * @author wubj
 *
 */
@Controller
@RequestMapping(value = "/web")
public class MessageController extends BaseController{
	
	@Autowired
	private TAppMessageService messageService;
	@Autowired
	private FileService fileService ;
	@Autowired
	private MsgService msgService ;
	@Autowired
	private EmailConfigMapper emailConfigMapper ;


	/**
	 * 跳转到新增公告页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/toAddNotice")
	public String toAddNotice() {

		return "message/addmessage";
	}
	/**
	 * 
	 */

	

	/**
	 * 跳转到历史公告页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/toHistoryNotice")
	public String toHistoryNotice() {
		return "/message/tohistroynotice";
	}
	
	/**
	 * 添加公告
	 * 
	 * @param message
	 * @return
	 */
	@RequestMapping(value = "/notice/add")
	@ResponseBody
	public JsonResult addNotice(MultipartHttpServletRequest picfile, AppMessageVo message,
			HttpServletRequest request, String isSend) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss") ;
		JsonResult result = new JsonResult();
		HttpSession session = request.getSession();
		String userId = session.getAttribute("userId").toString();
		String companyId = session.getAttribute("companyId").toString();
		String userName = session.getAttribute("userName").toString() ;
		Boolean sendMessage = false;
		if (StringUtils.isNotEmpty(isSend) && isSend.equals("1")) {
			sendMessage = true;
		}
		if (StringUtils.isEmpty(message.getTitle())) {
			return new JsonResult(false, "公告标题不可为空！", null);
		} else if (!(4 < message.getTitle().length()) || !(message.getTitle().length() < 31)) {
			return new JsonResult(false, "公告标题长度应为【5-30】字！当前已输入" + message.getTitle().length() + "字", null);
		}
		if (StringUtils.isEmpty(message.getDetail())) {
			return new JsonResult(false, "公告摘要不可为空！", null);
		}
		if (StringUtils.isEmpty(message.getContent())) {
			return new JsonResult(false, "公告详情不可为空", null);
		} else if (CheckoutUtil.getTextFromTHML(message.getContent()).length() > 5000) {
			return new JsonResult(false, "公告详情长度应为【0-5000】字", null);
		}
		Collection<MultipartFile> pic = picfile.getFileMap().values() ;
		if(picfile!=null || pic.size()!=0){
			try {
			String num = null ;
			
			File file = fileService.fileUpload(picfile, request, num, userId, companyId);
			message.setPicurl( PropertiesUtil.getAppByKey("IMG_PATH")+file.getId()+"/"+file.getAddr()+".do");
		} catch (Exception e) {
			return new JsonResult(false, "文件上传失败！", null);
		}
			
		}
		
		if (StringUtils.isEmpty(userId)) {
			return new JsonResult(false, "登陆连接失效，请先登录！", null);
		}

		message.setUserId(userId);
		message.setCompanyId(companyId);
		message.setUserName(userName);
		message.setCreateTime(sdf.format(new Date()));	
		MultipleDataSource.setDataSourceKey("business1");
		List<String> strings = new ArrayList<String>();
		messageService.save(message,!sendMessage,strings);
		
		/**
		 * 短信开关控制发送短信提醒
		 */
		// 如果不是单元测试，真实推送
		if(sendMessage){
			MultipleDataSource.setDataSourceKey("");
			String messageTemplate = "管理员：" + message.getUserName() + "发布了一条新公告《" + message.getTitle() + "》，请打开个人助理APP查看详情！";
			msgService.checkdSendBatchByMobile(strings, messageTemplate, message.getCompanyId()) ;
		}
		result.setMessage("保存成功！");
		result.setSuccess(true);
		return result;
	}
	
	/**
	 * 查询历史公告
	 * 
	 * @return
	 */
	@RequestMapping(value = "/notice/findAll")
	@ResponseBody
	public JsonResult getByHistoryNotice(String type, String status, HttpServletRequest request, String title,
			String beginTime, String endTime,  PageVo vo) {		
		String companyId = request.getSession().getAttribute("companyId").toString();
		return messageService.selectByHistroyNotice(type, companyId,title, beginTime, endTime, status,vo);
	}
	/**
	 * 编辑通知公告
	 */
	@RequestMapping(value = "/updateMessage")
	@ResponseBody
	public JsonResult updateNotice(MultipartHttpServletRequest picfile, TAppMessage message) {
		HttpSession session = request.getSession();
		String userId = session.getAttribute("userId").toString();
		String companyId = session.getAttribute("companyId").toString();
		if (null != message) {
			if (StringUtils.isEmpty(message.getTitle())) {
				return new JsonResult(false, "公告标题不可为空！", null);
			} else if (!(4 < message.getTitle().length()) || !(message.getTitle().length() < 31)) {
				return new JsonResult(false, "公告标题长度应为【5-30】字！当前已输入" + message.getTitle().length() + "字", null);
			}
			if (StringUtils.isEmpty(message.getDetail())) {
				return new JsonResult(false, "公告摘要不可为空！", null);
			}
			if (StringUtils.isEmpty(message.getContent())) {
				return new JsonResult(false, "公告详情不可为空", null);
			} else if (CheckoutUtil.getTextFromTHML(message.getContent()).length() > 5000) {
				return new JsonResult(false, "公告详情长度应为【0-5000】字", null);
			}
			if(!picfile.getFileMap().values().isEmpty()){
				try {
				String num = null ;
				File file = fileService.fileUpload(picfile, request, num, userId, companyId);
				message.setPicurl( PropertiesUtil.getAppByKey("IMG_PATH")+file.getId()+"/"+file.getAddr()+".do");
			} catch (Exception e) {
				return new JsonResult(false, "文件上传失败！", null);
			}
				
			}
			return messageService.updateByPrimaryKey(message);
		}
		return new JsonResult(false, "公告发布失败！", null);
	}
	/**
	 * 删除通知公告
	 */
	@RequestMapping(value = "/deleteMessage")
	@ResponseBody
	public JsonResult deleteNotice(String id, HttpServletRequest request) {
		if (StringUtils.isEmpty(id)) {
			return new JsonResult(false, "参数错误！", null);
		}
		TAppMessage message = messageService.selectByPrimaryKey(Long.parseLong(id));
		if (null != message) {
			String loginCid = (String) request.getSession().getAttribute("companyId");
			//MultipleDataSource.setDataSourceKey("business1");
			if (StringUtils.isNotEmpty(loginCid) && loginCid.equals(message.getCompanyId())) {
				messageService.deleteByPrimaryKey(message.getId());
				return new JsonResult(true, "删除成功！", null);
			}
			return new JsonResult(false, "权限不足！不可删除其他公告！", null);
		}
		return new JsonResult(false, "公告已删除！", null);
	}

	/**
	 * 公告详情
	 */
	@RequestMapping(value = "/getMessage")
	@ResponseBody
	public JsonResult getByMessageDetail(Long id) {
		JsonResult result = new JsonResult();
		TAppMessage message = messageService.selectByPrimaryKey(id);
		/**
		 * 获取公告发送对象
		 */
		if (null != message) {
//			String json = messageService.selectReceivers(message.getId());
//			message.setReceiver(json);
			result.setSuccess(true);
			result.setModel(message);
		} else {
			result.setMessage("公告已被删除！");
			result.setSuccess(false);
			result.setModel(null);
		}
		return result;
	}
	
	/**
	 * 查看公告详情
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = ("/message/detail/{id:\\d+}"), method = RequestMethod.GET)
	public ModelAndView getMessage(@PathVariable Long id) {
		MultipleDataSource.setDataSourceKey("business1");
		ModelAndView view = new ModelAndView("PCFrame/detail");
		TAppMessage message = messageService.selectByPrimaryKey(id);
		if (null != message) {
			message.setReadCount(message.getReadCount() + 1);
			messageService.updateByPrimaryKeySelective(message);
			view.addObject("time", message.getCreateTime());
			view.addObject("message", message);
		} else
			view.addObject("error", "公告连接已失效！");
		return view;
	}
	
	/**
	 * 首页公告详情接口
	 */
	@RequestMapping("/message/details")
	@ResponseBody
	public JsonResult getDetails(Long id){
		MultipleDataSource.setDataSourceKey("business1");
		TAppMessage message = messageService.selectByPrimaryKey(id);
		if (null != message) {
			message.setReadCount(message.getReadCount() + 1);
			messageService.updateByPrimaryKeySelective(message);
			return new JsonResult(true,"查询成功",message) ;
		}
		return new JsonResult(false,"公告已失效！",null) ;
	}
	
	
	/**
	 * 获取公告更新条数
	 * 
	 * @param cid
	 * @param org
	 * @param time
	 * @return
	 */
	@RequestMapping(value = "/checkMessage", method = RequestMethod.GET)
	@ResponseBody
	public JsonResult checkMessage(String companyId,Long time,String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss") ;
		if ((StringUtils.isEmpty(companyId) || null == time) && StringUtils.isNotEmpty(type)) {
			return new JsonResult(false, "参数错误！", null);
		}
		TAppMessage vo = new TAppMessage();
		vo.setType(Long.valueOf(type));
		vo.setCompanyId(companyId);
		vo.setCreateTime(sdf.format(new Date(time)));
		MultipleDataSource.setDataSourceKey("business1");
		return messageService.checkMessageUpdate(vo);
	}

	/**
	 * cid APP公告列表接口
	 * 
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public JsonResult getListMessage( String id,String type) {
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(type)) {
			return new JsonResult(false, "参数错误！", null);
		}
		AppMessageVo vo = new AppMessageVo();
		vo.setCompanyId(id);
		vo.setTypeId(Long.parseLong(type));
		MultipleDataSource.setDataSourceKey("business1");
		return messageService.selectAllParams(vo);   
	}
	
	/**
	 * 公告置顶
	 */
	/**
	 * 公告置顶
	 * 
	 * @param id
	 * @param sort
	 * @return
	 */
	@RequestMapping(value = "setMessageTop")
	@ResponseBody
	public JsonResult setMessageTop(String id, String sort) {
		if (StringUtils.isEmpty(id)) {
			return new JsonResult(false, "参数错误！", null);
		}
		TAppMessage message = messageService.selectByPrimaryKey(Long.parseLong(id));
		if (null != message) {
			if (message.getSort() == Integer.parseInt(sort)) {
				return new JsonResult(true, "置顶状态已变更！", null);
			} else {
				message.setSort(Integer.parseInt(sort));
				messageService.updateByPrimaryKeySelective(message);
				return new JsonResult(true, "操作成功！", null);
			}
		}
		return new JsonResult(false, "公告已删除！", null);
	}
//	
//	
//	/**
//	 * 常用邮箱添加
//	 */
//	@RequestMapping("/addEmailUrl")
//	@ResponseBody
//	public JsonResult addEmailUrl(String emailUrl){
//		//从Session获取用户信息
//		String userId = getUserId() ;
//		String companyId = getCompanyId() ;
//		//new一个实体接受参数
//		EmailConfig config = new EmailConfig() ;
//		config.setCompanyId(companyId);
//		config.setUserId(userId);
//		config.setEmailUrl(emailUrl);
//		if(StringUtils.isNotEmpty(emailUrl)){
//			messageService.addEmailUrl(config) ;
//		}
//		return new JsonResult(false,"参数错误",null) ;
//	}
	
	/**
	 * 编辑邮箱
	 */
	@RequestMapping("/updateEmailUrl")
	@ResponseBody
	public JsonResult updateEmailUrl(String emailUrl){
		//从Session获取用户信息
				String userId = getUserId() ;
				String companyId = getCompanyId() ;
				//new一个实体接受参数
				EmailConfig config = new EmailConfig() ;
				config.setCompanyId(companyId);
				config.setUserId(userId);
				config.setEmailUrl(emailUrl);
				if(StringUtils.isNotEmpty(emailUrl)&&StringUtils.isNotEmpty(companyId)&&StringUtils.isNotEmpty(userId)){
				EmailConfig email = emailConfigMapper.selectByUserId(config) ;
					//判断该用户存不存在存在就更新不存在就新增
					if(email!=null){
						return messageService.updateEmailUrl(config) ;
					}else{
						return messageService.addEmailUrl(config) ;
					}
				}
				return new JsonResult(false,"参数错误",null) ;
	}
	
	/**
	 * 获取邮箱
	 */
	@RequestMapping("/getEmailUrl")
	@ResponseBody
	public JsonResult getEmailUrl(){
		String userId = getUserId() ;
		String companyId = getCompanyId() ;
		//new一个实体接受参数
		EmailConfig config = new EmailConfig() ;
		config.setCompanyId(companyId);
		config.setUserId(userId);
		if(StringUtils.isNotEmpty(companyId)&&StringUtils.isNotEmpty(userId)){
			return messageService.selectEmailUrl(config) ;
		}
		return new JsonResult(false,"参数错误",null) ;
	}
}
