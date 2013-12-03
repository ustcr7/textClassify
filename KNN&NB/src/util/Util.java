package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Util {
	private static long start = 0;
	private static BufferedReader reader;
	public static String [] readWordsFromFile(File file){
		String [] words = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine())!=null){
				if(words==null) words = line.split(" ");
				else{
					String [] tmp = line.split(" ");
					String combine [] = new String[words.length+tmp.length];
					System.arraycopy(words, 0, combine, 0, words.length);
					System.arraycopy(tmp, 0, combine, words.length, tmp.length);
					words = combine;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}
	public static void mark(){
		start  = System.currentTimeMillis();
	}
	public static void timeUsed(){
		System.out.println((System.currentTimeMillis()-start)/1000);
	}
	public static void timeUsed(String method){
		System.out.println("方法"+method+"共用时："+(System.currentTimeMillis()-start)/(double)1000);
	}
}
