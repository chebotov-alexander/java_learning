package org.example.java_learning;


class PairDistinct<X, Y>  {
    private X first;
    private Y second;
    public PairDistinct(X a1, Y a2) {
        first  = a1;
        second = a2;
    }
    public X getFirst()  { return first; }
    public Y getSecond() { return second; }
    public void setFirst(X arg)  { first = arg; }
    public void setSecond(Y arg) { second = arg; }
}

