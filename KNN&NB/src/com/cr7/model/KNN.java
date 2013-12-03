package com.cr7.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.Category;
import util.GenarateVSM;
import util.computeDF;

/**
 * 疑问：test文本中出现了训练集中没有的词怎么办？
 * 疑问：treeset怎么根据value排序
 * @author wcc
 *
 */
public class KNN implements Model{
	private int K=20;		//邻居个数
	
	private static double getSimilarity(double sigmaW,Map<String,Double> vsm1,Map<String,Double> vsm2){
		//vsm1(测试文本)的权值平方和重复计算了。。。。
		double nominator = 0;
		double sigmaW2 = 0;
		for(Entry<String,Double> entry : vsm2.entrySet()){
			String word = entry.getKey();
			double weight = entry.getValue();
			sigmaW2+=Math.pow(weight, 2);
			if(vsm1.containsKey(word)) nominator+=weight*vsm1.get(word);
		}
		if(Double.compare(nominator, 0.0)==0) return 0.0;
		return nominator/(sigmaW*Math.sqrt(sigmaW2));
	}

	//计算单词文件频率
	@Override
	public void init() {
		File f = new File("./data/DF");
		if(f.exists())  System.out.println("各单词文件频率已经存在");
		else{
			new computeDF();
			System.out.println("各单词文件频率计算完毕");
		}
	}


	@Override
	public void train(File [] files) {
		//KNN 不需要事先训练
	}


	/**
	 * TreeMap<String name,String age>要按照年龄大小选出最大的前K个人的姓名应该怎么做？？
	 * treeMap只能按key排序，但是age却能重复，不能作为key啊。
	 * 
	 * 将就的做法：遍历，每次出现一个新age时将其几下来，当第k小的age出现时，以后map中age小于该值的
	 * 直接remove,如果出现更小的age，则替换当前最小age值。（这种做法会破坏map结构，或者需要提前
	 * 备份一个map）
	 */
	@Override
	public String predict(File file) {
		String result  = "";
		try {
			double [] category= new double[Category.size()];
			Map<String,Double> KNeigh = findNeighbour(file);
			//计算每个类别得分
			for(Map.Entry<String, Double> entry :KNeigh.entrySet()){
				category[Category.index(entry.getKey().substring(0,2))]+=entry.getValue();
			}
			double max = 0.0;
			//选择得分最大的类别作为结果
			for(int i=0;i<category.length;i++){
				if(category[i]>max){
					max = category[i];
					result = Category.getCategory(i);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("predict result "+result);
		return result;
	}
	
	private Map<String,Double> findNeighbour(File testFile) throws IOException{
		GenarateVSM generator = GenarateVSM.getInstance();
		Map<String,Double> neigh = new HashMap<String,Double>();
		
		Map<String,Double> vsm = generator.getVSM(testFile);
		double sigmaW=0;
		for(double w : vsm.values()){
			sigmaW+=Math.pow(w, 2);
		}
		sigmaW=Math.sqrt(sigmaW);
		File [] files = new File("./data/train/").listFiles();
		for(int i=0;i<files.length;i++){
			File neighFile = files[i];
			double tmp = getSimilarity(sigmaW,vsm,generator.getVSM(neighFile));
			neigh.put(neighFile.getName(),tmp);
		}
		//只保留K个邻居
		List<Map.Entry<String,Double>> al = new ArrayList<Map.Entry<String,Double>>(neigh.entrySet());
		Collections.sort(al,new Comparator<Map.Entry<String,Double>>(){
			public int compare(Map.Entry<String,Double> m1,Map.Entry<String,Double>m2){
				return (m2.getValue()-m1.getValue()>0)?1:-1;
			}
		});
		Map<String,Double> KNeigh = new HashMap<String,Double>();
		for(int i=0;i<al.size() && i<K;i++){
			KNeigh.put(al.get(i).getKey(), al.get(i).getValue());
		}
		return KNeigh;
	}
	
	
	

}
