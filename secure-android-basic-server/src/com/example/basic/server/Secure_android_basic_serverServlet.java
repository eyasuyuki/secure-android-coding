package com.example.basic.server;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import com.example.basic.velocity.Renderer;
import com.google.apphosting.utils.remoteapi.RemoteApiPb.Request;

@SuppressWarnings("serial")
public class Secure_android_basic_serverServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		Context context = new VelocityContext();
		context.put("names", req.getHeaderNames());
		context.put("req", req);
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		
		Renderer.render("WEB-INF/showrequest.vm", context, resp.getWriter());
	}
}
