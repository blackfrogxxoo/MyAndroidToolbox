package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
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
    static Scheduler oriFileScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
    static Scheduler ecgFileScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
    static Scheduler getPacketScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
    static Scheduler cacheScheduler = Schedulers.from(Executors.newSingleThreadExecutor());

    static DeinterleaverCache deinterleaverCache = new DeinterleaverCache();

    private static final String ORI_PATH = "ori.txt";
    private static final String ECG_PATH = "ecg.txt";
    private static List<Subscription> subscriptions = new ArrayList<>();
    static FakeBytesGenerator.Listener listener = new FakeBytesGenerator.Listener() {
        int sequenceId = -1;
        @Override
        public void onBytes(byte[] bytes) {
            Subscription subscription = getBytesObservable(bytes)
                    .doOnSubscribe(() -> {})
                    .subscribeOn(Schedulers.immediate())
                    .observeOn(getPacketScheduler)
                    .map(bytes1 -> {
                        sequenceId++;
                        if (sequenceId == 32) {
                            sequenceId = 0;
                        }
                        int id = sequenceId;
                        return DataFactory.getEcgPacket(id);
                    })
                    .observeOn(oriFileScheduler)
                    .doOnNext(packet -> {
                        int id = packet.sequenceID;
                        short[] shorts = packet.data;
                        StringBuilder sb = new StringBuilder();
                        int i = 0;
                        for (short item : shorts) {
                            sb.append(item);
                            if (i < shorts.length - 1) {
                                sb.append(',');
                            } else {
                                sb.append(',');
                                sb.append(id);
                                sb.append('\n');
                            }
                            i++;
                        }
                        try {
                            OriFileUtil.getInstance(ORI_PATH).store(sb.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .filter(shorts -> shorts != null)
                    .observeOn(cacheScheduler)
                    .doOnNext(packet -> {
                        System.out.println(Arrays.toString(packet.data));
                        System.out.println(packet.sequenceID);
                    })
                    .map(packet -> deinterleaverCache.setMeasurementsAndSequenceID(packet.data, packet.sequenceID))
                    .filter(shorts -> shorts != null)
                    .observeOn(fakeUiScheduler)
                    .doOnNext(shorts -> {
                        System.out.println(shorts.length);
                    })
                    .observeOn(ecgFileScheduler)
                    .doOnNext(shorts -> {
                        StringBuilder sb = new StringBuilder();
                        for (short item : shorts) {
                            sb.append(item);
                            sb.append('\n');
                        }
                        try {
                            EcgFileUtil.getInstance(ECG_PATH).store(sb.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .observeOn(fakeUiScheduler)
                    .subscribe(shorts -> {
                        System.out.println("Success");
                    }, throwable -> {
                        System.out.println(throwable.getMessage());
                        System.out.println("Failed");
                    });
            subscriptions.add(subscription);
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

    public static void main(String[] args) throws IOException {
        Observable.just(0).observeOn(fakeUiScheduler).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {

                try {
                    OriFileUtil.close();
                    EcgFileUtil.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long time = System.currentTimeMillis();
                connectBle(true);

                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                connectBle(false);
                for (Subscription subscription : subscriptions) {
                    if (subscription != null && !subscription.isUnsubscribed()) {
                        subscription.unsubscribe();
                    }
                }
                subscriptions.clear();
                System.out.println("Cost: " + (System.currentTimeMillis() - time) / 1000 + "s");
                try {
                    OriFileUtil.close();
                    EcgFileUtil.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }
}
