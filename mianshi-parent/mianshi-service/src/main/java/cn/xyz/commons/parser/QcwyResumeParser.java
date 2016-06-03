package cn.xyz.commons.parser;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

import org.htmlparser.Node;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;

import cn.xyz.commons.constants.KConstantsUtil;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.vo.cv.Edu;
import cn.xyz.mianshi.vo.cv.Intent;
import cn.xyz.mianshi.vo.cv.Personal;
import cn.xyz.mianshi.vo.cv.Project;
import cn.xyz.mianshi.vo.cv.ResumeV2;
import cn.xyz.mianshi.vo.cv.Work;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class QcwyResumeParser extends AbstractResumeParser {

	public class CVInfoFilter extends AndFilter {
		private static final long serialVersionUID = 1L;

		public CVInfoFilter() {
			super(new TagNameFilter("span"), new HasAttributeFilter("class",
					"blue1"));
		}
	}

	public class CVNameFilter extends TagNameFilter {

		private static final long serialVersionUID = 1L;

		public CVNameFilter() {
			super("STRONG");
		}
	}

	public QcwyResumeParser(InputStream in) {
		super(in);
	}

	List<Edu> getEduList(Node node) {
		List<Edu> eduList = Lists.newArrayList();

		// 获取单元格所在行
		Node row = node.getParent();
		// 跳过3行
		for (int i = 0; i < 3; i++)
			row = row.getNextSibling();

		// 取第一个教育经历
		// TR//TD[0]//TABLE[0]//TR[0]//TD
		// row.getChildren().elementAt(0).getChildren().elementAt(0).getChildren().elementAt(0).getChildren();

		// 迭代教育经历数组
		// TR//TD[0]//TABLE[0]//TR
		NodeList nodeTrList = row.getChildren().elementAt(0).getChildren()
				.elementAt(0).getChildren();
		for (int i = 0; i < nodeTrList.size(); i++) {
			// 获取单元格集合
			NodeList nodeTdList = nodeTrList.elementAt(i).getChildren();
			// 跳过分隔行
			if (nodeTdList.size() < 4)
				continue;

			String[] s = Splitter.on("--").trimResults()
					.splitToList(nodeTdList.elementAt(0).toPlainTextString())
					.toArray(new String[] {});
			Long begin = DateUtil.s2s(s[0].toString());
			Long end = DateUtil.s2s(s[1].toString());
			String name = nodeTdList.elementAt(1).toPlainTextString();
			String degreeText = nodeTdList.elementAt(3).toPlainTextString();
			Integer degree = KConstantsUtil.MAP_DEGREE.get(degreeText);
			String majorText = nodeTdList.elementAt(2).toPlainTextString();
			Integer major = KConstantsUtil.MAP_MAJOR.get(majorText);

			eduList.add(new Edu(begin, end, name, degree, degreeText, major,
					majorText));
		}

		return eduList;
	}

	Personal getPersonal() {
		Long birthday = 0l;
		String email = "";
		String location = "";
		String name = "";
		Integer sex = 0;
		String telephone = "";
		Integer workExp = 0;
		Integer cityId = 0;
		// Integer areaId = 0;

		// 提取工作经验、性别、生日、
		Node nodeTd = getNode(new AndFilter(new TagNameFilter("span"),
				new HasAttributeFilter("class", "blue1")));
		List<String> results = Splitter.on("|").trimResults()
				.splitToList(trim(nodeTd.toPlainTextString()));

		workExp = KConstantsUtil.MAP_WORK_EXP.get(results.get(0));
		sex = "男".equals(results.get(1)) ? 1 : 0;
		birthday = new Function<String, Long>() {
			@Override
			public Long apply(String t) {
				String s = t.substring(t.indexOf("(") + 1, t.length() - 1)
						.replace("年", "-").replace("月", "-").replace("日", "");
				return DateUtil.toSeconds(s);
			}

		}.apply(results.get(2));

		// 提取居住地、电话、邮箱
		Node nodeTr = nodeTd.getParent().getParent();

		while (null != nodeTr) {
			NodeList nodeList = nodeTr.getChildren();
			if (null == nodeList || 0 == nodeList.size())
				continue;
			String text = trim(nodeList.elementAt(0).toPlainTextString());
			String value = trim(nodeList.elementAt(1).toPlainTextString());
			if ("居住地：".equals(text)) {
				location = value;
				cityId = KConstantsUtil.getCityIdByName(location.split("-")[0]);
			} else if ("电话：".equals(text)) {
				telephone = value.replace("（手机）", "");
			} else if ("E-mail：".equals(text)) {
				email = value;
			}
			// 下一行
			nodeTr = nodeTr.getNextSibling();
		}

		// 提取姓名
		name = parse(new CVNameFilter()).elementAt(0).toPlainTextString();

		Personal p = new Personal();
		// p.setAddress(address);
		// p.setAreaId(areaId);
		p.setB(birthday);
		p.setCityId(cityId);
		// p.setCountryId(countryId);
		// p.setD(d);
		p.setEmail(email);
		// p.setEvaluate(evaluate);
		// p.setIdNumber(idNumber);
		// p.setJ(jobStatus);
		p.setLocation(location);
		// p.setM(m);
		p.setName(name);
		// p.setNmCo(nmCo);
		// p.setNmMa(nmMa);
		// p.setNmSc(nmSc);
		// p.setProvinceId(provinceId);
		p.setS(sex);
		// p.setSalary(salary);
		p.setTelephone(telephone);
		p.setW(workExp);

		return p;
	}

	List<Project> getProjectList(Node nodeTd) {
		List<Project> projectList = Lists.newArrayList();

		Node nodeTr = nodeTd.getParent();// 获取单元格所在行
		for (int i = 0; i < 3; i++) {// 跳过3行
			nodeTr = nodeTr.getNextSibling();
		}

		NodeList nodeTrList = nodeTr.getChildren().elementAt(0).getChildren()
				.elementAt(0).getChildren();
		Project project = null;
		for (int i = 0; i < nodeTrList.size(); i++) {
			if (1 == nodeTrList.elementAt(i).getChildren().size() || i == 0) {
				if (i == 0) {

				} else {
					i++;
				}

				String[] array1 = nodeTrList.elementAt(i).toPlainTextString()
						.split("：");
				String[] array2 = array1[0].split("--");

				project = new Project();
				project.setBegin(DateUtil.s2s(array2[0]));
				project.setEnd(DateUtil.s2s(array2[1]));
				project.setName(array1[1]);

				i++;
			}

			String rowName = StringUtil.trim(nodeTrList.elementAt(i)
					.getChildren().elementAt(0).toPlainTextString());
			String value = nodeTrList.elementAt(i).getChildren().elementAt(1)
					.toPlainTextString();
			switch (rowName) {
			case "软件环境：":
				project.setSoftware(value);
				break;
			case "硬件环境：":
				project.setHardware(value);
				break;
			case "开发工具：":
				project.setTool(value);
				break;
			case "项目描述：":
				project.setDesc(value);
				break;
			case "责任描述：":
				project.setDuty(value);
				break;
			default:
				break;
			}

			if (i == nodeTrList.size() - 1
					|| nodeTrList.elementAt(i + 1).getChildren().size() == 1) {
				projectList.add(project);
			}

		}

		return projectList;
	}

	Work getWork(NodeList nodeList, int i) {
		String text = trim(nodeList.elementAt(i).toPlainTextString());
		String[] dates = text.substring(0, text.indexOf("：")).split("--");
		long begin = DateUtil.s2s(dates[0]);
		long end = DateUtil.s2s(dates[1]);
		String companyName = text.substring(text.indexOf("：") + 1,
				text.indexOf("（"));
		String scaleText = text.substring(text.indexOf("（") + 1,
				text.indexOf("）"));
		int scale = KConstantsUtil.MAP_SCALE.get(scaleText);
		String industryText = nodeList.elementAt(i + 1).toPlainTextString()
				.substring(5);
		int industry = KConstantsUtil.MAP_INDUSTRY.get(industryText);
		String department = nodeList.elementAt(i + 2).getChildren()
				.elementAt(0).toPlainTextString();
		String fnText = nodeList.elementAt(i + 2).getChildren().elementAt(1)
				.toPlainTextString();
		int fn = KConstantsUtil.MAP_FN.get(fnText);
		String desc = nodeList.elementAt(i + 3).toPlainTextString();
		String natureText = "";
		int nature = 0;

		return new Work(begin, companyName, department, desc, end, fn, fnText,
				industry, industryText, nature, natureText, scale, scaleText);
	}

	List<Work> getWorkList(Node node) {
		List<Work> workList = Lists.newArrayList();

		// 获取单元格所在行
		Node row = node.getParent();
		// 跳过3行
		for (int i = 0; i < 3; i++) {
			row = row.getNextSibling();
		}

		NodeList nodeList = row.getChildren().elementAt(0).getChildren()
				.elementAt(0).getChildren();
		for (int i = 0; i < nodeList.size() / 4; i++) {
			workList.add(getWork(nodeList, i * 5));
		}

		return workList;
	}

	@Override
	public ResumeV2 read() {
		Personal personal = getPersonal();
		Intent intent = new Intent();
		List<Edu> eduList = Lists.newArrayList();
		List<Work> workList = Lists.newArrayList();
		List<Project> projectList = Lists.newArrayList();

		NodeList nodeTdList = parse(new AndFilter(new TagNameFilter("td"),
				new HasAttributeFilter("class", "cvtitle")));
		for (int i = 0; i < nodeTdList.size(); i++) {
			Node nodeTd = nodeTdList.elementAt(i);
			String title = nodeTd.toPlainTextString();

			// 提取自我评价
			if ("自我评价".equals(title)) {
				Node row = nodeTd.getParent();
				for (int j = 0; j < 3; j++) {
					row = row.getNextSibling();
				}
				String evaluate = row.toPlainTextString();
				personal.setEvaluate(evaluate);
			} else if ("求职意向".equals(title)) {
				Node row = nodeTd.getParent();
				// 跳过3行
				for (int j = 0; j < 3; j++) {
					row = row.getNextSibling();
				}
				//
				NodeList nodeList = row.getChildren().elementAt(0)
						.getChildren().elementAt(0).getChildren();
				//
				String fnText = "", industryText = "", workTypeText = "", cityText = "", salaryText = "", dutyTimeText = "";
				int w = 0, c = 0, s = 0, d = 0;
				StringBuilder iBuilder = new StringBuilder();

				for (int j = 0; j < nodeList.size(); j++) {
					Node node = nodeList.elementAt(j);
					if (node instanceof TableRow) {
						String textString = trim(node.toPlainTextString());
						List<String> results = Splitter.on("：").trimResults()
								.splitToList(textString);
						switch (results.get(0)) {
						case "到岗时间":// 到岗时间
							dutyTimeText = results.get(1);
							d = KConstantsUtil.MAP_DUTY_TIME.get(dutyTimeText);
							break;
						case "工作性质":// 工作性质、工作类型
							workTypeText = results.get(1);
							w = KConstantsUtil.MAP_WORK_TYPE.get(workTypeText);
							break;
						case "希望行业":// 希望行业
							industryText = results.get(1);
							Splitter.on("，")
									.splitToList(industryText)
									.forEach(
											ss -> {
												iBuilder.append(
														KConstantsUtil.MAP_INDUSTRY
																.get(ss))
														.append(",");
											});
							break;
						case "目标地点":// 工作地点、目标地点
							cityText = results.get(1);
							c = KConstantsUtil.getCityIdByName(cityText);
							break;
						case "期望月薪":// 期望月薪
							salaryText = results.get(1);
							salaryText = salaryText.replaceAll("/月", "");
							s = KConstantsUtil.MAP_SALARY.get(salaryText);
							break;
						case "目标职能":// 目标职能
							fnText = results.get(1);
							// Splitter.on("，").splitToList(fnText).forEach(ss
							// -> {
							// fBuilder.append(Props.MAP_FN.get(ss)).append(",");
							// });
							break;
						}
					}
				}
				// intent = new Intent(c, cityText, d, dutyTimeText,
				// fBuilder.substring(0, fBuilder.length() - 1), fnText,
				// iBuilder.substring(0, iBuilder.length() - 1), industryText,
				// w, workTypeText, s, salaryText);
				intent = new Intent(c, cityText, d, dutyTimeText, "", fnText,
						iBuilder.substring(0, iBuilder.length() - 1),
						industryText, w, workTypeText, s, salaryText);
			} else if ("工作经验".equals(title)) {
				workList = getWorkList(nodeTd);
			} else if ("项目经验".equals(title)) {
				projectList = getProjectList(nodeTd);
			} else if ("教育经历".equals(title)) {
				eduList = getEduList(nodeTd);
			}

		}

		ResumeV2 resume = new ResumeV2();
		resume.setE(eduList);
		resume.setI(intent);
		resume.setP(personal);
		resume.setW(workList);
		resume.setProjectList(projectList);

		return resume;
	}
}
