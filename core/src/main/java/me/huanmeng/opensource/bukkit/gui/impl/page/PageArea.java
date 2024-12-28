package me.huanmeng.opensource.bukkit.gui.impl.page;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.page.Pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2024/12/28<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public class PageArea {
    protected Slots slots = Slots.full();
    protected List<? extends Button> items = Collections.emptyList();
    protected int elementsPerPage;
    protected PageSetting pageSetting;


    protected int currentPage = 1;
    protected Pagination<? extends Button> pagination = new Pagination<>(items, elementsPerPage);

    public void previousPage() {
        currentPage--;
    }

    public void previousPage(int n){
        currentPage -= n;
    }

    public void nextPage() {
        currentPage++;
    }

    public void nextPage(int n){
        currentPage += n;
    }

    public void setToFirstPage() {
        currentPage = pagination.getMinPage();
    }

    public void setToLastPage() {
        currentPage = pagination.getMaxPage();
    }
    public boolean hasPreviousPage(){
        return pagination.hasLast(currentPage);
    }

    public boolean hasNextPage(){
        return pagination.hasNext(currentPage);
    }


    public int getMaxPage(){
        return pagination.getMaxPage();
    }

    public List<? extends Button> getCurrentItems() {
        return pagination.getElementsFor(currentPage);
    }

    public Slots slots() {
        return slots;
    }

    public PageArea slots(Slots slots) {
        this.slots = slots;
        return this;
    }

    public List<? extends Button> items() {
        return items;
    }

    public PageArea items(List<? extends Button> items) {
        this.items = new ArrayList<>(items);
        refreshPagination();
        return this;
    }

    public int elementsPerPage() {
        return elementsPerPage;
    }

    public PageArea elementsPerPage(int elementsPerPage) {
        this.elementsPerPage = elementsPerPage;
        refreshPagination();
        return this;
    }

    public int currentPage() {
        return currentPage;
    }

    public PageArea currentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public Pagination<? extends Button> pagination() {
        return pagination;
    }

    public PageSetting pageSetting() {
        return pageSetting;
    }

    public PageArea pageSetting(PageSetting pageSetting) {
        this.pageSetting = pageSetting;
        return this;
    }

    public void refreshPagination() {
        pagination = new Pagination<>(items, elementsPerPage);
    }
}
