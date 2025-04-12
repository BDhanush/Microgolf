package com.cs487.dhanushbommavaram.project4;

import static android.os.SystemClock.sleep;
import static com.cs487.dhanushbommavaram.project4.Microgolf.nPlayers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cs487.dhanushbommavaram.project4.databinding.ActivityMainBinding;
import com.cs487.dhanushbommavaram.project4.Microgolf.Response;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;    //for view binding
    Microgolf game;             //to hold the game
    Player[] players=new Player[nPlayers];  //player threads
    Handler uiHandler;      //uiHandler to pass to players

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        game=new Microgolf(binding.playGround);         //initialize game

        //uiHandler
        uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(game==null){
//                    Log.i("After Reset",msg.what+"");
                    return;             //game is non existent/killed for reset, ignore messages
                }

                //Player threads communicate their choice of the hole using what parameter of Message they queue
                int hole = msg.what;
                Response response = game.move(hole, game.turn);
                Log.i("Player"+game.turn, "Response: " + hole + " " + response.toString());
                updateInfo(game.turn, hole, response);  //update dashboard ui elements
                //send response of the move to the player who made the move via what parameter of Message
                players[game.turn].mHandler.sendEmptyMessage(response.ordinal());
                if (response == Response.JACKPOT || response == Response.CATASTROPHE) {     //game ends
                    this.removeCallbacksAndMessages(null);      //clear all messages in queue
                    return;
                }

                game.turn=(game.turn+1)%nPlayers;           //update turn to next player
                //queue a runnable to for the player to pick a hole
                players[game.turn].mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        players[game.turn].nextMove();
                    }
                });
            }
        };

        //initialize players
        players[0]=new Strategy1_2(uiHandler);
        players[1]=new Strategy2(uiHandler);

        for(int i=0;i<nPlayers;i++) {
            players[i].start();     //start the thread
        }

        sleep(100);   //give time for handlers in players to be initialized
        //queue a runnable to for player 0 to make the first move
        players[0].mHandler.post(new Runnable() {
            @Override
            public void run() {
                players[0].nextMove();
            }
        });

        binding.dashboard.reset.setOnClickListener(v -> reset());   //reset button

    }
    void updateInfo(int turn,int hole,Response response) {
        //show last hole stats section if invisible(default, before any moves)
        if(binding.dashboard.last.getVisibility()==View.INVISIBLE){
            binding.dashboard.last.setVisibility(View.VISIBLE);
        }
        //update last hole stats
        binding.dashboard.turn.setText(String.format(getString(R.string.playerTurn),turn));
        binding.dashboard.holeChosen.setText(String.format(getString(R.string.holeChosen),hole));
        binding.dashboard.response.setText(response.toString());

        if(response==Response.JACKPOT || response==Response.CATASTROPHE) {  //game ends
            int winner;
            if(response==Response.CATASTROPHE)
            {
                winner=(turn+1)%nPlayers; //if response is catastrophe, winner is the other player
            }else{
                winner=turn;    //if response is jackpot, winner is the current player
            }
            //show winner section and update it's text
            binding.dashboard.winner.setVisibility(View.VISIBLE);
            String winnerString=String.format(getString(R.string.winnerInfo),winner);
            binding.dashboard.winner.setText(winnerString);
            //show a dialog box with winner info
            new MaterialAlertDialogBuilder(this)
                    .setTitle(response.toString())
                    .setMessage(winnerString)
                    .show();

        }
    }
    void reset() {
        for(int i=0;i<players.length;i++){
            players[i].stopMoves(); //clear player message queue
            players[i]=null;    //kill player thread
        }
        uiHandler=null;     //remove uiHandler
        game=null;      //end game
        //start new game by creating new Activity and ending old Activity
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}