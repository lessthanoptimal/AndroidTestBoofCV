package boofcv.benchmark.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;

/**
 * For reading and writing to results log file
 
 * @author Peter Abeles
 *
 */
public class BenchmarkResultsCodec {
	
	Activity activity;
	
	public BenchmarkResultsCodec( Activity activity ) {
		this.activity = activity;
	}
	
	public String resultsToString( Collection<BenchmarkResults> results ) {
		StringBuffer buffer = new StringBuffer();
		
		for( BenchmarkResults r : results ) {
			buffer.append(String.format("%s %d\n",r.name,r.results.size()));
			
			for( BenchmarkResults.Pair p : r.results ) {
				buffer.append(String.format("%f %s\n",p.result,p.testName));
			}
		}
		
		return buffer.toString();
	}
	
	public boolean copyToExternal( String fileName ) {
		try {
			FileInputStream in = activity.openFileInput(fileName);
			File file = new File(activity.getExternalFilesDir(""), fileName);
			
			if( file.exists() )
				file.delete();
			
			FileOutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while( (bytesRead = in.read(buffer)) > 0){
				out.write(buffer,0,bytesRead);
			}
			out.close();
			in.close();

			// Hack so that it will be visible to users when mounted on the computer
			MediaScannerConnection.scanFile(activity, new String[] {file.getAbsolutePath()}, null, null);
			
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
	}
	
	public void write( String fileName , List<BenchmarkResults> results ){
		try {

			FileOutputStream fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
			PrintStream out = new PrintStream(fos);
			
			out.print(resultsToString(results));
			
			out.flush();
			out.close();
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Map<String,BenchmarkResults> read( String fileName , boolean rawResource ) {
		try {
			BufferedReader fis;
			if( rawResource ) {
				fis = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(R.raw.galaxys3)));
			} else {
				fis = new BufferedReader(new InputStreamReader(activity.openFileInput(fileName)));
			}
			
			Map<String,BenchmarkResults> map = new HashMap<String,BenchmarkResults>();
			
			while( true ) {
				String s = fis.readLine();
				if( s == null )
					break;
				String[] line = splitLine(s);
				
				BenchmarkResults r = new BenchmarkResults();
				r.name = line[0];
				
				int N = Integer.parseInt(line[1]);
				
				for( int i = 0; i < N; i++ ) {
					line = splitLine(fis.readLine());
					double value = Double.parseDouble(line[0]);
					
					r.results.add( new BenchmarkResults.Pair(line[1], value));
				}
				
				map.put(r.name,r);
			}

			fis.close();
			
			return map;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String[] splitLine( String text ) {
		String[] ret = new String[2];
		
		int w = 0;
		for( ; w < text.length(); w++ ) {
			if( text.charAt(w) == ' ') {
				break;
			}
		}
		
		ret[0] = text.substring(0,w);
		ret[1] = text.substring(w+1);
		
		return ret;
	}

}
