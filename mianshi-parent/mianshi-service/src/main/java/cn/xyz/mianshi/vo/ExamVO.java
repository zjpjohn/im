package cn.xyz.mianshi.vo;

import java.util.List;

public class ExamVO {
	private Integer companyId;
	private Integer examId;
	private String examName;
	private Integer examType;
	private Integer isPreciseTime;
	private List<QuestionVO> questions;
	private Integer status;
	private Integer time;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public Integer getExamType() {
		return examType;
	}

	public void setExamType(Integer examType) {
		this.examType = examType;
	}

	public Integer getIsPreciseTime() {
		return isPreciseTime;
	}

	public void setIsPreciseTime(Integer isPreciseTime) {
		this.isPreciseTime = isPreciseTime;
	}

	public List<QuestionVO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionVO> questions) {
		this.questions = questions;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

}
