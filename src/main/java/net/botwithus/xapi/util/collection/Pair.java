package net.botwithus.xapi.util.collection;

import java.util.Arrays;
import java.util.Objects;

public class Pair<L, R> {
    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public final L getLeft() {
        return this.left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public final R getRight() {
        return this.right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public int hashCode() {
        int code = this.left != null ? this.left.hashCode() : 0;
        return 31 * code + (this.right != null ? this.right.hashCode() : 0);
    }

    public boolean equals(Object o) {
        if (o instanceof Pair pair) {
            return Objects.deepEquals(this.left, pair.left) && Objects.deepEquals(this.right, pair.right);
        } else {
            return false;
        }
    }

    public String toString() {
        Object obj;
        Pair pair;
        if (this.left instanceof Object[]) {
            obj = Arrays.deepToString((Object[]) this.left);
            pair = this;
        } else {
            obj = this.left;
            pair = this;
        }

        return "Pair(" + obj + ", " + (pair.right instanceof Object[] ? Arrays.deepToString((Object[]) this.right) : this.right) + ")";
    }
}