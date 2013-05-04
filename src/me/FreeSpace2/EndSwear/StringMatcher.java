package me.FreeSpace2.EndSwear;

public interface StringMatcher {
	public boolean add(String string);
	public void remove(String string);
	public boolean contains(String string);
	public boolean isFuzzy();
	public boolean fuzzilyContains(String string);
}
