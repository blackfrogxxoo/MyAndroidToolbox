package com.example;

import java.util.concurrent.Callable;

/**
 * Created by wxc on 2016/10/30.
 */

public class FibonacciCallable implements Callable<Long> {
    private final int n;

    public FibonacciCallable(int n) {
        this.n = n;
    }

    @Override
    public Long call() throws Exception {
        return fibonacci(n);
    }

    private long fibonacci(int n) {
        long res0 = 0;
        long res1 = 1;
        if(n<=0) {
            return res0;
        } else if(n == 1){
            return res1;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    };
}
