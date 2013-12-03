package com.cr7.dataFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PreProcessOfTanCorp {

	private static BufferedReader reader;
	private static BufferedWriter writer;
	private static double ratio = 0.1;	//测试集比例	

	/**
	 * 预处理谭松波数据集
	 */
	public static void main(String[] args) throws IOException {
		String category [] = {"财经","地域","电脑","房产", "教育",  "科技",  "汽车",
				"人才",  "体育", "卫生", "艺术" ,"娱乐"};
		for(int i=0;i<category.length;i++){
			try {
				System.out.println(category[i]);
				extract(category[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static void extract(String category) throws Exception {
		Random rnd = new Random();
		File folder = new File("/home/IT/dataMining/谭松波/TanCorp-12-Txt/"+category);
		File [] files = folder.listFiles();
		for(int i=0;i<files.length && i<300;i++){		//测试阶段为提高效率没类最多选300个样本
			File f = files[i];
			//1-ratio的概率作为训练集合样本；ratio的概率作为测试集合样本
			File of = null;
			if(Double.compare(rnd.nextDouble(), ratio)>0){
				of = new File("./data/train/"+category+f.getName());	
			}else{
				of = new File("./data/test/"+category+f.getName());
			}
			
			reader = new BufferedReader(new FileReader(f));
			writer = new BufferedWriter(new FileWriter(of));
			String line = "";
			while((line = reader.readLine())!=null){
				writer.write(line);
			}
			writer.flush();
		}
	}

}
