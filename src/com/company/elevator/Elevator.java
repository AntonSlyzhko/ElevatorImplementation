package com.company.elevator;

import java.util.List;

public interface Elevator<T> {
    List<T> release();
    void accept(List<T> elements);
    void start();
    void stop();
    void move();
}
