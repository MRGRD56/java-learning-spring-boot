package com.mrgrd56.javalearningspringboot.domain;

public interface Figure {
    default void move() {
        System.out.println("Figure moved");
    }
}
