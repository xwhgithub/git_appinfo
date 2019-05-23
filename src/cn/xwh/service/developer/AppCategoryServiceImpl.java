package cn.xwh.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.xwh.dao.appcategory.AppCategoryMapper;
import cn.xwh.entity.AppCategory;
@Service("appCategoryService")
public class AppCategoryServiceImpl implements AppCategoryService {
	
	@Resource(name="appCategoryMapper")
	private AppCategoryMapper appCategoryMapper;
	/**
	 * 根据父级id查询分类列表
	 */
	public List<AppCategory> getAppCategoryListByParentId(Integer parentId) throws Exception {
		
		return appCategoryMapper.getAppCategoryListByParentId(parentId);
	}

}
