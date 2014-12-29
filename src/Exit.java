import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/* 	
 * 	an Exit is run as a thread and will create a Vehicle thread
 *	every number of seconds.
 *	
 *	These Vehicle objects are randomly selected from the Carpark's
 *	list of Vehicles that are currently in the Carpark
 *
 *	The Entrance will keep track of the number of Vehicle threads 
 *	attempting to leave the Carpark with a Queue
 */ 
public class Exit extends Queue implements Runnable{
	
	Carpark carpark;
	// the rate at which Exit creates and runs Vehicle threads
	int departureRate;
	int exitid;
	int poolSize;
	
	Exit(Carpark c, int d, int p, int id){
		carpark = c;
		departureRate = d;
		exitid = id;
		poolSize = p;
	}
	
	public void run(){
		//ExecutorService for executing Vehicle Threads
    	ExecutorService threadExecutor= Executors.newFixedThreadPool(poolSize);
		while(!carpark.hasVehicles()){}
		// keep creating Vehicles threads to leave while the carpark
		// still has Vehicles
	    while (carpark.hasVehicles()) {
	    	try{
	    		Thread.sleep(departureRate);
	            Vehicle vehicleToLeave = carpark.getVehicle();
	            vehicleToLeave.setExitid(exitid);
	            threadExecutor.execute(vehicleToLeave);
	            incQueue(vehicleToLeave);
	    	}
	        catch(Exception e){
	        	break;
	        }
	    }
	    // shutdown Executor Service
	    threadExecutor.shutdown();
	    try {
	    	threadExecutor.awaitTermination(10, TimeUnit.MINUTES);
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
	}
}
