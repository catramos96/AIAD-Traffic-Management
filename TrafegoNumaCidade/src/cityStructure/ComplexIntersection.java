package cityStructure;

import java.util.ArrayList;
import java.util.HashMap;
import resources.Point;
import resources.Resources;
import resources.Resources.Direction;

/**
 * Intersection with more than one point of area.
 * 
 * Example:
 * 
 *  Area with 4 points	
 * 8 Possible Roads			
 * Complex Intersection		
 * 
 * 		_|||_						
 * 		_X|X_							
 * 		_X|X_	 					 
 * 	 	 |||
 *
 */
public class ComplexIntersection extends Intersection {

	private static final long serialVersionUID = 1L;
	ArrayList<Point> circuit = new ArrayList<Point>();
	
	/**
	 * Constructor.
	 * @param area
	 * @param name
	 */
	public ComplexIntersection(ArrayList<Point> area, String name) {
		super(area, name);
		loadCircuit(area);
	}
	
	/**
	 * Constructor just to be used by the behavior LearnMap.
	 * @param area
	 * @param name
	 * @param circuit
	 */
	public ComplexIntersection(ArrayList<Point> area, String name,ArrayList<Point> circuit) {
		super(area, name);
		this.circuit = circuit;
	}
	
	@Override
	public ArrayList<Point> getRouteToRoad(String roadEntryName, String roadOutName) {
		
		ArrayList<Point> route = new ArrayList<Point>();
		
		Road in = isInRoad(roadEntryName);
		Road out = isOutRoad(roadOutName);
		
		if(in == null || out == null)
			return route;
		
		Point areaOfEntry = getAreaPointOfEntry(in.getEndPoint());
		Point areaOfOut = getAreaPointOfEntry(out.getStartPoint());
		
		if(areaOfEntry == null || areaOfOut == null)
			return route;
		
		//Enters the intersection
		route.add(areaOfEntry);
		
		int index = -1;
		
		//Look for position in circuit
		for(int i = 0; i < circuit.size(); i++){
			if(circuit.get(i).equals(areaOfEntry)){
				index = i;
				break;
			}
		}
		
		if(index == -1)
			return route;
		
		Point lastPoint;
		
		//Follow the circuit order until it reaches the out area
		do{
			index ++;
			if(index == circuit.size())
				index = 0;
			lastPoint = circuit.get(index);
			route.add(lastPoint);
		}while(!lastPoint.equals(areaOfOut));
		
		//Adds the start point of out road (of the intersection)
		route.add(out.getStartPoint());
		
		return route;
	}
	
	/**
	 * Loads order of the circuit counter clockwise.
	 * @param area
	 */
	private void loadCircuit(ArrayList<Point> area){
		ArrayList<Direction> circuit_direction = new ArrayList<Direction>();
		
		//Direction counterclockwise
		circuit_direction.add(Direction.North);
		circuit_direction.add(Direction.West);
		circuit_direction.add(Direction.South);
		circuit_direction.add(Direction.East);

		Point lastPoint = area.get(0);
		int dir_index = 0;
		
		circuit.add(lastPoint);
		area.remove(0);

		while(area.size() != 0){
			boolean found = false;
			
			for(int i = 0; i < area.size();){
				Direction adjDir = Resources.getAdjacentDirection(lastPoint, area.get(i));
				
				if(adjDir.equals(circuit_direction.get(dir_index))){
					found = true;
	
					circuit.add(area.get(i));
					lastPoint = area.get(i);
					area.remove(i);
				}
				else
					i++;
			}
			
			if(!found){
				dir_index++;
				if(dir_index == circuit_direction.size())
					dir_index = 0;
			}
			
		}
		
	}
	
	/*
	 * GETS & SETS
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Point> getArea() {
		ArrayList<Point> ret = new ArrayList<Point>();
		
		for(Point p : entries.keySet())
			ret.add(p);
		
		return ret;
	}
	
	/**
	 * Gets the circuit order of the intersection.
	 * @return
	 */
	public ArrayList<Point> getCircuit(){
		return circuit;
	}
}
