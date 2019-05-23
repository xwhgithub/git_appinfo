package cn.xwh.service.developer;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.xwh.entity.AppCategory;

public interface AppCategoryService {

	/**
	 * 根据父级id查询分类列表
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<AppCategory> getAppCategoryListByParentId(@Param("parentId") Integer parentId) throws Exception;
}
