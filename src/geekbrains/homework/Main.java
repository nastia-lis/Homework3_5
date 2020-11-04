package geekbrains.homework;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static final int CARS_COUNT = 4;
    public static volatile int c = 1;

    public static void main(String[] args) {
        CyclicBarrier cb = new CyclicBarrier(CARS_COUNT);
        Lock lock = new ReentrantLock();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            final int w = i;
            new Thread(() -> {
                try {
                    cars[w].repair();
                    cb.await();
                    lock.lock();
                    try {
                        if (c == 1) {
                            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
                            c = 2;
                        }
                    } finally {
                        lock.unlock();
                    }
                    cars[w].begin();
                    lock.lock();
                    try {
                        if (c == 2) {
                            System.out.println(cars[w].getName() + " WIN");
                            c = 3;
                        }
                    } finally {
                        lock.unlock();
                    }
                    cb.await();
                    lock.lock();
                    try {
                        if (c == 3) {
                            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
                            c = 0;
                        }
                    } finally {
                        lock.unlock();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
