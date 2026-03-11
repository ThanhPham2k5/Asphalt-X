package com.example.asphalt_demo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Khởi tạo GameView và đặt làm nội dung chính của màn hình
        gameView = new GameView(this, null);
        setContentView(gameView);

        // 2. Thiết lập hệ thống cảm biến
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đăng ký lắng nghe cảm biến với tốc độ GAME (nhanh để lái xe mượt)
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Quan trọng: Hủy đăng ký khi thoát App để không gây tốn pin (theo Điều 24 Luật hạ tầng số)
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Lấy giá trị nghiêng trục X (ngang)
            // values[0] > 0 khi nghiêng trái, < 0 khi nghiêng phải
            // Ta đảo dấu (-) để nghiêng bên nào xe chạy bên đó cho tự nhiên
            float tiltX = -event.values[0];

            // Truyền giá trị nghiêng vào GameView để điều khiển xe
            gameView.setTilt(tiltX);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý ở bản mini này
    }
}