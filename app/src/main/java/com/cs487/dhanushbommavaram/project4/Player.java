package com.cs487.dhanushbommavaram.project4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cs487.dhanushbommavaram.project4.Microgolf.Response;

//abstract base class to represent a player, implement with strategies to use
abstract class Player extends Thread {
    public Handler mHandler;    //thread's handler
    private final Handler uiHandler;    //uiHandler to interact with MainActivity
    Player(Handler uiHandler) {
        this.uiHandler=uiHandler;   //initialize uiHandler
    }

    public void run() {
        Looper.prepare();       //prepare looper

        //create a handler for the player
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //response received from MainThread in the form of what parameter of Message
                int responseValue=msg.what;
                Response response= Response.values()[responseValue];    //get the corresponding response from enum
                updateResponse(response);   //ask thread to update it's information based on response
            }
        };

        Looper.loop();  //start the looper and wait for messages
    }

    abstract void nextMove();   //method indicate it's player's turn to make a move
    abstract void updateResponse(Response response);    //method to update information based on response to help make better next moves

    //method to play a hole and communicate it to MainThread via uiHandler
    void playHole(int hole){
        uiHandler.sendEmptyMessage(hole);
    }

    //method to stop listening to messages in message queue
    void stopMoves() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);  //clear all messages in queue
            Looper looper = mHandler.getLooper();
            looper.quit();  //stop the looper
        }
    }
}


