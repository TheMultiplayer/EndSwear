package me.FreeSpace2.EndSwear;

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
	public static String generateBleep(String string, String[] bleepChars){
		String gen="";
		for(int i=0;i<string.length();i++){
			gen=gen+bleepChars[(int)(Math.random()*bleepChars.length)];
		}
		return gen;
	}
}
