package com.example;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by wxc on 2016/10/30.
 */

public class CallableTest {
    static class TaskWithResult implements Callable<String> {

        private final int id;

        TaskWithResult(int id) {
            this.id = id;
        }

        @Override
        public String call() throws Exception {
            return "result of TaskWithResult " + id;
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ArrayList<Future<String>> results = new ArrayList<>();
        for(int i=0; i<10; i++) {
            results.add(executorService.submit(new TaskWithResult(i)));
        }
        for(Future<String> fs : results) {
            try {
                System.out.println(fs.get());
            } catch (InterruptedException e) {
                System.out.println(e);
                return;
            } catch (ExecutionException e) {
                System.out.println(e);
            } finally {
                executorService.shutdown();
            }
        }
    }
}
