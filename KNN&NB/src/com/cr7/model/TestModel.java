package com.cr7.model;

import java.io.File;

import com.cr7.evaluate.Evaluator;


public class TestModel {


	public static void main(String[] args) {
		File [] train = new File("./data/train/").listFiles();
		File [] test = new File("./data/test/").listFiles();
		testNB(train,test);
//		test();
	}
	static void test(){
		File [] train = new File("./data/train/").listFiles();
		Model nb = new NB();
		nb.train(train);
		System.out.println(nb.predict(new File("./data/test/财经163.txt")));
		System.out.println(nb.predict(new File("./data/test/电脑12512.txt")));
		System.out.println(nb.predict(new File("./data/test/汽车10826.txt")));
		System.out.println(nb.predict(new File("./data/test/体育1727.txt")));
	}
	static void testNB(File [] train,File [] test){
		Model nb = new NB();
		nb.train(train);
		Evaluator.evaluate(test,nb,true);
	}
	static void testKNN(File [] train,File [] test){
		Model knn = new KNN();
		Evaluator.evaluate(test,knn,true);
	}

}
