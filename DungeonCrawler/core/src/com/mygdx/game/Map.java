package com.mygdx.game;

import java.util.Vector;

public class Map {

    static Node[][] nodeMap = new Node[16][16];

    public Map(){
        for(int y =0; y<16; y++){
            for (int x = 0; x<16; x++){
                if (x == 0 || x == 15 || y == 0 || y == 12 || y == 15) nodeMap[x][y] = new Node(x, 15-y, 1);
                else nodeMap[x][y] = new Node(x, 15-y, 0);
            }
        }
    }
}
