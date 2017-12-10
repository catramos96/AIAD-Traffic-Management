package cityStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import resources.Point;

/**
 * Class to represent an intersection that is connected
 * with the in roads and out roads.
 * 
 * Area with 4 points		Area with 1 point
 * 8 Possible Roads			4 Possible Roads
 * Complex Intersection		Simple Intersection
 * 
 * 	_|||_						_| |_
 * 	_X|X_						_ X _		
 *  _X|X_	 					 | |
 * 	 |||
 */
public abstract class Intersection implements Serializable {

	private static final long serialVersionUID = 1L;

	enum EntryType {In,Out,NotPart}					
	enum CellEntry {North, South, East, West}		//Entry Cell Direction.
	
	protected ArrayList<Road> inRoads = new ArrayList<Road>();
	protected ArrayList<Road> outRoads = new ArrayList<Road>();
	
	//To look for entries by their area points
	protected HashMap<Point,HashMap<CellEntry,Road>> entries = new HashMap<Point,HashMap<CellEntry,Road>>();
	
	protected String name = new String();
	
	protected int length = 0; // = weight in Dijkstra
	
	/**
	 * Constructor for area with more than one point.
	 * @param area
	 * @param name
	 */
	public Intersection(ArrayList<Point> area, String name){
		this.name = name;
		
		HashMap<Point,ArrayList<CellEntry>> occupied = getOccupiedAdjacentCells(area);
		
		//Loads the available entries
		for(int i = 0; i < area.size(); i++){
			HashMap<CellEntry,Road> entries_tmp = new HashMap<CellEntry,Road>();
			entries_tmp.put(CellEntry.North, null);
			entries_tmp.put(CellEntry.South, null);
			entries_tmp.put(CellEntry.East, null);
			entries_tmp.put(CellEntry.West, null);
			
			for (CellEntry cellEntry : occupied.get(area.get(i))) {
				entries_tmp.remove(cellEntry);
			}
			
			if(entries_tmp.size() != 0)
				entries.put(area.get(i), entries_tmp);
			
		}
		
		length = entries.keySet().size();
	}
	
	/**
	 * Constructor for area with one point.
	 * @param area
	 * @param name
	 */
	public Intersection(Point area, String name){
		this.name = name;
		
		HashMap<CellEntry,Road> entries_tmp = new HashMap<CellEntry,Road>();
		entries_tmp.put(CellEntry.North, null);
		entries_tmp.put(CellEntry.South, null);
		entries_tmp.put(CellEntry.East, null);
		entries_tmp.put(CellEntry.West, null);
			
		entries.put(area, entries_tmp);
		length = entries.keySet().size();
	}
	
	/**
	 * Method that gives the route out of the intersection starting in one of the
	 * in roads and ending in one of the out roads.
	 * If there is no route, then the method returns an empty array.
	 * @param roadEntry
	 * @param roadOut
	 * @return
	 */
	public abstract ArrayList<Point> getRouteToRoad(String roadEntry,String roadOut);

	/**
	 * Method that returns a String with the main information about the object.
	 * @return
	 */
	public String print(){
		String s = "Intersection " + name + ":\n";
		
		for(Point entry : entries.keySet()){
			for(CellEntry c : entries.get(entry).keySet()){
				Road r = entries.get(entry).get(c);
				if(r!=null)
					s += "   " + entry.print() +  " " + c + " " + r.print() + "\n";
			}
		}
		
		return s;
	}
	
	/*
	 * GETS & SETS
	 */
	
	/**
	 * Returns a road if the given name is the name of an out road
	 * or it will return null.
	 * @param roadName
	 * @return
	 */
	public Road isOutRoad(String roadName){
		for(int i = 0; i < outRoads.size(); i++){
			if(outRoads.get(i).getName().equals(roadName))
				return outRoads.get(i);
		}
		return null;
	}
	
	/**
	 * Returns a road if the given name is the name of an in road
	 * or it will return null.
	 * @param roadName
	 * @return
	 */
	public Road isInRoad(String roadName){
		for(int i = 0; i < inRoads.size(); i++){
			if(inRoads.get(i).getName().equals(roadName))
				return inRoads.get(i);
		}
		return null;
	}
	
	/**
	 * Method that inserts a road in an entry. I returns the type
	 * of entry in which it was inserted.
	 * @param r
	 * @return
	 */
	public EntryType insertRoad(Road r){
		Point startPoint = r.getStartPoint();
		Point endPoint = r.getEndPoint();

		//Search for the cell of the intersection where the road ends/starts
		for(Point p : entries.keySet()){
			
			HashMap<CellEntry, Road> entries_tmp = entries.get(p);

			for(CellEntry key : entries_tmp.keySet()){
				
				if(startPoint != null){
					if(startPoint.equals(getCellEntryPoint(key, p))){
							outRoads.add(r);
						entries_tmp.put(key, r);
						return EntryType.Out;
					}
				}
				if(endPoint != null){
					if(endPoint.equals(getCellEntryPoint(key, p))){
							inRoads.add(r);
						
						entries_tmp.put(key, r);
						return EntryType.In;
					}
				}
			}
		}
		System.out.println("COULDN'T INSERT ROAD");
		return EntryType.NotPart;
	}
	
	/*
	 * GETS & SETS
	 */
	
	/**
	 * Gets the name of the intersection.
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Gets the out roads of the intersection.
	 * @return
	 */
	public ArrayList<Road> getOutRoads(){
		return outRoads;
	}
	
	/**
	 * Gets the in roads of the intersection.
	 * @return
	 */
	public ArrayList<Road> getInRoads(){
		return inRoads;
	}
	
	/**
	 * Gets the area of the intersection.
	 * @return
	 */
	public abstract <T> T getArea();
	
	/**
	 * Gets the occupied adjacent cells. The cells that can't have road entries.
	 * @param area
	 * @return
	 */
	private HashMap<Point,ArrayList<CellEntry>> getOccupiedAdjacentCells(ArrayList<Point> area){
		HashMap<Point,ArrayList<CellEntry>> ret = new HashMap<Point,ArrayList<CellEntry>>();
		
		for(Point p : area) {
			ret.put(p, new ArrayList<CellEntry>());
		}
		
		int n = 1;
		for(Point p1: ret.keySet()){
			int n2 = 0;

			for(Point p2: ret.keySet()){
				
				//skip points treated in the previous for
				if(n2 < n)
					n2++;
				else{
					if(getCellEntryPoint(CellEntry.North, p1).equals(p2)){
						ret.get(p1).add(CellEntry.North);
						ret.get(p2).add(CellEntry.South);
					}
					else if(getCellEntryPoint(CellEntry.South, p1).equals(p2)){
						ret.get(p1).add(CellEntry.South);
						ret.get(p2).add(CellEntry.North);
					}
					else if(getCellEntryPoint(CellEntry.East, p1).equals(p2)){
						ret.get(p1).add(CellEntry.East);
						ret.get(p2).add(CellEntry.West);
					}
					else if(getCellEntryPoint(CellEntry.West, p1).equals(p2)){
						ret.get(p1).add(CellEntry.West);
						ret.get(p2).add(CellEntry.East);
					}
				}
			}
			n++;
		}
		
		return ret;
	}
	
	/**
	 * Gets the entry point of a cell by its CellEntry.
	 * @param entry
	 * @param cell
	 * @return
	 */
	public Point getCellEntryPoint(CellEntry entry, Point cell){
		switch (entry) {
		case North:
			return new Point(cell.x,cell.y+1);
		case South:
			return new Point(cell.x,cell.y-1);
		case East:
			return new Point(cell.x+1,cell.y);
		case West:
			return new Point(cell.x-1,cell.y);
		default:
			return null;
		}
	}

	/**
	 * Gets the area point where an entry point in connected.
	 * @param entry
	 * @return
	 */
	public Point getAreaPointOfEntry(Point entry){
		for(Point p : entries.keySet()){
			
			for(CellEntry c : entries.get(p).keySet()){
				if(getCellEntryPoint(c, p).equals(entry))
					return p;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the length of the intersection.
	 * @return
	 */
	public int getLength(){
		return length;
	}
	
	/**
	 * Gets one point of the intersection area.
	 * @return
	 */
	public Point getOneEntry(){
		for(Point p : entries.keySet())
			return p;
		return null;
	}
	
	/**
	 * Gets the name of the intersection.
	 * @return
	 */
	public String getName(){
		return name;
	}
}
