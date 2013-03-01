package me.FreeSpace2.EndSwear;

public class Ordinal {
	public static String getOrdinal(int value){
		if((value%100)>10&&(value%100)<20){
			return "th";
		}
		value=value % 10;
		switch(value){
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}
}
