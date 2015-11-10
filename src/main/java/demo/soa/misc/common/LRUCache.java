package demo.soa.misc.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 6841923784469767574L;
	
	private Map<K,V>  cache = null ;
    private int cacheSize = 0;
       
    public LRUCache(int cacheSize){
        this.cacheSize = cacheSize;
        int hashTableCapacity = (int) Math.ceil (cacheSize / 0.75f) + 1;
        cache = (Map<K, V>)Collections.synchronizedMap(new LinkedHashMap<K, V>(hashTableCapacity, 0.75f,true)
        {
            // (an anonymous inner class)
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry (Map.Entry<K, V> eldest)
            {
                return size () > LRUCache.this.cacheSize;
            }
        });
    }
   
    public V put(K key,V value){
        return cache.put(key, value);
    }

    public V get(Object key){
        return cache.get(key);
    }
    
    public V remove(Object key){
    	return cache.remove(key);
    }
    
    public void clear(){
    	cache.clear();
    }
}
