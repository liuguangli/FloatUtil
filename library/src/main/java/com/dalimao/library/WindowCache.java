package com.dalimao.library;


import android.content.Context;
import android.util.SparseArray;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WindowCache {
	public Map<Class<? extends Context>, SparseArray<Window>> sWindows;

	public WindowCache() {
		sWindows = new HashMap<Class<? extends Context>, SparseArray<Window>>();
	}

	/**
	 * Returns whether the window corresponding to the class and id exists in
	 * the {@link #sWindows} cache.
	 * 
	 * @param id
	 *            The id representing the window.
	 * @param cls
	 *            Class corresponding to the window.
	 * @return True if the window corresponding to the class and id exists in
	 *         the cache, or false if it does not exist.
	 */
	public boolean isCached(int id, Class<? extends Context> cls) {
		return getCache(id, cls) != null;
	}

	/**
	 * Returns the window corresponding to the id from the {@link #sWindows}
	 * cache.
	 * 
	 * @param id
	 *            The id representing the window.
	 * @param cls
	 *            The class of the implementation of the window.
	 * @return The window corresponding to the id if it exists in the cache, or
	 *         null if it does not.
	 */
	public Window getCache(int id, Class<? extends Context> cls) {
		SparseArray<Window> l2 = sWindows.get(cls);
		if (l2 == null) {
			return null;
		}

		return l2.get(id);
	}

	/**
	 * Add the window corresponding to the id in the {@link #sWindows} cache.
	 * 
	 * @param id
	 *            The id representing the window.
	 * @param cls
	 *            The class of the implementation of the window.
	 * @param window
	 *            The window to be put in the cache.
	 */
	public void putCache(int id, Class<? extends Context> cls, Window window) {
		SparseArray<Window> l2 = sWindows.get(cls);
		if (l2 == null) {
			l2 = new SparseArray<Window>();
			sWindows.put(cls, l2);
		}

		l2.put(id, window);
	}

	/**
	 * Remove the window corresponding to the id from the {@link #sWindows}
	 * cache.
	 * 
	 * @param id
	 *            The id representing the window.
	 * @param cls
	 *            The class of the implementation of the window.
	 */
	public void removeCache(int id, Class<? extends Context> cls) {
		SparseArray<Window> l2 = sWindows.get(cls);
		if (l2 != null) {
			l2.remove(id);
			if (l2.size() == 0) {
				sWindows.remove(cls);
			}
		}
	}

	/**
	 * Returns the size of the {@link #sWindows} cache.
	 * 
	 * @return True if the cache corresponding to this class is empty, false if
	 *         it is not empty.
	 * @param cls
	 *            The class of the implementation of the window.
	 */
	public int getCacheSize(Class<? extends Context> cls) {
		SparseArray<Window> l2 = sWindows.get(cls);
		if (l2 == null) {
			return 0;
		}

		return l2.size();
	}

	/**
	 * Returns the ids in the {@link #sWindows} cache.
	 * 
	 * @param cls
	 *            The class of the implementation of the window.
	 * @return The ids representing the cached windows.
	 */
	public Set<Integer> getCacheIds(Class<? extends Context> cls) {
		SparseArray<Window> l2 = sWindows.get(cls);
		if (l2 == null) {
			return new HashSet<Integer>();
		}

		Set<Integer> keys = new HashSet<Integer>();
		for (int i = 0; i < l2.size(); i++) {
			keys.add(l2.keyAt(i));
		}
		return keys;
	}

    public void clear(Class<? extends Context> cls) {
        SparseArray<Window> l2 = sWindows.remove(cls);
        if(l2 != null && l2.size() > 0) {
            l2.clear();
        }
    }

	public int size() {
		return sWindows.size();
	}
}
