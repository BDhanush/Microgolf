package com.cs487.dhanushbommavaram.project4;

import static com.cs487.dhanushbommavaram.project4.Microgolf.nHoles;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.cs487.dhanushbommavaram.project4.Microgolf.Response;

/*
Strategy3 to play Microgolf:
1. Hit holes in reverse order starting from hole n-1 to 0, where n is the number of holes.
 */
class Strategy3 extends Player{
    int curHole=nHoles;
    Strategy3(Handler uiHandler) {
        super(uiHandler);
    }
    @Override
    void nextMove() {

        //sleep for 2 seconds
        SystemClock.sleep(2000);
//        try {
//            sleep(2000);
//        } catch (InterruptedException e) {
//            Log.e("Strategy3",e.getMessage());
//        }

        // 1.
        //hit next hole in reverse order.
        curHole--;
        if(curHole>=0) {
            playHole(curHole);
        }
    }

    @Override
    void updateResponse(Response response) {
        //stop communication with MainThread if jackpot or catastrophe
        if(response==Response.JACKPOT || response==Response.CATASTROPHE) {
            stopMoves();
        }
    }
}
