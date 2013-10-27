package danaus;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/** 
 * An instance represents the park on which a butterfly flies. In terms of 
 * abstraction, a park is aware of the concept of a simulation. A park knows
 * about turns and score and slowness and victory. A Park is mainly a map, 
 * some state about the game, and a means for assessing victory. It does not,
 * however, know anything about a GUI. It passes all necessary information up
 * to the Simulator to have it handle the interface with the GUI.
 */
public class Park {
	/** The minimum fraction of total flowers that the butterfly must find. */
	public static double MIN_REQUIRED_FLOWERS_FRACTION = 0.5;
	/** The maximum fraction of total flowers that the butterfly must find. */
	public static double MAX_REQUIRED_FLOWERS_FRACTION = 1.0;
	/** The simulator driving this park. */
	Simulator simulator;
	
	/** The map of this park. */
	Map map;
	
	/** Various information about the state of the game. */
	ParkState state;
	/** The score of the tile states returned by the butterfly after it has
	 * learned the map, as a percentage. */
	double learningScore;
	/** The current phase of the simulation. */
	SimulationPhase phase;
	
	/** The time taken for a butterfly to learn a map. */
	long learningTime;
	/** The time taken for a butterfly to execute on a map. */
	long runningTime;	
	
	/**
     * Initialize this park with a random map associated with simulator sim.
	 */
	Park(Simulator sim) {
        // Initialize the park state first, since the map references it.
		phase = SimulationPhase.LEARNING;
		this.simulator = sim;
		state = new ParkState();
		map = new Map(this);
	
		state.allFlowers = map.getFlowers(); 
		state.foundFlowers = new ArrayList<Flower>();
	}
	
	/** 
	 * Initializes a park from a map file. 
	 * 
	 * @param sim A simulator.
	 * @param f The filename of the map file.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	Park(Simulator sim, String f) throws 
	ParserConfigurationException, SAXException, IOException {
        // Initialize the park state first, as the map references it.
		phase = SimulationPhase.LEARNING;
		simulator = sim;
		state = new ParkState();
		map = new Map(this, f);
		
		state.allFlowers = map.getFlowers(); 
		state.foundFlowers = new ArrayList<Flower>();
	}

	
	/**
	 * Begins a simulation. A butterfly first learns a map and then executes
	 * on the map. 
	 */ 
	void simulate() {
		long start = 0;
		long stop = 0;
		
		// Learn the map
		Debugger.DEBUG("Begin Learning...");
		
		start = System.nanoTime();
		TileState[][] states = map.butterfly.learn();
		stop = System.nanoTime();
		
		learningScore = gradeStates(states);
		learningTime = stop - start;
		
		// Run the map
		Debugger.DEBUG("Begin Running...");
		phase = SimulationPhase.RUNNING;
		map.reflower();
		simulator.retile();
		ArrayList<Flower> allFlowers = new ArrayList<Flower>();
		allFlowers.addAll(map.learningFlowers);
		allFlowers.addAll(map.runningFlowers);
		int requiredFlowersSize = map.rand.nextInt(
			(int) (MIN_REQUIRED_FLOWERS_FRACTION * allFlowers.size()),
			(int) (MAX_REQUIRED_FLOWERS_FRACTION * allFlowers.size())
		);
		state.requiredFlowers = 
				(ArrayList<Flower>) map.rand.sample(allFlowers, requiredFlowersSize);
		
		start = System.nanoTime();
		map.butterfly.run(state.requiredFlowers);
		stop = System.nanoTime();
		
		runningTime = stop - start;
	}
	
	/**
	 * Grades a set of tile states against the true tile states of the map. A
	 * point is awarded for every tile provided that matches the actual tiles.
	 * If any part of the provided map is null, a score of zero is awarded. 
	 * 
	 * @param states A two dimensional array of tile states. 
	 * @return A percentage of correct tile states.
	 */
	private double gradeStates(TileState[][] states) {
		if (null == states || null == states[0]) {
			return 0.0;
		}
		
		int actualHeight = map.tiles.length;
		int actualWidth = map.tiles[0].length;
		int statesHeight = states.length;
		int statesWidth = states[0].length;
		
		int width = Math.min(actualWidth, statesWidth);
		int height = Math.min(actualHeight, statesHeight);
		
		int points = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (!map.tiles[row][col].flyable || 
					map.tiles[row][col].tileState.equals(states[row][col])) {
					points++;
				}
			}
		}
		
		return points / (double) (actualHeight * actualWidth) * 100;
	}
	
	/** 
     * Send a request up to the Simulator to handle potential 
	 * GUI updates. This is usually done only for northbound requests.
	 * 
	 * @param slowDown The amount of slow down of the move. The fastest move
	 * possible is -1. The slowest is 2. This can change if wind speed is made
	 * to affect move speed. Currently it does not.
	 * @param heading The direction to face while travelling.
	 * @param fromRow The source row.
	 * @param fromCol The source column.
	 * @param toRow The destination row.
	 * @param toCol The destination column.
	 */
	public void update(int slowDown, Direction heading, 
			int fromRow, int fromCol, int toRow, int toCol) {
		simulator.update(slowDown, heading, fromRow, fromCol, toRow, toCol);
	}
	
	/** 
     * Return the number of flowers the butterfly correctly found, and, if
	 * print is true, print differences between the expected flowers and the 
	 * found flowers. 
     */
	public int numberCorrect(boolean print) {
		int correct = 0;
		
		for (int i = 0; i < state.requiredFlowers.size(); ++i) {
			boolean foundExists = i < state.foundFlowers.size();
			Flower flower = state.requiredFlowers.get(i);
			
			if (foundExists) {
				Flower foundFlower = state.foundFlowers.get(i);
				if (foundFlower == flower) {
					++correct;
				}
				else {
					if (print) {
					String warning = String.format(
							"Expected %s at index %d, but got %s!", 
							flower, i, foundFlower);
					Debugger.WARNING(warning);
					}
				}
			}
			else {
				if (print) {
				String warning = String.format(
					"Expected %s at index %d, but no flower at that index!", 
					flower, i);
				Debugger.WARNING(warning);
				}
			}
		}
		
		return correct;
	}
	
	/** 
     * Return the victory status of the park --true iff the butterfly collected
	 * all the flowers. 
     */
	public boolean isVictorious() {
		return numberCorrect(false) == state.requiredFlowers.size();
	}
	
	
	public boolean isVictoriousA5() {
		return state.exploredTiles == state.numTiles;
	}
}
