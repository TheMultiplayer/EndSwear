package me.FreeSpace2.EndSwear;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FuzzyStringList extends ArrayList<String>{
	private static final long serialVersionUID = 8024657740286372018L;
	private List<String>phoneticArray=new ArrayList<String>();
	public boolean approxContains(String stringToSearch,int threshold){
		List<Future<Boolean>> threadedReturns=new ArrayList<Future<Boolean>>();
		ExecutorService executor = Executors.newFixedThreadPool(4);
		boolean match = false;
		for(String string:this){
			threadedReturns.add(executor.submit(new FZThread(stringToSearch,string,threshold)));
		}
		for(Future<Boolean> future:threadedReturns){
			try {
				if(match=future.get()){
					break;
				}
			} catch (Exception e) {}
		}
		return match;
	}
	public StringMatch phoneticMatch(String stringToSearch){
		if (phoneticArray.contains(phoProcess(stringToSearch))){
			return new StringMatch(true, stringToSearch, phoProcess(stringToSearch));
		}else{
			return new StringMatch(false, stringToSearch, phoProcess(stringToSearch));
		}
	}
	public StringMatch phoneticMatchSubstring(String stringToSearch){
		if (phoneticArray.contains(phoProcess(stringToSearch))){
			return new StringMatch(true, stringToSearch, phoProcess(stringToSearch));
		}else{
			for(String word:this){
				if(stringToSearch.contains(word)){
					return new StringMatch(true, stringToSearch, phoProcess(stringToSearch));
				}
			};
			return new StringMatch(false, stringToSearch, phoProcess(stringToSearch));
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

	 class FZThread implements Callable<Boolean>{
			String word;
			String dict;
			int threshold;
			FZThread(String wordToMatch,String wordToMatchWith, int threshold){
				word=wordToMatch;
				dict=wordToMatchWith;
				this.threshold=threshold;
			}
			FZThread(String wordToMatch,String wordToMatchWith){
				word=wordToMatch;
				dict=wordToMatchWith;
			}
			public Boolean call() throws Exception {
				if(threshold==0){
					this.threshold=(int) Math.sqrt(word.length());
				}
				if(computeDistance(word,dict)<=threshold && word.charAt(0)==dict.charAt(0)){
					return true;
				}
				return false;
			}
			private int minimum(int a, int b, int c) {
				return Math.min(Math.min(a, b), c);
			}
			private int computeDistance(CharSequence str1, CharSequence str2) {
		        int[][] distance = new int[str1.length() + 1][str2.length() + 1];
		        for (int i = 0; i <= str1.length(); i++){
		                distance[i][0] = i;
		        }
		        for (int j = 0; j <= str2.length(); j++){
		                distance[0][j] = j;
		        }
		        for (int i = 1; i <= str1.length(); i++){
		                for (int j = 1; j <= str2.length(); j++){
		                        distance[i][j] = minimum(distance[i - 1][j] + 1,distance[i][j - 1] + 1,distance[i - 1][j - 1]+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0: 1));
		                }
		        }
		        return distance[str1.length()][str2.length()];
			}
	 }
}
