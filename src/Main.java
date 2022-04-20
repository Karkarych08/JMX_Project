import javax.management.*;
import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {
        ProcessPool process = new ProcessPool();
        ManagementFactory.getPlatformMBeanServer().registerMBean(
                process,
                new ObjectName("ProcessPool:name=processOperations")
        );
        while (true){
            Thread.sleep(10000);
        }
    }
}