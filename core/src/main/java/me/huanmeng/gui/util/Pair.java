package me.huanmeng.gui.util;

/**
 * A simple generic container class that holds two related objects.
 * <p>
 * Pair is a utility class for grouping two values together without creating
 * a dedicated class. It is commonly used when a method needs to return two
 * values or when storing key-value associations.
 *
 * @param <A> the type of the first element
 * @param <B> the type of the second element
 * @author huanmeng_qwq
 * @since 2.1.1
 */
public class Pair<A, B> {
    /**
     * The first element of the pair.
     */
    private A a;

    /**
     * The second element of the pair.
     */
    private B b;

    /**
     * Creates a new Pair with the specified elements.
     *
     * @param a the first element
     * @param b the second element
     */
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Gets the first element of the pair.
     *
     * @return the first element
     */
    public A getA() {
        return a;
    }

    /**
     * Sets the first element of the pair.
     *
     * @param a the new value for the first element
     */
    public void setA(A a) {
        this.a = a;
    }

    /**
     * Gets the second element of the pair.
     *
     * @return the second element
     */
    public B getB() {
        return b;
    }

    /**
     * Sets the second element of the pair.
     *
     * @param b the new value for the second element
     */
    public void setB(B b) {
        this.b = b;
    }
}
