package com.example.bootinit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class InstallUtil {
	private Context context;
	int count = 0;
	private File[] files; // Ҫ��װ���ļ��б�
	SharedPreferences preferences;
	Editor editor;
	
	public InstallUtil(Context context) {
		this.context = context;
		preferences = context.getSharedPreferences("fla", Context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Log.i("InstallUtil", "Install OVER+++++++++++++++++++");
				editor.putBoolean("ins", true);
				editor.commit();
				sendInstallBroadCast();
				break;

			default:
				break;
			}
		}
	};
	
	
//	
//	public void silenceInstall1() {
//		Uri uri;
//		PackageInfo info;
//		PackageManager pm = context.getPackageManager();
//		files = new File("/system/preinstall1").listFiles();
//
//		if (files != null && files.length != 0) {
//			for (int i = 0; i < files.length; i++) {
//				uri = Uri.fromFile(files[i]);
//				int installFlags = 0;
//				try {
//					info = pm.getPackageInfo(getPackageName(files[i]),
//							PackageManager.GET_UNINSTALLED_PACKAGES);
//					if (info != null) {
//						installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
//					}
//				} catch (NameNotFoundException e) {
//					e.printStackTrace();
//				}
//				PackageInstallObserver observer = new PackageInstallObserver();
//				pm.installPackage(uri, observer, installFlags,
//						getPackageName(files[i]));
//			}
//		} else {
//			sendInstallBroadCast();
//		}
//	}
//	
	public void silenceInstall() {
		new Thread() {
			public void run() {
				File zip = new File("/system/usr/xbmc.zip");
				if (zip.exists()){
					unzip(new File("/system/usr/xbmc.zip"),new File("/sdcard/Android/data/"));
				}
				File folder = new File("/system/preinstall");
				if (folder.exists()) {
					File[] files = folder.listFiles();
					if (files != null && files.length != 0) {
						for (int i = 0; i < files.length; i++) {
							PackageUtils.installSlient(
									context, files[i].getPath());
							Log.i("install", "index = " + i + " / "
									+ files[i].getName());
						}

					}
				}
				handler.sendEmptyMessage(1);
			}
		}.start();
	}
	
	public void silenceInstall(final File file) {
		new Thread() {
			@Override
			public void run() {
				PackageUtils.installSlient(context, file.getPath());
				handler.sendEmptyMessage(1);
			}
		}.start();
	}
	private long unzip(File mInput,File mOutput){
		long extractedSize = 0L;
		Enumeration<ZipEntry> entries;
		ZipFile zip = null;
		try {
			zip = new ZipFile(mInput);
			long uncompressedSize = getOriginalSize(zip);


			entries = (Enumeration<ZipEntry>) zip.entries();
			while(entries.hasMoreElements()){
				ZipEntry entry = entries.nextElement();
				if(entry.isDirectory()){
					continue;
				}
				File destination = new File(mOutput, entry.getName());
				if(!destination.getParentFile().exists()){

					destination.getParentFile().mkdirs();
				}

				ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
				extractedSize+=copy(zip.getInputStream(entry),outStream);
				outStream.close();
			}
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				zip.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return extractedSize;
	}
	private long getOriginalSize(ZipFile file){
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
		long originalSize = 0l;
		while(entries.hasMoreElements()){
			ZipEntry entry = entries.nextElement();
			if(entry.getSize()>=0){
				originalSize+=entry.getSize();
			}
		}
		return originalSize;
	}
	private int copy(InputStream input, OutputStream output){
		byte[] buffer = new byte[1024*8];
		BufferedInputStream in = new BufferedInputStream(input, 1024*8);
		BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
		int count =0,n=0;
		try {
			while((n=in.read(buffer, 0, 1024*8))!=-1){
				out.write(buffer, 0, n);
				count+=n;
			}
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}

	private final class ProgressReportingOutputStream extends FileOutputStream{

		public ProgressReportingOutputStream(File file)
				throws FileNotFoundException {
			super(file);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount)
				throws IOException {
			// TODO Auto-generated method stub
			super.write(buffer, byteOffset, byteCount);
		}

	}


//	class PackageInstallObserver extends IPackageInstallObserver.Stub {
//		@Override
//		public void packageInstalled(String arg0, int arg1)
//				throws RemoteException {
//			count++;
//			Log.i("InstallUtil", "installed count = " + count + "/" + arg0);
//			if (count == files.length) {
//				Log.i("InstallUtil", "Install OVER+++++++++++++++++++");
//				editor.putBoolean("ins", true);
//				editor.commit();
//				sendInstallBroadCast();
//			}
//		}
//	}
	

	

	public void sendInstallBroadCast() {
		Intent intent = new Intent();
		intent.setAction("installed_over");
		context.sendBroadcast(intent);

	}

	public String getPackageName(File file) {
		PackageInfo info = context.getPackageManager().getPackageArchiveInfo(
				file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
		String packageName = info.packageName;
		return packageName;
	}
	
	
	public void beginInstall() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				silenceInstall();
			}
		}, 3000);
	}
	
}
