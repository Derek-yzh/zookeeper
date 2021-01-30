package org.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * test ZK_watch
 *
 */
public class App {
    public static void main( String[] args ) throws Exception {

        //zookeeper是有session概念的，没有连接池的概念

        //watch:观察 回调
        //watch注册只发生在 读类型调用 get exist...
        //  第一类：new zk的时候，传入的watch，这个watch是session级别的 跟path、node没有关系

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final ZooKeeper zk = new ZooKeeper(
                "192.168.74.31:2181,192.168.74.32:2181,192.168.74.33:2181",
                3000,
                new Watcher() {
                    //watch的回调方法
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        Event.KeeperState state = watchedEvent.getState();
                        Event.EventType type = watchedEvent.getType();
                        String path = watchedEvent.getPath();
                        System.out.println(watchedEvent.toString());

                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                System.out.println("connected!");
                                countDownLatch.countDown();
                                break;
                            case AuthFailed:
                                break;
                            case ConnectedReadOnly:
                                break;
                            case SaslAuthenticated:
                                break;
                            case Expired:
                                break;
                            case Closed:
                                break;
                        }

                        switch (type) {
                            case None:
                                break;
                            case NodeCreated:
                                break;
                            case NodeDeleted:
                                break;
                            case NodeDataChanged:
                                break;
                            case NodeChildrenChanged:
                                break;
                            case DataWatchRemoved:
                                break;
                            case ChildWatchRemoved:
                                break;
                            case PersistentWatchRemoved:
                                break;
                        }

                    }
                });

        System.out.println("==============================================");
        countDownLatch.await();
        final ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("connecting......");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("connected.......");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        System.out.println("==============================================");

        String pathName = zk.create("/zk", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        final Stat s = new Stat();
        byte[] data = zk.getData("/zk", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("getData with : "+watchedEvent.toString());
                try {
                    //true : default watch 被重新注册
                    zk.getData("/zk",true,s);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, s);

        System.out.println(new String(data));
        System.out.println("+------+");

        //触发回调
        Stat stat = zk.setData("/zk", "newData".getBytes(), 0);

        Stat stat2 = zk.setData("/zk", "newData02".getBytes(), stat.getVersion());

        System.out.println("----------async start----------");
        zk.getData("/zk", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println("--async call back--");
                System.out.println(new String(bytes));
            }
        },"abc");
        System.out.println("----------async over----------");

        Thread.sleep(222222);

    }
}
