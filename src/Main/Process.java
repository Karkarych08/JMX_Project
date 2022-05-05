package main;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Process implements ProcessPoolMBean {

    private String name;
    private final String status;
    private final ScheduledExecutorService scheduledExecutorService;

    public Process() {
        name = "Default name";
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        status = "running";
    }

    private  void Task(String apath, String className) {
        var path = Path.of(apath);
        try{
            ClassLoader loader = new URLClassLoader(new URL[] {path.toUri().toURL()});
            var clazz = loader.loadClass(className);
            clazz.getMethod("run").invoke(null);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            this.status = ("error: " + Arrays.toString(e.getStackTrace()));
            this.cancel(this.name);
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void submit(String name, String classpath, String mainClass, int period) {
        this.name=name;
        Runnable task = () -> Task(classpath,mainClass);
        scheduledExecutorService.scheduleAtFixedRate(task, 1, period, TimeUnit.SECONDS);
    }

    @Override
    public String status(String name) {
        return status;
    }

    @Override
    public void cancel(String name) {
        this.scheduledExecutorService.shutdown();
        System.out.println( this.name +  " is stopped");
    }
}
