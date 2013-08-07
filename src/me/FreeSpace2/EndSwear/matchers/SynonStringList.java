package me.FreeSpace2.EndSwear.matchers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.FreeSpace2.EndSwear.StringMatcher;


public class SynonStringList implements Serializable, StringMatcher{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7803519666213586122L;
	ArrayList<String[]> phonemes = new ArrayList<String[]>();
	ArrayList<String> words = new ArrayList<String>();
	public boolean fuzzilyContains(String string){
		String[] stArray=phoneticize(string);
		for(String[] str:phonemes){
			float similar = 0;
			int lesserString;
				if(stArray.length>str.length){
					lesserString=str.length;
				}else{
					lesserString=stArray.length;
				}
			for(int i=0;i<lesserString;i++){
				if(stArray[i].equalsIgnoreCase(str[i])){
					similar++;
				}
			}
			if(similar/lesserString>0.68){
				System.out.println(similar+"/"+lesserString+"="+similar/lesserString+" yields a "+(similar/lesserString>0.68));
				return true;
			}
		}
		return false;
	}
	public String[] phoneticize(String string){
		string.replaceAll("(.)\\1+", "$1").replaceAll("(ing|in|ity|ies|able|y|ible|ous|ed|es|ers|a)$","");
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
		words.add(string);
		return phonemes.add(phoneticize(string));
	}
	public void remove(String string){
		words.remove(string);
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
	public Iterator<String> iterator() {
		return words.iterator();
	}
	public boolean isFuzzy() {
		return true;
	}
	@Override
	public String getType() {
		return "synlist";
	}
}
