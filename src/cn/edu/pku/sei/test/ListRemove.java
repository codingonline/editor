package cn.edu.pku.sei.test;

import java.util.ArrayList;

public class ListRemove {

    public static void main(String[] args) {  
        ArrayList<String> list=new ArrayList<String>();  
        list.add("s1");  
        list.add("s2");  
        list.add(null);  
        list.add("s3");  
        list.add("s4");  
  
        System.out.println("before remove list size:"+list.size());  
        for(Object obj:list)  
            System.out.println(obj);  
        System.out.println();  
          
        list.remove("s2");  
          
        System.out.println("after remove list size:"+list.size());  
        for(Object obj:list)  
            System.out.println(obj);  
    }  
}  