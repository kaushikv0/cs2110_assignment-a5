package student;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import danaus.*;

public class Butterfly extends AbstractButterfly {	
	
	private TileState[][] result;
	
	// Initialize a stack to keep track of the butterfly's flight path
	static Deque<Location> visitStack = new ArrayDeque<Location>();
			
	// Initialize a hash table to keep track of tiles which have been visited
	static Hashtable<Location,Boolean> tilesVisited = new Hashtable<Location,Boolean>();
	
	// Initialize a hash set to keep track of any flowers that were spotted
	static HashSet<Location> flowerSet = new HashSet<Location>();
	
	// Initialize a hash map to record each flower's location
	private Map<Flower, Location> m = new HashMap<Flower, Location>();
	
	/** Adds current tilestate to array */ 
	private void updateState() {
		refreshState();
		result[this.location.row][this.location.col] = state;
	}
	
	/** An instance prints the stack for debugging purposes */
	private void printStack(Deque<Location> stack) {
		Object[] stackArray = stack.toArray();
		System.out.println(Arrays.toString(stackArray));
	}
	
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
		if(rowdiff==getMapHeight()-1) rowdiff= -1;
		if(rowdiff==1-getMapHeight()) rowdiff= 1;
		if(coldiff==getMapWidth()-1) rowdiff= -1;
		if(rowdiff==1-getMapWidth()) rowdiff= 1;
		
		if (coldiff > 0) {		   // Eastern-half
				 if (rowdiff <  0) { fly(danaus.Direction.NE,danaus.Speed.FAST); } 
			else if (rowdiff == 0) { fly(danaus.Direction.E,danaus.Speed.FAST);  } 
			else if (rowdiff >  0) { fly(danaus.Direction.SE,danaus.Speed.FAST); }
		} else if (coldiff == 0) { // Central-axis
				 if (rowdiff <  0) { fly(danaus.Direction.N,danaus.Speed.FAST);  } 
			else if (rowdiff == 0) { return; } 
			else if (rowdiff > 0) { fly(danaus.Direction.S,danaus.Speed.FAST);  }
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
			
			updateState();
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
		result = new TileState[getMapHeight()][getMapWidth()];
		updateState();
		
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
			
			// printStack(visitStack);
		}
		
		return result;
	}

	public @Override List<Flower> flowerList() {
		List<Flower> f = new ArrayList<Flower>();
		for(TileState[] t1 : result) {
			for(TileState t2 : t1) {
				try {	
					for(Flower fl : t2.getFlowers()) {
						m.put(fl, t2.location);
					}
					f.addAll(t2.getFlowers());
				}
				catch (NullPointerException e) {};
			}
		}
		return f;
	}

	public @Override Location flowerLocation(Flower f) {
		return m.get(f);
	}

	public @Override void run(List<Flower> flowers) {
		// DO NOT IMPLEMENT
	}
}



// ALTERNATE IMPLEMENTATION

/*package student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Map;

import danaus.*;

*//**
 * This file is the only .java file you will be submitting to CMS. For javadoc
 * specifications, refer to AbstractButterfly. You do not need to copy the
 * javadocs into this file, but you may if you wish.
 *//*
public class Butterfly extends AbstractButterfly {
	private TileState[][] result;
	private boolean[][] v;
	private int row, col;
	private Speed s = Speed.FAST;
	private Stack<Direction> x = new Stack<Direction>();
	private Map<Flower, Location> m = new HashMap<Flower, Location>();
	
	// Adds current location to the array of TileStates, and marks current location as visited
	private void updateState() {
		refreshState();
		row = state.location.row;
		col = state.location.col;
		result[row][col] = state;
		v[row][col] = true;
	}
	
	// Flies to another tile, adds the new tile's location to the array of TileStates,
	// and marks that location as visited
	protected @Override void fly(Direction heading, Speed s) {
		super.fly(heading, s);
		updateState();
	}
	
	// De-enumerates the direction values
	private Direction dir(int r, int c) {
		if(r==-1 && c==-1) return Direction.NW;
		if(r==-1 && c==0) return Direction.N;
		if(r==-1 && c==1) return Direction.NE;
		if(r==1 && c==-1) return Direction.SW;
		if(r==1 && c==0) return Direction.S;
		if(r==1 && c==1) return Direction.SE;
		if(r==0 && c==-1) return Direction.W;
		return Direction.E;
	}
	
	// Depth-first search implementation
	private void dfs() {
		for(int r=-1; r<=1; r++) {
			for(int c=-1; c<=1; c++) {
				if (!v[(row+r+v.length)%v.length][(col+c+v[0].length)%v[0].length]) {
					try {
						fly(dir(r,c),s);
						x.push(dir(r,c));
						dfs();
					}
					catch (ObstacleCollisionException e) {
						v[(row+r+v.length)%v.length][(col+c+v[0].length)%v[0].length] = true;
					}
				}
			}
		}
		if(x.empty()) return;
		fly(Direction.opposite(x.pop()),s);
	}
	
	public @Override TileState[][] learn() {
		result = new TileState[getMapHeight()][getMapWidth()];
		v = new boolean[getMapHeight()][getMapWidth()];
		updateState();
		dfs();
		System.out.println(flowerList());
		return result;
	}
	
	public @Override List<Flower> flowerList() {
		List<Flower> f = new ArrayList<Flower>();
		for(TileState[] t1 : result) {
			for(TileState t2 : t1) {
				try {	
					for(Flower fl : t2.getFlowers()) {
						m.put(fl, t2.location);
					}
					f.addAll(t2.getFlowers());
				}
				catch (NullPointerException e) {};
			}
		}
		return f;
	}
	
	public @Override Location flowerLocation(Flower f) {
		return m.get(f);
	}
	
	public @Override void run(List<Flower> flowers) {
		// DO NOT IMPLEMENT
	}
}
*/
