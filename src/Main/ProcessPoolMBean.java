package main;

public interface ProcessPoolMBean {
    void submit(String name, String classpath, String mainClass, int period) throws ClassNotFoundException;

    String status(String name);

    void cancel(String name);

    void startProfiling(String name);

    void stopProfiling(String name);
}
