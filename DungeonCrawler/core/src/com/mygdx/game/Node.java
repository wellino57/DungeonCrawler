package com.mygdx.game;

public class Node {
    int x;
    int y;
    int value;
    int g = (int) Double.POSITIVE_INFINITY;
    int h;
    int f;
    Boolean start = false;
    Boolean end = false;
    Boolean inOpen = false;
    Boolean inClosed = false;

    Node cameFrom = null;

    public Node(int xIn, int yIn, int valueIn){
        x = xIn;
        y = yIn;
        value = valueIn;
    }

}
