/*
 * Used to keep track of spaces
 * 
 * The Carpark will only make used of normal and precedence spaces
 * if the precedence variable has been set in Carpark
 */
public class Spaces{
	
	private int numFreeNormalSpaces;
	private int numFreePrecedenceSpaces;
	private final int CAPACITY = 500;
	private int totalSpaces = CAPACITY;
	
	Spaces(){
		
	}
	
	// Takes in a integer representing the percentage of spaces that
	// will be lecturer precedence spaces
	Spaces(int precentagePrec){
		double prec = precentagePrec*0.01;
		numFreePrecedenceSpaces = (int)(CAPACITY * (prec));
		numFreeNormalSpaces = CAPACITY - numFreePrecedenceSpaces;
	}
	
	public int getNumFreeNormalSpaces(){
		return numFreeNormalSpaces;
	}
	
	public int getNumFreePrecedenceSpaces(){
		return numFreePrecedenceSpaces;
	}
	
	public int getTotalSpaces(){
		return totalSpaces;
	}
	
	public int getCapacity(){
		return CAPACITY;
	}
	
	// if a vehicle is a bad parker 2 spaces will be decremented
	
	public void incNumFreeNormalSpaces(boolean badParker){

		if(badParker){
			numFreeNormalSpaces = numFreeNormalSpaces +2;
			totalSpaces = totalSpaces + 2;
		}
		else{
			numFreeNormalSpaces++;
			totalSpaces++;
		}
	}
	
	public void incNumFreePrecedenceSpaces(boolean badParker){
		
		if(badParker){
			numFreePrecedenceSpaces = numFreePrecedenceSpaces +2;
			totalSpaces = totalSpaces +2;
		}
		else{
			numFreePrecedenceSpaces++;
			totalSpaces++;
		}
	}
	
	public void incTotalSpaces(boolean badParker){
		
		if(badParker){
			totalSpaces = totalSpaces + 2;
		}
		else{
			totalSpaces++;
		}
	}
	
	public void decNumFreeNormalSpaces(boolean badParker){

		if(badParker){
			numFreeNormalSpaces = numFreeNormalSpaces - 2;
			totalSpaces = totalSpaces -2;
		}
		else{
			numFreeNormalSpaces--;
			totalSpaces--;
		}
	}
	
	public void decNumFreePrecedenceSpaces(boolean badParker){
		
		if(badParker){
			numFreePrecedenceSpaces = numFreePrecedenceSpaces - 2;
			totalSpaces = totalSpaces -2;
		}
		else{
			numFreePrecedenceSpaces--;
			totalSpaces--;
		}
	}
	
	public void decTotalSpaces(boolean badParker){
		
		if(badParker){
			totalSpaces = totalSpaces - 2;
		}
		else{
			totalSpaces--;
		}
	}	
}