package org.example.java_learning;

public class Tree {
    String key;
    int val;
    Tree left, right;

    public Tree(String k, int v, Tree l, Tree r) {
        key = k;
        val = v;
        left = l;
        right = r;
    }

    public static int lookup(String k, int defaultval, Tree t) {
        if (t == null) { return defaultval; }
        if (k.equals(t.key)) { return t.val; }
        return lookup(k, defaultval, k.compareTo(t.key) < 0 ? t.left : t.right);
    }
}
