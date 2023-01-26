package com.example.sokoban;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tvStatus;
    Mosaic mosaic = null;
    int stageNum = 1, remain = 0;
    int rows=0, cols=0;
    Mosaic.Card[][] stageCards = null;
    Point pushMan = new Point(-1,-1);
    static String STAGE_KEY = "stage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = findViewById(R.id.tvStatus);
        mosaic = findViewById(R.id.mosaic);
        stageNum = mosaic.getInt(STAGE_KEY, 1);
        initGame(stageNum);
    }

    @Override
    protected void onDestroy() {
        if(mosaic != null)
            mosaic.clearMemory();
        super.onDestroy();
    }

    void initGame(int stageN) {
        int record = mosaic.getInt(STAGE_KEY, 1);
        if(record < stageN)
            mosaic.set(STAGE_KEY, stageN);
        remain = 0;
        int[][] stage = mosaic.assetFileIntArray("stage_" + stageN + ".txt");
        if(stage == null || stage.length < 1) return;
        rows = stage.length; cols = stage[0].length;
        mosaic.deleteAllCards();
        mosaic.setScreenGrid(cols,rows);
        stageCards = new Mosaic.Card[rows][cols];
        for(int y=0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                stageCards[y][x] = mosaic.addCard(R.drawable.img_back, x, y, 1, 1); // 0
                stageCards[y][x].addImage(R.drawable.img_block); // 1
                if(stage[y][x] != 1) {
                    stageCards[y][x].addImage(R.drawable.img_stone); // 2
                    stageCards[y][x].addImage(R.drawable.img_house_empty); // 3
                    stageCards[y][x].addImage(R.drawable.img_house_full); // 4
                    stageCards[y][x].addImage(R.drawable.img_push_man); // 5
                    stageCards[y][x].addImage(R.drawable.img_man_in_house); // 6
                    if(stage[y][x] >= 5) {
                        pushMan.x = x;
                        pushMan.y = y;
                    } else if(stage[y][x] == 2) {
                        remain ++;
                    }
                }
                stageCards[y][x].imageChange(stage[y][x]);
            }
        }
        tvStatus.setText("Stage - " + stageN);
    }

    // User Event start ====================================

    public void onBtnStage(View v) {
        switch(v.getId()) {
            case R.id.btnPrev:
                if(stageNum <= 1) return;
                stageNum --;
                initGame(stageNum);
                break;
            case R.id.btnNext:
                if(stageNum >= 36) return;
                stageNum ++;
                initGame(stageNum);
                break;
        }
    }

    public void onBtnArrow(View v) {
        int currx = pushMan.x;
        int curry = pushMan.y;
        int nextx1 = currx, nexty1 = curry;
        int nextx2 = currx, nexty2 = curry;

        switch (v.getId()) {
            case R.id.btnLeft:
                nextx1 --;
                nextx2 -= 2;
                break;
            case R.id.btnRight:
                nextx1 ++;
                nextx2 += 2;
                break;
            case R.id.btnUp:
                nexty1 --;
                nexty2 -= 2;
                break;
            default:
                nexty1 ++;
                nexty2 += 2;
                break;
        }

        if(nextx1 < 0 || nextx1 >= cols || nexty1 < 0 || nexty1 >= rows
                || stageCards[nexty1][nextx1].imageIndex() == 1)
            return;
        if(stageCards[nexty1][nextx1].imageIndex() == 0 || stageCards[nexty1][nextx1].imageIndex() == 3) {
            pushMan.x = nextx1;
            pushMan.y = nexty1;
            insertPushMan(nextx1, nexty1);
            removePushMan(currx, curry);
        } else {
            if(nextx2 < 0 || nextx2 >= cols || nexty2 < 0 || nexty2 >= rows
                    || stageCards[nexty2][nextx2].imageIndex() == 1 || stageCards[nexty2][nextx2].imageIndex() == 2
                    || stageCards[nexty2][nextx2].imageIndex() == 4)
                return;
            pushMan.x = nextx1;
            pushMan.y = nexty1;
            insertStone(nextx2, nexty2);
            insertPushMan(nextx1, nexty1);
            removePushMan(currx, curry);
            if(remain == 0) {
                mosaic.popupDialog(null, "Congratulate! You passed current stage.", "Close");
                if(stageNum < 36){
                    stageNum++;
                    initGame(stageNum);
                }
            }
        }
    }

    // User Event end ====================================

    void insertStone(int x, int y) {
        if(stageCards[y][x].imageIndex() == 3) {
            stageCards[y][x].imageChange(4);
            remain --;
        } else {
            stageCards[y][x].imageChange(2);
        }
    }

    void removePushMan(int x, int y) {
        if(stageCards[y][x].imageIndex() == 6)
            stageCards[y][x].imageChange(3);
        else
            stageCards[y][x].imageChange(0);
    }

    void insertPushMan(int x, int y) {
        if(stageCards[y][x].imageIndex() == 3 || stageCards[y][x].imageIndex() == 4) {
            if(stageCards[y][x].imageIndex() == 4)
                remain ++;
            stageCards[y][x].imageChange(6);
        } else {
            stageCards[y][x].imageChange(5);
        }
    }

}