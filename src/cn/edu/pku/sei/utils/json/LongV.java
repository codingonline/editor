package cn.edu.pku.sei.utils.json;

public class LongV extends AbsV{
	long v;
	public LongV(long v){
		this.v = v;
	}
	@Override
	public String toString(){
		return new Long(v).toString();
	}
}
