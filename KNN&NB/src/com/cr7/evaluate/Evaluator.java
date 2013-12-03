package com.cr7.evaluate;

import java.io.File;

import util.Category;

import com.cr7.model.Model;

public class Evaluator {
	static int [][]  matrix; 
	/*
	 * @param     files 待预测的文本
     * @param	  m	用来预测的模型
     * @param 	  progress  是否打印进度
     * @exception IndexOutOfBoundsException if the <code>index</code>
	 */
	public static void evaluate(File [] files,Model m,boolean progress){
		int cNum = Category.size();
		matrix = new int[cNum][cNum];
		for(int i=0;i<files.length;i++){
			if(progress) System.out.println(i+"/"+files.length);
			String categoryT = files[i].getName().substring(0,2);
			String categoryP = m.predict(files[i]);
			matrix[Category.index(categoryT)][Category.index(categoryP)]++;
		}
		toMatrixString();
		getPrecision();
		
	}
	
	public static void getPrecision(){
		int cNum = Category.size();
		double [] rNum = new double[cNum];	//每个类别正确分类个数
		double [] fNum = new double[cNum];	//每个类别错误分类个数
		double totalRight = 0;
		double totalWrong = 0;
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix[0].length;j++){
				if(j==i) {rNum[i]+=matrix[i][j];totalRight+=matrix[i][j];}
				else {fNum[i]+=matrix[i][j];totalWrong+=matrix[i][j];}
			}
		}
		System.out.println("总体准确率："+((double)totalRight/(totalRight+totalWrong)));
		for(int i=0;i<matrix.length;i++){
			System.out.println("类别"+Category.getCategory(i)+"准确率："+((double)rNum[i]/(rNum[i]+fNum[i])));
		}
	}
	
	public static void toMatrixString(){
		//打印标题行
		System.out.print("类别：\t");
		for(int i=0;i<matrix.length;i++){
			System.out.print(Category.getCategory(i)+"\t");
		}
		System.out.println();
		//打印混淆矩阵
		for(int i=0;i<matrix.length;i++){
			System.out.print(Category.getCategory(i)+"\t");
			for(int j=0;j<matrix[0].length;j++){
				System.out.print(matrix[i][j]+"\t");
			}
			System.out.println("");
		}
	}
}
