package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class computeDF {

	/**
	 * 计算DF时要不要把测试集包含进来？
	 * 如果不包含，预测时出现了DF中没有的词怎么办
	 */
	
	private static BufferedReader reader;
	private static BufferedWriter writer;
	private static Random rnd = new Random();
	public static void main(String[] args) throws Exception{
		File [] files = new File("./data/train/").listFiles();
		System.out.println("文档总数： "+files.length);
		String [] words;
		Map<String,Integer> df = new TreeMap<String,Integer>();
		for(int i=0;i<files.length;i++){
			if(Double.compare(rnd.nextDouble(), 0.9)>0) System.out.println(i+"/"+files.length);  //报告进度
			File f = files[i];
			reader = new BufferedReader(new FileReader(f));
			words = reader.readLine().split(" ");
			Set<String> exists = new HashSet<String>();	//计算DF时，每个词在每个文档中最多算一次
			for(int j=0;j<words.length;j++){
				String word = words[j];
				if(exists.contains(word)) continue;	//已经算过了
				else exists.add(word);
				if(df.containsKey(word)){
					df.put(word,df.get(word)+1);
				}else{
					df.put(word, 1);
				}
			}
		}
		writer = new BufferedWriter(new FileWriter("./data/DF"));
		for(Entry<String, Integer> entry : df.entrySet()){
			writer.write(entry.getKey()+":"+entry.getValue()+"\n");
			writer.flush();	//不flush会出错的
		}

	}

}
