package com.example.ran.happymoments.logic;

import android.telecom.Call;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Executor extends Thread{

    private ConcurrentLinkedQueue<Worker> workers;
    private Callback callback;
    private CountDownLatch latch;

    public Executor(List<Runnable> tasks , Callback callback){
        this.callback = callback;
        workers = new ConcurrentLinkedQueue<>();
        latch = new CountDownLatch(tasks.size());

        for (Runnable task : tasks) {
            workers.add(new Worker(task, latch));
        }
    }

    public void execute() {
        start();
    }

    @Override
    public void run() {
        while (true) {
            Worker worker = workers.poll();
            if (worker == null) {
                break;
            }
            worker.start();
        }
        try {
            //make the thread wait until all workers count down latch
            latch.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.onComplete();
        }
    }

    public static class Builder{
        private List<Runnable> tasks = new ArrayList<>();
        private Callback callback;

        public Builder add(Runnable task) {
            tasks.add(task);
            return this;
        }

        public Builder callback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Executor build(){
            return new Executor(tasks , callback);
        }
    }




    public interface Callback {
        void onComplete();
    }

}
