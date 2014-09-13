package org.jtester.module.tracer.spring;

import java.util.ArrayList;
import java.util.List;

import org.jtester.module.tracer.TracerBeanManager;
import org.jtester.utility.StringHelper;

@SuppressWarnings("rawtypes")
public class MethodTracerEvent {
	private Class sourceClazz;

	private Class targetClazz;

	private String methodName;

	private List<String> paras;

	public MethodTracerEvent(Class sourceClazz, Class targetClazz, String methodName) {
		this.sourceClazz = sourceClazz;
		this.targetClazz = targetClazz;
		this.methodName = methodName;
		this.paras = new ArrayList<String>();

	}

	public String getMethodName() {
		return methodName;
	}

	public Class getSourceClazz() {
		return sourceClazz;
	}

	public Class getTargetClazz() {
		return targetClazz;
	}

	public String getSourceBeanName() {
		String beanName = TracerBeanManager.getBeanName(sourceClazz);
		return StringHelper.isBlankOrNull(beanName) ? "test-class" : beanName;
	}

	public String getTargetBeanName() {
		return TracerBeanManager.getBeanName(targetClazz);
	}

	public void addPara(Object para) {
		String info = BeanTracerManager.toTracerString(para);
		this.paras.add(info);
	}

	/**
	 * 清除一些不必要的信息，避免java heap dump错误
	 */
	public void clean() {
		this.paras.clear();
		this.paras = null;
	}

	public String toHtmlString() {
		StringBuffer html = new StringBuffer();
		html.append(htmlHeader(this.sourceClazz, this.targetClazz, this.methodName));

		html.append("<tr><td>paras</td>");
		html.append("<td>");
		boolean firstPara = true;
		for (String para : this.paras) {
			if (firstPara) {
				firstPara = false;
			} else {
				html.append("<br/>");
			}
			html.append(para);
		}
		html.append("&nbsp;</td></tr>");
		return html.toString();
	}

	public static String getResultHtml(final Class source, final Class target, final String method, String result) {
		StringBuffer html = new StringBuffer();
		html.append(htmlHeader(source, target, method));
		html.append("<tr><td>result</td>");
		html.append("<td>");
		html.append(result);
		html.append("&nbsp;</td></tr>");
		return html.toString();
	}

	private static String htmlHeader(final Class source, final Class target, final String method) {
		StringBuffer html = new StringBuffer();
		html.append("<tr><td colspan=2>");
		if (source == null) {
			html.append("<b>unkown source</b>");
		} else {
			html.append(String.format("<b title='%s'>%s</b>", source.getName(),
					TracerBeanManager.getSourceBeanName(source)));
		}
		html.append(" call ");
		if (target == null) {
			html.append("<b>unkown target</b>");
		} else {
			html.append(String.format("<b title='%s'>%s</b>", target.getName(),
					TracerBeanManager.getTargetBeanName(target)));
		}
		html.append(String.format("#%s", method));
		html.append("</td></tr>");
		return html.toString();
	}
}
