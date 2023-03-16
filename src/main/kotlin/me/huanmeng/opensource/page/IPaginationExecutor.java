package me.huanmeng.opensource.page;

@FunctionalInterface
public interface IPaginationExecutor<T> {
    void print(T element);
}
