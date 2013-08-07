package me.FreeSpace2.EndSwear;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class StringFilter {
	static String sterilize(String string){
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	static String prepare(String string){
		return string.replaceAll("(ing|in|ity|ies|able|y|ible|ous|ed|es|ers|a)$","").replaceAll("([aeiuy])\\1+", "o").replaceAll("(.)\\1+", "$1").replace("ph", "f").replaceAll("(ck)$", "k").replaceAll("[^A-Za-z0-9]", "");
	}
	static String deLeet(String string){
		return string.replace("4","a").replace("(", "c").replace("3", "e").replace("9","g").replace("|-|", "h").replace("1", "i").replace("0", "O");
	}
}
