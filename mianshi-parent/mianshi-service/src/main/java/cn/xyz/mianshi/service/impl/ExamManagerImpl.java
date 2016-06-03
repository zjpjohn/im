package cn.xyz.mianshi.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.mapper.CompanyMapper;
import cn.xyz.mapper.ExamMapper;
import cn.xyz.mapper.OptionMapper;
import cn.xyz.mapper.QuestionMapper;
import cn.xyz.mianshi.service.ExamManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.ExamType;
import cn.xyz.mianshi.vo.ExamVO;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.QuestionOptionVO;
import cn.xyz.mianshi.vo.QuestionVO;
import cn.xyz.mianshi.vo.User;

import com.google.common.collect.Maps;

@Service
public class ExamManagerImpl implements ExamManager {

	@Autowired
	private ExamMapper examMapper;
	@Autowired
	private OptionMapper optionMapper;
	@Autowired
	private QuestionMapper questionMapper;
	@Autowired
	private UserManager userManager;
	@Autowired
	private CompanyMapper compamyMapper;

	@Override
	public int insert(int userId, ExamVO exam) {
		User user = userManager.getUser(userId);
		if (0 != user.getCompanyId()) {
			ExamVO entity = new ExamVO();
			entity.setCompanyId(user.getCompanyId());
			entity.setExamName(exam.getExamName());
			entity.setExamType(exam.getExamType());
			entity.setIsPreciseTime(exam.getIsPreciseTime());
			entity.setTime(0);
			entity.setStatus(1);

			return examMapper.insert(entity);
		}
		return 0;
	}

	@Override
	public int delete(int userId, int examId) {
		User user = userManager.getUser(userId);
		if (0 != user.getCompanyId()) {
			ExamVO exam = new ExamVO();
			exam.setExamId(examId);
			exam.setStatus(0);

			return examMapper.update(exam);
		}
		return 0;
	}

	@Override
	public int updateExam(int userId, ExamVO exam) {
		User user = userManager.getUser(userId);
		if (0 != user.getCompanyId()) {
			return examMapper.update(exam);
		}
		return 0;
	}

	@Override
	public List<ExamVO> selectByCompanyId(Integer companyId, int type, int status) {
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("companyId", companyId);
		parameter.put("type", type);
		parameter.put("status", status);

		List<ExamVO> examList = examMapper.selectByExample(parameter);
		List<Integer> paidExamIdList = examMapper.selectPaidExamId(parameter);

		if (null != paidExamIdList && !paidExamIdList.isEmpty()) {
			parameter = Maps.newHashMap();
			parameter.put("idList", paidExamIdList);

			List<ExamVO> paidExamList = examMapper.selectByExample(parameter);

			examList.addAll(paidExamList);
		}

		return examList;
	}

	@Override
	public ExamVO get(int examId, boolean isContainQuestion) {
		ExamVO exam = examMapper.get(examId);
		if (isContainQuestion) {
			List<QuestionVO> questions = questionMapper.selectByExamId(examId);
			questions.forEach(question -> {
				List<QuestionOptionVO> options = optionMapper.selectByQuestionId(question.getQuestionId());
				question.setOptions(options);
			});
			exam.setQuestions(questions);
		}

		return exam;
	}

	@Override
	public List<ExamVO> selectByIdList(List<Integer> examIdList, boolean isContainQuestion) {
		HashMap<String, Object> parameter = Maps.newHashMap();
		parameter.put("idList", examIdList);

		List<ExamVO> results = examMapper.selectByExample(parameter);

		if (isContainQuestion)
			results.forEach(exam -> {
				List<QuestionVO> questions = questionMapper.selectByExamId(exam.getExamId());
				questions.forEach(question -> {
					List<QuestionOptionVO> options = optionMapper.selectByQuestionId(question.getQuestionId());
					question.setOptions(options);
				});
				exam.setQuestions(questions);
			});

		return results;
	}

	@Override
	public List<ExamVO> selectByUserId(Integer userId, int type, int status) {
		User user = userManager.getUser(userId);
		return selectByCompanyId(user.getCompany().getId(), type, status);
	}

	@Override
	public List<ExamType> selectExamType() {
		return examMapper.selectExamType();
	}

	@Override
	public PageVO selectPage(int userId, int type, int status, int offset, int limit) {
		User user = userManager.getUser(userId);
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("companyId", user.getCompanyId());
		parameter.put("type", type);
		parameter.put("status", status);
		parameter.put("limit", offset + "," + limit);

		long total = examMapper.countByExample(parameter);
		List<?> pageData = examMapper.selectByExample(parameter);

		return new PageVO(pageData, total);
	}
}
