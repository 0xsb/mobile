package cmcc.mobile.admin.controller.renlg;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
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
import cmcc.mobile.admin.entity.TotalUser;
import cmcc.mobile.admin.entity.VerifyMobile;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.UserService;
import cmcc.mobile.admin.service.VerifyService;
import cmcc.mobile.admin.util.ImagesUtil;
import cmcc.mobile.admin.vo.UserCompanyVo;

/**
 * pc端登录控制层
 * 
 * @author renlinggao
 * @Date 2016年9月23日
 */
@Controller
@RequestMapping("OA")
public class OALoginController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private VerifyService verifyService;

	/**
	 * pc端密码登录接口
	 * 
	 * @param mobile
	 * @param password
	 * @param imageCode
	 * @return
	 */
	@RequestMapping("login")
	@ResponseBody
	public JsonResult login(@RequestParam(value = "mobile", required = true) String mobile,
			@RequestParam(value = "password", required = true) String password, String imageCode) {
		JsonResult result = new JsonResult(true, null, null);
		if (!getOAImageCode().toLowerCase().equals(imageCode.toLowerCase()))
			throw new RuntimeException("验证码错误");
		// 分装传过来的参数并查询用户登录信息
		TotalUser user = new TotalUser();
		user.setMobile(mobile);
		user.setPassword(password);
		MultipleDataSource.setDataSourceKey(null);
		List<UserCompanyVo> userCompanyVos = userService.loginByPassword(user);
		if(userCompanyVos == null || userCompanyVos.size() == 0){
			result.setSuccess(false);
			result.setMessage("用户名密码错误");
		}
		result.setModel(userCompanyVos);
		return result;
	}
	
	/**
	 * pc端密码登录接口
	 * 
	 * @param mobile
	 * @param password
	 * @param imageCode
	 * @return
	 */
	@RequestMapping("appLogin")
	@ResponseBody
	public JsonResult login(@RequestParam(value = "mobile", required = true) String mobile,
			@RequestParam(value = "password", required = true) String password) {
		JsonResult result = new JsonResult(true, null, null);
		// 分装传过来的参数并查询用户登录信息
		TotalUser user = new TotalUser();
		user.setMobile(mobile);
		user.setPassword(password);
		MultipleDataSource.setDataSourceKey(null);
		List<UserCompanyVo> userCompanyVos = userService.loginByPassword(user);
		if(userCompanyVos == null || userCompanyVos.size() == 0){
			result.setSuccess(false);
			result.setMessage("用户名密码错误");
		}
		result.setModel(userCompanyVos);
		return result;
	}

	/**
	 * oa pc登录生成一组验证码
	 * 
	 * @param response
	 * @param request
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
		setOAImageCode(code);
		// 将map里的图片取出
		BufferedImage image = map.get(code);
		try {
			ImageIO.write(image, "jpg", response.getOutputStream());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param mobile
	 * @param password
	 * @return
	 */
	@RequestMapping("updatePassword")
	@ResponseBody
	public JsonResult updatePassword(String oldPassword,@RequestParam(value = "password", required = true) String password) {
		JsonResult result = new JsonResult(true, null, null);
		String mobile = getMobile();
		if (StringUtils.isEmpty(mobile))
			throw new RuntimeException("登录失效，请重新登录");
		MultipleDataSource.setDataSourceKey(null);
		userService.updatePassword(mobile,oldPassword,password);
		return result;
	}

	/**
	 * 找回密码
	 * 
	 * @param mobile
	 * @param password
	 * @param code短信的验证码
	 * @return
	 */
	@RequestMapping("getPassByMobile")
	@ResponseBody
	public JsonResult retrievePassword(@RequestParam(value = "mobile", required = true) String mobile,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "code", required = true) String code) {
		JsonResult result = new JsonResult(true, null, null);
		// 验证验证码
		VerifyMobile verifyMobile = new VerifyMobile();
		verifyMobile.setCode(code);
		verifyMobile.setMobile(mobile);
		verifyMobile.setOverTime(new Date());
		MultipleDataSource.setDataSourceKey("business1");
		VerifyMobile vm = verifyService.selcectByMobileAndCode(verifyMobile);

		if (vm != null) {
			verifyService.updateStasusById(vm.getId());
			TotalUser user = new TotalUser();
			user.setMobile(mobile);
			user.setPassword(password);
			MultipleDataSource.setDataSourceKey(null);
			userService.updatePassword(user);
		} else {
			result.setSuccess(false);
			result.setMessage("验证码错误");
		}
		return result;
	}

}
