/**
 * 
 */
package com.sc.utilities;

/**
 * Spent way too much time trying to import it from java sdk so I just built
 * one. Allows me to return more than one object in a function
 * 
 * @author Felipe
 *
 */
public class Pair<K, V> {

	private final K first;
	private final V second;

	public static <K, V> Pair<K, V> createPair(K first, V second) {
		return new Pair<K, V>(first, second);
	}

	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}

	public K first() {
		return first;
	}

	public V second() {
		return second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

}
