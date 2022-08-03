package com.mrgrd56.javalearningspringboot.domain.a;

public interface Movable {
    default void move() {
        System.out.println("Movable moved");
    }
}
