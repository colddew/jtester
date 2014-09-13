package org.jtester.module.tracer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jtester.module.tracer.spring.MethodTracerEvent;

public class TracerSequece {
//	public static void generateJpg(String sequenceDescription, File outFile) {
//		try {
//			if (outFile.exists()) {
//				outFile.delete();
//			}
//			TracerSequece.generateSequence(sequenceDescription, outFile);
//		} catch (Throwable e) {
//			throw new RuntimeException(e);
//		}
//	}

//	/**
//	 * 根据输入的sequence描述符生成jpg文件
//	 * 
//	 * @param sequenceDescription
//	 * @throws Exception
//	 */
//	private static void generateSequence(String sequenceDescription, File outFile) throws Exception {
//		String type = "jpg";
//		InputStream in = new ByteArrayInputStream(sequenceDescription.getBytes());
//		OutputStream out = new FileOutputStream(outFile);
//		try {
////			String encoding = ConfigurationManager.getGlobalConfiguration().getFileEncoding();
//			Pair<String, Bean<Configuration>> pair = DiagramLoader.load(in, "utf-8");
//			TextHandler th = new TextHandler(pair.getFirst());
//			Bean<Configuration> conf = pair.getSecond();
//
//			if (type.equals("png")) {
//				ImagePaintDevice paintDevice = new ImagePaintDevice();
//				new Diagram(conf.getDataObject(), th, paintDevice).generate();
//				paintDevice.writeToStream(out);
//			} else {
//				Exporter paintDevice = Exporter.getExporter(type, "Portrait", "A4", out);
//				new Diagram(conf.getDataObject(), th, paintDevice).generate();
//				paintDevice.export();
//			}
//			out.flush();
//		} catch (Throwable e) {
//			throw new RuntimeException(sequenceDescription, e);
//		} finally {
//			out.close();
//			in.close();
//		}
//	}

	/**
	 * 根据事件跟踪对列生成sequence描述信息
	 * 
	 * @return
	 */
	public static String getSequenceDescription(List<MethodTracerEvent> tracers) {
		if (tracers == null) {
			return null;
		}
		StringBuffer prefix = new StringBuffer();
		StringBuffer surfix = new StringBuffer();
		Set<String> beans = new HashSet<String>();
		surfix.append("[c]\n");
		for (MethodTracerEvent tracer : tracers) {
			String source = tracer.getSourceBeanName();
			String target = tracer.getTargetBeanName();
			if (beans.contains(source) == false) {
				prefix.append(String.format("%s: \"%s\"\n", filterUnsupport(source), source));
				beans.add(source);
			}
			if (beans.contains(target) == false) {
				prefix.append(String.format("%s: \"%s\"\n", filterUnsupport(target), target));
				beans.add(target);
			}
			String method = tracer.getMethodName();
			surfix.append(String.format("%s:%s.%s\n", filterUnsupport(source), filterUnsupport(target), method));
		}
		surfix.append("[/c]");

		if (beans.size() < 2) {
			return null;
		} else {
			return prefix.toString() + "\n" + surfix.toString();
		}
	}

	/**
	 * 过滤掉sequence chart不支持的字符
	 * 
	 * @param in
	 * @return
	 */
	private static String filterUnsupport(String in) {
		return in.replaceAll("\\W", "_");
	}
}
