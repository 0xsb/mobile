package cmcc.mobile.admin.base;

import java.io.FileNotFoundException;
import java.net.BindException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import cmcc.mobile.admin.server.db.MultipleDataSource;

/**
 * Controller 父类
 * 
 * @author zhangxs
 *
 */
@Controller
public class BaseController {

	protected HttpServletResponse response;

	protected HttpServletRequest request;

	protected HttpSession session;

	public Logger log = Logger.getLogger(this.getClass());

	public static final String IS_STATIC_LOGIN = "isStaticLogin";// 是否是测试登录的session的key
	public static final String MOBILE = "mobile";// 手机号的session的key
	public static final String COMPANY_ID = "companyId";// 公司id的session的key
	public static final String USER_ID = "userId";// 用户id的session的key
	public static final String DB_NAME = "DynamicDbName";// 数据库名称的session的key
	public static final String WYY_ID = "wyyId";// 微应用的session的key
	public static final String OA_IMAGE_CODE = "oaImageCode";// pc端登录的验证码
	public static final String OA_SEND_MESS_CODE = "sendMessCode";// 发送短信的验证码

	@ModelAttribute
	public void preRun(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		this.request = request;
		this.response = response;
		this.session = session;
		MultipleDataSource.setDataSourceKey(getDBName());
	}

	/**
	 * 获取公司id
	 * 
	 * @return
	 */
	public String getCompanyId() {
		return (String) request.getSession().getAttribute("companyId");
	}

	/**
	 * 获取用户id
	 * 
	 * @return
	 */
	public String getUserId() {
		return (String) request.getSession().getAttribute("userId");
	}

	/**
	 * 获取数据库字段
	 * 
	 * @return
	 */
	public String getDBName() {
		return (String) request.getSession().getAttribute("DynamicDbName");
	}

	/**
	 * 获取手机号
	 * 
	 * @return
	 */
	public String getMobile() {
		return (String) request.getSession().getAttribute("mobile");
	}

	/**
	 * 获取微应用id
	 * 
	 * @return
	 */
	public String getWyydId() {
		String wyyId = session.getAttribute("wyyId") != null ? (String) session.getAttribute("wyyId") : "wyy0001";
		return wyyId;
	}

	/**
	 * 获取是否是测试登录
	 * 
	 * @return
	 */
	public boolean isStaticLogin() {
		Object isStaticLogin = session.getAttribute(IS_STATIC_LOGIN);
		boolean result = isStaticLogin != null ? (boolean) isStaticLogin : false;
		return result;
	}

	/**
	 * 设置是否是静态登录到session
	 * 
	 * @param isStaticLogin
	 */
	public void setIsStaticLogin(boolean isStaticLogin) {
		session.setAttribute(IS_STATIC_LOGIN, isStaticLogin);
	}

	/**
	 * 获取session中的OaPc端登录的验证码
	 * 
	 * @return
	 */
	public String getOAImageCode() {
		Object obj = session.getAttribute(OA_IMAGE_CODE);
		return obj != null ? (String) obj : "";
	}

	/**
	 * 保存Oa pc端的验证码到session中
	 * 
	 * @param code
	 */
	public void setOAImageCode(String code) {
		session.setAttribute(OA_IMAGE_CODE, code);
	}
	
	/**
	 * 获取session中的OaPc端登录的验证码
	 * 
	 * @return
	 */
	public String getSendMessCode() {
		Object obj = session.getAttribute(OA_SEND_MESS_CODE);
		return obj != null ? (String) obj : "";
	}

	/**
	 * 保存Oa pc端的验证码到session中
	 * 
	 * @param code
	 */
	public void setSendMessCode(String code) {
		session.setAttribute(OA_SEND_MESS_CODE, code);
	}

	/**
	 * 基于异常处理机制
	 * 
	 * @param request
	 * @param ex
	 * @return
	 * @author renlinggao
	 */
	@ExceptionHandler
	@ResponseBody
	public JsonResult exp(HttpServletRequest request, Exception exception) {
		log.error(exception.getMessage(),exception);
		String message = "";
		if (exception instanceof NumberFormatException) {
			message = "参数类型错误！";
		} else if (exception instanceof NoSuchRequestHandlingMethodException) {// 404
			message = "路径请求错误！";
		} else if (exception instanceof MissingServletRequestParameterException
				|| exception instanceof TypeMismatchException || exception instanceof HttpMessageNotReadableException) { // 400
			message = "接口请求错误(参数类型不匹配或参数缺失)！";
		} else if (exception instanceof NoSuchAlgorithmException) {
			message = "短信网关异常！";
		} else if (exception instanceof BindException) {
			message = "参数绑定错误！";
		} else if (exception instanceof NullPointerException) {
			message = "参数不可为空！";
		} else if (exception instanceof FileNotFoundException) {
			message = "所选文件不存在！";
		} else if (exception instanceof RuntimeException) {
			message = exception.getMessage().length() <= 20 ? exception.getMessage() : "操作失败";
		} else if (exception instanceof ConnectException) {
			message = "请求连接错误！";
		} else if (exception instanceof MaxUploadSizeExceededException) {
			Long size = (((MaxUploadSizeExceededException) exception).getMaxUploadSize()) / 1024;
			message = "上传文件大小应小于" + size + "KB（" + size / 1024 + "MB）";
		} else {
			message = "系统错误！";
		}
		return new JsonResult(false, "错误原因：" + message, null);
	}
}
