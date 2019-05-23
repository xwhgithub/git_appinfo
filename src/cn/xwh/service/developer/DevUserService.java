package cn.xwh.service.developer;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.DevUser;

public interface DevUserService {
	
	/**
	 * 开发者登录方法
	 * @param devCode
	 * @return
	 * @throws Exception
	 */
	public DevUser getLoginUser(String devCode,String passWord) throws Exception;
}
