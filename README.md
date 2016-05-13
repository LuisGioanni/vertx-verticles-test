# vertx-verticles-test

This is an example project to underline Vert.x's threading model.

Run with maven :

`mvn clean package vertx:runMod -Dvertx.pool.worker.size=5 -Dvertx.pool.eventloop.size=1`

You can tweak the worker pool size and event loop size, but I recommend using these values to stress the effect of having :
* a blocked event loop
* no more threads available in worker pool

Go play with `TestVerticle.java` to showcase the differences !
