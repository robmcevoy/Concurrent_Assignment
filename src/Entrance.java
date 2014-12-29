import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/* 	
 * 	an Entrance is run as a thread and will create 400 Vehicle thread
 *	which all attempt to enter the carpark.
 *	
 *	The Entrance will keep track of the number of Vehicle threads 
 *	attempting to get into the carpark with a Queue
 */ 
public class Entrance extends Queue implements Runnable{
	
	private Carpark carpark;
	// the rate at which the Entrance creates and runs Vehicle threads
	private int arrivalRate;
	private int poolSize;
	private int entranceid;
	private final int NUM_VEHICLES = 400;
	
	Entrance(Carpark c, int a, int p, int id){
		entranceid = id;
		carpark = c;
		arrivalRate = a;
		poolSize = p;
	}
	
	public void run(){
		
		//ExecutorService for executing Vehicle Threads
		ExecutorService threadExecutor= Executors.newFixedThreadPool(poolSize);
		int count = 0;
		try {
			//keep creating and running threads until 400 have been created
	        while (count < NUM_VEHICLES) {
	            Thread.sleep(arrivalRate);
	            Vehicle v = new Vehicle(carpark, entranceid);
	        	threadExecutor.execute(v);
	        	incQueue(v);
	            count++;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
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
