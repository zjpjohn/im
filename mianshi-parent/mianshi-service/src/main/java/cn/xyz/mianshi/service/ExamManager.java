package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.mianshi.vo.ExamType;
import cn.xyz.mianshi.vo.ExamVO;
import cn.xyz.mianshi.vo.PageVO;

public interface ExamManager {

	/**
	 * 新增题库
	 * 
	 * @param userId
	 * @param exam
	 * @return
	 */
	int insert(int userId, ExamVO exam);

	/**
	 * 删除题库
	 * 
	 * @param userId
	 * @param examId
	 * @return
	 */
	int delete(int userId, int examId);

	/**
	 * 更新题库
	 * 
	 * @param userId
	 * @param exam
	 * @return
	 */
	int updateExam(int userId, ExamVO exam);

	/**
	 * 获取题库类型
	 * 
	 * @return
	 */
	List<ExamType> selectExamType();

	/**
	 * 获取题库
	 * 
	 * @param examId
	 *            题库Id
	 * @param isContainQuestion
	 *            题库是否包含试题
	 * @return
	 */
	ExamVO get(int examId, boolean isContainQuestion);

	List<ExamVO> selectByUserId(Integer userId, int type, int status);

	List<ExamVO> selectByCompanyId(Integer companyId, int type, int status);

	/**
	 * 根据题库Id获取题库
	 * 
	 * @param examIdList
	 *            题库Id列表
	 * @param isContainQuestion
	 *            题库是否包含试题
	 * @return
	 */
	List<ExamVO> selectByIdList(List<Integer> examIdList, boolean isContainQuestion);

	PageVO selectPage(int userId, int type, int status, int offset, int limit);

}
