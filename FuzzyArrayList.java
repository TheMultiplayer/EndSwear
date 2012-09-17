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
		return "No computation valid!";
	}
	public String getLowDict(){
		String words=null;
		for(String wordle:lowPhonetic){
			words=words+", "+wordle;
		}
		return words;
	}
	public String getMedDict(){
		String words=null;
		for(String wordle:medPhonetic){
			words=words+", "+wordle;
		}
		return words;
	}
	public boolean phoneticMatch(String stringToSearch, int i){
		stringToSearch=UnEd(stringToSearch.toLowerCase());
		if(this.contains(stringToSearch)){
			return true;
		}
		if(i==3){
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
		medPhonetic=new ArrayList<String>();
		lowPhonetic=new ArrayList<String>();
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
		return currentString;
	}
	private String vowelFilter(String string){		
		return string.replaceAll("(^[aeiouy])|([aeiouy]$)",vowelChar).replaceAll("[aeiouy]","").replaceAll("s$","");
	}
	private String phoneticPairFilter(String string){
		return string.replace("ck", "k").replace("ed", "d").replace("ou", "u").replace("eigh", "a").replace("kn", "n").replace("ph", "f").replace("gh","g").replace("cs", "x").replace("ks","s").replace("es", "s").replace("lk", "k").replace("mn", "m").replace("q","k").replaceAll("h$","");
	}
	private String noDouble(String string){
		return string.replaceAll("(.)\\1+", "$1").replace("oo",vowelChar);
	}
	private String noDoubleVowel(String string){
		return string.replaceAll("([aeiuy])\\1+", "$1").replace("oo",vowelChar);
	}
	private String deLeet(String string){
		return string.replace("es", "").replace("!", "i").replace("@", "a").replace("4", "h").replace("$","s").replace("0", "O").replace("'", "aps");
	}
	private String UnEd(String string){
		return string.replaceAll("(ing|in|er|ity|ies|able|y|ible|ous|ed)$","").replaceAll("es$","");
	}
}
