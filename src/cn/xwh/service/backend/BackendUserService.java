package cn.xwh.service.backend;

import cn.xwh.entity.BackendUser;

public interface BackendUserService {
	
	/**
	 * 后台用户登录
	 * @param userCode
	 * @param userPassword
	 * @return
	 * @throws Exception
	 */
	public BackendUser login(String userCode,String userPassword) throws Exception;
}
