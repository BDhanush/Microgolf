package com.cs487.dhanushbommavaram.project4;

import static com.cs487.dhanushbommavaram.project4.Microgolf.groupSize;
import static com.cs487.dhanushbommavaram.project4.Microgolf.nHoles;

import static java.lang.Math.abs;
import static java.util.Collections.binarySearch;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.cs487.dhanushbommavaram.project4.Microgolf.Response;

import java.util.ArrayList;
import java.util.Random;

/*
Mixture of Strategy1 and Strategy2 to play Microgolf:
1. Have a list of all available holes.
2. a) If previously we didn't have all big hits, then randomly choose hole from available,
else b) hit groups with difference 3 starting from group 1 (1,4,7...). Then:
        (i) If near miss, update available holes to be holes from this group only.
        (ii) If near group, update available holes to be only holes from adjacent (current group - 1 and current group + 1) groups.
        (iii) If jackpot or catastrophe, stop.
        (iv) If big miss, then remove all holes from current and adjacent groups (current group, current group- 1 and current group+ 1).
3. Remove the picked hole from the list, if still present.
4. Repeat 2, 3 until game ends.
 */
class Strategy1_2 extends Player{
    ArrayList<Integer> options=new ArrayList<>(nHoles);
    int curGroup=-2;
    int curHole=-1;
    boolean clue=false;     //to indicate if we have a clue where the winning hole is (if we hit near miss or near group sometime)
    Strategy1_2(Handler uiHandler) {
        super(uiHandler);
        // 1.
        for(int i=0;i<nHoles;i++){
            options.add(i);
        }
    }

    @Override
    void nextMove() {
        //sleep for 2 seconds
        SystemClock.sleep(2000);
//        try {
//            sleep(2000);
//        } catch (InterruptedException e) {
//            Log.e("Strategy2",e.getMessage());
//        }

        if(!clue && curGroup+3<nHoles/groupSize){
            //next group, 2. b)
            curGroup+=3;
            curHole = groupSize*curGroup;
            options.remove(lowerBound(options,curHole));
        }else {
            // 2. a)
            Random random = new Random();
            int in = random.nextInt(options.size());
            curHole = options.get(in);
            options.remove(in);
        }
        if(curHole<nHoles)
            playHole(curHole);  //play the selected hole
    }

    @Override
    void updateResponse(Response response) {
        // (iii)
        if(response==Response.JACKPOT || response==Response.CATASTROPHE) {
            stopMoves();
        }
        if(curHole==-1 || response==Response.INVALID){
            return;     //if invalid response, ignore
        }
        int curGroup=curHole/groupSize; // group no. of the hole
        ArrayList<Integer> newOptions=new ArrayList<>();    //list of next available holes
        if(response==Response.NEAR_MISS){
            clue=true;
            // (i)
            int startIndex=lowerBound(options,curGroup*groupSize);
            for(int i=startIndex;i<options.size() && options.get(i)<(curGroup+1)*groupSize;i++){
                newOptions.add(options.get(i));
            }
        }else if(response==Response.NEAR_GROUP){
            clue=true;
            // (ii)
            int startIndex=lowerBound(options,(curGroup-1)*groupSize);
            for(int i=startIndex;i<options.size() && options.get(i)<curGroup*groupSize;i++){
                newOptions.add(options.get(i));
            }
            startIndex=lowerBound(options,(curGroup+1)*groupSize);
            for(int i=startIndex;i<options.size() && options.get(i)<(curGroup+2)*groupSize;i++){
                newOptions.add(options.get(i));
            }
        }else if(response==Response.BIG_MISS){
            // (iv)
            int end=lowerBound(options,(curGroup-1)*groupSize);
            for(int i=0;i<end;i++){
                newOptions.add(options.get(i));
            }
            int start=lowerBound(options,(curGroup+2)*groupSize);
            for(int i=start;i<options.size();i++){
                newOptions.add(options.get(i));
            }
//            for(int i=0;i<options.size();i++){
//                if(abs(curGroup-options.get(i)/groupSize)>1)
//                    newOptions.add(options.get(i));
//            }
        }
        options=newOptions; //update available holes
    }

    //returns index of first element in ar greater than or equal to x, if ar contains unique elements sorted in non-decreasing order
    int lowerBound(ArrayList<Integer> ar,int x){
        int in=binarySearch(ar,x);
        //if element is not present, in = (-(insertion point) - 1). Assign in=insertion point
        if(in<0){
            in=-in-1;
        }
        return in;
    }
}
