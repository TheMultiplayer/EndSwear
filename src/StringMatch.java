package me.FreeSpace2.EndSwear;

public class StringMatch{
	private boolean match;
	private String matched="";
	private String matchedPhrase="";
	StringMatch(boolean match, String matched){
		this.match=match;
		this.matched=matched;
	}
	StringMatch(boolean match, String matched, String matchedPhrase){
		this.match=match;
		this.matched=matched;
	}
	public boolean getMatched(){
		return match;
	}
	public String getString(){
		return matched;
	}
	public String getMatchedPhrase(){
		return matchedPhrase;
	}

}
