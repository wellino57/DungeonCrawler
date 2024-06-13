package com.mygdx.game;

import java.util.Random;
import java.util.Vector;

public class Map {

    static Node[][] nodeMap = new Node[16][16];

    public Map(){
        for(int y =0; y<16; y++){
            for (int x = 0; x<16; x++){
                if (x == 0 || x == 15 || y == 0 || y == 15) nodeMap[x][y] = new Node(x, 15-y, 1);
                else nodeMap[x][y] = new Node(x, 15-y, 0);
            }
        }

        for (int i=0; i<12;i++){
            GenerateWall();
        }
    }

    public void GenerateWall(){
        Random random = new Random();
        int randX = random.nextInt(13)+1;
        int randY = random.nextInt(13)+1;
        int ver = random.nextInt(20);
        if (ver%2==0){
            nodeMap[randX][randY] = new Node(randX, 15-randY, 1);
            nodeMap[randX][randY-1] = new Node(randX, 15-(randY-1), 1);
            nodeMap[randX][randY+1] = new Node(randX, 15-(randY+1), 1);
        }else{
            nodeMap[randX][randY] = new Node(randX, 15-randY, 1);
            nodeMap[randX-1][randY] = new Node(randX+1, 15-randY, 1);
            nodeMap[randX+1][randY] = new Node(randX-1, 15-randY, 1);
        }

    }
}
