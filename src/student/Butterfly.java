package student;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import java.util.List;

import danaus.*;

public class Butterfly extends AbstractButterfly {	
	
	// Initialize a stack to keep track of the butterfly's flight path
	Deque<Location> visitStack = new ArrayDeque<Location>();
			
	// Initialize a hash table to keep track of tiles which have been visited
	Hashtable<Location,Boolean> tilesVisited = new Hashtable<Location,Boolean>();
	
	private Location peekLocation(Direction dir) {
		Location loc = null;
		int col_E = this.location.col+1;
		int col_W = this.location.col-1;
		int row_N = this.location.row-1;
		int row_S = this.location.row+1;
		
		switch (dir) {
		case N:  loc = new Location(this.location.col,row_N); 	break;
		case NE: loc = new Location(col_E,row_N); 				break;
		case E:  loc = new Location(col_E,this.location.row); 	break;
		case SE: loc = new Location(col_E,row_S); 				break;
		case S:  loc = new Location(this.location.col,row_S); 	break;
		case SW: loc = new Location(col_W,row_S); 				break;
		case W:  loc = new Location(col_W,this.location.row); 	break;
		case NW: loc = new Location(col_W,row_N); 				break;
		}
		return loc;
	}
	
	private Location[] getNeighbors(Location currentLocation) {
		Location[] neighbors = new Location[8];
		
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
	
	public void flyTo(Location desiredLocation) {
		Location currentLocation = this.location;
		int rowdiff = desiredLocation.row - currentLocation.row;
		int coldiff = desiredLocation.col - currentLocation.col;
		
		if (coldiff > 0) { // Eastern-half
			if (rowdiff < 0) { // NE
				fly(danaus.Direction.NE,danaus.Speed.FAST);
				System.out.println("Flying NE");
			} else if (rowdiff == 0) { // N
				fly(danaus.Direction.E,danaus.Speed.FAST); 
				System.out.println("Flying E");
			} else { // SE
				fly(danaus.Direction.SE,danaus.Speed.FAST); 
				System.out.println("Flying SE");
			}
		} else if (coldiff == 0) { // Central-axis
			if (rowdiff < 0) { // N
				fly(danaus.Direction.N,danaus.Speed.FAST); 
				System.out.println("Flying N");
			} else if (rowdiff == 0) { // Current Location
				System.out.println("No flight required");
				return;
			} else { // S
				fly(danaus.Direction.S,danaus.Speed.FAST); 
				System.out.println("Flying S");
			}
		} else { // Western-half
			if (rowdiff < 0) { // NW
				fly(danaus.Direction.NW,danaus.Speed.FAST);
				System.out.println("Flying NW");
			} else if (rowdiff == 0) { // W 
				fly(danaus.Direction.W,danaus.Speed.FAST);
				System.out.println("Flying W");
			} else { // SW
				fly(danaus.Direction.SW,danaus.Speed.FAST);
				System.out.println("Flying SW");
			}
		}
	}
	
	public void tryFly() {
		Location desiredLocation = visitStack.pop();
		try {
			flyTo(desiredLocation);
			// Set the current location as visited
			tilesVisited.put(this.location,true);
		}
		catch (danaus.ObstacleCollisionException e) {
			visitStack.remove(desiredLocation);
			tryFly();
		}
	}
	
	public @Override TileState[][] learn() {		
		// Set the current location as visited
		tilesVisited.put(this.location,true);
		
		// Add the starting location to the bottom of the stack
		visitStack.push(this.location);
		
		while(!visitStack.isEmpty()) {
			tryFly();
			
			// Retrieve and load neighboring tiles into the stack
			for(Location neighbor : getNeighbors(this.location)) {
				if(!tilesVisited.containsKey(neighbor)) {
					visitStack.push(neighbor);
				}
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
