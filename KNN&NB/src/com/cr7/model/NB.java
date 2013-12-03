package com.cr7.model;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import util.Category;
import util.Util;
	/**
	 * 
	 * 1、p(wi|cj)用二维数组,double类型 表示如果有10个类别，50000个单词占用内存： 
	 * 10*50000*4=10M 也不是很多
	 * 2、如果太多了话可以考虑按照汉字顺序写入不同的文件中，然后模型中建立一个池子，需要计算p(wi|cj)时
	 * 从池子中寻找，找不到再去文件中load。当池子达到限制大小时删除最早加入的p(wi|cj)。本模型直接使用
	 * 二维数组。
	 *
	 */
public class NB implements Model{
	private static int cNum;	//类别总数
	private static int DNum;	//文档总数
	private static int [] DCNum;//每个类别文档总数
	private static int [] WCNum;//每个类别不重复单词个数
	//p(wi|cj)，每个单词wi对应一个map，map中保存了该单词在各个类别中出现的文本次数。
	//然后除以类别总文本得到条件概率。
	private static Map<String,Map<Integer,Double>> conProb;	
	private static double prior [];
	//每个单词wi出现在类别cj中文本的个数
	public NB(){
		init();
	}
	@Override
	public void init() {
		cNum = Category.size();
		DNum=0;
		DCNum = new int [cNum];
		WCNum = new int [cNum];
		conProb = new HashMap<String,Map<Integer,Double>>();
		prior = new double[cNum];
	}

	@Override
	public void train(File [] files) {
		DNum = files.length;
		for(int i=0;i<files.length;i++){
//if(i%100==0 && (i>100 || i==0)) System.out.println(i+"/"+files.length);//报告进度
			String cateStr = files[i].getName().substring(0,2);
			int categoryIndex = Category.index(cateStr);
			DCNum[categoryIndex]++;
			String [] words = Util.readWordsFromFile(files[i]);
			Set<String> oldWord = new HashSet<String>();
			for(int j=0;j<words.length;j++){
				if(!oldWord.contains(words[j])){	//每篇文章中的每个词只能处理一边
					oldWord.add(words[j]);
					//该词在conProb中还没有出现过，为他的建立一个map,保存各个类别中出现该词的次数
					if(!conProb.containsKey(words[j])){	
						Map<Integer,Double> cMap = new HashMap<Integer,Double>();
						for(int k=0;k<Category.size();k++){
							if(k==categoryIndex) {
								cMap.put(k, 1.0);
								continue;
							}
							
							cMap.put(k, 0.0);
						}
						conProb.put(words[j], cMap);
					}else{
					//该词在conProb中出现过了，为该词对应的类别加1.
						Map<Integer,Double> tmp = conProb.get(words[j]);
						tmp.put(categoryIndex, tmp.get(categoryIndex)+1);
					}
				}else{
					//已经处理过该单词了
				}
				
			}
		}
		//计算每个类别的先验概率
		for(int i=0;i<Category.size();i++){
			prior[i] = ((double)(DCNum[i]+1))/(DNum+cNum);
		}
		//计算每个类别出现不同词的个数（用于laplace平滑）
		for(Entry<String,Map<Integer,Double>> entry : conProb.entrySet()){
			Map<Integer,Double> tmp = conProb.get(entry.getKey());
			for(Entry<Integer,Double> e : tmp.entrySet()){
				if(e.getValue()>0) WCNum[e.getKey()]++;
			}
		}
		//把出现次数转换成条件概率
		for(Entry<String,Map<Integer,Double>> entry : conProb.entrySet()){
			Map<Integer,Double> tmp = conProb.get(entry.getKey());
			for(Entry<Integer,Double> e : tmp.entrySet()){
				double condition = ((double)(e.getValue()+1))/(DCNum[e.getKey()]+WCNum[e.getKey()]);
				tmp.put(e.getKey(), condition);
			}
		}
		
		//显示中间结果
//		System.out.println("total num of text"+DNum);
//		for(int i=0;i<Category.size();i++){
//			System.out.println(Category.getCategory(i)+" num :"+DCNum[i]);
//		}
//		for(Entry<String,Map<Integer,Double>> entry : conProb.entrySet()){
//			Map<Integer,Double> tmp = conProb.get(entry.getKey());
//			for(Entry<Integer,Double> e : tmp.entrySet()){
//				if(e.getValue()>20) System.out.println("word "+ entry.getKey()+" exists in " + e.getValue()+" texts of category "+Category.getCategory(e.getKey()));
//			}
//		}
//		for(Entry<String,Map<Integer,Double>> entry : conProb.entrySet()){
//			Map<Integer,Double> tmp = conProb.get(entry.getKey());
//			for(Entry<Integer,Double> e : tmp.entrySet()){
//				if(e.getValue()>0.001) System.out.println("p("+entry.getKey()+"|"+Category.getCategory(e.getKey())+")= "+e.getValue());
//			}
//		}
//		
//		for(int i=0;i<Category.size();i++){
//			System.out.println("类别"+Category.getCategory(i)+"先验概率为： "+prior[i]);
//		}
		
	}
	
	@Override
	public String predict(File file) {
		String [] words = Util.readWordsFromFile(file);
		Set<String> set = new HashSet<String>();
		for(int i=0;i<words.length;i++){
			set.add(words[i]);
		}
		BigDecimal [] post = new BigDecimal[cNum];
		for(int i=0;i<post.length;i++){
			post[i] = new BigDecimal(prior[i]);
		}
		Iterator<String> itr = set.iterator();
		while(itr.hasNext()){
			String word = itr.next();
			for(int i=0;i<post.length;i++){
				//如果单词在训练集中没有出现过,则跳过这个词
				if(!conProb.containsKey(word)) continue;
				post[i]=post[i].multiply(new BigDecimal(conProb.get(word).get(i)));
			}
		}
		BigDecimal max = new BigDecimal(0.0);
		int result = 0;
		for(int i=0;i<post.length;i++){
			if(post[i].compareTo(max)>0){
				max = post[i];
				result = i;
			}
		}
		
		return Category.getCategory(result);
	}

}

