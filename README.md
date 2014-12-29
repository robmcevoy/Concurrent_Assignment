Concurrent_Assignment
=====================

## Assignment Description


University Car Park
 
A big Dublin University has a medium-sized car park, holding a maximum of 500 vehicles. The car park has two entrances and two exits. Automatic barriers control the entrances. There is a problem that can occur during the rush-hour: Because of the two sets of entrances and exits, cars (belonging to Lecturers and Students, each numbering in excess of 500) can enter or leave the car park simultaneously, so it is difficult to monitor the exact number of free spaces left, and it is possible for cars to enter the car park when it is full, looking around for spaces that might have just been vacated or ones that other drivers have found too difficult to park in. Further, some drivers can, owing to a lack of dexterity in parking, occupy 1.5 spaces each.  All these (which can be modelled using probabilities) can cause the car park to become clogged with traffic, making it difficult for cars to leave. It has been decided that each entrance and exit should be monitored concurrently, and a running total must be displayed showing the number of vehicles in the car park.  The following two situations should be simulated:
 
1. In term-time, the Lecturers and Students both use the car park for a similar purpose, i.e. coming in for giving/attending    lectures and then leaving to go home.  Suggest a solution to the above so that it gives Lecturers and Students equal            precedence and provides a fair solution.
    
    
2. The Secretary of the University decides that Lecturers should have precedence over Students using the University Car Park    for 70% of the spaces in the car park, with equal precedence for the remaining 30%.  Implement a solution that would reflect this decision.


## Design


The design of this assignment is made up of 7 distinct classes:
    
#### Vehicle
  * This class encapsulates a vehicle that will enter the carpark.
  * The Vehicle class implements the Runnable interface and therefore will be run as a thread. In its run() method it will          either be trying to enter the carpark or trying to leave the carpark.
  * A Vehicle can be a Lecturer or a Student. This is decide during its construction and it has a 50/50 chance of being either.
  * During its construction it is also decided if a Vehicle will be a bad driver or not. There is a 1 in 20 chance of this          occurring.
  * A Vehicle has a public method makeGoodParker() which is called by the Carpark if the Vehicle has no choice but to be a good     parker ie: there is only one space left
  * Other public methods for this class are in large either getters or setters.
  
#### Entrance
  * This class represents the Entrance to a carpark.
  * The Entrance class implements the Runnable interface and therefore will be run as a thread.
  * The purpose of this class is to create Vehicle Objects and run them using the ExecutorService.
  * Each Entrance will create these Vehicle objects at a frequency provided in the arrivalRate variable and each Vehicle when       run will be trying to enter the carpark.
  * An Entrance will keep track of how many Vehicles are trying to enter the carpark by extending the Queue class.
  * When a Vehicle enters the carpark the carpark will call the Entrance instance to notify it that it has entered the carpark      and it will decrement its queue.
  * Each instance of Entrance will create 400 Vehicles.

#### Exit
  * This class represents the Exit to the carpark.
  * The Exit class implements the Runnable interface and therefore will be run as a thread.
  * The purpose of this class take Vehicle objects from the carpark and run them using the ExecutorService.
  * Each Exit will take these Vehicle objects from the carpark at a frequency provided in the departureRate variable and each       Vehicle object will be trying to leave the carpark.
  * When an Exit calls the Carpark for a Vehicle to leave, the Carpark will return a random Vehicle from its list of Vehicles.
  * An Exit will keep track of how many Vehicles are trying to enter the carpark by extending the Queue class.
  * When a Vehicle exits the carpark the carpark will call the Exit instance to notify it that it has left the carpark and it       will decrement its queue.
  * The Exit thread will run until all vehicles have left the carpark.

#### Queue
  * Queue is a small class Entrance and Exit make use of.
  * It allows for the increment and decrement of a Queue of Students and Lecturers.

#### Spaces
  * This class is used to keep track of spaces in a carpark.
  * Spaces keeps track of the total number of spaces and the normal and precedence spaces.
  * A precedence space is a space only lecturers can occupy.
  * A normal spaces can be occupied by a Student or a Lecturer.
  * The percentage is of spaces that are normal/precedence spaces is provided in Spaces constructor.
  * If a Vehicle is a bad parker Spaces will decrement 2 spaces in the carpark. The assignment description states that a bad        driver would take up 1.5 spaces but in the a carpark this would mean that two spaces would actually be occupied as a Vehicle     needs a full space to park in.
  * There are a 500 total spaces available.

#### Carpark
  * A Carpark object is made up of two Entrances and two Exits and a number of variables which reflect the current state of a       carpark.
  * When you call the open() method of Carpark it creates an ExecutorService which runs four threads: two entrances and two         exits.
  * The two most important methods in Carpark are arrive() and depart() as these are the methods the Vehicle threads will be        calling.
  * When a Vehicle thread calls the arrive() method the wait condition applied is based on whether the lecturerPreference          variable flag is set. If it is not the same wait condition is applied to both Lecturers and Students. If the flag is set        Students must wait until a normal space is available whereas Lecturers wait until a normal or precedence spaces is free.
  * When a Vehicle thread calls the depart() method() no distinction is made on whether the lecturerPreference variable flag is     set or if a Vehicle is a Lecturer or Student as no precedence is applied in leaving the car park.
  
#### University
  * This class contains the main method.
  * The main method takes in user input, the first part is
      “Enter 0 for equal precedence, Enter 1 for Lecturer precedence:”
      If the user enters 1 then they are required to specify the percentage of spaces they wish to have Lecturer precedence.
        “Enter percentage of spaces to have Lecturer precedence eg 70 :”
      The example input of 70 meets the conditions for the requirements of the assignment but this solution also allows for
      other inputs.

## Issues Tackled 
          
#### Fairness/Starvation
  * ExecutorService: Each instance of an ExecutorService in this assignment runs all threads with equal priority. This ensures      that there are no threads with higher priority hogging CPU time. For example, in the eyes of the ExecutorService:
        * A Vehicle thread whether a Student or Lecturer, has equal priority.
        * An Exit/Entrance thread has equal priority.
  * Reentrantlocks: Both uses of Reentrantlocks(Carpark & Queue) are implemented using Renentrantlocks fair policy. This means      that the Reentrantlock will favour granting access to the longest waiting thread. So for example: a Vehicle waiting to get      access to the Carpark will not be starved.

#### Mutual Exclusion
  * Reentrantlocks: There are two circumstances in this project where critical sections need to be protected. These are in the      Carpark and Queue classes. Reentrantlocks enable protection of critical sections. When a thread needs to access shared          variables it must first acquire the lock and only one thread can acquire this lock at a time. Then when the thread is           finished in the critical section it releases the lock.
 
#### Deadlock Prevention
  * Hold and Wait: The hold and wait condition will not occur in relation to the Carpark. If a Vehicle thread arrives into the      Carpark and acquires the lock but there is are no spaces available it will call the await() method for the notFull Condition     and will release the lock until it is signalled. This will prevent deadlock.
