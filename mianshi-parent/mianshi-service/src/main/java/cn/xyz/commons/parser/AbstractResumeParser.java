package cn.xyz.commons.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.cyberneko.html.parsers.DOMParser;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.xml.sax.InputSource;

import cn.xyz.commons.utils.FileUtil;
import cn.xyz.mianshi.vo.cv.ResumeV2;

public abstract class AbstractResumeParser {
	public static class StrongTag extends CompositeTag {
		private static final long serialVersionUID = 1L;
		private static final String[] mIds = new String[] { "STRONG" };

		public String[] getIds() {
			return (mIds);
		}

		public String[] getEnders() {
			return (mIds);
		}

	}

	public static final int CV_QCWY = 1;

	protected DOMParser domParser;
	protected Parser htmlParser;

	public static AbstractResumeParser newReader(int zpcId, InputStream in) {
		if (CV_QCWY == zpcId)
			return new QcwyResumeParser(in);
		throw new RuntimeException("");
	}

	public AbstractResumeParser(InputStream in) {
		InputStream inStream = convertStream(in);
		inStream.mark(0);
		try {
			domParser = new DOMParser();
			domParser.setProperty("http://cyberneko.org/html/properties/default-encoding", "gbk");
			domParser.setFeature("http://xml.org/sax/features/namespaces", false);
			domParser.parse(new InputSource(inStream));

			inStream.reset();
			String resource = FileUtil.readAll(inStream, "gbk");
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
			factory.registerTag(new StrongTag());
			htmlParser = new Parser(resource);
			htmlParser.setNodeFactory(factory);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != inStream)
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private InputStream convertStream(InputStream in) {
		try {
			Session session = Session.getDefaultInstance(System.getProperties(), null);
			MimeMessage message = new MimeMessage(session, in);
			Object content = message.getContent();

			if (content instanceof Multipart) {
				MimeMultipart multipart = (MimeMultipart) content;
				MimeBodyPart bp = (MimeBodyPart) multipart.getBodyPart(0);

				return bp.getInputStream();
			} else
				throw new RuntimeException("Mht流转换Html流失败");
		} catch (Exception e) {
			e.printStackTrace();

			throw new RuntimeException(e);
		} finally {
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	protected Node getNode(NodeFilter filter) {
		return parse(filter).elementAt(0);
	}

	protected NodeList parse(NodeFilter filter) {
		try {
			htmlParser.reset();
			return htmlParser.parse(filter);
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
	}

	protected String trim(String s) {
		return s.replaceAll("&nbsp;", "").replaceAll(" ", "").replaceAll("　", "").replaceAll("\t", "").replaceAll("\n", "");
	}

	public abstract ResumeV2 read();

}
