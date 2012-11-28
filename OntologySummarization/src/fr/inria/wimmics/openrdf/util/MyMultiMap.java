package fr.inria.wimmics.openrdf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class MyMultiMap<Key,Val> {

	Map<Key, ArrayList<Val>> map = new HashMap<Key, ArrayList<Val>>();

	public int size() {

		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Key key) {
		return map.containsKey(key);
	}

//	public boolean containsValue(Object value) {
//		return false;
//	}

	
	public ArrayList<Val> get(Key key) {
		return map.get(key);
	}
	
	public Val getFirst(Key key) {
		return map.get(key).get(0);
	}

	public ArrayList<Val> put(Key key, Val value) {
		ArrayList<Val> list = null;
		 
		ArrayList<Val> v = null;
		if(map.containsKey(key)) {
			list = map.get(key);
		
			
		} else {
			list = new ArrayList<Val>();
		}
		list.add(value);

		v = map.put(key, list);
		
		return v;
	}

	public ArrayList<Val> remove(Key key) {
		return map.remove(key);
	}

//	public void putAll(Map<? extends Key, ? extends Val> m) {
//		// TODO Auto-generated method stub
//		
//		
//	}

	public void clear() {
		map.clear();
	}

	public Set<Key> keySet() {
		return map.keySet();
	}


	public Collection<ArrayList<Val>> values() {
		return map.values();
	}

	public Set<Entry<Key, ArrayList<Val>>> entrySet() {
		return map.entrySet();
	}
	
	
}
