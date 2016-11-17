package cmcc.mobile.admin.controller.shaoww;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.base.JsonResult;
import cmcc.mobile.admin.dao.TotalUserMapper;
import cmcc.mobile.admin.entity.MsgSend;
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.User;
import cmcc.mobile.admin.entity.VerifyMobile;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.VerifyService;
import cmcc.mobile.admin.util.ImagesUtil;
import cmcc.mobile.admin.vo.CompanyVo;
import cmcc.mobile.admin.vo.CustomerVo;

@Controller
@RequestMapping("user")
public class VerifyController extends BaseController{

	@Autowired
	private VerifyService verifyService;
	@Autowired
	private TotalUserMapper totalUserMapper ;

	@RequestMapping("verifyImageCode")
	@ResponseBody
	public JsonResult verifyImageCode(String imageCode, HttpServletRequest request){
		
		JsonResult result = new JsonResult();

		String sessionImageCode = (String) request.getSession().getAttribute("imageCode");
		
		if(StringUtils.isEmpty(imageCode) || StringUtils.isEmpty(sessionImageCode)){
			result.setSuccess(false);
			result.setMessage("不能为空");			
			return result;
		}
		sessionImageCode = sessionImageCode.toLowerCase();
		imageCode=imageCode.toLowerCase();
		if(imageCode.equals(sessionImageCode)){
			result.setSuccess(true);
			result.setMessage("验证成功");
		}else{
			result.setSuccess(false);
			result.setMessage("验证码不正确");
		}
		return result;
	}

	/**
	 * 通过手机号创建验证信息
	 * 
	 * @param mobile
	 * @param request
	 * @return
	 */
	@RequestMapping("createCode")
	@ResponseBody
	public JsonResult createCode(String mobile, HttpServletRequest request) {
		JsonResult result = new JsonResult();


		if (StringUtils.isEmpty(mobile)) {
			result.setSuccess(false);
			result.setMessage("手机号码不能为空");
			return result;
		}

		MultipleDataSource.setDataSourceKey(null);
		List<TotalUser> users = verifyService.selectByMobile(mobile);
		if (null == users || users.size() == 0) {
			result.setSuccess(false);
			result.setMessage("手机号码不存在");
			return result;
		}
		VerifyMobile verifyMobile2 = new VerifyMobile();
		verifyMobile2.setMobile(mobile);
		verifyMobile2.setOverTime(new Date());
		MultipleDataSource.setDataSourceKey("business1");
		VerifyMobile vMobile = verifyService.selcectByMobile(verifyMobile2);

		if (null == vMobile) {

			int random = (int) (Math.random() * 1000000);//生成6位验证码
			Date overTime = new Date(new Date().getTime() + 3 * 60 * 1000);//设置有效时间
			Date createTime = new Date();
			VerifyMobile verifyMobile = new VerifyMobile();
			verifyMobile.setCode(String.valueOf(random));
			verifyMobile.setMobile(mobile);
			verifyMobile.setCreateTime(createTime);
			verifyMobile.setOverTime(overTime);
			verifyMobile.setStatus("0");// 0为未登陆 1 为登陆

			MultipleDataSource.setDataSourceKey("business1");
			int m = verifyService.insert(verifyMobile);

			sendMsg(String.valueOf(random), mobile);
			result.setSuccess(true);

		} else {

			MultipleDataSource.setDataSourceKey(null);
			List<MsgSend> msgSends = verifyService.selectByMsgSendMobile(mobile);//获取所有发送的信息
			Date inserttime = msgSends.get(msgSends.size() - 1).getInserttime();//取出最近的一条
			Date now = new Date();
			long distinct = now.getTime() - inserttime.getTime();// 间隔多久发送一次
			if (distinct < 1000 * 60) {
				result.setMessage("操作频繁，请稍后操作");
				result.setSuccess(false);
				return result;
			}
			sendMsg(vMobile.getCode(), vMobile.getMobile());
			result.setSuccess(true);

		}

	
		
		return result;
	}

	public int sendMsg(String random, String mobile) {
		// 发送短信
		String content = "您收到V网通移动审批手机登录验证码为" + random + "请您及时登录验证";
		Date inserttime = new Date();
		Date processtime = new Date(new Date().getTime());
		MsgSend msg = new MsgSend();
		msg.setMobile(mobile);
		msg.setContent(content);
		msg.setInserttime(inserttime);
		msg.setProcessTime(processtime);
		MultipleDataSource.setDataSourceKey(null);
		int n = verifyService.insertSelective(msg);
		return n;
	}

	/**
	 * 验证码后登陆
	 * 
	 * @param verifyCode
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */

	@RequestMapping("verifyMobile")
	@ResponseBody
	public JsonResult VerifyMobile(String mobile, String verifyCode, HttpServletRequest request)
			throws IOException, ServletException {
		JsonResult result = new JsonResult();

		VerifyMobile verifyMobile = new VerifyMobile();
		verifyMobile.setCode(verifyCode);
		verifyMobile.setMobile(mobile);
		verifyMobile.setOverTime(new Date());
		MultipleDataSource.setDataSourceKey("business1");
		VerifyMobile vm = verifyService.selcectByMobileAndCode(verifyMobile);
		
		int failTimes = 0;// 计算登陆验证错误次数
		if (request.getSession().getAttribute(mobile) != null) {
			failTimes = (int) request.getSession().getAttribute(mobile);
		}

		if (null != vm && StringUtils.isNotEmpty(vm.getMobile())) {
			verifyService.updateStasusById(vm.getId());
			result.setSuccess(true);
			result.setMessage("登录成功");
			request.getSession().setAttribute("mobile", mobile);	
			return result;
		} else {
			request.getSession().setAttribute(mobile, ++failTimes);
			result.setSuccess(false);
			result.setMessage("验证失败");
			result.setModel(failTimes);
		}
	

		return result;
	}

	/**
	 * App端登录接口
	 * @param mobile
	 * @param verifyCode
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping("appLogin")
	@ResponseBody
	public JsonResult appLogin(String mobile, String verifyCode, HttpServletRequest request)
			throws IOException, ServletException {
		JsonResult result = new JsonResult();

		VerifyMobile verifyMobile = new VerifyMobile();
		verifyMobile.setCode(verifyCode);
		verifyMobile.setMobile(mobile);
		verifyMobile.setOverTime(new Date());
		MultipleDataSource.setDataSourceKey("business1");
		VerifyMobile vm = verifyService.selcectByMobileAndCode(verifyMobile);
		
		int failTimes = 0;// 计算登陆验证错误次数
		if (request.getSession().getAttribute(mobile) != null) {
			failTimes = (int) request.getSession().getAttribute(mobile);
		}

		if (null != vm && StringUtils.isNotEmpty(vm.getMobile())) {
			verifyService.updateStasusById(vm.getId());
			result.setSuccess(true);
			result.setMessage("登录成功");
			request.getSession().setAttribute("mobile", mobile);	
			MultipleDataSource.setDataSourceKey("");
			List<CompanyVo> tuser = new ArrayList<>() ;
			List<CompanyVo> list = totalUserMapper.selectByMobilel(mobile) ;
			for (int i = 0; i < list.size(); i++) {
				CompanyVo companyVo = list.get(i) ;
				companyVo.setPassword(null);
				tuser.add(companyVo) ;
			}
			result.setModel(list);
			return result;
		} else {
			request.getSession().setAttribute(mobile, ++failTimes);
			result.setSuccess(false);
			result.setMessage("验证失败");
			result.setModel(failTimes);
		}
	

		return result;
	}
	
	/**
	 * 生成一组验证码
	 */
	@RequestMapping("imageCheck")
	@ResponseBody
	public void imageCheck(HttpServletResponse response, HttpServletRequest request) {

		// 调用图片工具，生成一个图片以及验证码
		Map<String, BufferedImage> map = ImagesUtil.createImage();
		// 将map里面的验证码取出
		String code = null;
		Set<String> set = map.keySet();
		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			code = iter.next();
		}
		// 将验证码放入session，以便后面验证
		request.getSession().setAttribute("imageCode", code);
		// 将map里的图片取出
		BufferedImage image = map.get(code);
		try {
			ImageIO.write(image, "jpg", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("quit")
	@ResponseBody
	public void quit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().invalidate();// 清空session
		// return new ModelAndView("login");
		response.sendRedirect(request.getContextPath() + "/moblicApprove/toPcLogin.do");
	}

}
