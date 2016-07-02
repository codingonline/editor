package utils.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonTuple{
	List<JsonEntry> l = new ArrayList<JsonEntry>();
	public void addEntry(JsonEntry e){
		l.add(e);
	}
	@Override
	public String toString(){
		String t="[";
		for(Iterator<JsonEntry> i=l.iterator();i.hasNext();){
			JsonEntry e= i.next();
			t+=e;
			if(i.hasNext())
				t+=",";
		}
		t+="]";
		return t;
	}
	public boolean isEmpty(){
		return l.isEmpty();
	}
}