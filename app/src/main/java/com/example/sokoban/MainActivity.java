package com.example.sokoban;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    JGameLib gameLib = null;
    int stageNum = 1;
    int m=0, n=0;
    JGameLib.Card[][] stageCards = null;
    Point pushMan = new Point(-1,-1);
    int remain = 0;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameLib = findViewById(R.id.gameLib);
        tvStatus = findViewById(R.id.tvStatus);
        initGame(stageNum);
    }

    @Override
    protected void onDestroy() {
        if(gameLib != null)
            gameLib.clearMemory();
        super.onDestroy();
    }

    private void initGame(int stageN) {
        remain = 0;
        int[][] stage = gameLib.assetFileIntArray("stage_" + stageN + ".txt");
        if(stage == null || stage.length < 1) return;
        m = stage.length; n = stage[0].length;
        gameLib.deleteAllCards();
        gameLib.setScreenGrid(n,m);
        stageCards = new JGameLib.Card[m][n];
        for(int i=0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                stageCards[i][j] = gameLib.addCard(R.drawable.img_back, j, i, 1, 1); // 0
                stageCards[i][j].addImage(R.drawable.img_block); // 1
                if(stage[i][j] != 1) {
                    stageCards[i][j].addImage(R.drawable.img_stone); // 2
                    stageCards[i][j].addImage(R.drawable.img_house_empty); // 3
                    stageCards[i][j].addImage(R.drawable.img_house_full); // 4
                    stageCards[i][j].addImage(R.drawable.img_push_man); // 5
                    stageCards[i][j].addImage(R.drawable.img_push_man); // 6
                    if(stage[i][j] >= 5) {
                        pushMan.x = j;
                        pushMan.y = i;
                    } else if(stage[i][j] == 2) {
                        remain ++;
                    }
                }
                stageCards[i][j].imageChange(stage[i][j]);
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

        if(nextx1 < 0 || nextx1 >= n || nexty1 < 0 || nexty1 >= m
                || stageCards[nexty1][nextx1].idx == 1)
            return;
        if(stageCards[nexty1][nextx1].idx == 0 || stageCards[nexty1][nextx1].idx == 3) {
            pushMan.x = nextx1;
            pushMan.y = nexty1;
            insertPushMan(nextx1, nexty1);
            removePushMan(currx, curry);
        } else {
            if(nextx2 < 0 || nextx2 >= n || nexty2 < 0 || nexty2 >= m
                    || stageCards[nexty2][nextx2].idx == 1 || stageCards[nexty2][nextx2].idx == 2
                    || stageCards[nexty2][nextx2].idx == 4)
                return;
            pushMan.x = nextx1;
            pushMan.y = nexty1;
            insertStone(nextx2, nexty2);
            insertPushMan(nextx1, nexty1);
            removePushMan(currx, curry);
            if(remain == 0) {
                gameLib.popupDialog(null, "Congratulate! You passed current stage.", "Close");
                if(stageNum < 36){
                    stageNum++;
                    initGame(stageNum);
                }
            }
        }
    }

    // User Event end ====================================

    void insertStone(int x, int y) {
        if(stageCards[y][x].idx == 3) {
            stageCards[y][x].imageChange(4);
            remain --;
        } else {
            stageCards[y][x].imageChange(2);
        }
    }

    void removePushMan(int x, int y) {
        if(stageCards[y][x].idx == 6)
            stageCards[y][x].imageChange(3);
        else
            stageCards[y][x].imageChange(0);
    }

    void insertPushMan(int x, int y) {
        if(stageCards[y][x].idx == 3 || stageCards[y][x].idx == 4) {
            if(stageCards[y][x].idx == 4)
                remain ++;
            stageCards[y][x].imageChange(6);
        } else {
            stageCards[y][x].imageChange(5);
        }
    }

}