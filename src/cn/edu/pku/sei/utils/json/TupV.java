package cn.edu.pku.sei.utils.json;



public class TupV extends AbsV{
	JsonTuple t;
	public TupV(JsonTuple tt){
		t= tt;
	}
	public String toString(){
		return t.toString();
	}
}