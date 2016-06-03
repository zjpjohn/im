package cn.xyz.mianshi.service;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.example.JobExample;
import cn.xyz.mianshi.vo.JobVO;

public interface JobManager {

	Object save(int userId, JobExample example);

	Object republish(int userId, JobExample example);

	Object delete(int userId, List<ObjectId> idList);

	Object updateOne(int userId, JobExample example);

	Object updateMulti(int userId, List<ObjectId> idList, Long refreshTime);

	Object updateMulti(int userId, List<ObjectId> idList, Integer status);

	JobVO get(ObjectId jobId);

	/**
	 * 获取职位
	 * <p>
	 * 浏览数+1
	 * </p>
	 * 
	 * @param id
	 * @return
	 */
	JobVO selectById(ObjectId id);

	/**
	 * 获取关注公司的职位列表
	 * 
	 * @param userId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<JobVO> selectByCompanyId(int userId, int pageIndex, int pageSize);

	List<JobVO> selectByCompanyId(List<Integer> idList, int pageIndex, int pageSize);

	/**
	 * 职位搜索
	 * 
	 * @param example
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<JobVO> query(JobExample example, int pageIndex, int pageSize);

	/**
	 * 企业用户的职位
	 * 
	 * @param userId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<JobVO> selectByUserId(int userId, int status, int pageIndex, int pageSize);

	/**
	 * 最热的职位
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<JobVO> selectOrderByActive(int pageIndex, int pageSize);

	/**
	 * 最新的职位
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<JobVO> selectOrderByTime(int pageIndex, int pageSize);

}
