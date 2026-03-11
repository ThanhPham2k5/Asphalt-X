package com.example.asphalt_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    private Car player;
    private List<RectF> obstacles = new ArrayList<>();
    private Paint paint = new Paint();
    private float gx = 0;
    private boolean gameOver = false;
    private Random random = new Random();

    // Nút Restart (Vị trí và kích thước)
    private RectF restartBtn = new RectF();
    private int score = 0;
    private int highScore = 0;
    private android.content.SharedPreferences prefs;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        player = new Car(0, 0, 100, 180, Color.RED);

        prefs = context.getSharedPreferences("AsphaltPrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("high_score", 0);
    }

    // Hàm reset game
    private void resetGame() {
        obstacles.clear();
        score = 0; // Reset điểm về 0 khi chơi lại
        gameOver = false;
        player.x = getWidth() / 2 - player.width / 2; // Đưa xe về giữa
        invalidate();
    }
    public void setTilt(float x) {
        this.gx = x;
        // Không cần invalidate() ở đây vì hàm onDraw đã có invalidate() ở cuối rồi
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Kiểm tra nếu người dùng chạm vào màn hình khi Game Over
        if (gameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (restartBtn.contains(event.getX(), event.getY())) {
                resetGame();
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        if (gameOver) {
            if (score > highScore) {
                highScore = score;
                prefs.edit().putInt("high_score", highScore).apply();
            }
            drawGameOverScreen(canvas, w, h);
            return;
        }

        canvas.drawColor(Color.parseColor("#2C3E50")); // Màu đường nhựa xịn hơn

        // Vẽ vạch kẻ đường (cho giống Asphalt)
        paint.setColor(Color.WHITE);
        for (int i = 0; i < h; i += 100) {
            canvas.drawRect(w / 2 - 5, i, w / 2 + 5, i + 50, paint);
        }

        if (player.y == 0) player.y = h - 800;
        player.move(gx, w);
        paint.setColor(player.color);
        canvas.drawRect(player.x, player.y, player.x + player.width, player.y + player.height, paint);

        // --- ĐIỀU CHỈNH ĐỘ KHÓ (Dễ hơn) ---
        // Giảm tỉ lệ xuất hiện: từ < 2 xuống < 1.2 (vật cản thưa hơn)
        if (random.nextInt(100) < 1) {
            obstacles.add(new RectF(random.nextInt(w - 120), -200, 0, 0));
        }

        if (random.nextInt(100) < 1.2) {
            obstacles.add(new RectF(random.nextInt(w - 120), -200, 0, 0));
        }
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            RectF obs = obstacles.get(i);
            obs.bottom += 12; // Tốc độ rơi chậm lại một chút (từ 15 xuống 12)
            obs.top += 12;
            obs.right = obs.left + 120;

            paint.setColor(Color.parseColor("#F1C40F")); // Màu vàng cảnh báo
            canvas.drawRect(obs, paint);

            // Xóa vật cản khi trôi khỏi màn hình để nhẹ máy (Điều 24 Luật hạ tầng số)
            if (obs.top > h) {
                obstacles.remove(i);
                score += 10; // CỘNG 10 ĐIỂM mỗi khi né được 1 xe
                continue;
            }

            if (RectF.intersects(obs, new RectF(player.x, player.y, player.x + player.width, player.y + player.height))) {
                gameOver = true;
            }
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score: " + score, 50, 80, paint);
        canvas.drawText("Best: " + highScore, 50, 150, paint);

        invalidate();
    }

    private void drawGameOverScreen(Canvas canvas, int w, int h) {
        paint.setColor(Color.argb(150, 0, 0, 0)); // Nền mờ đen
        canvas.drawRect(0, 0, w, h, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("GAME OVER", w / 2, h / 2 - 100, paint);

        paint.setColor(Color.YELLOW);
        paint.setTextSize(60);
        canvas.drawText("Your Score: " + score, w / 2, h / 2 - 200, paint);

        if (score >= highScore && score > 0) {
            paint.setColor(Color.GREEN);
            canvas.drawText("NEW RECORD!", w / 2, h / 2 - 300, paint);
        }

        // Vẽ nút Restart
        restartBtn.set(w / 2 - 200, h / 2, w / 2 + 200, h / 2 + 120);
        paint.setColor(Color.parseColor("#E74C3C"));
        canvas.drawRoundRect(restartBtn, 20, 20, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("CHƠI LẠI", w / 2, h / 2 + 75, paint);
    }
}