package me.FreeSpace2.EndSwear;

import java.util.List;

public class Bleeper {
	public static String generateBleep(String string){
		String[] bleepChars={"!","@","#","%","$","*"};
		String gen="";
		for(int i=0;i<string.length();i++){
			gen=gen+bleepChars[(int)(Math.random()*6)];
		}
		return gen;
	}
	public static String generateRegexBleep(String string){
		String[] bleepChars={"!","@","#","%","\\$","\\*"};
		String gen="";
		for(int i=0;i<string.length();i++){
			gen=gen+bleepChars[(int)(Math.random()*6)];
		}
		return gen;
	}
	public static String generateBleep(String string, List<String> list){
		String gen="";
		for(int i=0;i<string.length();i++){
			gen=gen+list.get((int) (Math.random()*list.size()));
		}
		return gen;
	}
}
