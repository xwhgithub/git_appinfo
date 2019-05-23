package cn.xwh.controller.developer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.xwh.entity.DevUser;
import cn.xwh.service.developer.DevUserService;
import cn.xwh.tools.Constants;

/**
 * 开发者登录控制器
 * @author xwh
 *
 */
@Controller
@RequestMapping("/dev")
public class DevLoginController {
	
	Logger logger=Logger.getLogger(DevLoginController.class);
	
	@Resource(name="devUserService")
	private DevUserService devUserService;
	
	/**
	 * 获得开发者登录界面
	 * @return
	 */
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(){
		logger.debug("===============获取开发者登录页面=================");
		return "devlogin";
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
	public String doLogin(@RequestParam String devCode,@RequestParam String devPassword,HttpServletRequest request,HttpSession session){
		logger.debug("==============================验证登录信息===============================");
		DevUser user=null;
		try {
			//执行登录方法
			user=devUserService.getLoginUser(devCode,devPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user!=null) {	//成功跳转到首页
			session.setAttribute(Constants.DEV_USER_SESSION, user);
			return "redirect:/dev/flatform/main";
		}else {
			//失败返回登录页面
			request.setAttribute("error", "用户名或密码不正确");
			return "devlogin";
		}
		
	}
	
	/**
	 * 跳转到首页
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/flatform/main")
	public String main(HttpSession session){
		//判断是否登录
		if (session.getAttribute(Constants.DEV_USER_SESSION)==null) {
			return "redirect:/dev/login";
		}
		return "/developer/main";
	}
	
	/**
	 * 退出登录
	 * @return
	 */
	@RequestMapping("/loginOut")
	public String loginOut(HttpSession session){
		//清除登录信息
		session.removeAttribute(Constants.DEV_USER_SESSION);
		return"devlogin";
	}
	
}
