package agent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MainTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try{
            if ("main/Main".equals(className)){
                ClassPool pool = ClassPool.getDefault();
                CtClass clazz = pool.get("main.Main");

                CtField StopTimeField = CtField.make("StopWatch stopWatch = new StopWatch(\"Program in work time\");",clazz);
                clazz.addField(StopTimeField);

                CtMethod StartTimer = CtMethod.make("""
                        public static void StartTimer(String[] args) {
                                 stopwatch.start("inizilizing");
                                 System.out.println("It's work");
                             }
                        """, clazz);
                clazz.addMethod(StartTimer);
                clazz.getDeclaredMethod("main").insertBefore("StartTimer();");

                CtMethod StopTimer = CtMethod.make("""
                        public static void StartTimer(String[] args) {
                                 stopwatch.stop();
                                 System.out.println(stopWatch.prettyPrint());
                             }
                        """, clazz);
                clazz.addMethod(StopTimer);
                clazz.getDeclaredMethod("main").insertAfter("StopTimer();");

                return clazz.toBytecode();
            }else {
                return classfileBuffer;
            }
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
