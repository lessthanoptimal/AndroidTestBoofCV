
package boofcv.benchmark.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

public class KillBenchmarkThread extends Thread {
	
	Handler handler = new Handler();
	ProgressDialog  dialog;
	
	public KillBenchmarkThread(Context context) {
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("Killing Benchmark");
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);
		dialog.show();
	}
	
	@Override
	public synchronized void run() {
		// kill the thread by interrupting it
		while (CentralMemory.thread.isAlive()) {
			CentralMemory.thread.interrupt();
			try {
				wait(50);
			} catch (InterruptedException e) {
			}
		}

		CentralMemory.isRunning = false;
		CentralMemory.reset();
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					dialog.dismiss();	
				} catch( Exception e ) {
					// if the user tries to kill the benchmark then rotates the screen that causes
					// a new view to be created and creates problems here.  Based on a search online
					// the best fix seems to be to ignore the exception
				}
			}
		});
	}

}
