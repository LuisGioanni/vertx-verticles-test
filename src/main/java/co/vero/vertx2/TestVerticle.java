package co.vero.vertx2;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import java.time.LocalDateTime;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/*
maven command to run :

mvn clean package vertx:runMod -Dvertx.pool.worker.size=10 -Dvertx.pool.eventloop.size=1

This a test class to showcase how Vert.x Verticles and Worker Verticles work.
Run these tests with at least a worker pool of size 4, and with only 1 event loop.
First run only one of the messages sent in the for loop, and see the behavior.
You can also run "worker:longOperation" and "longOperationAsync" together, but don't run
"longOperationBlocking" since it blocks the event loop, and because we only have 1 here it will block
all the other events.
 */
public class TestVerticle extends Verticle {

  //The number of Verticle instances to deploy for TestWorker and TestAsyncWorker
  static final int INSTANCES = 3;
  //The compuation time of long operations
  static final int COMPUTE_TIME = 500;
  static final boolean MULTITHREAD = false;

  public void start() {

    JsonObject config = new JsonObject().putNumber("opTime", COMPUTE_TIME);

    container.deployWorkerVerticle(TestWorker.class.getName(), config, INSTANCES, MULTITHREAD);
    container.deployVerticle(TestAsyncWorker.class.getName(), config, INSTANCES);

    getVertx().setTimer(2000, aLong -> {
        for (int i = 0; i < 10; i++) {
          //I RECOMMEND TO ONLY RUN ONE OF THESE AT A TIME AT FIRST
          System.out.println("[TestVerticle][" + LocalDateTime.now() + "] Sending message " + i);
          //Send a message to a worker Verticle that takes some time to finish
          vertx.eventBus().send("worker:longOperation", i);

          //Send a message to a normal Verticle that takes some time to finish and is non-blocking
          //vertx.eventBus().send("longOperationAsync", i);

          //Send a message to a normal Verticle that takes some time to finish and is blocking
          //vertx.eventBus().send("longOperationBlocking", i);
        }
        //Send messages that computes instantly
        //Worker
        //vertx.eventBus().send("worker:instant", true);
        //Normal
        //vertx.eventBus().send("instant", true);

        System.out.println("[TestVerticle][" + LocalDateTime.now() + "] Sent all messages");
      }
    );

    container.logger().info("Verticle started");

  }
}
