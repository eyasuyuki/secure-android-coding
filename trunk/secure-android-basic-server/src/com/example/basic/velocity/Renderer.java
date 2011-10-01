/*
 * 下記を参考にしました:
 * 中田 秀基「すっきりわかるGoogle App Engine for Javaクラウドプログラミング」
 * ISBN978-4-7973-5760-8
 * p.446-447
 */

package com.example.basic.velocity;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.log.JdkLogChute;

public class Renderer {
	static Logger logger = Logger.getAnonymousLogger();
	private static boolean initialized = false;
	
	private static void initializeVelocity() throws Exception {
		Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, new JdkLogChute());
		Velocity.init();
		initialized = true;
	}
	
	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
	
	public static void render(String filename, Context context, Writer writer)
		throws IOException {
		try {
			synchronized (logger) {
				if (!initialized) {
					initializeVelocity();
				}
			}
			context.put("_datetimeFormat", dateTimeFormat);
			Template template = Velocity.getTemplate(filename);
			template.merge(context, writer);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
