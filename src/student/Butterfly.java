package student;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import danaus.*;

public class Butterfly extends AbstractButterfly {
	
	/** An instance fetches the information on the tile and writes it to an array */
	private void writeState(int col, int row, TileState[][] a) {
		refreshState();
		a[row][col] = state;
	}
	
	/** An instance iterates through the tilesVisited HashMap and prints the key-value pair */
	private void printHashTable(Hashtable<Location,Boolean> m) {
		Set<Entry<Location,Boolean>> hashSet = m.entrySet();
        for(Entry<Location,Boolean> entry:hashSet ) {
            System.out.println("Location = "+entry.getKey()+" | Visited = "+entry.getValue());
        }
        System.out.println("---------------");
	}
	
	private void tryFly(TileState[][] k, Hashtable<Location,Boolean> tilesVisited, Direction flyDir) {
		int col_E = this.location.col+1;
		int col_W = this.location.col-1;
		int row_N = this.location.row-1;
		int row_S = this.location.row+1;
		
		Location N 	= new Location(this.location.col,row_N);
		Location NE = new Location(col_E,row_N);
		Location E 	= new Location(col_E,this.location.row);
		Location SE = new Location(col_E,row_S);
		Location S 	= new Location(this.location.col,row_S);
		Location SW = new Location(col_W,row_S);
		Location W 	= new Location(col_W,this.location.row);
		Location NW = new Location(col_W,row_N);
		
		try {
			fly(flyDir,danaus.Speed.FAST);
			tilesVisited.put(this.location,true);
		}
		catch (danaus.ObstacleCollisionException e) {
			switch (flyDir) {
				case N:	 tilesVisited.put(N,true);  break;
				case NE: tilesVisited.put(NE,true); break;
				case E:	 tilesVisited.put(E,true);  break;
				case SE: tilesVisited.put(SE,true); break;
				case S:	 tilesVisited.put(S,true);  break;
				case SW: tilesVisited.put(SW,true); break;
				case W:  tilesVisited.put(W,true);  break;
				case NW: tilesVisited.put(NW,true); break;
			}
		}
		
		int next_col_E = this.location.col+1;
		int next_col_W = this.location.col-1;
		int next_row_N = this.location.row-1;
		int next_row_S = this.location.row+1;
		
		Location nextN 	= new Location(this.location.col,next_row_N);
		Location nextNE = new Location(next_col_E,next_row_N);
		Location nextE 	= new Location(next_col_E,this.location.row);
		Location nextSE = new Location(next_col_E,next_row_S);
		Location nextS 	= new Location(this.location.col,next_row_S);
		Location nextSW = new Location(next_col_W,next_row_S);
		Location nextW 	= new Location(next_col_W,this.location.row);
		Location nextNW = new Location(next_col_W,next_row_N);
		
			 if(!tilesVisited.containsKey(nextE)) 	{ tryFly(k,tilesVisited,Direction.E);  } 
		else if(!tilesVisited.containsKey(nextSE)) 	{ tryFly(k,tilesVisited,Direction.SE); } 
		else if(!tilesVisited.containsKey(nextNE)) 	{ tryFly(k,tilesVisited,Direction.NE); } 
		else if(!tilesVisited.containsKey(nextS)) 	{ tryFly(k,tilesVisited,Direction.S);  } 
		else if(!tilesVisited.containsKey(nextSW)) 	{ tryFly(k,tilesVisited,Direction.SW); }
		else if(!tilesVisited.containsKey(nextW)) 	{ tryFly(k,tilesVisited,Direction.W);  } 
		else if(!tilesVisited.containsKey(nextNW)) 	{ tryFly(k,tilesVisited,Direction.NW); } 
		else if(!tilesVisited.containsKey(nextN)) 	{ tryFly(k,tilesVisited,Direction.N);  }
		else { fly(Direction.opposite(flyDir),danaus.Speed.FAST); }
	}

	public @Override TileState[][] learn() {
		// Preallocate an array of identical size to the generated map to store the mapped information
		TileState[][] k = new TileState[getMapHeight()][getMapWidth()];
		
		// Create an hash map to keep track of which tiles have been visited
		Hashtable<Location,Boolean> tilesVisited = new Hashtable<Location,Boolean>(getMapWidth()*getMapHeight());
		
		// Store the starting position
//		int startRow = this.location.row;
//		int startCol = this.location.col;
		
		// Store the tile which the butterfly starts on
//		writeState(this.location.row-startRow,this.location.col-startCol,k);
		
		tilesVisited.put(this.location,true);
		tryFly(k,tilesVisited,Direction.E);
		
		// printHashMap(tilesVisited);
		
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
