package student;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import danaus.*;

public class Butterfly extends AbstractButterfly {	
	
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
			if (rowdiff < 0) { // Desired tile is to the North-east
				try { fly(danaus.Direction.NE,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			} else if (rowdiff == 0) { // Desired tile is to the North
				try { fly(danaus.Direction.N,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			} else { // Desired tile is to the South-east
				try { fly(danaus.Direction.SE,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			}
		} else if (coldiff == 0) { // Central axis
			if (rowdiff < 0) { // North
				try { fly(danaus.Direction.N,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			} else if (rowdiff == 0) { // Desired tile is the current location
				return;
			} else { // South
				try { fly(danaus.Direction.S,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			}
		} else { // Western-half
			if (rowdiff < 0) { // Desired tile is to the North-west
				try { fly(danaus.Direction.NW,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			} else if (rowdiff == 0) { // Desired tile is to the West
				try { fly(danaus.Direction.W,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			} else { // Desired tile is to the South-west
				try { fly(danaus.Direction.SW,danaus.Speed.FAST); }
				catch (danaus.ObstacleCollisionException e) { }
			}
		}
	}
	
	public @Override TileState[][] learn() {
		// Initialize a stack to keep track of the butterfly's flight path
		Stack<Location> visitStack = new Stack<Location>();
		
		// Initialize a hash table to keep track of tiles which have been visited
		Hashtable<Location,Boolean> tilesVisited = new Hashtable<Location,Boolean>();
		
		// Initialize a variable for the current location
		Location currentLocation = this.location;
		
		// Set the current location as visited
		tilesVisited.put(currentLocation,true);
		
		// Add the starting location to the bottom of the stack
		visitStack.push(this.location);
		
		while(!visitStack.isEmpty()) {
			Location desiredLocation = visitStack.pop();
			
			flyTo(desiredLocation);
			
			currentLocation = desiredLocation;
			
			// Set the current tile as visited
			if(!tilesVisited.containsKey(currentLocation)) {
				tilesVisited.put(currentLocation,true);
			}
			
			for(Location neighbor : getNeighbors(currentLocation)) {
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
