package cityStructure;

import java.util.ArrayList;
import java.util.HashMap;

import resources.Point;
import resources.Resources;
import resources.Resources.Direction;

public class ComplexIntersection extends Intersection{

	ArrayList<Point> circuit = new ArrayList<Point>();
	
	public ComplexIntersection(ArrayList<Point> area, String name) {
		super(area, name);
		loadCircuit(area);
	}
	
	/**
	 * Constructor
	 * Creates a new ComplexIntersection equal to ci
	 * but withour any roads associated
	 * @param entries
	 * @param name
	 * @param circuit
	 * @param length
	 */
	private ComplexIntersection(ComplexIntersection ci) {
		super(ci.getCleanedEntries(),ci.getLength(),ci.getName());
		this.circuit = ci.getCircuit();
	}
	
	@Override
	public ComplexIntersection getIntersectionPerception(){
		return new ComplexIntersection(this);
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
		
		route.add(areaOfEntry);
		
		if(areaOfEntry.equals(areaOfOut))
			return route;
		
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
		
		do{
			index ++;
			if(index == circuit.size())
				index = 0;
			lastPoint = circuit.get(index);
			route.add(lastPoint);
		}while(!lastPoint.equals(areaOfOut));
		
		route.add(out.getStartPoint());
		
		return route;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Point> getArea() {
		ArrayList<Point> ret = new ArrayList<Point>();
		
		for(Point p : entries.keySet())
			ret.add(p);
		
		return ret;
	}
	
	public void loadCircuit(ArrayList<Point> area){
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
	
	public ArrayList<Point> getCircuit(){
		return circuit;
	}
}
