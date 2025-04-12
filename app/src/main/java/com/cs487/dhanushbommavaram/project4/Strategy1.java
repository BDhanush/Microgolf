package com.cs487.dhanushbommavaram.project4;

import static com.cs487.dhanushbommavaram.project4.Microgolf.groupSize;
import static com.cs487.dhanushbommavaram.project4.Microgolf.nHoles;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.cs487.dhanushbommavaram.project4.Microgolf.Response;

/*
Strategy1 to play Microgolf:
1. Hit alternate groups starting from group 0:
    (i) If near miss, hit all holes in the group one by one.
    (ii) If near group, hit all holes in the next group one by one.
    (iii) If jackpot or catastrophe, stop.
    (iv) If big miss repeat with next group.
*/
class Strategy1 extends Player{
    boolean winningGroup=false;
    int curGroup=-2;
    int curHole=-1;
    Strategy1(Handler uiHandler) {
        super(uiHandler);
    }
    @Override
    void nextMove() {
        //sleep for 2 seconds
        SystemClock.sleep(2000);
//        try {
//            sleep(2000);
//        } catch (InterruptedException e) {
//            Log.e("Strategy1",e.getMessage());
//        }

        //if winning group is hit, use (i)
        if(winningGroup) {
            curHole++;
            if(curHole<nHoles) {
                playHole(curHole);
            }
            return;
        }
        //otherwise, uses 1.
        curGroup+=2;
        int hole=groupSize*curGroup;
        if(hole<nHoles)
            playHole(hole);
    }

    @Override
    void updateResponse(Response response) {

        // (iii)
        if(response==Response.JACKPOT || response==Response.CATASTROPHE) {
            stopMoves();
            return;
        }

        if(response==Response.NEAR_MISS){   // (i)
            if(curHole==-1)
                curHole=curGroup*groupSize;
            winningGroup=true;
        }else if(response==Response.NEAR_GROUP){    // (ii)
            curGroup++;
            curHole=curGroup*groupSize-1;
            winningGroup=true;
        }
    }

}
