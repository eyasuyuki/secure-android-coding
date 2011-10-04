/*
 * 下記を参考にしました:
 * 
 * 中田 秀基「すっきりわかるGoogle App Engine for Javaクラウドプログラミング」
 * ISBN978-4-7973-5760-8
 * p.146-149
 * 
 * 徳丸 浩「体系的に学ぶ 安全なWebアプリケーションの作り方」
 * ISBN978-4-7973-6119-3
 * p.327-328
 */

package com.example.basic.server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;

public class BasicAuthFilter implements Filter {
	private Logger logger = Logger.getLogger(BasicAuthFilter.class.getName());
	
	private static final byte[] FIXED_SALT = "aec0dbc8e6348d590d8f8cea55b573b4602a4150".getBytes();
	private static final int STRETCH_COUNT = 1;
	
	public static final String BASIC_AUTH_USERNAME = "BASIC_AUTH_USERNAME";
	
	Map<String, String> userMap = new HashMap<String, String>();

	static byte[] getSalt(String str) {
		byte[] src = str.getBytes();
		return ArrayUtils.addAll(src, FIXED_SALT);
	}
	
	static byte[] getPasswordHash(String user, String password) {
		byte[] salt = getSalt(user);
		byte[] hash = new byte[0];
		for (int i=0; i<STRETCH_COUNT; i++) {
			hash = ArrayUtils.addAll(hash, password.getBytes());
			hash = ArrayUtils.addAll(hash, salt);
			hash = DigestUtils.sha256(hash);
		}
		return hash;
	}
	
	private boolean tryAuth(String authHeader, HttpServletRequest req) {
		if (authHeader == null) return false;
		String[] pair = authHeader.split(" ");
		if (pair.length != 2) {
			return false;
		}
		String decoded = new String(Base64.decodeBase64(pair[1].getBytes()));
		String[] userPass = decoded.split(":");
		byte[] pass = null;
		try {
			String ps = userMap.get(userPass[0]);
			logger.severe("tryAuth: ps="+ps);
			if (ps != null) {
				pass = Hex.decodeHex(ps.toCharArray());
			}
		} catch (Exception e) {
 			e.printStackTrace();
		}
		byte[] hash = getPasswordHash(userPass[0], userPass[1]);
		if (userPass.length != 2 || !userMap.containsKey(userPass[0])
			|| !ArrayUtils.isEquals(pass, hash)) {
			return false;
		}
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
				logger.severe("init: user="+user+", pass="+pass);
				userMap.put(user, pass);
			}
		} catch (FileNotFoundException e) {
			logger.severe(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}
	}
}
