package org.example.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StatCallback,AsyncCallback.DataCallback {

    ZooKeeper zk;
    MyConf myConf;
    CountDownLatch cc = new CountDownLatch(1);
    public void setCc(CountDownLatch cc){
        this.cc = cc;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    //DataCallback
    @Override
    public void processResult(int i, String s, Object o, byte[] data, Stat stat) {
        if (data != null){
            String string = new String(data);
            myConf.setConf(string);
            cc.countDown();
        }
    }

    //StatCallback
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null){
            zk.getData("/AppConf",this,this,"sdf");
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/AppConf",this,this,"sdf");
                break;
            case NodeDeleted:
                //容忍性
                myConf.setConf("");
                cc = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/AppConf",this,this,"sdf");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    public void aWait(){
        zk.exists("/AppConf", this,this,"ABC");
        try {
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
