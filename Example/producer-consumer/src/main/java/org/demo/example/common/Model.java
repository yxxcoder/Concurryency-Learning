package org.demo.example.common;

public interface Model {
    Runnable newRunnableConsumer();

    Runnable newRunnableProducer();
}
