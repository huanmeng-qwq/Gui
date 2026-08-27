package me.huanmeng.gui.api.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.huanmeng.gui.api.layout.Slot;

public final class PageArea<T> {
    private final List<Slot> slots;
    private List<T> items;
    private int currentPage = 1;

    public PageArea(List<Slot> slots, List<T> items) {
        if (slots == null || slots.isEmpty()) throw new IllegalArgumentException("slots must not be empty");
        this.slots = Collections.unmodifiableList(new ArrayList<Slot>(slots));
        items(items);
    }

    public synchronized void items(List<T> items) {
        if (items == null) throw new NullPointerException("items");
        this.items = Collections.unmodifiableList(new ArrayList<T>(items));
        currentPage = pagination().clampPage(currentPage);
    }
    public synchronized void currentPage(int page) { currentPage = pagination().clampPage(page); }
    public synchronized int currentPage() { return currentPage; }
    public synchronized int pageCount() { return pagination().pageCount(); }
    public synchronized List<T> currentItems() { return pagination().page(currentPage); }
    public List<Slot> slots() { return slots; }
    public synchronized List<T> items() { return items; }
    public synchronized PageArea<T> copy() { return new PageArea<T>(slots, items).atPage(currentPage); }
    private PageArea<T> atPage(int page) { currentPage(page); return this; }
    private Pagination<T> pagination() { return new Pagination<T>(items, slots.size()); }
}
