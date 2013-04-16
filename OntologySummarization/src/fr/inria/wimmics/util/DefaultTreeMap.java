package fr.inria.wimmics.util;


import java.util.TreeMap;

public class DefaultTreeMap<K,V> extends TreeMap<K,V> {
	protected V defaultValue;
	public DefaultTreeMap(V defaultValue) {
		this.defaultValue = defaultValue;
	}
	@Override
	public V get(Object k) {
		V v = super.get(k);
		return ((v == null) && !this.containsKey(k)) ? this.defaultValue : v;
	}
}
