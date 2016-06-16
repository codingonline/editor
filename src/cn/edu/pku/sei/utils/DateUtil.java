package cn.edu.pku.sei.utils;

import java.sql.Timestamp;

import cn.edu.pku.sei.jdbc.MySQL;

public class DateUtil {

	public static Timestamp getCurrentDate(){
		Timestamp tt;
		try {
			tt = (Timestamp) (new MySQL().execute("select now()")).get(0).get("now()");
			return tt;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main (String[] args){
		System.out.println(getCurrentDate());
	}
	
}
