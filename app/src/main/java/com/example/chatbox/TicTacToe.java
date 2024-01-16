package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.CameraExtensionSession;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicTacToe extends AppCompatActivity {
    private LinearLayout player1Layout,player2Layout;
    private ProgressBar progressBar;
    private ImageView image1,image2,image3,image4,image5,image6,image7,image8,image9;
    private TextView player1TV,player2TV;

    private String playerUniqueId="0"; // player unique ID

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatbox-3d93b-default-rtdb.firebaseio.com/");  //getting firebase database reference from url

    private boolean opponentFound = false;       // true when opponent will be found to play the game
    private  final String [] boxesSelectedBy = {"","","","","","","","",""} ;  //selected boxes by players empty field will be replaced by player ids.
    private String opponentUniqueID="0";        // unique id of the opponent
    private String playerTurn = "";            // player turn
    private String connectionId= "";          // connection id oin which player has joined to play the game
    private ValueEventListener turnsEventListener,wonEventListener;   //generating valueEventLister for firebase database. turnsEvenetListener listen if players turns is exchanged and wonEventlistener listen if player won .
    private String status = "matching";// values must be matching or waiting . when a user create a new room and he is waiting for other to join then the value will be waiting.
    private final List<int[]> combinationsList=new ArrayList<>();    //winning combinations
    private  final List<String> doneBoxes= new ArrayList<>();   //clicked boxes positions by users so users won't select the box again
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        player1TV=findViewById(R.id.player1TV);
        player2TV=findViewById(R.id.player2TV);
        player1Layout=findViewById(R.id.player1Layout);
        player2Layout=findViewById(R.id.player2Layout);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        image4=findViewById(R.id.image4);
        image5=findViewById(R.id.image5);
        image6=findViewById(R.id.image6);
        image7=findViewById(R.id.image7);
        image8=findViewById(R.id.image8);
        image9=findViewById(R.id.image9);
        progressBar = findViewById(R.id.progressBar);

        //getting the code form chat list activity.
        final String getPlayerName = getIntent().getStringExtra("username");

        //generating winning combinations
        combinationsList.add(new int[]{0,1,2});
        combinationsList.add(new int[]{3,4,5});
        combinationsList.add(new int[]{6,7,8});
        combinationsList.add(new int[]{0,3,6});
        combinationsList.add(new int[]{1,4,7});
        combinationsList.add(new int[]{2,5,8});
        combinationsList.add(new int[]{0,4,8});
        combinationsList.add(new int[]{2,4,6});

        //showing progress dialog while waiting for opponent
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Waiting for the opponent");

        // Use AlertDialog.Builder to create a custom dialog with a cancel button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Waiting for the opponent")
                .setCancelable(false)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Cancel the ProgressDialog
                    progressDialog.dismiss();
                    deleteConnection();

//                    // Redirect to ChatsListActivity
//                    Intent intent = new Intent(TicTacToe.this, ChatsListActivity.class);
//                    startActivity(intent);
//                    finish();
                });

        // Show the AlertDialog instead of ProgressDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

       // Now, you can still use progressDialog.dismiss() to dismiss the dialog when needed.


        //generating player unique id player will be identified by this id
        playerUniqueId = String.valueOf(System.currentTimeMillis());


        //setting player name to the Textview
        player1TV.setText(getPlayerName);


//        if(getPlayerName.equals(player2TV.getText().toString()))
//        {
//            Intent intent = new Intent(TicTacToe.this, ChatsListActivity.class);
//            startActivity(intent);
//            finish();
//        }

        databaseReference.child("connections").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if opponent found or not. If not then look for the opponent
                if(!opponentFound)
                {
                    // checking if there are others  in the firebase realtime database
                    if(snapshot.hasChildren())
                    {
                        //checking all connections if other users are also waiting for a user id to play  match
                        for(DataSnapshot connections : snapshot.getChildren()){

                            //getting connection unique id
                            String conId = connections.getKey();

                            //2 players are required to play the game.
                            // if getPlayersCount is 1 means other player is waiting for a opponent to play the game.
                            // else if getPlayersCount is 2 it means this connection has complete with 2 players.
                            int getPlayersCount = (int)connections.getChildrenCount();

                            // after created a ne connection waiting for other to join
                            if(status.equals("waiting"))
                            {
                                //if getPlayerCount is 2 means other player joined the match
                                if(getPlayersCount==2)
                                {
                                    playerTurn = playerUniqueId;
                                    applyPlayerTurn(playerTurn);

                                    // this will be true when opponentFound to play the match
                                    boolean playerFound = false;

                                    //getting players in connection
                                    for(DataSnapshot players : connections.getChildren())
                                    {

                                        String getPlayerUniqueId = players.getKey();


                                        //check if player id match with user ho created connnection (this user)
                                        // if match the get opponent details
                                        if(getPlayerUniqueId.equals(playerUniqueId))
                                        {
                                            playerFound = true;
                                        }
                                        else if(playerFound)
                                        {
                                            String getOpponentPlayerName = players.child("player_name").getValue(String.class);
                                            opponentUniqueID = players.getKey();

                                            // set opponent player name to the TextView
                                            player2TV.setText(getOpponentPlayerName);

                                            //assigning connection id
                                            connectionId = conId;

                                            opponentFound=true;

                                            // adding turns listener and won listener to the database reference
                                            databaseReference.child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                            databaseReference.child("won").child(connectionId).addValueEventListener(wonEventListener);

                                            //hide progress dialog if showing
                                            if(progressDialog.isShowing())
                                            {
                                                progressDialog.dismiss();
                                            }
                                            if(alertDialog.isShowing())
                                            {
                                                alertDialog.dismiss();
                                            }
                                            progressBar.setVisibility(ProgressBar.GONE);
                                            // once the connection has made then remove  connectionListener form the database Reference
                                            databaseReference.child("connections").removeEventListener(this);
                                        }
                                    }
                                }
                            }

                            // in case user has not created the connection because fo other rooms are available to join
                            else {
                                //checking if the connection has 1 player and need 1 more player to play the match then join this connection
                                if(getPlayersCount == 1 ){

                                    //add player to the connection
                                    connections.child(playerUniqueId).child("player_name").getRef().setValue(getPlayerName);

                                    //getting both players
                                    for(DataSnapshot players : connections.getChildren()){

                                        String getOpponentName = players.child("player_name").getValue(String.class);
                                        opponentUniqueID = players.getKey();

                                        // first turn will be of who created the connection or room
                                        playerTurn = opponentUniqueID;

                                        applyPlayerTurn(playerTurn);
                                        //setting playerName to the Textview
                                        player2TV.setText(getOpponentName);

                                        //assigning connection id.
                                        connectionId = conId;
                                        opponentFound = true;

                                        // adding turns listener and won listener to the database reference
                                        databaseReference.child("turns").child(connectionId).addValueEventListener(turnsEventListener);
                                        databaseReference.child("won").child(connectionId).addValueEventListener(wonEventListener);

                                        //hide progress dialog if showing
                                        if(progressDialog.isShowing())
                                        {
                                            progressDialog.dismiss();
                                        }
                                        if(alertDialog.isShowing())
                                        {
                                            alertDialog.dismiss();
                                        }
                                        // once the connection has made then remove  connectionListener form the database Reference
                                        databaseReference.child("connections").removeEventListener(this);
                                        progressBar.setVisibility(ProgressBar.GONE);
                                        break;
                                    }
                                }
                            }
                        }

                        // check if opponent is not found and user is not waiting for the opponent anymore then create a new connection
                        if(!opponentFound && !status.equals("waiting")){
                            //generating unique id for the connection
                            String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                            //adding first player to the connection and waiting for the other to complete the connection and play the game
                            snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(getPlayerName);

                            status = "waiting";
                        }
                    }
                    //if there is no connection available in the firebase database then create a new connection.
                    //if is like creating a room and waiting for others player to join the room.
                    else {
                        //generating unique id for the connection
                        String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                        //adding first player to the connection and waiting for the other to complete the connection and play the game
                        snapshot.child(connectionUniqueId).child(playerUniqueId).child("player_name").getRef().setValue(getPlayerName);

                        status = "waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        turnsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getting all turns of the connection
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.getChildrenCount() == 2 ){
                        //getting boxPosition selected by the user
                        final int getBoxPosition = Integer.parseInt(dataSnapshot.child("box_position").getValue(String.class));
                        //getting player id who selected the box
                        final String getPlayerId = dataSnapshot.child("player_id").getValue(String.class);
                        // checking if user has not selected the box before
                        if(!doneBoxes.contains(String.valueOf(getBoxPosition)))
                        {
                            doneBoxes.add(String.valueOf(getBoxPosition));

                            if(getBoxPosition == 1 )
                            {
                                selectBox(image1,getBoxPosition,getPlayerId);
                            }
                            else if (getBoxPosition==2) {
                                selectBox(image2,getBoxPosition,getPlayerId);

                            }
                            else if (getBoxPosition==3) {
                                selectBox(image3,getBoxPosition,getPlayerId);

                            }
                            else if (getBoxPosition==4) {
                                selectBox(image4,getBoxPosition,getPlayerId);

                            }
                            else if (getBoxPosition==5) {
                                selectBox(image5,getBoxPosition,getPlayerId);

                            }
                            else if (getBoxPosition==6) {
                                selectBox(image6,getBoxPosition,getPlayerId);

                            }
                            else if (getBoxPosition==7) {
                                selectBox(image7,getBoxPosition,getPlayerId);

                            }
                            else if (getBoxPosition==8) {
                                selectBox(image8,getBoxPosition,getPlayerId);

                            }
                            else if (getBoxPosition==9) {
                                selectBox(image9,getBoxPosition,getPlayerId);

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        wonEventListener = new ValueEventListener() {
            WinDialog winDialog = null;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if a user has won the match
                if (snapshot.hasChild("player_id")) {
                    String getWinPlayerId = snapshot.child("player_id").getValue(String.class);

                    if (getWinPlayerId.equals(playerUniqueId)) {
                        // show win dialog
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TicTacToe.this, "You won the game", Toast.LENGTH_SHORT).show();
                                winDialog = new WinDialog(TicTacToe.this, "You won the game");
                                winDialog.setCancelable(false);
                                winDialog.show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TicTacToe.this, "Opponent won the game", Toast.LENGTH_SHORT).show();
                                winDialog = new WinDialog(TicTacToe.this, "Opponent won the game");
                                winDialog.setCancelable(false);
                                winDialog.show();
                            }
                        });
                    }

                    // remove listeners from the database
                    databaseReference.child("turns").child(connectionId).removeEventListener(turnsEventListener);
                    databaseReference.child("won").child(connectionId).removeEventListener(wonEventListener);

                    // Use winDialog as needed outside the if conditions
                    // For example, you can check if winDialog is not null before using it.
                    // If it's null, it means no win dialog was created.
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {
                // Empty implementation or handle the onCancelled event if needed
            }
        };


        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image1, 1);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image2, 2);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image3, 3);
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image4, 4);
            }
        });

        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image5, 5);
            }
        });

        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image6, 6);
            }
        });

        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image7, 7);
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image8, 8);
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoxClick(image9, 9);
            }
        });
    }
    private void applyPlayerTurn(String playerUniqueId2)
    {
        if(playerUniqueId2.equals(playerUniqueId))
        {
            player1Layout.setBackgroundResource(R.drawable.layoutborder_turn);
            player2Layout.setBackgroundResource(R.drawable.corner_radius_edittext_border);
        }
        else{
            player1Layout.setBackgroundResource(R.drawable.corner_radius_edittext_border);
            player2Layout.setBackgroundResource(R.drawable.layoutborder_turn);
        }
    }
    private void selectBox(ImageView imageView, int selectedBoxPosition, String selectedByPlayer) {
        boxesSelectedBy[selectedBoxPosition - 1] = selectedByPlayer;

        if (selectedByPlayer.equals(playerUniqueId)) {
            imageView.setImageResource(R.drawable.cross_icon);
            playerTurn = opponentUniqueID;
        } else {
            imageView.setImageResource(R.drawable.zero_icon);
            playerTurn = playerUniqueId;
        }

        applyPlayerTurn(playerTurn);

        // checking whether player has won the match
        if (checkPlayerWin(selectedByPlayer)) {
            // sending won player unique id to firebase so opponent can be notified
            databaseReference.child("won").child(connectionId).child("player_id").setValue(selectedByPlayer);
        }

        // over the game if there are no boxes left to be selected
        if (doneBoxes.size() == 9) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TicTacToe.this, "It's a draw", Toast.LENGTH_SHORT).show();
                    final WinDialog winDialog = new WinDialog(TicTacToe.this, "It's a Draw");
                    winDialog.setCancelable(false);
                    winDialog.show();
                }
            });
        }
    }


    private boolean checkPlayerWin(String playerId) {
        boolean isPlayerWon = false;

        for (int[] combination : combinationsList) {
            int pos1 = combination[0];
            int pos2 = combination[1];
            int pos3 = combination[2];

            if (boxesSelectedBy[pos1].equals(playerId) && boxesSelectedBy[pos2].equals(playerId) && boxesSelectedBy[pos3].equals(playerId)) {
                isPlayerWon = true;
                break;
            }
        }

        return isPlayerWon;
    }
    private void handleBoxClick(ImageView imageView, int boxPosition) {
        if (!doneBoxes.contains(String.valueOf(boxPosition)) && playerTurn.equals(playerUniqueId)) {
            imageView.setImageResource(R.drawable.cross_icon);
            databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("box_position").setValue(String.valueOf(boxPosition));
            databaseReference.child("turns").child(connectionId).child(String.valueOf(doneBoxes.size() + 1)).child("player_id").setValue(playerUniqueId);
            playerTurn = opponentUniqueID;
            applyPlayerTurn(playerTurn);
        }
    }
    private void deleteConnection() {
        // Check if connectionId is not empty
            // Get the reference to the "connections" node
            DatabaseReference connectionsRef = databaseReference.child("connections");

            // Remove the connection with the specified connectionId
            connectionsRef.child(connectionId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Connection deleted successfully
                        Log.d("TicTacToe", "Connection deleted successfully");

                        // Redirect to ChatsListActivity
                        Intent intent = new Intent(TicTacToe.this, ChatsListActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Error deleting connection
                        Log.e("TicTacToe", "Error deleting connection", e);
                        // Handle the error or show a message to the user
                        Toast.makeText(TicTacToe.this, "Error deleting connection", Toast.LENGTH_SHORT).show();
                    });
    }
}

