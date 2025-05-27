package automationFramework;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultitreadedExecution{
	
	public static void main(String[] args) throws Exception {
		
		ExecutionTask executionTask1 = new ExecutionTask("chrome");
		
		//ExecutionTask executionTask2 = new ExecutionTask("edge");
		
		//ExecutionTask executionTask3 = new ExecutionTask("firefox");
		
		Map<String, ExecutionTask> hmTasks= new HashMap<>(); 
		hmTasks.put("executionTask1", executionTask1);
		//hmTasks.put("executionTask2", executionTask2);
		//hmTasks.put("executionTask3", executionTask3);
		
		ExecutorService service
        = Executors.newFixedThreadPool(3);

    // Creating 5 threads using loops
    for (int i = 1; i <=1; i++) {

        // Submitting task to service's execute method
        service.execute(hmTasks.get("executionTask"+i));
    }

    // In order to avoid further coming execution of
    // tasks shutdown() method is used
    service.shutdown();

	}

}
