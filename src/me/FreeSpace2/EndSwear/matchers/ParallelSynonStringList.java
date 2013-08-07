package me.FreeSpace2.EndSwear.matchers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.FreeSpace2.EndSwear.StringMatcher;


public class ParallelSynonStringList implements Serializable, StringMatcher{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7803519666213586122L;
	ExecutorService executor = Executors.newFixedThreadPool(4);
	ArrayList<String[]> phonemes = new ArrayList<String[]>();
	public boolean phoneticMatch(String string){
		List<Future<Boolean>> threadedReturns=new ArrayList<Future<Boolean>>();
		String[] stArray=phoneticize(string);
		for(String[] str:phonemes){
			Future<Boolean> future=executor.submit(new CheckThread(stArray, str));
			threadedReturns.add(future);
		}
		for(Future<Boolean> future:threadedReturns){
			try {
				if(future.get()){
					threadedReturns.clear();
					return true;
				}else{
					continue;
				}
			} catch (Exception e) {}
		}
		return false;
	}
	public String[] phoneticize(String string){
		string.replaceAll("(.)\\1+", "$1");
		String lastPhernome=""+string.charAt(0);
		boolean isReadyToMoveOn = false;
		ArrayList<String> synons = new ArrayList<String>();
		char c;
		for(int i=1;i<string.length();i++){
			
			c=string.charAt(i);
			if(isVowel(c)){
				lastPhernome = lastPhernome+c;
				isReadyToMoveOn=true;
			}else{
				if(isReadyToMoveOn){
					synons.add(new String(lastPhernome));
					lastPhernome = "";
					isReadyToMoveOn=false;
				}
				lastPhernome=lastPhernome+c;
			}
		}
		synons.add(lastPhernome);
		Object[] obj=synons.toArray();
		return Arrays.copyOf(obj,obj.length,String[].class);
	}
	public boolean add(String string){
		return phonemes.add(phoneticize(string));
	}
	public void remove(String string){
		phonemes.remove(string);
	}
	public boolean contains(String string){
		return phonemes.contains(phoneticize(string));
	}
	private static boolean isVowel(char chr){
		return (chr == 'A' || chr == 'E' || chr == 'I' || chr == 'O' || chr == 'U' || chr == 'a' || chr == 'e' || chr == 'i' || chr == 'o' || chr == 'u');
    }
	@SuppressWarnings("unused")
	private static boolean isConsonant(char chr){
		return !isVowel(chr);
	}
	public List<char[]> bigram(String input)
	{
		ArrayList<char[]> bigram = new ArrayList<char[]>();
		for (int i = 0; i < input.length() - 1; i++)
		{
			char[] chars = new char[2];
			chars[0] = input.charAt(i);
			chars[1] = input.charAt(i+1);
			bigram.add(chars);
		}
		return bigram;
	}
	class CheckThread implements Callable<Boolean>{
		String[] stringToMatch;
		String[] stringToMatchWith;
		CheckThread(String[] stringToMatch, String[] stringToMatchWith){
			this.stringToMatch=stringToMatch;
			this.stringToMatchWith=stringToMatchWith;
		}
		public Boolean call() throws Exception {
			float similar = 0;
			for(int i=0;i<stringToMatch.length;i++){
				if(stringToMatch[i].equalsIgnoreCase(stringToMatchWith[i])){
					similar++;
				}
			}
			return similar/stringToMatch.length>0.68;
		}
		
	}
	@Override
	public boolean isFuzzy() {
		return true;
	}
	@Override
	public boolean fuzzilyContains(String string) {
		return contains(string);
	}
	@Override
	public Iterator<String> iterator() {
		return null;
	}
	@Override
	public String getType() {
		return "ppslist";
	}
}
