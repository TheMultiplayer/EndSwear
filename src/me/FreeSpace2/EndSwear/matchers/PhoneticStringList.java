package me.FreeSpace2.EndSwear.matchers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import me.FreeSpace2.EndSwear.StringMatcher;


public class PhoneticStringList extends ArrayList<String> implements StringMatcher,Serializable{
	private static final long serialVersionUID = 8024657740286372018L;
	private List<String>phoneticArray=new ArrayList<String>();
	public boolean fuzzilyContains(String stringToSearch){
		if (phoneticArray.contains(phoProcess(stringToSearch))){
			return true;
		}else{
			return false;
		}
	}
	public boolean substringFuzzilyContains(String stringToSearch){
		if (phoneticArray.contains(phoProcess(stringToSearch))){
			return true;
		}else{
			for(String word:this){
				if(stringToSearch.contains(word)){
					return true;
				}
			};
			return false;
		}
	}


	public boolean add(String string){
		phoneticArray.add(phoProcess(string));
		return super.add(string);
	}
	private String phoProcess(String stringToSearch){
		if(stringToSearch.length()>2){
			return stringToSearch.toLowerCase().replace("@", "a").replaceAll("z$","s").replace("4", "h").replace("$","s").replace("0", "O").replace("'", "aps").replace("got", "gt").replace("ks","x").replace("er","r").replace("!", "i").replaceAll("(ing|in|ity|ies|able|y|ible|ous|ed|es|ers|a)$","").replaceAll("([aeiuy])\\1+", "o").replaceAll("(.)\\1+", "$1").replace("ph", "f").replaceAll("(ck)$", "k").replaceAll("[^A-Za-z0-9]", "");
		}else{
			return stringToSearch+"null00unmatch*^*(&^*(";
		}

	}
	public void remove(String string) {
		super.remove(string);		
	}
	public boolean contains(String string) {
		return super.contains(string);
	}
	public boolean isFuzzy() {
		return true;
	}
	@Override
	public String getType() {
		return "pholist";
	}

}