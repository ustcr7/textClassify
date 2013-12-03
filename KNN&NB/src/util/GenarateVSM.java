package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GenarateVSM {
	static Map<String , Integer> df;
	private static BufferedReader reader;
	private static int N=3105;
	
	private static GenarateVSM generator;
	private GenarateVSM(){
		loadDF();
	}
	public static GenarateVSM getInstance(){
		if(generator==null) generator = new GenarateVSM();
		return generator;
	}
	
	public static void main(String[] args) throws IOException {
		GenarateVSM generator = GenarateVSM.getInstance();
		File [] files = new File("./data/").listFiles();
		for(int i=0;i<files.length;i++){
			generator.getVSM(files[i]);
		}
		
	}

	private static void loadDF(){
		try {
			df = new HashMap<String,Integer>();
			reader = new BufferedReader(new FileReader(new File("./data/DF")));
			String line = "";
			while((line = reader.readLine())!=null){
				String [] pair = line.split(":"); 
				df.put(pair[0], Integer.parseInt(pair[1]));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Double> getVSM(String filename) throws IOException{
		return getVSM(new File(filename)) ;
	}
	public Map<String,Double> getVSM(File f) throws IOException{
			reader = new BufferedReader(new FileReader(f));
			String [] words = reader.readLine().split(" ");
			Map<String,Double> weight = new HashMap<String , Double> ();
			
			//计算每个单词在该文件中出现次数
			for(int j=0;j<words.length;j++){
				String word = words[j];
				if(weight.containsKey(word)){
					weight.put(word, weight.get(word)+1);
				}else{
					weight.put(word, 1.0);
				}
			}
			
			//计算每个单词的TF*IDF
			//小疑问：遍历map同时修改map，不会产生错误？？？
			int nTotal = words.length;
			double totalW = 0.0;
			for(Entry<String,Double> entry : weight.entrySet()){
				if(!df.containsKey(entry.getKey())) continue;
				int dnum = df.get(entry.getKey());
				double tfIdf = (entry.getValue()/nTotal)*(Math.log10(N/(double)dnum+0.01));
				totalW+=Math.pow(tfIdf,2);
				weight.put(entry.getKey(), tfIdf);
			}
			
			//权重归一化，输出
			totalW = Math.sqrt(totalW);
			StringBuffer bf = new StringBuffer();
			for(Entry<String,Double> entry : weight.entrySet()){
				String word = entry.getKey();
				double wgh = entry.getValue()/totalW; 
				weight.put(word, wgh);
				bf.append(word+":"+wgh+"\n");
			}
		return weight;
	}
	
}
