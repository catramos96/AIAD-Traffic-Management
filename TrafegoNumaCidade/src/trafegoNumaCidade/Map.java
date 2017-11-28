package trafegoNumaCidade;

import java.util.ArrayList;

import sajas.core.Agent;

public class Map extends Agent 
{
	
	private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private ArrayList<Road> roads = new ArrayList<Road>();

	public Map() {
		//Test square
		Intersection i1 = new Intersection(new Point(0,0),"A");
		Intersection i2 = new Intersection(new Point(0,10),"B");
		Intersection i3 = new Intersection(new Point(10,10),"C");
		Intersection i4 = new Intersection(new Point(10,0),"D");
		
		Road r1 = new Road(new Point(0,1),new Point(0,9));
		Road r2 = new Road(new Point(1,10),new Point(9,10));
		Road r3 = new Road(new Point(10,9),new Point(10,1));
		Road r4 = new Road(new Point(9,0),new Point(1,0));

		
		i1.insertRoad(r1);
		i2.insertRoad(r1);
		i2.insertRoad(r2);
		i3.insertRoad(r2);
		i3.insertRoad(r3);
		i4.insertRoad(r3);
		i4.insertRoad(r4);
		i1.insertRoad(r4);
		
		intersections.add(i1);
		intersections.add(i2);
		roads.add(r1);
	}
	
	public boolean updateCarRoad(CarAgent car, Point position){

		for(int i = 0; i < roads.size(); i++){
				car.setRoad(roads.get(i));
				return true;
		}
		
		/*
		for(int i = 0; i < intersections.size(); i++){
			if(intersections.get(i).getArea().equals(position))
				car.setIntersection(intersections.get(i));
		}*/
		
		return false;
	}
	
	public ArrayList<Intersection> getIntersections(){
		return intersections;
	}
	
	public ArrayList<Road> getRoads(){
		return roads;
	}
}
