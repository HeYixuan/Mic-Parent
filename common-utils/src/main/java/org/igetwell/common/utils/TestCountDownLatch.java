package org.igetwell.common.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author Administrator
 * 每天起早贪黑的上班，父母每天也要上班，话说今天定了个饭店，一家人一起吃个饭，通知大家下班去饭店集合。
 * 假设：3个人在不同的地方上班，必须等到3个人到场才能吃饭，用程序如何实现呢？
 */
public class TestCountDownLatch {

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool(); //创建一个线程池
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        for(int i=0;i<3;i++){
            service.submit(()->{

                try {
                    System.out.println("线程" + Thread.currentThread().getName() +  "到达");

                    countDownLatch.countDown();

                } catch (Exception e) {
                    e.printStackTrace();
                }finally{

                    //这里把countDownLatch.await(); 放到finally 里面是为了防止 发生异常后，计数器不能减下去，导致后面的线程不能执行

                    try {
                        countDownLatch.await();
                        System.out.println("线程" + Thread.currentThread().getName() + "处理完毕...");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            });

        }

    }
}
