package com.cs487.dhanushbommavaram.project4;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Random;

//class to represent and handle interactions with the game Microgolf
class Microgolf {
    static final int nPlayers=2;    //no. of players
    static final int nHoles=50;     //no. of holes
    static final int groupSize=5;   //no. of holes in a group
    int turn=0;     //which player's turn it is
    int winningHole;    //jackpot hole
    int[] lastSelected=new int[nPlayers];   //last hole selected by each player
    int[] colors={Color.RED,Color.BLUE};    //color used to indicate each player
    int[] holeStatus=new int[nHoles];   //to keep track of which player has selected which hole
    Hole[] holes=new Hole[nHoles];  //Hole views to store all holes

    Microgolf(LinearLayout linearLayout)
    {
        for(int i=0;i<nHoles;i++)
        {
            //inflate hole view and add to linear layout
            holes[i]=(Hole) LayoutInflater.from(linearLayout.getContext()).inflate(R.layout.hole, linearLayout, false);
            linearLayout.addView(holes[i]);
            //initialize hole status to -1
            holeStatus[i]=-1;
        }

        Arrays.fill(lastSelected, -1);  //initialize lastSelected[i] to -1
//        Arrays.fill(holeStatus, -1);

        //randomly select a winning hole
        Random random = new Random();
        winningHole=random.nextInt(50);
        holes[winningHole].setHoleColor(Color.YELLOW);  //set winning hole color to yellow

    }

    //method to make a move and give a response to a hole selected by a player
    Response move(int hole,int player)
    {
        if(hole>=nHoles || hole<0 || player<0 || player>=nPlayers) {
            return Response.INVALID;    //invalid hole or player
        }

        //jackpot
        if(hole==winningHole) {
            //remove all other colors from holes and only show the color of player who hit the jackpot
            for(int i=0;i<nPlayers;i++){
                if(i==player){
                    holes[hole].setHoleColor(colors[i]);
                }
                if(lastSelected[i]!=-1)
                    holes[lastSelected[i]].setHoleColor(Color.BLACK);
            }
            return Response.JACKPOT;    //player hit the jackpot
        }

        //catastrophe
        if(holeStatus[hole]!=-1 && holeStatus[hole]!=player) {
            //show the first player's color who occupied where catastrophe occurs and clear the hole of current player
            if(lastSelected[player]!=-1)
                holes[lastSelected[player]].setHoleColor(Color.BLACK);
            return Response.CATASTROPHE;    //player hit hole occupied by other player
        }

        int winningGroup=winningHole/groupSize; //group no. of winning hole
        int holeGroup=hole/groupSize; //group no. of current hole

        if(lastSelected[player]!=-1){
            holes[lastSelected[player]].setHoleColor(Color.BLACK);      //update previous hole color of the player
        }
        //update new hole color of the player
        holes[hole].setHoleColor(colors[player]);
        lastSelected[player]=hole;  //update last selected hole for next shot
        holeStatus[hole]=player;   //update hole status of the current hole

        if(holeGroup==winningGroup) {
            return Response.NEAR_MISS;  //player hit the same group as winning hole
        }
        if(Math.abs(holeGroup-winningGroup)==1) {
            return Response.NEAR_GROUP; //player hit the adjacent group as winning hole
        }
        return Response.BIG_MISS;   //player missed the hole by 2 or more groups

    }

    //enum to represent all possible responses for each shot
    enum Response {
        JACKPOT,
        NEAR_MISS,
        NEAR_GROUP,
        BIG_MISS,
        CATASTROPHE,
        INVALID
    }

}