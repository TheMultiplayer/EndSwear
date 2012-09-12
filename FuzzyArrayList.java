package me.FreeSpace2.EndSwear;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FuzzyArrayList extends ArrayList<String>{
	private static final long serialVersionUID = 8024657740286372018L;
	private List<String>medPhonetic=new ArrayList<String>();
	private List<String>lowPhonetic=new ArrayList<String>();
	private final String vowelChar="%";

	public boolean approxContains(String stringToSearch,int threshold){
		stringToSearch=UnEd(stringToSearch.toLowerCase());
		List<Future<Boolean>> threadedReturns=new ArrayList<Future<Boolean>>();
		ExecutorService executor = Executors.newFixedThreadPool(4);
		boolean swear = false;
		for(String string:this){
			threadedReturns.add(executor.submit(new FZThread(stringToSearch,string,threshold)));
		}
		for(Future<Boolean> future:threadedReturns){
			try {
				if(swear=future.get()){
					break;
				}
			} catch (Exception e) {}
		}
		return swear;
	}
	public String getPhoneticMatchingWord(String stringToSearch, int i){
		stringToSearch.toLowerCase();
		if(this.contains(stringToSearch)){
			return stringToSearch;
		}
		if(i==3){
			String noVowel=calculateMedPhone(stringToSearch);
				return noVowel;
		}
		if(i==2){
			if(stringToSearch.length()<=4){
					String noVowel=calculateLowPhone(stringToSearch);
						return noVowel;
			}
			String noVowel=calculateMedPhone(stringToSearch);
			if (medPhonetic.contains(noVowel)){
				return noVowel;
			}
		}else if(i==1){
				return calculateLowPhone(stringToSearch);
		}
		return "ERROR : INVALID THRESHOLD";
	}
	public boolean phoneticMatch(String stringToSearch, int i){
		stringToSearch=UnEd(stringToSearch.toLowerCase());
		if(this.contains(stringToSearch)){
			return true;
		}
		if(i==3){
			if(stringToSearch.equalsIgnoreCase("as") | stringToSearch.equalsIgnoreCase("cant")){
				return false;
			}
			String noVowel=calculateMedPhone(stringToSearch);
			if (noVowel.equalsIgnoreCase("")){
				return false;
			}
			if (medPhonetic.contains(noVowel)){
				return true;
			}
		}
		if(i==2){
			if(stringToSearch.length()<=4){
					String noVowel=calculateLowPhone(stringToSearch);
					if(noVowel.equalsIgnoreCase("")){
						return false;
					}else{
						if(lowPhonetic.contains(noVowel)){
							return true;
						}
					}
			}
			String noVowel=calculateMedPhone(stringToSearch);
			if (noVowel.equalsIgnoreCase("")){
				return false;
			}
			if (medPhonetic.contains(noVowel)){
				return true;
			}
		}else if(i==1){
			return lowPhonetic.contains(phoneticPairFilter(noDouble(stringToSearch)));
		}
		return false;
	}
	public void genDictionary(){
		for(String word:this){
			medPhonetic.add(calculateMedPhone(word));
			lowPhonetic.add(calculateLowPhone(word));
		}
	}
	
	
	
	
	private String calculateLowPhone(String string){
		return phoneticPairFilter(deLeet(noDoubleVowel(string)));
	}
	private String calculateMedPhone(String string){
		String currentString=vowelFilter(phoneticPairFilter(deLeet(noDouble(string))));
		if(string.length()>0){
			if((string.length()<=4 & string.charAt(0)=="d".charAt(0))|string.length()<=3){
				currentString=calculateLowPhone(string);
			}
		}
		return currentString;
	}
	private String vowelFilter(String string){		
		return string.replaceAll("(^[aeiouy])|([aeiouy]$)",vowelChar).replaceAll("[aeiouy]","").replaceAll("s$","");
	}
	private String phoneticPairFilter(String string){
		return string.replace("ck", "k").replace("ed", "d").replace("ou", "u").replace("eigh", "").replace("kn", "n").replace("ph", "f").replace("gh","g").replace("cs", "x").replace("ks","s").replace("es", "s").replace("lk", "k").replace("mn", "m").replaceAll("(ing|in|er|ity|ies|able|y|ible|ous|it)$","");
	}
	private String noDouble(String string){
		return string.replace("oo","%").replaceAll("(.)\\1+", "$1");
	}
	private String noDoubleVowel(String string){
		return string.replaceAll("([aeiouy])\\1+", "$1");
	}
	private String deLeet(String string){
		return string.replace("es", "").replace("!", "i").replace("@", "a").replace("4", "h").replace("$","s").replace("0", "O").replace("'", "aps");
	}
	private String UnEd(String string){
		return string.replaceAll("(ing|ed|er)", "");
	}
}
