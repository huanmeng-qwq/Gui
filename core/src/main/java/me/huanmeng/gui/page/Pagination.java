package me.huanmeng.gui.page;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class Pagination<@NonNull T> {
    @NonNull
    private List<@Nullable T> content;
    private final int elementsPerPage;

    public Pagination(@NonNull List<T> content, int elementsPerPage) {
        this.content = content;
        this.elementsPerPage = elementsPerPage;
    }

    public void printPage(int page, @NonNull IPaginationExecutor<T> executor) {
        for (T element : getElementsFor(page)) {
            executor.print(element);
        }
    }

    /**
     * 获取某一页的元素
     *
     * @param page 页数
     * @return 元素列表
     */
    @NonNull
    public List<T> getElementsFor(int page) {
        if (page <= 0 || page > getPages()) return new ArrayList<T>();

        int startIndex = (page - 1) * this.elementsPerPage;
        int endIndex = Math.min(page * this.elementsPerPage, this.content.size());

        return new ArrayList<>(this.content).subList(startIndex, Math.min(endIndex, this.content.size()));
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getPages() {
        return (int) Math.ceil(content.size() / (double) elementsPerPage);
    }

    /**
     * 获取最大页数
     *
     * @return 最大页数
     */
    public int getMaxPage() {
        return getPages();
    }

    /**
     * 获取最小页数
     *
     * @return 最小页数
     */
    public int getMinPage() {
        return 1;
    }

    /**
     * 是否有下一页
     *
     * @param now 当前页
     * @return 是否有下一页
     */
    public boolean hasNext(int now) {
        return now < getMaxPage();
    }

    /**
     * 是否有上一页
     *
     * @param now 当前页
     * @return 是否有上一页
     */
    public boolean hasLast(int now) {
        return now > getMinPage();
    }

    /**
     * 添加元素
     *
     * @param element 元素
     */
    public void addElement(@Nullable T element) {
        if (!this.content.contains(element)) this.content.add(element);
    }

    /**
     * 获取所有元素
     *
     * @return 元素列表
     */
    @NonNull
    public List<T> getContent() {
        return content;
    }

    /**
     * 设置所有元素
     *
     * @param content 元素列表
     */
    public void setContent(@NonNull List<T> content) {
        this.content = content;
    }

    /**
     * 获取每页元素数量
     *
     * @return 每页元素数量
     */
    public int getElementsPerPage() {
        return elementsPerPage;
    }
}
