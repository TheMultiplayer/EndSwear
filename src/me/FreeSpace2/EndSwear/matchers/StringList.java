package me.FreeSpace2.EndSwear.matchers;

import java.util.ArrayList;

import me.FreeSpace2.EndSwear.StringMatcher;

public class StringList extends ArrayList<String> implements StringMatcher{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4502250155347662488L;
	public boolean add(String string) {
		return super.add(string.replaceAll("(ing|in|ity|ies|able|y|ible|ous|ed|es|ers|a)$",""));
	}
	public void remove(String string) {
		super.remove(string.replaceAll("(ing|in|ity|ies|able|y|ible|ous|ed|es|ers|a)$",""));
	}
	public boolean contains(String string) {
		return super.contains(string.replaceAll("(ing|in|ity|ies|able|y|ible|ous|ed|es|ers|a)$",""));
	}
	public boolean isFuzzy() {
		return false;
	}
	public boolean fuzzilyContains(String string) {
		return super.contains(string.replaceAll("(ing|in|ity|ies|able|y|ible|ous|ed|es|ers|a)$",""));
	}
	@Override
	public String getType() {
		return "strlist";
	}

}
