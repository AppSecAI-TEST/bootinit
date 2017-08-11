package com.example.bootinit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.R.attr.data;

public class MainActivity extends Activity {

	SharedPreferences preferences;
	PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initsystem_dialog);

//		preferences = getSharedPreferences("fla", Context.MODE_PRIVATE);
		pm = getPackageManager();

//		boolean isInstalled = preferences.getBoolean("ins", false);

		IntentFilter filter = new IntentFilter();
		filter.addAction("installed_over");
		registerReceiver(installReceiver, filter);




		Intent intent = getIntent();
		String pkgName = intent.getStringExtra("package");
		if (pkgName == null) {
			InstallUtil installUtil = new InstallUtil(MainActivity.this);
			installUtil.beginInstall();
		} else {
			File apk = getApkFile(pkgName);
			if(apk != null) {
				InstallUtil installUtil = new InstallUtil(MainActivity.this);
				installUtil.silenceInstall(apk);
			}
		}

//		PackageManager pm = getPackageManager();
//		ComponentName name = new ComponentName(this, MainActivity.class);
//		pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED ,PackageManager.DONT_KILL_APP );

	}


	public File getApkFile(String pkgName) {
		File folder = new File("/system/preinstall");
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(),
						PackageManager.GET_ACTIVITIES);
				info.applicationInfo.publicSourceDir = file.getAbsolutePath();
				String packageName = info.packageName;
				if(pkgName.equals(packageName)) {
					return file;
				}
			}
		}
		return null;
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(installReceiver);
	}


	BroadcastReceiver installReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			PackageManager pm = getPackageManager();
		ComponentName name = new ComponentName(MainActivity.this, MainActivity.class);
		pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED ,PackageManager.DONT_KILL_APP );
			MainActivity.this.finish();
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return true;
	}

}
