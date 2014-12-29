import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 	A queue keeps track of the number of Lecturers & Students
 * 
 * 	Both Entrance and Exit extend this class
 * 
 *  All methods require access to a lock to ensure only one thread access
 *  the critical section
 */


public class Queue {

	private int numLecturersInQueue;
	private int numStudentsInQueue;
	final Lock lock = new ReentrantLock(true);
	
	//Takes in a Vehicle and based on whether it is a student/lecturer
	// will increment the corresponding integer variable
	public void incQueue(Vehicle v){
		lock.lock();
		try{
			if(v.isLecturer()){
				numLecturersInQueue++;
			}
			else{
				numStudentsInQueue++;
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	public int getNumLecturersInQueue(){
		lock.lock();
		try{
			return numLecturersInQueue;
		}
		finally{
			lock.unlock();
		}
	}
	
	public int getNumStudentsInQueue(){
		lock.lock();
		try{
			return numStudentsInQueue;
		}
		finally{
			lock.unlock();
		}		
	}
	
	public void decNumLecturersInQueue(){
		lock.lock();
		try{
			numLecturersInQueue--;
		}
		finally{
			lock.unlock();
		}
	}
	
	public void decNumStudentsInQueue(){
		lock.lock();
		try{
			numStudentsInQueue--;
		}
		finally{
			lock.unlock();
		}
	}
}
