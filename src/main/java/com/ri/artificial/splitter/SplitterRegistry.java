package com.ri.artificial.splitter;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ri
 * @date 2026-10-02 20:58
 * 注册表
 */
@Component
public class SplitterRegistry {
    private final Map<String, DocumentSplitter> splitters = new LinkedHashMap<>();

    public SplitterRegistry(List<DocumentSplitter> splitterList) {
        for (DocumentSplitter s : splitterList) {
            splitters.put(s.name(), s);
        }
    }

    public DocumentSplitter get(String name) {
        DocumentSplitter s = splitters.get(name);
        if (s == null) {
            throw new IllegalArgumentException("未知分片策略: " + name);
        }
        return s;
    }

    public List<DocumentSplitter> all() {
        return List.copyOf(splitters.values());
    }
}
