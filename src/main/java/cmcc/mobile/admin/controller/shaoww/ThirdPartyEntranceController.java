package cmcc.mobile.admin.controller.shaoww;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.dao.UserMobileLoginLogMapper;
import cmcc.mobile.admin.entity.UserMobileLoginLog;
import cmcc.mobile.admin.entity.UserToken;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.UserTokenService;
import cmcc.mobile.admin.util.Base64;
import cmcc.mobile.admin.util.PropertiesUtil;
import cmcc.mobile.admin.vo.UserCompanyVo;

@Controller
@RequestMapping("thirdPartyEntrance")
public class ThirdPartyEntranceController extends BaseController {

	@Autowired
	private UserTokenService userTokenService;

	@Autowired
	UserMobileLoginLogMapper userMobileLoginLogMapper;

	/**
	 * 生成token 同时将数据插入到表中
	 * 
	 * @param VVTmobile
	 *            V网通手机号
	 * @param VVTuserId
	 *            V网通用户Id
	 * @param src
	 * @param request
	 * @return
	 */
	@RequestMapping("createToken")
	@ResponseBody
	public Map<String, String> creatToken(String FromUserTelNum, String FromUserId, String src,
			HttpServletRequest request, @RequestParam(value = "prttype", defaultValue = "0") String prttype) {

		Map<String, String> model = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(FromUserTelNum) && StringUtils.isNotEmpty(FromUserId)) {

			// 切库
			MultipleDataSource.setDataSourceKey(null);
			String token = userTokenService.createToken(FromUserTelNum, FromUserId);
			// String url = request.getRequestURL().toString();
			// String url1 = url.replace("http://", "");
			// String[] urlArr = url1.split("/");
			String priexUrl = PropertiesUtil.getAppByKey("TOKEN_URL");
			String urlnew = priexUrl + request.getContextPath() + "/thirdPartyEntrance/forwardPage/" + prttype
					+ ".do?prttype=" + prttype;
			model.put("url", urlnew);
			model.put("token", token);
			model.put("result", "200");
			model.put("resultMsg", "验证成功");
		} else {
			model.put("result", "-2");
			model.put("resultMsg", "用户id和手机号不能为空");

		}
		return model;
	}

	/**
	 * 
	 * @param token
	 * @param urlStr
	 * @param VVTmobile
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */

	@RequestMapping("forwardPage/{prttype}")
	@ResponseBody
	public String forwardPage(String token, String urlStr, String FromUserTelNum, String FromUserId,
			HttpServletRequest request, HttpServletResponse response, @PathVariable String prttype)
			throws IOException, ServletException {
		setIsStaticLogin(false);
		// 切主库
		MultipleDataSource.setDataSourceKey(null);
		// 初始化日志信息放到session中
		UserMobileLoginLog log = new UserMobileLoginLog();
		log.setPhone(FromUserTelNum);
		log.setvId(FromUserId);
		log.setToken(token);
		request.getSession().setAttribute("log", log);

		UserToken tokens = userTokenService.getToken(FromUserTelNum, token, request);
		if (tokens == null) {
			log.setStatus(2);// 0正常登入，1用户不存在，2异常token
			log.setLoginTime(new Date());
			userMobileLoginLogMapper.insertSelective(log);
			response.sendRedirect(request.getContextPath() + "/assets/html/building.html");
			return null;
		}
		// 切库
		// MultipleDataSource.setDataSourceKey("dataSource");
		// 把v网通id放入session
		request.getSession().setAttribute("vId", FromUserId);
		List<UserCompanyVo> users = userTokenService.getUserCompny(tokens, request);
		if (users == null || users.size() == 0) {
			response.sendRedirect(request.getContextPath() + "/assets/html/building.html");
			log.setLoginTime(new Date());
			log.setStatus(1);// 0正常登入，1用户不存在，2异常token
			userMobileLoginLogMapper.insertSelective(log);

		} else if (users.size() == 1) {
			session.setAttribute(MOBILE, FromUserTelNum);
			request.getSession().setAttribute("companyId", users.get(0).getCompanyId());
			request.getSession().setAttribute("companyName", users.get(0).getCompanyName());
			request.getSession().setAttribute("userId", users.get(0).getUserId());
			request.getSession().setAttribute("DynamicDbName", users.get(0).getDatabaseName());
			String url = null;
			if (StringUtils.isNotEmpty(urlStr)) {
				url = Base64.getFromBase64(urlStr);
			} else {
				switch (Integer.parseInt(prttype)) {
				case 0:
					url = "/moblicApprove/toIndex.do";
					break;
				case 1:
					url = "/moblicApprove/toDailys.do";
					break;
				case 2:
					url = "/moblicApprove/toWelfale.do";
					break;
				case 3:
					url = "/moblicApprove/toTasks.do";
					break;
				default:
					break;
				}

			}
			// UserMobileLoginLog log = new UserMobileLoginLog();
			// log.setPhone(tokens.getMobile());
			// log.setvId(tokens.getUserId());
			// log.setToken(token);
			// log.setCompanyId(users.get(0).getCompanyId());
			// log.setUserId(users.get(0).getUserId());
			// log.setLoginTime(new Date());
			// log.setStatus(0);// 0正常登入，1用户不存在，2异常token
			// userMobileLoginLogMapper.insertSelective(log);
			RequestDispatcher rd = request.getRequestDispatcher(url);
			rd.forward(request, response);
		} else {
			// String mobile = (String)
			// request.getSession().getAttribute("mobile");
			session.setAttribute(MOBILE, FromUserTelNum);
			request.setAttribute("mobile", FromUserTelNum);
			request.setAttribute("prttype", prttype);
			RequestDispatcher rd = request.getRequestDispatcher("/moblicApprove/toOldLogin.do?prttype=" + prttype);// 如果有多个公司就跳转
			rd.forward(request, response);
		}

		return null;
	}
}
