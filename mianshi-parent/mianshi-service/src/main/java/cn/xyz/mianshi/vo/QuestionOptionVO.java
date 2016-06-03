package cn.xyz.mianshi.vo;

public class QuestionOptionVO {
	private String body;// 选项内容
	private Integer correct;// 正确选项（0=否；1=是）
	private Integer optionId;// 选项索引
	private Integer questionId;// 问题索引
	private Integer score;// 选项分数，暂定问题类型为心理测试题时使用

	public String getBody() {
		return body;
	}

	public Integer getCorrect() {
		return correct;
	}

	public Integer getOptionId() {
		return optionId;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public Integer getScore() {
		return score;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setCorrect(Integer correct) {
		this.correct = correct;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

}
