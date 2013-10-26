package student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import danaus.*;

/**
 * This file is the only .java file you will be submitting to CMS. For javadoc
 * specifications, refer to AbstractButterfly. You do not need to copy the==
 * javadocs into this file, but you may if you wish.
 */
public class Butterfly extends AbstractButterfly {						
	private void writeState(int col, int row, TileState[][] a) {
		refreshState();
		a[row][col] = state;
	}
	
	private void printHashMap(HashMap m) {
		Iterator<?> iterator = m.keySet().iterator();  
		   
		while (iterator.hasNext()) {  
		   String key = iterator.next().toString();  
		   Boolean value = (Boolean) m.get(key);  
		   
		   System.out.println(key + " " + value);  
		}  
	}
	
	// Create an hash map to keep track of which tiles have been visited
	private HashMap<Location,Boolean> tilesVisited = new HashMap<Location,Boolean>();
	
	public @Override TileState[][] learn() {
		// Preallocate an array of identical size to the generated map to store the mapped information
		TileState[][] k = new TileState[getMapHeight()][getMapWidth()];
		
		// Store the starting position
		int startRow = this.location.row;
		int startCol = this.location.col;
		
		// Store the tile which the butterfly starts on
		writeState(this.location.row-startRow,this.location.col-startCol,k);
		
		tilesVisited.put(this.location,true);
		
		printHashMap(tilesVisited);
		
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
