package cn.xwh.controller.backend;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.xwh.entity.BackendUser;
import cn.xwh.entity.DevUser;
import cn.xwh.service.backend.BackendUserService;
import cn.xwh.tools.Constants;

/**
 * 后台登录控制器
 * 
 * @author xwh
 *
 */
@Controller
@RequestMapping("/manager")
public class UserLoginController {

	// 日志对象
	private Logger logger = Logger.getLogger(UserLoginController.class);
	
	@Resource(name="backendUserService")
	private BackendUserService backendUserService;
	
	/**
	 * 获得后台管理登录界面
	 * @return
	 */
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(){
		logger.debug("===============获取开发者登录页面=================");
		return "backendlogin";
	}
	
	/**
	 * 处理登录请求
	 * @param devCode
	 * @param devPassword
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/doLogin",method=RequestMethod.POST)
	public String doLogin(@RequestParam String userCode,@RequestParam String userPassword,HttpServletRequest request,HttpSession session){
		logger.debug("==============================验证登录信息===============================");
		BackendUser user=null;
		try {
			//执行登录方法
			user=backendUserService.login(userCode,userPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user!=null) {	//成功跳转到首页
			session.setAttribute(Constants.USER_SESSION, user);
			return "redirect:/manager/backend/main";
		}else {
			//失败返回登录页面
			request.setAttribute("error", "用户名或密码不正确");
			return "backendlogin";
		}
		
	}
	
	/**
	 * 跳转到首页
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/backend/main")
	public String main(HttpSession session){
		//判断是否登录
		if (session.getAttribute(Constants.USER_SESSION)==null) {
			return "redirect:/manager/login";
		}
		return "/backend/main";
	}
	
	/**
	 * 退出登录
	 * @return
	 */
	@RequestMapping("/loginOut")
	public String loginOut(HttpSession session){
		//清除登录信息
		session.removeAttribute(Constants.USER_SESSION);
		//跳转到后台首页
		return"backendlogin";
	}
}
