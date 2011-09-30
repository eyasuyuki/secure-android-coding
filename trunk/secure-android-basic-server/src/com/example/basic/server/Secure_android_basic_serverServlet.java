package com.example.basic.server;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import com.example.basic.velocity.Renderer;

@SuppressWarnings("serial")
public class Secure_android_basic_serverServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		Context context = new VelocityContext();
		context.put("user", req.getAttribute(BasicAuthFilter.BASIC_AUTH_USERNAME));
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		
		Renderer.render("WEB-INF/showrequest.vm", context, resp.getWriter());
	}
}
