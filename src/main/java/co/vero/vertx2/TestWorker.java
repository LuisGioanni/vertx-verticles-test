package co.vero.vertx2;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.vertx.java.platform.Verticle;

/**
 * Created by luis on 04/05/16.
 */
public class TestWorker extends Verticle{

  static final AtomicInteger serial = new AtomicInteger();
  final int id = serial.incrementAndGet();

  int opTime;

  @Override
  public void start()  {
    opTime = container.config().getInteger("opTime");

    vertx.eventBus().registerHandler("worker:longOperation", msg -> {
      int nb = (Integer)msg.body();
      try{
        printWithTimeStamp("Message " + nb + " received by worker " +id+ ", starting doing work");
        Thread.sleep(opTime);
        printWithTimeStamp("Executed "+nb +" with sleep by worker " +id+ " on thread : " + Thread.currentThread());
      } catch (InterruptedException e) {
        msg.fail(0, "Interrupted");
      }
    });
    vertx.eventBus().registerHandler("worker:instant", msg -> {
      printWithTimeStamp("Instant message received by worker " +id+ ", starting doing work");
      printWithTimeStamp("Executed instantly by worker " +id+ " on thread : " + Thread.currentThread());
    });
  }

  public void printWithTimeStamp(String msg){
    System.out.println("[TestWorker]["+ LocalDateTime.now()+"] : "+msg);
  }
}

