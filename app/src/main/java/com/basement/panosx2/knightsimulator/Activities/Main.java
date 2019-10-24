package com.basement.panosx2.knightsimulator.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basement.panosx2.knightsimulator.R;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    private static final String TAG = "Main";

    private static Context context;

    private static ImageView start = null, end = null;

    private TextView solution;

    private Button find, reset, see_all;

    private static final String ids[][] = {
            {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"},
            {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"},
            {"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3"},
            {"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4"},
            {"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5"},
            {"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6"},
            {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"},
            {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"}
    };

    private int startPositionX, startPositionY, endPositionX, endPositionY;

    private static boolean solutionExist = false;
    private String solutions = "";
    private ImageView move2view, move3view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = getApplicationContext();

        find = findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start != null & end != null) runAlgorithm();
            }
        });

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearChessboard();
            }
        });

        solution = findViewById(R.id.solution);

        see_all = findViewById(R.id.see_all);
        see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Main.this).create();
                alertDialog.setTitle("All Solutions");
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.setMessage(""+solutions);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    public void boardItemClicked(View view) {
        if (start == null) {
            start = (ImageView) view;
            start.setImageResource(R.drawable.knight);
        }
        else if (end == null) {
            end = (ImageView) view;
            end.setImageResource(R.drawable.pawn);
        }
        else {
            clearChessboard();
            boardItemClicked(view);
        }
    }

    private void clearChessboard() {
        solutions = "";
        solutionExist = false;
        solution.setVisibility(View.INVISIBLE);
        see_all.setVisibility(View.INVISIBLE);
        if (start != null) start.setImageResource(android.R.color.transparent);
        if (end != null) end.setImageResource(android.R.color.transparent);
        start = end = null;
    }

    private void runAlgorithm() {
        findPositions();

        List<int[]> first_moves = findMoves(startPositionX, startPositionY);
        List<int[]> second_moves = new ArrayList<>();
        List<int[]> third_moves = new ArrayList<>();

        int count = 0;
        for (int[] move1 : first_moves) {
            second_moves = findMoves(move1[0], move1[1]);
            for (final int[] move2 : second_moves) {
                third_moves = findMoves(move2[0], move2[1]);
                for (final int[] move3 : third_moves) {
                    if (move3[0] == endPositionX && move3[1] == endPositionY) {
                        if (count == 0) solutionExist = true;
                        count++;
                        Log.d(TAG, "SOLUTION " + count);
                        Log.d(TAG, "first move going to: (" + move1[0] + "," + move1[1] + ")");
                        Log.d(TAG, "second move going to: (" + move2[0]  + "," + move2[1] + ")");
                        Log.d(TAG, "third move eats pawn: (" + move3[0]  + "," + move3[1] + ")");

                        solutions += "Solution "+count+":\n" +
                                ids[startPositionX][startPositionY] + "-->" + ids[move1[0]][move1[1]] +
                                "-->" + ids[move2[0]][move2[1]] + "-->" + ids[move3[0]][move3[1]] + "\n\n";

                        /*
                        solution.setVisibility(View.VISIBLE);
                        see_all.setVisibility(View.VISIBLE);

                        //display solutions
                        solution.setText("Solution " + count);
                        start.setImageResource(android.R.color.transparent);
                        final ImageView move1view = findSquare(move1[0], move1[1]);
                        move1view.setImageResource(R.drawable.knight);

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                move1view.setImageResource(android.R.color.transparent);
                                move2view = findSquare(move2[0], move2[1]);
                                move2view.setImageResource(R.drawable.knight);
                            }
                        }, 3000);

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                move2view.setImageResource(android.R.color.transparent);
                                move3view = findSquare(move3[0], move3[1]);
                                move3view.setImageResource(R.drawable.knight);
                            }
                        }, 3000);

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                move3view.setImageResource(android.R.color.transparent);
                                start.setImageResource(R.drawable.knight);
                                end.setImageResource(R.drawable.pawn);
                            }
                        }, 3000);
                         */
                    }
                }
            }
        }


        if (solutionExist) see_all.performClick();
        else Toast.makeText(context, "No Solutions Found!", Toast.LENGTH_SHORT).show();
    }

    private void findPositions() {
        int x = -1, y = -1;
        for (String[] row : ids) {
            x++;
            for (String item : row) {
                if (y < 7) y++;
                else y = 0;
                if (start.getTag().equals(item)) {
                    startPositionX = x;
                    startPositionY = y;
                    break;
                }
            }
        }

        Log.d(TAG, "start position = ( " + startPositionX + " , " + startPositionY + " )");

        x = y = -1;
        for (String[] row : ids) {
            x++;
            for (String item : row) {
                if (y < 7) y++;
                else y = 0;
                if (end.getTag().equals(item)) {
                    endPositionX = x;
                    endPositionY = y;
                    break;
                }
            }
        }

        Log.d(TAG, "end position = ( " + endPositionX + " , " + endPositionY + " )");
    }

    private List<int[]>findMoves(int x, int y) {
        List<int[]> moves = new ArrayList<>();
        int[] temp = new int[2];

        if ((x + 1 >= 0 && y + 2 <= 7) && (y + 2 >= 0 && x + 1 <= 7)) {
            temp[0] = x + 1;
            temp[1] = y + 2;
            moves.add(temp);
        }
        temp = new int[2];

        if ((x + 1 >= 0 && y - 2 <= 7) && (y - 2 >= 0 && x + 1 <= 7)) {
            temp[0] = x + 1;
            temp[1] = y - 2;
            moves.add(temp);
        }
        temp = new int[2];

        if ((x - 1 >= 0 && y + 2 <= 7) && (y + 2 >= 0 && x - 1 <= 7)) {
            temp[0] = x - 1;
            temp[1] = y + 2;
            moves.add(temp);
        }
        temp = new int[2];

        if ((x - 1 >= 0 && y - 2 <= 7) && (y - 2 >= 0 && x - 1 <= 7)) {
            temp[0] = x - 1;
            temp[1] = y - 2;
            moves.add(temp);
        }
        temp = new int[2];

        if ((x + 2 >= 0 && y + 1 <= 7) && (y + 1 >= 0 && x + 2 <= 7)) {
            temp[0] = x + 2;
            temp[1] = y + 1;
            moves.add(temp);
        }
        temp = new int[2];

        if ((x + 2 >= 0 && y - 1 <= 7) && (y - 1 >= 0 && x + 2 <= 7)) {
            temp[0] = x + 2;
            temp[1] = y - 1;
            moves.add(temp);
        }
        temp = new int[2];

        if ((x - 2 >= 0 && y + 1 <= 7) && (y + 1 >= 0 && x - 2 <= 7)) {
            temp[0] = x - 2;
            temp[1] = y + 1;
            moves.add(temp);
        }
        temp = new int[2];

        if ((x - 2 >= 0 && y - 1 <= 7) && (y - 1 >= 0 && x - 2 <= 7)) {
            temp[0] = x - 2;
            temp[1] = y - 1;
            moves.add(temp);
        }

        return  moves;
    }

    private ImageView findSquare(int x, int y) {
        String tag = ids[x][y];
        View rootView = start.getRootView();

        return rootView.findViewWithTag(tag);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(context, "No exit for you. You're trapped.", Toast.LENGTH_SHORT);
    }
}
