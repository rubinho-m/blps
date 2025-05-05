package com.example.demo.connector;

import jakarta.resource.cci.MappedRecord;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class StripeMappedRecord implements MappedRecord {
    @Serial
    private static final long serialVersionUID = 1L;

    private String recordName;

    private String recordShortDescription;

    private Map<String, Object> parameters = new HashMap<>();

    private String response;

    public StripeMappedRecord(String recordName) {
        this.recordName = recordName;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StripeMappedRecord clone = new StripeMappedRecord(recordName);
        clone.recordShortDescription = recordShortDescription;
        clone.parameters = new HashMap<>(parameters);
        clone.response = response;
        return clone;
    }

    @Override
    public int size() {
        return parameters.size();
    }

    @Override
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return parameters.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return parameters.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return parameters.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        if (key instanceof String) {
            return parameters.put((String) key, value);
        }
        throw new IllegalArgumentException("Key must be a String");
    }

    @Override
    public Object remove(Object key) {
        return parameters.remove(key);
    }

    @Override
    public void putAll(Map m) {
        for (Object entryObj : m.entrySet()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObj;
            Object key = entry.getKey();
            if (key instanceof String) {
                parameters.put((String) key, entry.getValue());
            } else {
                throw new IllegalArgumentException("Keys must be Strings");
            }
        }
    }

    @Override
    public void clear() {
        parameters.clear();
    }

    @Override
    public Set<String> keySet() {
        return parameters.keySet();
    }

    @Override
    public Collection<Object> values() {
        return parameters.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return parameters.entrySet();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MappedRecord) {
            return getRecordName().equals(((MappedRecord) other).getRecordName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return recordName != null ? recordName.hashCode() : 0;
    }
}
