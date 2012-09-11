package me.FreeSpace2.EndSwear;

import java.util.concurrent.Callable;

public class FZThread implements Callable<Boolean>{
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
