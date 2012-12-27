package me.FreeSpace2.EndSwear;


public class Range {
	int start;
	int end;
	Range(int begin, int finish){
		start=begin;
		end=finish;
	}
	public Range(String range) {
		if (range.contains("-")){
			String[] strTC=range.split("-");
			start=Integer.parseInt(strTC[0]);
			end=Integer.parseInt(strTC[1]);
		}else if(range.contains("+")){
			start=Integer.parseInt(range.replace("+", ""));
			end=Integer.MAX_VALUE;
			}
		}
	public boolean containsInclusive(Integer n){
		return (n<=end) & (n>= start);
	}
	public boolean containsExclusive(Integer n){
		return (n<end) & (n>start);
	}
}
