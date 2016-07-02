package utils.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class JsonEntry{
	Map<String,AbsV> s = new HashMap<String,AbsV>();
	public void setAttribute(String k,AbsV v){
		//TODO sanity check? for json Strings
		s.put(k, v);
	}
	public void setAttribute(String k,boolean b){
		s.put(k, new BooV(b));
	}
	public void setAttribute(String k,JsonTuple v){
		this.setAttribute(k, new TupV(v));
	}
	public void setAttribute(String k,String v){
		this.setAttribute(k, new StrV(v));
	}
	public void setAttribute(String k, long v){
		this.setAttribute(k, new LongV(v));
	}
	@Override
	public String toString(){
		String t="{";
		for(Iterator<String> i=s.keySet().iterator();i.hasNext();){
			String k=i.next();
			
			AbsV v = s.get(k);
			
			t+=k+":"+v;
			
			if(i.hasNext())
				t+=",";
		}
		t+="}";
		return t;
	}
		
	public boolean isEmpty(){
		return s.isEmpty();
	}
}
