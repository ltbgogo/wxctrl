package com.abc.wxctrl.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

public class CollUtil {
	
	public static <T> List<List<T>> group(List<T> list, int capabilityPerGroup) {
		List<List<T>> groups = new ArrayList<>();
		List<T> group = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			group.add(list.get(i));
			if (group.size() % capabilityPerGroup == 0) {
				groups.add(group);
				group = new ArrayList<>();
			}
		}
		if (!group.isEmpty()) {
			groups.add(group);	
		}
		return groups;
	}
	
	public static <T> Iterable<T> toIterable(final Enumeration<T> enumeration) {
		return toIterable(CollectionUtils.toIterator(enumeration));
	}
	
    /**
     * 此方法是为了防止结果集中有复杂类型，导致框架将结果集转成JSON格式时出现的死循环
     *
     * @param rs 要处理的结果集
     * @return 处理后的结果集
     */
    public static <T extends Iterable<Map<String, Object>>> T stringfyResultset(T rs) {
        //循环结果集
        for (Map<String, Object> row : rs) {
            if (row != null) {
                //循环一行中的所有列
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    row.put(column.getKey(), column.getValue() == null ? null : column.getValue().toString());
                }
            }
        }
        return rs;
    }
	
    public static <T> T getFirst(Iterable<T> iterable) {
    	Iterator<T> iterator = iterable.iterator();
    	return iterator.hasNext() ? iterator.next() : null;
    }
	
	public static <T> Iterable<T> toIterable(final Iterator<T> iterator) {
		return new Iterable<T>() {
			public Iterator<T> iterator() { return iterator; }
		};
	}
    
	public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
		return map == null ? new HashMap<K, V>() : map;
	}
	

	public static Map<String, List<Map<String, Object>>> group(String groupKey, List<Map<String, Object>> entities) {
		Map<String, List<Map<String, Object>>> groupMap = new LinkedHashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> entity : entities) {
			if (!groupMap.containsKey(entity.get(groupKey))) {
				groupMap.put(entity.get(groupKey).toString(), new ArrayList<Map<String, Object>>());
			}
			groupMap.get(entity.get(groupKey).toString()).add(entity);
		}
		return groupMap;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static  Map getByEqual(List<Map> tableData, String key, Object value) {
		for (Map row : tableData) {
			if (ObjectUtils.equals(value, row.get(key))) {
				return row;
			}
		}
		return null;
	}
	
	public static <T> T getFirst(List<T> list, T defaultValue) {
		return list.isEmpty() ? defaultValue : list.get(0);
	}
	
	/**
	 * 将对象数组转换成Map
	 */
	public static Map toMap(Object...kvs) {
		Map map = new LinkedHashMap<>();
		for (int i = 0; i < kvs.length;) {
			map.put(kvs[i++], kvs[i++]);
		}
		return map;
	}
}








