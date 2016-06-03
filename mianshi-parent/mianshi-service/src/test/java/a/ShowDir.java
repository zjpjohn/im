package a;

import java.io.File;

public class ShowDir {

	public static void main(String[] args) {
		// File parent = new File(
		// "D:/Workspace/Java/mianshi-parent/mianshi-im-api/src/main/java");
		// tree(parent);

		// File parent = new File(
		// "D:/Workspace/Java/mianshi-parent/mianshi-service/src/main/java");

		// File parent = new File("D:/Workspace/Android/SkWeiChat-Baidu/src");
		//
		// tree(parent);

		File parent = new File("D:\\Workspace\\Android\\SkMianShi\\src");

		tree(parent);

	}

	public static void tree(File parent) {
		if (parent.isFile()) {
			// System.out
			// .println(parent
			// .getPath()
			// .replace(
			// "D://Workspace//Java//mianshi-parent//mianshi-im-api//src//main//java//",
			// "").replace("//", ".").replace(".java", ""));

			// System.out
			// .println(parent
			// .getPath()
			// .replace(
			// "D://Workspace//Java//mianshi-parent//mianshi-service//src//main//java//",
			// "").replace("//", ".").replace(".java", ""));

			System.out.println(parent.getPath()
					.replace("D:\\Workspace\\Android\\SkMianShi\\src\\", "")
					.replace("\\", ".").replace(".java", ""));
			return;
		} else {
			for (File child : parent.listFiles()) {
				tree(child);
			}
		}
	}
}
