package org.example.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {

    private static ZooKeeper zk;

    private static String address =
            "192.168.74.31:2181,192.168.74.32:2181,192.168.74.33:2181/MyConf";//测试WatchCallback
            //"192.168.74.31:2181,192.168.74.32:2181,192.168.74.33:2181/lock";//测试分布式锁

    private static DefaultWatch watch = new DefaultWatch();

   private static CountDownLatch init =  new CountDownLatch(1);

    public static CountDownLatch getInit() {
        return init;
    }

    public static ZooKeeper getZk(){

        try {
            zk = new ZooKeeper(address,1000,watch);
            watch.setCc(init);
            init.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return zk;
    }

}
