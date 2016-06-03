package cn.xyz.mapper;

import java.util.List;
import java.util.Map;

import cn.xyz.mianshi.vo.QuestionVO;

public interface QuestionMapper {

	int delete(int questionId);

	int insert(QuestionVO question);

	List<QuestionVO> selectByCompanyId(int companyId);

	List<QuestionVO> selectByExamId(int examId);

	List<QuestionVO> selectByExample(Map<String, Object> parameter);

	int countByExample(Map<String, Object> parameter);

	QuestionVO selectById(int questionId);

	int update(QuestionVO question);

}
