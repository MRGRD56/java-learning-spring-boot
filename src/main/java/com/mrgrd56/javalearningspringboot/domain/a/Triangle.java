package com.mrgrd56.javalearningspringboot.domain;

public class Triangle implements Figure, Movable {
    public void moveAsFigure() {
        Figure.super.move();
    }

    public void moveAsMovable() {
        Movable.super.move();
    }

    @Override
    public void move() {
        Figure.super.move();
    }
}
