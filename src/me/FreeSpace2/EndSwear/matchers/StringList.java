package me.FreeSpace2.EndSwear.matchers;

import java.util.ArrayList;

import me.FreeSpace2.EndSwear.StringMatcher;

public class StringList extends ArrayList<String> implements StringMatcher{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4502250155347662488L;
	public boolean add(String string) {
		return super.add(string);
	}
	public void remove(String string) {
		super.remove(string);
	}
	public boolean contains(String string) {
		return super.contains(string);
	}
	public boolean isFuzzy() {
		return false;
	}
	public boolean fuzzilyContains(String string) {
		return super.contains(string);
	}

}
