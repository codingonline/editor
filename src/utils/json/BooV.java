package utils.json;


public class BooV extends AbsV{
	boolean b;
	public BooV(boolean bb){
		b = bb;
	}
	public String toString(){
		if(b)
			return "true";
		return "false";
	}
}