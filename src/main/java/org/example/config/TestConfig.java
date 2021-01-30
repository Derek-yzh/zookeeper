package org.example.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class TestConfig {

    ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZKUtils.getZk();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf(){

        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setZk(zk);
        MyConf myConf = new MyConf();
        //myConf.setConf("aaa");
        watchCallBack.setMyConf(myConf);

        watchCallBack.aWait();

        System.out.println("===================");
        while (true){
            if (myConf.getConf().equals("")){
                System.out.println("conf is null ...");
                watchCallBack.setCc(new CountDownLatch(1));
                watchCallBack.aWait();
            }
            System.out.println(myConf.getConf());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
