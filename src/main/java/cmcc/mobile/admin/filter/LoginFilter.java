package cmcc.mobile.admin.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import cmcc.mobile.admin.base.BaseController;
import cmcc.mobile.admin.server.db.MultipleDataSource;
import cmcc.mobile.admin.service.UnionMessageService;

/**
 * 登录过滤器
 * 
 * @author zhuzy
 *
 */
public class LoginFilter extends OncePerRequestFilter {

	private Logger logger = Logger.getLogger(this.getClass());

	private UnionMessageService unionMessageService;

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		boolean authorized = validateAuthorization(request, response);
		if (authorized) {
			filterChain.doFilter(request, response);
		} else {
			response.sendRedirect(request.getContextPath() + "/moblicApprove/toPcLogin.htm?msg=301");
		}
	}

	/**
	 * 校验是否已经登录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean validateAuthorization(HttpServletRequest request, HttpServletResponse response) {
		String[] notFilter = new String[] { "/login", "toPcLogin", "toLogin", "thirdPartyEntrance", "/user/", "/getOrg", "/imageCheck",
				"showCompany", "/notice/add", "/web/list", "/checkMessage", "/message/detail", "/file/download", "/getInfoCount",
				"/getApproval", "/toAppWorkflowList", "/cutCompany", "/appLogin","fileUploads","showCompany","/checkMessage","/message/detail","/file/download","/getInfoCount","/getApproval",
				"/toAppWorkflowList","/list","/cutCompany","/getProcessList","toProcessForm","toProcessForm-e3"};
		String uri = request.getRequestURI();
		// .html,.do之外的请求直接放行
		if (!StringUtils.endsWithAny(uri, ".htm", ".do")) {
			return true;
		}

		// 白名单内的请求放行
		for (String s : notFilter) {
			if (uri.indexOf(s) != -1) {
				return true;
			}
		}

		// 来自统一待办接口的请求在校验通过后放行
		String obSSOCookie = getCookieValue(request.getCookies(), "ObSSOCookie");
		if (StringUtils.isNotEmpty(obSSOCookie)) {
			if (unionMessageService == null) {
				unionMessageService = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext())
						.getBean(UnionMessageService.class);
			}
			MultipleDataSource.setDataSourceKey("business1");
			if (unionMessageService.checkSSO(request, obSSOCookie)) {
				return true;
			}
		}

		Object object = request.getSession().getAttribute(BaseController.MOBILE);
		if (null != object) {
			return true;
		}

		return false;
	}

	private String getCookieValue(Cookie[] cookies, String name) {
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
