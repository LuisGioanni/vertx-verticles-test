package co.vero.vertx2;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.impl.management.ThreadPoolMXBean;
import org.vertx.java.platform.Verticle;

/**
 * Created by luis on 05/05/16.
 */
public class TestAsyncWorker extends Verticle {

  static final AtomicInteger serial = new AtomicInteger();
  final int id = serial.incrementAndGet();
  private int opTime;

  @Override
  public void start() {
    opTime = container.config().getInteger("opTime");

    vertx.eventBus().registerHandler("longOperationAsync", msg -> {
      int nb = (Integer)msg.body();
      printWithTimeStamp("Message "+ nb +" received by verticle " +id+ ", starting doing work (non-blocking)");
      getVertx().setTimer(opTime,
        aLong -> printWithTimeStamp("Executed "+ nb +" with sleep by verticle " + id + " on thread : " + Thread.currentThread()));
    });
    vertx.eventBus().registerHandler("instant", msg -> {
      printWithTimeStamp("Executed instantly by verticle " + id + " on thread : " + Thread.currentThread());
    });

    vertx.eventBus().registerHandler("longOperationBlocking", msg -> {
      printWithTimeStamp("Message received by verticle " +id+ ", starting doing work (blocking)");
      try {
        Thread.sleep(500);
        printWithTimeStamp("Executed with sleep by verticle " + id + " on thread : " + Thread.currentThread());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  public void printWithTimeStamp(String msg){
    System.out.println("[TestAsyncWorker]["+ LocalDateTime.now()+"] : "+msg);
  }
}
