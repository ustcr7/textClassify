package util;

import java.util.HashMap;
import java.util.Map;

public  class Category {
	private static final Map<String,Integer> category;
	private static String [] name = {"财经","地域","电脑","房产","教育",
		"科技","汽车","人才","体育","卫生","艺术","娱乐"};;
	private static int N=12;
	static{
		category = new HashMap<String,Integer>();
		category.put("财经", 0);
		category.put("地域", 1);
		category.put("电脑", 2);
		category.put("房产", 3);
		category.put("教育", 4);
		category.put("科技", 5);
		category.put("汽车", 6);
		category.put("人才", 7);
		category.put("体育", 8);
		category.put("卫生", 9);
		category.put("艺术", 10);
		category.put("娱乐", 11);
	}
	
	public static int index(String str){
		return category.get(str);
	}
	public static String getCategory(int i){
		if(i>=name.length){System.out.println("数组越界");return "";}
		return name[i];
	}
	public static int size(){
		return N;
	}
}
