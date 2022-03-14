package com.ebicep.warlords.util.java;

import java.util.Objects;

/**
 * This class allows you to keep a pair of items.
 * @param <A> Type of the first type
 * @param <B> Type of the second type
 */
public class Pair<A, B> {

    private A a;
    private B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public void setA(A a) {
        this.a = a;
    }

    public void setB(B b) {
        this.b = b;
    }

    @Override
    public int hashCode() {
        return 0xffffffff ^ Objects.hashCode(this.a) ^ Integer.reverse(Objects.hashCode(this.b));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        return Objects.equals(this.b, other.b);
    }

    @Override
    public String toString() {
        return "Pair{" + a + ", " + b + '}';
    }

}
