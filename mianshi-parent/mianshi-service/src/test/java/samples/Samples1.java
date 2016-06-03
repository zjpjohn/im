package samples;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import cn.xyz.mapper.OptionMapper;
import cn.xyz.mapper.QuestionMapper;
import cn.xyz.mianshi.vo.QuestionOptionVO;
import cn.xyz.mianshi.vo.QuestionVO;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = MyApplication.class)
public class Samples1 {

	@Autowired
	private QuestionMapper questionMapper;
	@Autowired
	private OptionMapper optionMapper;

	static Pattern pattern = Pattern.compile("[0-9]+\\.+[a-zA-Z_\\s,\\.]+");

	public static boolean isQuestion(String input) {
		return pattern.matcher(input).matches();
	}

	public static void test() {
		System.out.println(isQuestion("a"));
		System.out.println(isQuestion("a.b"));
		System.out.println(isQuestion("aaaaa.c"));
		System.out.println(isQuestion("1"));
		System.out.println(isQuestion("12344._"));
		System.out.println(isQuestion("12345.b_____"));
		System.out.println(isQuestion("12345.b_____"));
		System.out.println(isQuestion("1. _____"));
		System.out
				.println(isQuestion("41. He came back late, _B_ which time all the guests had already left."));
	}

	public static String getAnswer(String ss) {
		int index = ss.indexOf('_');
		return ss.substring(index + 1, index + 2);
	}

	public static String getQuestionBody(String answer, String ss) {
		return ss.substring(ss.indexOf(".") - 1).replace("_" + answer + "_",
				"_____");
	}

	public static void main(String[] args) throws Exception {

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

					String sss = sheet.getRow(i + 1).getCell(0)
							.getStringCellValue().trim();
//					 System.out.println(sss.replace("   ", "---"));
					 
					String aaaList[] = sss.split("   ");
					System.out.println(aaaList.length);
					for (String aaa : aaaList) {
						aaa = aaa.trim();
						String body = aaa.split(" ")[1];
						
//						System.out.println(body);
						QuestionOptionVO option = new QuestionOptionVO();
						option.setBody(body);
						if (aaa.substring(0, 1).equalsIgnoreCase(answer)) {
							option.setCorrect(1);
						} else
							option.setCorrect(0);
						option.setScore(0);
						// option.setOptionId(optionId);
						// option.setQuestionId(questionId);
					}

					// System.out.println(sss.replace("   ", "---"));
				}
			}

		}
		wb.close();
	}
}
