package mini.engine;
import javafx.beans.property.SimpleStringProperty;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkerThreadPoolManager {
    private ThreadPoolExecutor workerThreadPool;
    private Integer totalThreadsInPool;
    //SimpleStringProperty AmountOfBusyThreadsProperty, AmountOfAvailableThreadsProperty;

    public WorkerThreadPoolManager(Integer threadAmount){
        workerThreadPool= new ThreadPoolExecutor(threadAmount,threadAmount,1000, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
        totalThreadsInPool=threadAmount;
    }

    synchronized public Integer getAmountOfAvailableThreads(){
     return totalThreadsInPool-getAmountOfBusyThreads();
    }

    synchronized public Integer getAmountOfBusyThreads(){
       return workerThreadPool.getActiveCount();
    }

    public Integer getAmountOfTotalThreadsInPool(){
        return totalThreadsInPool;
    }

    public ThreadPoolExecutor getWorkerThreadPool() {return workerThreadPool;}


//    public String getAmountOfBusyThreadsProperty() {
//        return AmountOfBusyThreadsProperty.get();
//    }
//    public SimpleStringProperty amountOfBusyThreadsPropertyProperty() {
//        return AmountOfBusyThreadsProperty;
//    }
//    public String getAmountOfAvailableThreadsProperty() {
//        return AmountOfAvailableThreadsProperty.get();
//    }
//    public SimpleStringProperty amountOfAvailableThreadsPropertyProperty() {
//        return AmountOfAvailableThreadsProperty;
//    }
}
