package samples;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.xyz.mapper.OptionMapper;
import cn.xyz.mapper.QuestionMapper;
import cn.xyz.mianshi.vo.QuestionOptionVO;
import cn.xyz.mianshi.vo.QuestionVO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MyApplication.class)
public class InsertQuestionWithXls {

	@Autowired
	private QuestionMapper questionMapper;
	@Autowired
	private OptionMapper optionMapper;

	private Pattern pattern = Pattern.compile("[0-9]+\\.+[a-zA-Z_\\s,\\.]+");

	public boolean isQuestion(String input) {
		return pattern.matcher(input).matches();
	}

	public String getAnswer(String ss) {
		int index = ss.indexOf('_');
		return ss.substring(index + 1, index + 2);
	}

	@Test
	public void doInsert() throws Exception {
		String s = "C:/Users/Administrator/Desktop/123.xls";
		File file = new File(s);
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet = wb.getSheetAt(0);
		int rowCount = sheet.getLastRowNum();

		for (int i = 0; i < rowCount; i++) {
			HSSFRow row = sheet.getRow(i);
			HSSFCell cell = row.getCell(0);

			if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
				String ss = cell.getStringCellValue().trim();
				if (isQuestion(ss)) {
					String answer = getAnswer(ss);
					String questionBody = ss.substring(ss.indexOf(".") + 1)
							.trim().replace("_" + answer + "_", "_____");
					String theAnswer = "";
					QuestionVO question = new QuestionVO();
					// question.setAnswer(answer);
					question.setBody(questionBody);
					question.setCompanyId(0);
					question.setExamId(20);
					// question.setOptions(options);
					// question.setQuestionId(questionId);
					question.setScore(10);
					question.setTime(30);
					question.setType(1);

					if (1 == questionMapper.insert(question)) {
						String sss = sheet.getRow(i + 1).getCell(0)
								.getStringCellValue().trim();
						String aaaList[] = sss.split("   ");
						for (String aaa : aaaList) {
							aaa = aaa.trim();
							String body = aaa.split(" ")[1];
							QuestionOptionVO option = new QuestionOptionVO();
							option.setQuestionId(question.getQuestionId());
							option.setBody(body);
							option.setScore(0);
							if (aaa.split(" ")[0].equalsIgnoreCase(answer)) {
								option.setCorrect(1);
								optionMapper.insert(option);

								theAnswer = option.getOptionId() + ",";
							} else {
								option.setCorrect(0);

								optionMapper.insert(option);
							}
						}

						QuestionVO tQuestion = new QuestionVO();
						tQuestion.setQuestionId(question.getQuestionId());
						tQuestion.setAnswer(theAnswer);

						questionMapper.update(tQuestion);

					}

				}
			}

		}
		wb.close();
	}
}
