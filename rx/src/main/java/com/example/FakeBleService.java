package com.example;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FakeBleService {

    static short[] ECG_SHORTS = new short[]{1,2,3,4,5,6,7,8,9};
    static short[] PROCESSED_ECG_SHORTS = new short[]{
            1,2,3,4,5,6,7,8,9,
            1,2,3,4,5,6,7,8,9,
            1,2,3,4,5,6,7,8,9,
            1,2,3,4,5,6,7,8,9
    };
    static short[] INFO_SHORTS = new short[]{1,2,3,4,5,6,7,8,9};

    static Scheduler fakeUiScheduler = Schedulers.from(Executors.newSingleThreadExecutor());

    static FakeBytesGenerator.Listener listener = new FakeBytesGenerator.Listener() {
        int i=0;

        @Override
        public void onBytes(byte[] bytes) {
            getBytesObservable(bytes)
                    .subscribeOn(Schedulers.immediate())
                    .doOnSubscribe(new Action0() { // 准备工作
                        @Override
                        public void call() {
                            System.out.println("准备工作:" + Thread.currentThread().getName());
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<byte[], short[]>() {
                        @Override
                        public short[] call(byte[] bytes) {
                            i ++;
                            if(i % 10 == 0) {
                                return ECG_SHORTS;
                            }
                            return null;
                        }
                    })
                    .filter(new Func1<short[], Boolean>() {
                        @Override
                        public Boolean call(short[] shorts) {
                            if(shorts != null) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new Func1<short[], short[]>() {
                        @Override
                        public short[] call(short[] shorts) {
                            if(Math.random() < 0.1) {
                                return PROCESSED_ECG_SHORTS;
                            }
                            return null;
                        }
                    })
                    .filter(new Func1<short[], Boolean>() {
                        @Override
                        public Boolean call(short[] shorts) {
                            if(shorts != null) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .observeOn(fakeUiScheduler)
                    .doOnNext(new Action1<short[]>() {
                        @Override
                        public void call(short[] shorts) {
                            System.out.println(shorts.length);
                            System.out.println("Current Thread:" + Thread.currentThread().getName());
                        }
                    })
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<short[]>() {
                        @Override
                        public void call(short[] shorts) {
                            System.out.println("Current Thread:" + Thread.currentThread().getName());
                        }
                    });
        }
    };

    private static void connectBle(boolean connect) {
        if(connect) {
            FakeBytesGenerator.start(listener);
        } else {
            FakeBytesGenerator.stop();
        }
    }

    private static Observable<byte[]> getBytesObservable(byte[] bytes) {
        return Observable.just(bytes);
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        connectBle(true);

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        connectBle(false);
        System.out.println("Cost: " + (System.currentTimeMillis() - time)/1000 + "s");
    }
}
