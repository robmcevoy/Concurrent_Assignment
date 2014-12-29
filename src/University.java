import java.util.Scanner;

/*
 * contains main method
 */
public class University {
	
	public static void main(String [] args){
		
		//user input
		System.out.print("Enter 0 for equal precedence, Enter 1 for Lecturer precedence: ");
		Scanner in = new Scanner(System.in);
		int num = in.nextInt();
		System.out.println();
		//create Carpark
		Carpark DCU_carpark;
		if(num == 1){
			System.out.print("Enter percentage of spaces to have Lecturer precedence eg 70 : ");
			int prec = in.nextInt();
			System.out.println();
			DCU_carpark = new Carpark(true, prec);
		}
		else{
			DCU_carpark = new Carpark(false);
		}
		DCU_carpark.open();
		System.out.println("Car Park is Empty and Closed");
	}
}