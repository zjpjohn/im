package samples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MyApplication.class)
public class QuestionMapperTest {

	// @Autowired
	// private QuestionMapper questionMapper;
	// @Autowired
	// private OptionMapper optionMapper;

	@Test
	public void test2() throws Exception {
		// File file = new File("E:\\[已拷贝]\\aaaaaa.xls");
		// List<QuestionVO> data = null;
		//
		// try {
		// data = QuestionXlsParser.readQuestion(file, 0, 0);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// for (int i = 0; i < data.size(); i++) {
		// QuestionVO question = data.get(i);
		// StringBuffer sb = new StringBuffer();
		// if (1 == questionMapper.insert(question)) {
		// if (1 == question.getType() || 2 == question.getType()
		// || 5 == question.getType()) {
		// for (QuestionOptionVO option : question.getOptions()) {
		// option.setQuestionId(question.getQuestionId());
		// optionMapper.insert(option);
		// if (1 == option.getCorrect()) {
		// sb.append(option.getOptionId()).append(",");
		// }
		// }
		// QuestionVO tQuestion = new QuestionVO();
		// tQuestion.setQuestionId(question.getQuestionId());
		// // tQuestion.setAnswer(sb.substring(0, sb.length() - 1));
		// tQuestion.setAnswer(sb.toString());
		//
		// questionMapper.update(tQuestion);
		// }
		// } else {
		// }
		// }
	}

	@Test
	public void test() {
		// File file = new File("E:\\[已拷贝]\\a.xls");
		// List<QuestionVO> questionList = QuestionXlsParser
		// .parseToXL(file, 19, 0);
		//
		// for (int i = 0; i < questionList.size(); i++) {
		// QuestionVO question = questionList.get(i);
		// StringBuffer sb = new StringBuffer();
		// if (1 == questionMapper.insert(question)) {
		// for (QuestionOptionVO option : question.getOptions()) {
		// option.setQuestionId(question.getQuestionId());
		// optionMapper.insert(option);
		// if (1 == option.getCorrect()) {
		// sb.append(option.getOptionId()).append(",");
		// }
		// }
		// // QuestionVO tQuestion = new QuestionVO();
		// // tQuestion.setQuestionId(question.getQuestionId());
		// // // tQuestion.setAnswer(sb.substring(0, sb.length() - 1));
		// // tQuestion.setAnswer(sb.toString());
		// //
		// // questionMapper.update(tQuestion);
		// } else {
		// }
		// }
		//
		// System.out.println(questionMapper);
	}

}
