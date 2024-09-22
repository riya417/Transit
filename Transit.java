package transit;

import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {

	    // UPDATE THIS METHOD
		this.trainZero = new TNode();
		TNode bZero = new TNode();
		this.trainZero.setDown(bZero);

		TNode wlocZero = new TNode();
		bZero.setDown(wlocZero);

		TNode prevTrain = this.trainZero;
		TNode nextTrain = null;
		TNode prevBus = bZero;
		TNode nextBus = null;
		TNode prevWLoc = wlocZero;
		TNode nextWLoc = null;

		int bInd = -1;
		int tInd = -1;

		for(int i = 0; i < locations.length; i++){
			nextWLoc = new TNode(locations[i]);
			prevWLoc.setNext(nextWLoc);

			bInd = ifExists(locations[i],busStops);
			if(bInd > -1){
				nextBus = new TNode(busStops[bInd]);
				prevBus.setNext(nextBus);
				nextBus.setDown(nextWLoc);
				prevBus = nextBus;
			}

			tInd = ifExists(locations[i], trainStations);
			if(tInd > -1){
				nextTrain = new TNode(trainStations[tInd]); 
				prevTrain.setNext(nextTrain);
				nextTrain.setDown(nextBus);
				prevTrain = nextTrain;
			}

			
			prevWLoc = nextWLoc;
		}
		//System.out.println("here");
	}

	private int ifExists(int indexval, int index[]){
		for(int i = 0; i < index.length; i++){
			if(index[i] == indexval){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
	    // UPDATE THIS METHOD
		TNode prevS = trainZero;
		TNode currS = prevS.getNext();
		if(station == 0){
			trainZero.setNext(null);
			trainZero.setDown(null);
		}
		while(currS != null){
			// System.out.println("here");
			if(currS.getLocation() == station){
				prevS.setNext(currS.getNext());
				break;
			}
			prevS = currS;
			currS = currS.getNext();
		}
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
	    // UPDATE THIS METHOD
		TNode curBus = trainZero.getDown();
		while(curBus != null){
			if(curBus.getLocation() == busStop){
				break;
			}
			if(busStop > curBus.getLocation()){
				if(busStop < curBus.getNext().getLocation() || curBus.getNext() == null){
					TNode trainLoc = serachLocation(busStop);
					if(trainLoc != null) {
						TNode newBus= new TNode(busStop);
						newBus.setNext(curBus.getNext());
						curBus.setNext(newBus);
						newBus.setDown(trainLoc);
						break;
					}
				}
			}
			curBus = curBus.getNext();
		}
	}

	private TNode serachLocation(int location) {
		
		TNode currLoc = this.trainZero.getDown().getDown();
		while(currLoc != null) {
			if(currLoc.getLocation() == location) {
				return currLoc;
			}
			currLoc = currLoc.getNext();
		}
		return null;
	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {

	    // UPDATE THIS METHOD
	    ArrayList<TNode> bestpathList = new ArrayList<TNode>();
		TNode trainNode= searchLowN(this.trainZero,destination);
		
		if(trainNode != this.trainZero)
			bestpathList.add(trainNode);
	
		TNode busN= searchLowN(trainNode.getDown(),destination);
		
		if(busN.getLocation() > trainNode.getLocation())
			bestpathList.add(busN);
		
		TNode cLoc= busN.getDown().getNext();
		
		while(cLoc != null && cLoc.getLocation() <= destination ) {
			bestpathList.add(cLoc);
			cLoc= cLoc.getNext();
		}
	    return bestpathList;
	}

	//Find node less tahn stop
	private TNode searchLowN(TNode node,int stop){
		TNode cLoc = node;
		TNode pLoc = node;
		while(cLoc != null ) {
			if(cLoc.getLocation() == stop) {
				return cLoc;
			}
			else if(cLoc.getLocation() > stop) {
				return pLoc;
			}
			pLoc = cLoc;
			cLoc = cLoc.getNext();
			
		}
		return pLoc;
	}
	

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode duplicate() {

	    // UPDATE THIS METHOD
		TNode TDZero = new TNode();
		TNode BDZero = new TNode();
		TNode WLocDZero = new TNode();
		
		TDZero.setDown(BDZero);
		BDZero.setDown(WLocDZero);
		
		TNode PrevWLocD = WLocDZero;
		TNode PrevBD = BDZero;
		TNode PrevTD = TDZero;
		
		TNode tCurrLoc = this.trainZero.getDown().getDown().getNext();
		
		while(tCurrLoc != null) {
			TNode dLoc= new TNode(tCurrLoc.getLocation());
			
			PrevWLocD.setNext(dLoc);
			PrevWLocD = dLoc;
			TNode Bus = searchNode(this.trainZero.getDown().getNext(),tCurrLoc.getLocation());
			if(Bus != null) {
				TNode BDup = new TNode(Bus.getLocation());
				PrevBD.setNext(BDup);
				BDup.setDown(dLoc); 
				PrevBD = BDup;
				
				TNode tTrain = searchNode( this.trainZero,tCurrLoc.getLocation());
				if( tTrain != null) {
					TNode trainDup = new TNode(tTrain.getLocation());
					PrevTD.setNext(trainDup);
					trainDup.setDown(BDup);
					PrevTD=trainDup;
				}
			}
			
			tCurrLoc = tCurrLoc.getNext();
			
		}
	    return TDZero;
	}

	//Search for node existentence
	private TNode searchNode(TNode x,int location){
		TNode currN = x;
		while(currN != null ) {
			
			if(currN.getLocation() == location) {
				return currN;
			}
			currN = currN.getNext();
		}
		return null;
	}
	

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {

	    // UPDATE THIS METHOD
		TNode bZero = this.trainZero.getDown();
		TNode lZero = this.trainZero.getDown().getDown();
		TNode scooterZero = new TNode();

		scooterZero.setDown(lZero);
		bZero.setDown(scooterZero);
		TNode prevS= scooterZero;
		
		for(int i=0 ;i <scooterStops.length;i++ ) {
			
			TNode sNode = new TNode(scooterStops[i]);
			prevS.setNext(sNode);

			TNode locNode = searchNode(lZero,scooterStops[i]);
			sNode.setDown(locNode);
			
			TNode busNode = searchNode(bZero,scooterStops[i]);
			if(busNode != null) {
				busNode.setDown(sNode);
			}
			prevS = sNode;
			
		}
	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}
