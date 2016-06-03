package cn.xyz.mapper;

import java.util.List;
import java.util.Map;

import cn.xyz.mianshi.vo.ExamGrading;
import cn.xyz.mianshi.vo.ExamType;
import cn.xyz.mianshi.vo.ExamVO;

public interface ExamMapper {

	List<ExamType> selectExamType();

	ExamVO get(int examId);

	int countByExample(Object parameter);

	List<ExamVO> selectByExample(Object parameter);

	int insert(ExamVO exam);

	int delete(int examId);

	int update(ExamVO exam);

	int savePaidExam(Map<String, Object> parameter);

	List<Integer> selectPaidExamId(Map<String, Object> parameter);

	int saveExamGrading(ExamGrading examGrading);

	List<ExamGrading> selectExamGrading(int examId);

	ExamGrading getExamGradingByScore(int score);

}
