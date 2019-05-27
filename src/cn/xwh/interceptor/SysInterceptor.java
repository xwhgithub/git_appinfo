package cn.xwh.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.xwh.entity.BackendUser;
import cn.xwh.entity.DevUser;
import cn.xwh.tools.Constants;
/**
 * 拦截器
 * @author xwh
 *
 */
public class SysInterceptor  extends HandlerInterceptorAdapter {
	private Logger logger = Logger.getLogger(SysInterceptor.class);
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.debug("===============进入SysInterceptor拦截器");
		// 获取请求的url
		/*StringBuffer url = request.getRequestURL();
		// 不进行拦截的页面
		if (url.indexOf("http://localhost:6060/SSM_ch10_SMBMS/user/dologin") != -1 || url.indexOf("http://localhost:6060/SSM_ch10_SMBMS/user/login") != -1) {
			return true;
		}*/
		HttpSession session = request.getSession();

		BackendUser backendUser = (BackendUser)session.getAttribute(Constants.USER_SESSION);
		DevUser devUser = (DevUser)session.getAttribute(Constants.DEV_USER_SESSION);
		
		if(null != devUser){ //dev SUCCESS
			return true;
		}else if(null != backendUser){ //backend SUCCESS
			return true;
		}else{
			response.sendRedirect(request.getContextPath()+"/403.jsp");
			return false;
		}
	}
}
