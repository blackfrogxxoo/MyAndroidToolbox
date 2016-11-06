package com.example.singleton;

/**
 * Created by wxc on 2016/11/6.
 */

public class DifferentSingleton {
    static class EHan {
        // 立即加载方式 == 饿汉模式
        private static EHan instance = new EHan();
        private EHan(){}
        public static EHan getInstace() {
            // 此代码版本为立即加载
            // 缺点是不能有其他实例变量
            // 因为getInstance()方法没有同步
            // 所以有可能出现非线程安全问题
            return instance;
        }
    }

    static class LanHan {
        // 延迟加载方式 == 懒汉模式
        private static LanHan instance;
        private LanHan(){}
        public static LanHan getInstace() {
            // 缺点是可能在多线程环境下产生多个实例
            if(instance == null) {
                instance = new LanHan();
            }
            return instance;
        }
    }

    static class SynchronizedLanHan {
        // 延迟加载方式 == 懒汉模式
        private volatile static SynchronizedLanHan instance;
        private SynchronizedLanHan(){}
        public static SynchronizedLanHan getInstace() {
            try {
                if(instance == null) {
                    // 模拟在创建对象之前做一些准备性的工作
                    Thread.sleep(3000);
                    // 虽然部分代码被上锁，但还是有非线程安全问题
                    synchronized (SynchronizedLanHan.class) {
                        instance = new SynchronizedLanHan();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return instance;
        }
    }

    static class FixedSynchronizedLanHan {
        // 延迟加载方式 == 懒汉模式
        private volatile static FixedSynchronizedLanHan instance;
        private FixedSynchronizedLanHan(){}
        public static FixedSynchronizedLanHan getInstace() {
            try {
                if(instance == null) {
                    // 模拟在创建对象之前做一些准备性的工作
                    Thread.sleep(3000);
                    synchronized (FixedSynchronizedLanHan.class) {
                        if(instance == null) {
                            instance = new FixedSynchronizedLanHan();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return instance;
        }
    }
}
