package com.example.basic.server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

public class BasicAuthFilter implements Filter {
	private Logger logger = Logger.getLogger(BasicAuthFilter.class.getName());
	
	public static final String BASIC_AUTH_USERNAME = "BASIC_AUTH_USERNAME";
	
	Map<String, String> userMap = new HashMap<String, String>();
	
	private boolean tryAuth(String authHeader, HttpServletRequest req) {
		if (authHeader == null) return false;
		String[] pair = authHeader.split(" ");
		if (pair.length != 2) {
			logger.severe("faild to parse authHeader" + authHeader);
			return false;
		}
		if (!pair[0].equals("Basic")) {
			logger.severe("unsupported login scheme: "+pair[0]);
		}
		logger.severe("pair[1]="+pair[1]);
		String decoded= new String(Base64.decodeBase64(pair[1].getBytes()));
		String[] userPass = decoded.split(":");
		logger.severe("userPass[0]="+userPass[0]+", userPass[1]="+userPass[1]);
		Set<String> keys = userMap.keySet();
		for (String k: keys) {
			logger.severe("key="+k+", value="+userMap.get(k));
		}
		if (userPass.length != 2 || !userMap.containsKey(userPass[0])
				|| !userMap.get(userPass[0]).equals(userPass[1])) {
			logger.severe("AuthFailed: " + decoded);
			return false;
		}
		logger.info("authentication success for user '" + userPass[0] + "'");
		req.setAttribute(BASIC_AUTH_USERNAME, userPass[0]);
		return true;
	}
	
	private void send401(ServletResponse response, String realm, String message)
		throws IOException {
		HttpServletResponse res = (HttpServletResponse)response;
		res.setStatus(401);
		res.setHeader("WWW-Authenticate", "Basic realm=" + realm);
		res.getWriter().println("<body><h1>"+message+"</h1></body>\n");
		return;
	}
	
	String REALM = "Basic";
	String passwdFile = "WEB-INF/passwd.prop";
	

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest req = (HttpServletRequest)request;
			String authHeader = req.getHeader("Authorization");
			if (!tryAuth(authHeader, req)) {
				send401(response, REALM, "Authentication Reqired for " + REALM);
			} else {
				chain.doFilter(request, response);
			}
		} catch (ServletException e) {
			logger.severe(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String tmp = filterConfig.getInitParameter("realm");
		if (tmp != null) {
			REALM = tmp;
		}
		logger.info("realm = " + tmp);
		
		tmp = filterConfig.getInitParameter("passwdFile");
		if (tmp != null) {
			passwdFile = tmp;
		}
		
		Properties passProp = new Properties();
		
		try {
			passProp.load(new FileReader(passwdFile));
			for (Object o: passProp.keySet()) {
				String user = (String)o;
				String pass = passProp.getProperty(user).trim();
				userMap.put(user, pass);
			}
		} catch (FileNotFoundException e) {
			logger.severe(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}
	}
}
