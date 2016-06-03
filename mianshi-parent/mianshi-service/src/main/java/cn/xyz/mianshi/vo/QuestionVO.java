package cn.xyz.mianshi.vo;

import java.util.List;

public class QuestionVO {
	private String answer;
	private String body;
	private Integer companyId;
	private Integer examId;
	private List<QuestionOptionVO> options;
	private Integer questionId;
	private Integer score;
	private Integer time;
	private Integer type;

	public String getAnswer() {
		return answer;
	}

	public String getBody() {
		return body;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public Integer getExamId() {
		return examId;
	}

	public List<QuestionOptionVO> getOptions() {
		return options;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public Integer getScore() {
		return score;
	}

	public Integer getTime() {
		return time;
	}

	public Integer getType() {
		return type;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public void setOptions(List<QuestionOptionVO> options) {
		this.options = options;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
