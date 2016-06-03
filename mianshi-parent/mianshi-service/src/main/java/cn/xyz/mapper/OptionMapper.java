package cn.xyz.mapper;

import java.util.List;

import cn.xyz.mianshi.vo.QuestionOptionVO;

public interface OptionMapper {

	int insert(QuestionOptionVO option);

	int delete(int optionId);

	int update(QuestionOptionVO option);

	List<QuestionOptionVO> selectByQuestionId(int questionId);

	QuestionOptionVO selectById(int optionId);

}
