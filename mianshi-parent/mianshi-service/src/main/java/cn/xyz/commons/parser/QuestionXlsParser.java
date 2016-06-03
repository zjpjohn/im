package cn.xyz.commons.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cn.xyz.mianshi.vo.QuestionOptionVO;
import cn.xyz.mianshi.vo.QuestionVO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class QuestionXlsParser {
	public static final Map<String, Integer> map;
	static {
		map = Maps.newHashMap();
		map.put("单选题", 1);
		map.put("多选题", 2);
		map.put("填空题", 3);
		map.put("问答题", 4);
		map.put("是非题", 5);
		map.put("上传视频题", 6);
		map.put("上传图片题", 7);
		map.put("上传音频题", 8);
		map.put("上传文件题", 9);
	}

	public static void main(String... args) {

		File file = new File("E:\\[已拷贝]\\a.xls");
		List<QuestionVO> questionList = parseToXL(file, 1, 2);

		System.out.println(questionList);
	}

	public static List<QuestionVO> parseToXL(File file, int examId,
			int companyId) {
		List<QuestionVO> questionList = null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = wb.getSheetAt(0);
			int rowCount = sheet.getLastRowNum();

			questionList = Lists.newArrayList();
			QuestionVO question = null;
			List<QuestionOptionVO> options = Lists.newArrayList();

			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				HSSFRow row = sheet.getRow(rowIndex);
				// 空行、分隔行
				if (null == row
						|| null == row.getCell(0)
						|| "".equals(row.getCell(0).getStringCellValue().trim())) {
					question.setOptions(options);
					questionList.add(question);
					question = null;
					options = Lists.newArrayList();
				} else {
					HSSFCell bodyCell = row.getCell(0);
					HSSFCell scoreCell = row.getCell(1);

					if (null != bodyCell && null != scoreCell) {
						QuestionOptionVO option = new QuestionOptionVO();
						option.setBody(bodyCell.getStringCellValue().trim());
						option.setCorrect(1);
						option.setScore((int) scoreCell.getNumericCellValue());

						options.add(option);
					} else {
						question = new QuestionVO();
						// question.setAnswer(answer);
						question.setBody(bodyCell.getStringCellValue());
						question.setCompanyId(companyId);
						question.setExamId(examId);
						// question.setOptions(options);
						// question.setQuestionId(questionId);
						question.setScore(0);
						question.setTime(30);
						question.setType(21);
					}
				}
				if (rowIndex == rowCount) {
					question.setOptions(options);
					questionList.add(question);
					question = null;
					options = Lists.newArrayList();
				}
			}
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return questionList;
	}

	public static List<QuestionVO> readQuestion(File file, int examId,
			int companyId) {
		List<QuestionVO> questionList = null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = wb.getSheetAt(0);
			int rowCount = sheet.getLastRowNum();

			questionList = Lists.newArrayList();
			QuestionVO question = null;
			List<QuestionOptionVO> options = Lists.newArrayList();

			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				HSSFRow row = sheet.getRow(rowIndex);
				// 空行、分隔行
				if (null == row
						|| null == row.getCell(0)
						|| "".equals(row.getCell(0).getStringCellValue().trim())) {
					if (null != question) {
						question.setOptions(options);
						questionList.add(question);
						question = null;
						options = Lists.newArrayList();
					}
				} else {
					HSSFCell bodyCell = row.getCell(0);
					HSSFCell typeOrCorrectCell = row.getCell(1);
					HSSFCell scoreCell = row.getCell(2);
					HSSFCell timeCell = row.getCell(3);

					if (null != bodyCell && null != typeOrCorrectCell
							&& !isEmptyCell(scoreCell)
							&& !isEmptyCell(timeCell)) {
						question = new QuestionVO();
						// question.setAnswer(answer);
						question.setBody(bodyCell.getStringCellValue());
						question.setCompanyId(companyId);//
						question.setExamId(examId);//
						// question.setOptions(options);
						// question.setQuestionId(questionId);
						question.setScore((int) scoreCell.getNumericCellValue());
						question.setTime((int) timeCell.getNumericCellValue());
						question.setType(map.get(typeOrCorrectCell
								.getStringCellValue().trim()));
					} else {
						QuestionOptionVO option = new QuestionOptionVO();
						option.setBody(bodyCell.getStringCellValue().trim());
						// if (null != typeOrCorrectCell)
						option.setCorrect("是".equals(typeOrCorrectCell
								.getStringCellValue().trim()) ? 1 : 0);
						options.add(option);
					}
				}
				if (rowIndex == rowCount) {
					if (null != question) {
						question.setOptions(options);
						questionList.add(question);
						question = null;
						options = Lists.newArrayList();
					}
				}
			}
			wb.close();

			return questionList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}

	public static boolean isEmptyCell(HSSFCell cell) {
		if (null == cell)
			return true;
		if (HSSFCell.CELL_TYPE_STRING == cell.getCellType())
			return "".equals(cell.getStringCellValue());
		if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType())
			return true;
		return false;
	}

}
