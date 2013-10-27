package student;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import java.util.List;

import danaus.*;

public class Butterfly extends AbstractButterfly {	
	
	// Initialize a stack to keep track of the butterfly's flight path
	static Deque<Location> visitStack = new ArrayDeque<Location>();
			
	// Initialize a hash table to keep track of tiles which have been visited
	static Hashtable<Location,Boolean> tilesVisited = new Hashtable<Location,Boolean>();
	
	/** An instance retrieves the locations of all tiles surrounding the butterfly */
	private Location[] getNeighbors(Location currentLocation) {
		// Initialize an empty array with 8 elements - one for each cardinal direction
		Location[] neighbors = new Location[8];
		
		// Calculate the x and y coordinates of the cardinal directions
		int col_E = currentLocation.col+1;
		int col_W = currentLocation.col-1;
		int row_N = currentLocation.row-1;
		int row_S = currentLocation.row+1;
		
		neighbors[0] = new Location(currentLocation.col,row_N); 	// North
		neighbors[1] = new Location(col_E,row_N); 					// North-east
		neighbors[2] = new Location(col_E,currentLocation.row); 	// East
		neighbors[3] = new Location(col_E,row_S);  					// South-east
		neighbors[4] = new Location(currentLocation.col,row_S); 	// South
		neighbors[5] = new Location(col_W,row_S);  					// South-west
		neighbors[6] = new Location(col_W,currentLocation.row); 	// West
		neighbors[7] = new Location(col_W,row_N); 	 				// North-west
		
		return neighbors;
	}
	
	/** An instance determines the direction in which a butterfly should fly to reach a specified location */
	public void flyTo(Location desiredLocation) {
		// Initialize the current location into a variable
		Location currentLocation = this.location;
		
		// Calculate the x and y difference between the two tiles
		// This should always only be an integer difference of range [-1 1]
		int rowdiff = desiredLocation.row - currentLocation.row;
		int coldiff = desiredLocation.col - currentLocation.col;
		
		if (coldiff > 0) {		   // Eastern-half
				 if (rowdiff <  0) { fly(danaus.Direction.NE,danaus.Speed.FAST); } 
			else if (rowdiff == 0) { fly(danaus.Direction.E,danaus.Speed.FAST);  } 
			else if (rowdiff >  0) { fly(danaus.Direction.SE,danaus.Speed.FAST); }
		} else if (coldiff == 0) { // Central-axis
				 if (rowdiff <  0) { fly(danaus.Direction.N,danaus.Speed.FAST);  } 
			else if (rowdiff == 0) { return; } 
			else if (rowdiff >  0) { fly(danaus.Direction.S,danaus.Speed.FAST);  }
		} else {				   // Western-half
				 if (rowdiff <  0) { fly(danaus.Direction.NW,danaus.Speed.FAST); } 
			else if (rowdiff == 0) { fly(danaus.Direction.W,danaus.Speed.FAST);  } 
			else if (rowdiff >  0) { fly(danaus.Direction.SW,danaus.Speed.FAST); }
		}
	}
	
	/** An instance tries to fly the butterfly in a direction determined by the stack */
	public void tryFly() {
		// Pull off the topmost value from the stack and make it our target
		Location desiredLocation = visitStack.pop();
		
		try {
			flyTo(desiredLocation);
			
			// Set the current location as visited
			tilesVisited.put(this.location,true);
			
			// TODO: Write the TileState to memory
		}
		catch (danaus.ObstacleCollisionException e) {
			// Set the obstacle tile as visited
			tilesVisited.put(desiredLocation,true);
			
			tryFly();
		}
	}
	
	/**
	 * Returns a two-dimensional array of TileStates that represents the map the
	 * butterfly is on.
	 * 
	 * During the learning phase of a simulation, butterflies are tasked with
	 * learning the map in preparation for the running phase of a simulation. 
	 * A butterfly should traverse the entire map and generate a two-dimensional
	 * array of TileStates in which each TileState corresponds to the
	 * appropriate in the map. For example, consider the map with the following
	 * TileStates.
	 * 
	 * <code>
	 * 					 			 -----
	 * 								|a|b|c|
	 *                   			 -----
	 *                  			|d|e|f|
	 *                   			 -----
	 * </code>
	 * A butterfly should return an identical array. The following arrays are
	 * all incorrect.
	 * 
	 * <code>
	 *                               -----
	 * 								|f|e| |
	 *                   			 -----
	 *                  			|a|b|d|
	 *                   			 -----
	 *                                ---
	 * 								 |a|b|
	 * 								  ---
	 *                  			 |d|e|
	 *                         	      ---
	 * </code>
	 *
	 * The returned array is graded based on the percentage of correctly 
	 * identified TileStates. It is recommended that a butterfly save the 
	 * TileState array to use during the running phase of a simulation.
	 *
	 * For more information, refer to Danaus' documentation.
	 * 
	 * @return A two-dimensional array of TileStates that represents the map the
	 * butterfly is on.
	 */
	public @Override TileState[][] learn() {		
		// Set the current location as visited
		tilesVisited.put(this.location,true);
		
		// Add the starting location to the bottom of the stack
		visitStack.push(this.location);
		
		while(!visitStack.isEmpty()) {
			tryFly();
			
			// Retrieve and load neighboring tiles into the stack
			for(Location neighbor : getNeighbors(this.location)) {
				if(!tilesVisited.containsKey(neighbor)) { visitStack.push(neighbor); }
			}			
		}
		
		return null;
	}

	public @Override List<Flower> flowerList() {
		// TODO 
		return null;
	}

	public @Override Location flowerLocation(Flower f) {
		// TODO 
		return null;
	}

	public @Override void run(List<Flower> flowers) {
		// DO NOT IMPLEMENT
	}
}
