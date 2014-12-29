import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/*
 * Encapsulates a Carpark
 * 
 * A carpark is made up of two Entrances and two Exits
 * and a number of variables which reflect the current 
 * state of the carpark
 */
public class Carpark{
	
	private Spaces spaces;
	private int numVehicles;
	private int numBadParkers;
	private int numStudents;
	int numLecturers;
	private Entrance entrance1;
	private Entrance entrance2;
	private Exit exit1;
	private Exit exit2;
	private boolean lecturerPrecedence;
	int precedencePrec;
	ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	final Lock lock = new ReentrantLock(true);
	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();
	
	Carpark(boolean b){
		lecturerPrecedence=b;
	}
	
	Carpark(boolean b, int p){
		lecturerPrecedence=b;
		precedencePrec = p;
	}
	
	public void open(){
		
		numVehicles = 0;
		numBadParkers = 0;
		numStudents=0;
		numLecturers=0;
		if(lecturerPrecedence){
			spaces = new Spaces(precedencePrec);
		}
		else{
			spaces = new Spaces();
		}
		// crate the entrances and exits
		entrance1 = new Entrance(this, 50, 100, 1);
		entrance2 = new Entrance(this, 50, 100, 2);
		exit1 = new Exit(this, 200, 50, 1);
		exit2 = new Exit(this, 200, 50, 2);
		// ExecutorService with a pool size of 4 for the 2 exits and entrances
		ExecutorService threadExecutor= Executors.newFixedThreadPool(4);
		threadExecutor.execute(entrance1);
		threadExecutor.execute(entrance2);
		threadExecutor.execute(exit1);
		threadExecutor.execute(exit2);
		threadExecutor.shutdown();
		//wait for the exits and entrances to finish
		try {
			threadExecutor.awaitTermination(10, TimeUnit.MINUTES);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	// method each Vehicle method calls when it is trying to enter the carpark
	public void arrive(Vehicle vehicle) throws InterruptedException {
		
		// lock critical section
		lock.lock();
		try{
			//different wait conditions depending on whether lecturerPrecedence is set or not
			if(lecturerPrecedence){
				precedenceWait(vehicle);
			}
			else{
				nonPrecedenceWait();
			}
			
			if(shouldMakeGoodParker(vehicle)){
				vehicle.makeGoodParker();
			}
	
			if(vehicle.isBadParker()){
				numBadParkers++;
			}
 
			// decrement the number of spaces
			if(lecturerPrecedence){
				if(precedenceSpaceDec(vehicle)){
					vehicle.setOccupyingPrecedenceSpace();
				}
			}
			else{
				spaces.decTotalSpaces(vehicle.isBadParker());
			}
		
			//decrement the entrance queue
			if(!vehicle.isLecturer()){
				numStudents++;
				if(vehicle.getEntranceid() == 1){
					entrance1.decNumStudentsInQueue();
				}
				else{
					entrance2.decNumStudentsInQueue();
				}
			}
			else{
				numLecturers++;
				if(vehicle.getEntranceid() == 1){
					entrance1.decNumLecturersInQueue();
				}
				else{
					entrance2.decNumLecturersInQueue();
				}
			}
			// add the Vehicle object to the list of vehicles in the carpark
			vehicles.add(vehicle);
			numVehicles++;
			display();
			notEmpty.signal();
		}
		finally{
			lock.unlock();
		}
	}
	
	// wait while there are no spaces available
	// no distinction made between lecturers and students
	// therefore equal precedence
	private void nonPrecedenceWait(){
		while((spaces.getTotalSpaces() == 0)){
			try {
				notFull.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// if precedence is set there are different wait 
	// conditions if lecturer or student
	private void precedenceWait(Vehicle v){
		if(!v.isLecturer()){
			studentPrecedenceWait();
		}
		else{
			teacherPrecedenceWait();
		}
	}
	
	//students have to wait for a normal space
	private void studentPrecedenceWait(){
		while((spaces.getNumFreeNormalSpaces() == 0)){
			try {
				notFull.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// teachers wait for a normal spaces or precedence space
	private void teacherPrecedenceWait(){
		while(((spaces.getNumFreePrecedenceSpaces() == 0) && (spaces.getNumFreeNormalSpaces() == 0))){
			try {
				notFull.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//returns true if a precedence space was taken
	private boolean precedenceSpaceDec(Vehicle v){
		boolean result = false;
		if(!v.isLecturer()){
			spaces.decNumFreeNormalSpaces(v.isBadParker());
		}
		else{
			if(spaces.getNumFreePrecedenceSpaces() > 0){
				spaces.decNumFreePrecedenceSpaces(v.isBadParker());
				result = true;
			}
			else{
				spaces.decNumFreeNormalSpaces(v.isBadParker());
			}
		}
		return result;
	}
	
	private void precedenceSpaceInc(Vehicle v){
		
		if(!v.isLecturer()){
			spaces.incNumFreeNormalSpaces(v.isBadParker());
		}
		else{
			if(v.isOccupyingPrecedenceSpace()){
				spaces.incNumFreePrecedenceSpaces(v.isBadParker());
			}
			else{
				spaces.incNumFreeNormalSpaces(v.isBadParker());
			}
		}
	}
	
	// A vehicle has no option other than to be a good parker
	// if there is only space for them to park in
	private boolean shouldMakeGoodParker(Vehicle v){
		
		boolean result = false;
		if(!lecturerPrecedence){
			if(spaces.getTotalSpaces() == 1){
				result = true;
			}
		}
		else{
			if(!v.isLecturer()){
				if(spaces.getNumFreeNormalSpaces() == 1){
					result = true;
				}
			}
			else{
				if((spaces.getNumFreeNormalSpaces() == 1) || (spaces.getNumFreePrecedenceSpaces() == 1)){
					result = true;
				}
			}
		}
		return result;
	}

	// method each Vehicle method calls when it is trying to leave the carpark
	public void depart(Vehicle vehicle) throws InterruptedException {

		//lock critical section
		lock.lock();
		try{
			// wait if the number of spaces equals the capacity
			// ie: the carpark is empty
			while(spaces.getTotalSpaces()==spaces.getCapacity()){
				try{
					notEmpty.await();
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(vehicle.isBadParker()){
				numBadParkers--;
			}
		
			//increment spaces
			if(lecturerPrecedence){
				precedenceSpaceInc(vehicle);
			}
			else{
				spaces.incTotalSpaces(vehicle.isBadParker());
			}
		
			//decrement exit queue
			if(!vehicle.isLecturer()){
				if(vehicle.getExitid() == 1){
					exit1.decNumStudentsInQueue();
				}
				else{
					exit2.decNumStudentsInQueue();
				}
			}
			else{
				if(vehicle.getExitid() == 1){
					exit1.decNumLecturersInQueue();
				}
				else{
					exit2.decNumLecturersInQueue();
				}
			}
		
			numVehicles--;
			if(!vehicle.isLecturer()){
				numStudents--;
			}
			else{
				numLecturers--;
			}
			display();
			notFull.signal();
			}finally{
				lock.unlock();
			}
	}
	
	private void display(){
		System.out.printf("%9s%3d%9s%3d%13s%3d%11s%3d%12s%3s%18s%3s%1s%3s%14s%3s%1s%3s",
															"Vehicles:",numVehicles,
															"  Spaces:", spaces.getTotalSpaces(),
															"  Bad Parkers:", numBadParkers,
															"  Students:", numStudents,
															"  Lecturers:", numLecturers,
															"  Entrance Queues:", entrance1.getNumStudentsInQueue() + entrance1.getNumLecturersInQueue(),
															" ", entrance2.getNumStudentsInQueue() + entrance2.getNumLecturersInQueue(),
															"  Exit Queues:", exit1.getNumStudentsInQueue() + exit1.getNumLecturersInQueue(),
															" ", exit2.getNumStudentsInQueue() + exit2.getNumLecturersInQueue());
		if(lecturerPrecedence){
			System.out.printf("%14s%3d%14s%3d",
								"  Prec Spaces:", spaces.getNumFreePrecedenceSpaces(),
								"  Norm Spaces:", spaces.getNumFreeNormalSpaces());
		}
		System.out.println();
	}
	
	//random number generator
	private int randomNumber(int low, int high){
		Random rand = new Random();
		return(rand.nextInt((high - low) + 1) + low);
	}
	
	public boolean hasVehicles() {
		// accessing shared variables so lock
		lock.lock();
		try{
			return (numVehicles > 0);
		}
		finally{
			lock.unlock();
		}
	}
	
	//return a random vehicle from the list of vehicles
	public Vehicle getVehicle(){
		// accessing shared variables so lock
		lock.lock();
		try{
			return vehicles.remove(randomNumber(0, (vehicles.size()-1)));	
		}
		finally{
			lock.unlock();
		}
	}
}

