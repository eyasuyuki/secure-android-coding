package com.example.package2json;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

public class AsyncPackages extends AsyncTask<Void, Void, Void> {
	private static final String TAG = AsyncPackages.class.getName();
	private static final String JSON_FILE_NAME = "/apps.json";
	//private static final String JSON_FILE_NAME = "/sdcard/apps.json";

	Context context = null;
	TextView view = null;
	ProgressDialog dialog = null;
	String json = null;

	public AsyncPackages(Context context, TextView view) {
		this.context = context;
		this.view = view;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setTitle(R.string.progress_title);
		try {
			dialog.show();
		} catch (Exception e) {
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		PackageManager pm = context.getPackageManager();
		int flags = PackageManager.GET_ACTIVITIES
				| PackageManager.GET_CONFIGURATIONS
				| PackageManager.GET_DISABLED_COMPONENTS
				| PackageManager.GET_GIDS | PackageManager.GET_INSTRUMENTATION
				| PackageManager.GET_INTENT_FILTERS
				| PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS
				| PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS
				| PackageManager.GET_RESOLVED_FILTER
				| PackageManager.GET_SERVICES
				| PackageManager.GET_SHARED_LIBRARY_FILES
				| PackageManager.GET_SIGNATURES
				| PackageManager.GET_UNINSTALLED_PACKAGES
				| PackageManager.GET_URI_PERMISSION_PATTERNS;
		// List<ApplicationInfo> alist = pm.getInstalledApplications(flags);
		List<PackageInfo> plist = pm.getInstalledPackages(flags);
		List<JSONObject> pjsons = new ArrayList<JSONObject>();
		for (PackageInfo p : plist) {
			pjsons.add(packageInfo2JSON(p));
		}
		try {
			JSONArray jarray = new JSONArray(pjsons);
			if (jarray != null) {
				json = jarray.toString(4);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		view.setText(json);
		saveJSON(json,
				context.getExternalFilesDir(null).getAbsolutePath()+JSON_FILE_NAME);
		try {
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	JSONObject packageInfo2JSON(PackageInfo p) {
		Map<String, Object> kvp = new HashMap<String, Object>();
		String[] perms = p.requestedPermissions;
		if (perms != null && perms.length > 0) {
			List<String> px = new ArrayList<String>();
			for (String x : perms)
				px.add(x);
			JSONArray parray = new JSONArray(px);
			kvp.put("permission", parray);
		}
		Signature[] signatures = p.signatures;
		if (signatures != null && signatures.length > 0) {
			List<String> clist = new ArrayList<String>();
			for (Signature s : signatures) {
				X509Certificate c = loadCertificate(s);
				String base64 = Base64.encodeToString(c.getSignature(), Base64.DEFAULT);
				clist.add(base64);
			}
			if (clist.size() == 1) {
				kvp.put("publicKey", clist.get(0));
			} else {
				kvp.put("publicKey", new JSONArray(clist));
			}
		}
		ActivityInfo[] activities = p.activities;
		if (activities != null && activities.length > 0) {
			List<JSONObject> alist = new ArrayList<JSONObject>();
			for (ActivityInfo a: activities) {
				Map<String, Object>amap = new HashMap<String, Object>();
				amap.put("configChanges", a.configChanges);
				amap.put("descriptionRes", a.descriptionRes);
				amap.put("enabled", a.enabled);
				amap.put("exported", a.exported);
				amap.put("flags", a.flags);
				amap.put("icon", a.icon);
				amap.put("labelRes", a.labelRes);
				amap.put("launchMode", a.launchMode);
				amap.put("logo", a.logo);
				amap.put("name", a.name);
				amap.put("nonLocalizedLabel", a.nonLocalizedLabel);
				amap.put("packageName", a.packageName);
				amap.put("screenOrientation", a.screenOrientation);
				amap.put("softInputMode", a.softInputMode);
				amap.put("targetActivity", a.targetActivity);
				amap.put("taskAffinity", a.taskAffinity);
				amap.put("theme", a.theme);
				alist.add(new JSONObject(amap));
			}
			kvp.put("activities", new JSONArray(alist));
		}
		ServiceInfo[] services = p.services;
		if (services != null && services.length > 0) {
			List<JSONObject> slist = new ArrayList<JSONObject>();
			for (ServiceInfo s: services) {
				Map<String, Object>smap = new HashMap<String, Object>();
				smap.put("descriptionRes", s.descriptionRes);
				smap.put("enabled", s.enabled);
				smap.put("exported", s.exported);
				smap.put("icon", s.icon);
				smap.put("labelRes", s.labelRes);
				smap.put("logo", s.logo);
				smap.put("name", s.name);
				smap.put("nonLocalizedLabel", s.nonLocalizedLabel);
				smap.put("packageName", s.packageName);
				smap.put("permission", s.permission);
				smap.put("processName", s.processName);
				slist.add(new JSONObject(smap));
			}
			kvp.put("services", new JSONArray(slist));
		}
		ApplicationInfo a = p.applicationInfo;
		kvp.put("dataDir", a.dataDir);
		kvp.put("manageSpaceActivityName", a.manageSpaceActivityName);
		kvp.put("name", a.name);
		kvp.put("packageName", a.packageName);
		kvp.put("processName", a.processName);
		kvp.put("publicSourceDir", a.publicSourceDir);
		kvp.put("sourceDir", a.sourceDir);
		kvp.put("taskAffinity", a.taskAffinity);
		kvp.put("descriptionRes", String.valueOf(a.descriptionRes));
		kvp.put("enabled", String.valueOf(a.enabled));
		if (a.nonLocalizedLabel != null)
			kvp.put("nonLocalizedLabel", a.nonLocalizedLabel.toString());
		String[] f = a.sharedLibraryFiles;
		if (f != null && f.length > 0) {
			StringBuffer b = new StringBuffer();
			b.append(f[0]);
			for (int j = 1; j < f.length; j++) {
				b.append(",");
				b.append(f[j]);
			}
			kvp.put("sharedLibraryFiles", b.toString());
		}
		kvp.put("targetSdkVersion", String.valueOf(a.targetSdkVersion));
		kvp.put("uid", String.valueOf(a.uid));
		return new JSONObject(kvp);
	}

	/*
	 * 下記を参考にした
	 * Get Certificate from Android Application
	 * http://thomascannon.net/misc/android_apk_certificate/
	 */
	X509Certificate loadCertificate(Signature signature) {
		byte[] cert = signature.toByteArray();
		InputStream input = new ByteArrayInputStream(cert);

		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X509");
		} catch (Exception e) {
			e.printStackTrace();
		}
		X509Certificate c = null;
		try {
			c = (X509Certificate) cf.generateCertificate(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return c;
	}
	
	void saveJSON(String json, String fileName) {
		Log.d(TAG, "save2JSON: fileName="+fileName);
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName);
			writer.write(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
