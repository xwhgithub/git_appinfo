package cn.xwh.service.backend;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.xwh.dao.backenduser.BackendUserMapper;
import cn.xwh.entity.BackendUser;

@Service("backendUserService")
public class BackendUserServiceImpl implements BackendUserService {
	
	@Resource(name="backendUserMapper")
	private BackendUserMapper backendUserMapper;
	
	/**
	 * 后台用户登录
	 */
	public BackendUser login(String userCode, String userPassword) throws Exception {
		BackendUser user=backendUserMapper.getLoginUser(userCode);
		//判断是否存在此用户
		if (user!=null) {
			//判断密码是否正确
			if (!user.getUserPassword().equals(userPassword)) {
				//如果不正确 则设置返回对象为null
				user=null;
			}
		}
		return user;
	}

}
