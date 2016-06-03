package a;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

public class DiffTest {

	public static Map<String, String> a = Maps.newHashMap();
	public static Map<String, String> b = Maps.newHashMap();

	public static void main(String[] args) {
		File parent = new File(
				"D:\\Workspace\\Java\\kuliao-parent\\kuliao-admin\\src\\main\\webapp\\kuliao");
		treebb(parent);
		// File parent = new
		// File("E:\\BaiduYunDownload\\剑网3服务端\\Sword3\\Source");
		// treea(parent);
		//
		// File parentb = new File(
		// "E:\\BaiduYunDownload\\剑网3完整源代码（没有编译测试）\\剑网3完整源代码\\Source");
		// treeb(parentb);
		//
		// System.out.println(a.size());
		// System.out.println(b.size());
		// for (String key : a.keySet()) {
		// b.remove(key);
		// }
		// for (String key : b.keySet()) {
		// System.out.println(key);
		// }
	}

	public static void treebb(File parent) {
		System.out
				.println(parent
						.getPath()
						.replace(
								"D:\\Workspace\\Java\\kuliao-parent\\kuliao-admin\\src\\main\\webapp\\kuliao\\",
								""));
		if (parent.isFile()) {
			return;
		} else {
			for (File child : parent.listFiles()) {
				treebb(child);
			}
		}
	}

	public static void treea(File parent) {
		a.put(parent.getPath().replace(
				"E:\\BaiduYunDownload\\剑网3服务端\\Sword3\\Source", ""),
				parent.getPath());
		if (parent.isFile()) {
			return;
		} else {
			for (File child : parent.listFiles()) {
				treea(child);
			}
		}
	}

	public static void treeb(File parent) {
		b.put(parent.getPath().replace(
				"E:\\BaiduYunDownload\\剑网3完整源代码（没有编译测试）\\剑网3完整源代码\\Source", ""),
				parent.getPath());
		if (parent.isFile()) {
			return;
		} else {
			for (File child : parent.listFiles()) {
				treeb(child);
			}
		}
	}
}
