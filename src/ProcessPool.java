import java.util.ArrayList;

public class ProcessPool implements ProcessPoolMBean{

    ArrayList<Process> processes;

    ProcessPool(){
        processes = new ArrayList<>();
    }


    @Override
    public void submit(String name, String classpath, String mainClass, int period) {
        Process process = new Process();
        process.submit(name, classpath, mainClass, period);
        processes.add(process);
        System.out.println(name + " is started");
    }

    @Override
    public String status(String name) {
        boolean deleted = false;
        String status = null;
        for (Process process : processes) {
            if (process.getName().equals(name)) {
                status = process.status(name);
                deleted = true;
            }
        }
        if (!(deleted))
            status = "No such process";
        return status;
    }

    @Override
    public void cancel(String name) {
        boolean deleted = false;
        for (Process process : processes) {
            if (process.getName().equals(name)) {
                process.cancel(name);
                processes.remove(process);
                deleted = true;
            }
        }
        if (!(deleted))
            System.out.println("No such process");
    }
}
