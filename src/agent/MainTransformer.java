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
                ClassPool pool = ClassPool.getDefault();
                CtClass clazz = pool.get(className);

                CtField startTimeField = CtField.make("private static double startTime = 0.0;",clazz);
                clazz.addField(startTimeField);

                CtField endTimeField = CtField.make("private static double endTime = 0.0;",clazz);
                clazz.addField(endTimeField);

                CtMethod StartTimer = CtMethod.make("""
                        public static void StartTimer() {
                                 startTime = System.nanoTime();
                                 System.out.println("It's work");
                             }
                        """, clazz);
                clazz.addMethod(StartTimer);
                clazz.getDeclaredMethod("run").insertBefore("StartTimer();");

                CtMethod StopTimer = CtMethod.make("""
                        public static void StopTimer() {
                                 endTime = System.nanoTime();
                                 System.out.println(endTime - startTime);
                             }
                        """, clazz);
                clazz.addMethod(StopTimer);
                clazz.getDeclaredMethod("run").insertAfter("StopTimer();");

                return clazz.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
