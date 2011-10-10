package com.example.package2json;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.widget.TextView;

public class AsyncPackages extends AsyncTask<Void, Void, Void> {
	private static final String JSON_FILE_NAME = "/apps.json";
	
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
	    try { dialog.show();} catch (Exception e) {}
	}

	@Override
	protected Void doInBackground(Void... params) {
        PackageManager pm = context.getPackageManager();
        int flags = PackageManager.GET_ACTIVITIES
        	| PackageManager.GET_CONFIGURATIONS
        	| PackageManager.GET_DISABLED_COMPONENTS
        	| PackageManager.GET_GIDS
        	| PackageManager.GET_INSTRUMENTATION
        	| PackageManager.GET_INTENT_FILTERS
        	| PackageManager.GET_META_DATA
        	| PackageManager.GET_PERMISSIONS
        	| PackageManager.GET_PROVIDERS
        	| PackageManager.GET_RECEIVERS
        	| PackageManager.GET_RESOLVED_FILTER
        	| PackageManager.GET_SERVICES
        	| PackageManager.GET_SHARED_LIBRARY_FILES
        	| PackageManager.GET_SIGNATURES
        	| PackageManager.GET_UNINSTALLED_PACKAGES
        	| PackageManager.GET_URI_PERMISSION_PATTERNS;
        //List<ApplicationInfo> alist = pm.getInstalledApplications(flags);
        List<PackageInfo> plist = pm.getInstalledPackages(flags);
        List<JSONObject> pjsons = new ArrayList<JSONObject>();
        for (PackageInfo p: plist) {
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
	    try { dialog.dismiss();} catch (Exception e) {}
	}
	
	JSONObject packageInfo2JSON(PackageInfo p) {
        Map<String, Object> kvp = new HashMap<String, Object>();
        String[] perms = p.requestedPermissions;
        if (perms != null && perms.length > 0) {
                List<String> px = new ArrayList<String>();
                for (String x: perms) px.add(x);
                JSONArray parray = new JSONArray(px);
                kvp.put("permission", parray);
        }
        Signature[] signatures = p.signatures;
        if (signatures != null && signatures.length > 0) {
        	List<String> clist = new ArrayList<String>();
        	for (Signature s: signatures) {
        		clist.add(loadCertificate(s));
        	}
        	if (clist.size() == 1) {
        		kvp.put("publicKey", clist.get(0));
        	} else {
        		kvp.put("publicKey", new JSONArray(clist));
        	}
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

	String loadCertificate(Signature signature) {
		byte[] cert = signature.toByteArray();
		  InputStream input = new ByteArrayInputStream(cert);

          CertificateFactory cf = null;
          try {
                  cf = CertificateFactory.getInstance("X509");
          } catch (CertificateException e) {
                  // TODO some error checking
                  e.printStackTrace();
          }
          X509Certificate c = null;
          try {
                  c = (X509Certificate) cf.generateCertificate(input);
          } catch (CertificateException e) {
                  // TODO some error checking
                  e.printStackTrace();
          }
		
		return Hex.encodeHexString(c.getSignature());
	}
	
	void saveJSON(String json, String fileName) {
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
