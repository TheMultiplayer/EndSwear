package me.FreeSpace2.EndSwear;

public class StringMatch{
	private boolean match;
	private String matched="";
	StringMatch(boolean match, String matched){
		this.match=match;
		this.matched=matched;
	}
	public boolean isOK(){
		return match;
	}
	public String getString(){
		return matched;
	}

}
