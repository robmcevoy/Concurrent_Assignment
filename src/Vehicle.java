import java.util.Random;

/*	
 * 	encapsulates a Vehicle
 * 
 * 	A Vehicle is run as a thread. 
 * 	When it is running it will either be trying to 
 * 	leave of enter the Carpark
 *
 *	A Vehicle can be a lecturer or a student
 *	
 *	A Vehicle can or cannot be a bad driver
 */
public class Vehicle implements Runnable{
	
	private Carpark carpark;
	private boolean isLecturer;
	private boolean arrived;
	private boolean departed;
	private boolean badParker;
	private int entrance;
	private int exit;
	// used when the Vehicle is leaving which kind of
	// spaces it was occupying
	private boolean occupyingPrecedenceSpace;
	
	Vehicle(Carpark c, int entranceid){
		entrance = entranceid;
		carpark = c;
		arrived = false;
		departed = false;
		badParker = badParker();
		isLecturer = lecturerStudent();
	}

	public void run() {
		//while not arrived try enter the carpark
		if(!arrived){
			while(!arrived){
				try {
					carpark.arrive(this);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				arrived = true;
			}
		}
		//if you arrived try to leave
		else{
			while(!departed){
				try {
					carpark.depart(this);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				departed = true;
			}
		}
	}
	
	// Decides if a Vehicle will be a bad driver or not
	// 1 in 20 chance
	private boolean badParker(){
		int high = 20; int low = 1;
		Random rand = new Random();
		int randomNum = rand.nextInt((high - low) + 1) + low;
		return (randomNum == 1);
	}
	
	// Decides if a Vehicle will be a lecturer or student
	// 50/50 chance
	private boolean lecturerStudent(){
		if(randomNumber(1,2)==1){
			return true;
		}
		else{
			return false;
		}
	}
	
	private int randomNumber(int low, int high){
		Random rand = new Random();
		return(rand.nextInt((high - low) + 1) + low);
	}
	
	public boolean isBadParker(){
		return badParker;
	};
	
	public void makeGoodParker(){
		badParker = false;
	}
	
	public boolean isLecturer(){
		return isLecturer;
	}
	
	public void setExitid(int exitid){
		exit = exitid;
	}
	
	public int getExitid(){
		return exit;
	}
	
	public int getEntranceid(){
		return entrance;
	}
	
	public boolean isOccupyingPrecedenceSpace(){
		return occupyingPrecedenceSpace;
	}
	
	public void setOccupyingPrecedenceSpace(){
		occupyingPrecedenceSpace = true;
	}
}

