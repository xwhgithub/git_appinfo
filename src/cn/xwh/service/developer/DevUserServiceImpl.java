package cn.xwh.service.developer;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.xwh.dao.devuser.DevUserMapper;
import cn.xwh.entity.DevUser;

@Service("devUserService")
public class DevUserServiceImpl implements DevUserService {
	
	@Resource(name="devUserMapper")
	private DevUserMapper devUserMapper;

	/**
	 * 开发者登录
	 */
	public DevUser getLoginUser(String devCode,String passWord) {
		DevUser devUser=null;
		try {
			 devUser=devUserMapper.getLoginUser(devCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (devUser!=null) {
			if (!passWord.equals(devUser.getDevPassword())) {
				devUser=null;
			}
		}
		return devUser;
	}
}
