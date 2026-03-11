package com.example.asphalt_demo;

public class Car {
    public float x, y, width, height;
    public int color;

    public Car(float x, float y, float w, float h, int color) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.color = color;
    }

    public void move(float gx, int screenWidth) {
        // Nghiêng máy (gx) để thay đổi vị trí X
        x += gx * 5; // Độ nhạy lái xe

        // Giữ xe không chạy ra khỏi lề đường
        if (x < 0) x = 0;
        if (x > screenWidth - width) x = screenWidth - width;
    }
}