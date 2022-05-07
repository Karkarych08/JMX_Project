package main;

import agent.MainTransformer;
import  javassist.*;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Process {

    private String name;
    private final String status;
    private String classpath;
    private String mainClass;
    private Integer period;
    public boolean isProfiled;
    private final ScheduledExecutorService scheduledExecutorService;

    public Process() {
        name = "Default name";
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        status = "running";
        isProfiled = false;
    }

    private  void Task(String apath, String className) {
        var path = Path.of(apath);
        try{
            ClassLoader loader = new URLClassLoader(new URL[] {path.toUri().toURL()});
            var clazz = loader.loadClass(className);
            clazz.getMethod("run").invoke(null);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            this.status = ("error: " + Arrays.toString(e.getStackTrace()));
            this.cancel();
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void submit(String name, String classpath, String mainClass, int period) {
        this.name=name;
        this.mainClass = mainClass;
        this.classpath = classpath;
        this.period = period;
        Runnable task = () -> Task(classpath,mainClass);
        scheduledExecutorService.scheduleAtFixedRate(task, 1, period, TimeUnit.SECONDS);
    }

    public String status() {
        return status;
    }

    public void cancel() {
        this.scheduledExecutorService.shutdown();
        System.out.println( this.name +  " is stopped");
    }

    public void startProfiling() throws NotFoundException, IOException, CannotCompileException, IllegalClassFormatException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(mainClass);
        MainTransformer mainTransformer = new MainTransformer();
        mainTransformer.transform(this.scheduledExecutorService.getClass().getClassLoader(),
                this.scheduledExecutorService.getClass().getName(),
                this.scheduledExecutorService.getClass(),
                this.scheduledExecutorService.getClass().getProtectionDomain(),
                ctClass.toBytecode());
        this.isProfiled = true;
    }

    public void stopProfiling(){
        this.scheduledExecutorService.shutdown();
        Runnable task = () -> Task(this.classpath,this.mainClass);
        scheduledExecutorService.scheduleAtFixedRate(task, 1, this.period, TimeUnit.SECONDS);
        this.isProfiled = false;
    }
}
