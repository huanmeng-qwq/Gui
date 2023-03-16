package me.huanmeng.opensource.page;

import java.util.ArrayList;
import java.util.List;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class Pagination<T> {
    private List<T> content;
    private final int elementsPerPage;

    public Pagination(List<T> content, int elementsPerPage) {
        this.content = content;
        this.elementsPerPage = elementsPerPage;
    }


    public void printPage(int page, IPaginationExecutor<T> executor) {
        for (T element : getElementsFor(page)) {
            executor.print(element);
        }
    }

    public List<T> getElementsFor(int page) {
        if (page <= 0 || page > getPages()) return new ArrayList<T>();

        int startIndex = (page - 1) * this.elementsPerPage;
        int endIndex = Math.min(page * this.elementsPerPage, this.content.size());

        return new ArrayList<>(this.content).subList(startIndex, Math.min(endIndex, this.content.size()));
    }

    public int getPages() {
        return (int) Math.ceil(content.size() / (double) elementsPerPage);
    }

    public int getMaxPage() {
        return getPages();
    }

    public int getMinPage() {
        return 1;
    }

    public boolean hasNext(int now) {
        return now < getMaxPage();
    }

    public boolean hasLast(int now) {
        return now > getMinPage();
    }

    public void addElement(T element) {
        if (!this.content.contains(element)) this.content.add(element);
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getElementsPerPage() {
        return elementsPerPage;
    }
}
