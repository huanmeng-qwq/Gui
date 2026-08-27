package me.huanmeng.gui.api.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Pagination<T> {
    private final List<T> items;
    private final int elementsPerPage;

    public Pagination(List<T> items, int elementsPerPage) {
        if (items == null) throw new NullPointerException("items");
        if (elementsPerPage <= 0) throw new IllegalArgumentException("elementsPerPage must be positive");
        this.items = Collections.unmodifiableList(new ArrayList<T>(items));
        this.elementsPerPage = elementsPerPage;
    }

    public int pageCount() { return Math.max(1, (items.size() + elementsPerPage - 1) / elementsPerPage); }
    public int clampPage(int page) { return Math.max(1, Math.min(page, pageCount())); }
    public boolean hasNext(int page) { return clampPage(page) < pageCount(); }
    public boolean hasPrevious(int page) { return clampPage(page) > 1; }
    public List<T> page(int page) {
        int current = clampPage(page);
        int start = (current - 1) * elementsPerPage;
        int end = Math.min(start + elementsPerPage, items.size());
        if (start >= end) return Collections.emptyList();
        return new ArrayList<T>(items.subList(start, end));
    }
    public List<T> items() { return items; }
    public int elementsPerPage() { return elementsPerPage; }
}
