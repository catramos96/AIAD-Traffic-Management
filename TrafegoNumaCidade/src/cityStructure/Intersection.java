package cityStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import resources.Point;

/*
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
	enum CellEntry {North, South, East, West}
	
	protected ArrayList<Road> inRoads = new ArrayList<Road>();
	protected ArrayList<Road> outRoads = new ArrayList<Road>();
	protected HashMap<Point,HashMap<CellEntry,Road>> entries = new HashMap<Point,HashMap<CellEntry,Road>>();
	protected String name = new String();
	
	protected int length = 0; // = weight in Dijkstra
	
	public Intersection(ArrayList<Point> area, String name){
		this.name = name;
		
		HashMap<Point,ArrayList<CellEntry>> occupied = getOccupiedAdjacentCells(area);
		
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
	
	public abstract ArrayList<Point> getRouteToRoad(String roadEntry,String roadOut);

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
	public Road isOutRoad(String roadName){
		for(int i = 0; i < outRoads.size(); i++){
			if(outRoads.get(i).getName().equals(roadName))
				return outRoads.get(i);
		}
		return null;
	}
	
	public Road isInRoad(String roadName){
		for(int i = 0; i < inRoads.size(); i++){
			if(inRoads.get(i).getName().equals(roadName))
				return inRoads.get(i);
		}
		return null;
	}
	
	public EntryType insertRoad(Road r, boolean substituteRoad){
		Point startPoint = r.getStartPoint();
		Point endPoint = r.getEndPoint();

		//Search for the cell of the intersection where the road ends/starts
		for(Point p : entries.keySet()){
			
			HashMap<CellEntry, Road> entries_tmp = entries.get(p);

			for(CellEntry key : entries_tmp.keySet()){
				
				if(startPoint != null){
					if(startPoint.equals(getCellEntryPoint(key, p))){
						if(substituteRoad)
							updateEntry(r.getName(),r,false);
						else
							outRoads.add(r);
						entries_tmp.put(key, r);
						return EntryType.Out;
					}
				}
				if(endPoint != null){
					if(endPoint.equals(getCellEntryPoint(key, p))){
						if(substituteRoad)
							updateEntry(r.getName(),r,true);
						else
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
	
	private void updateEntry(String roadName,Road road, boolean inRoad){
		if(inRoad){
			for(int i = 0; i < inRoads.size(); i++){
				if(roadName.equals(inRoads.get(i).getName()))
					inRoads.set(i, road);
			}
		}
		else{
			for(int i = 0; i < outRoads.size(); i++){
				if(roadName.equals(outRoads.get(i).getName()))
					inRoads.set(i, road);
			}
		}
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public ArrayList<Road> getOutRoads(){
		return outRoads;
	}
	
	public ArrayList<Road> getInRoads(){
		return inRoads;
	}
	
	public abstract <T> T getArea();
	
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

	public Point getAreaPointOfEntry(Point entry){
		for(Point p : entries.keySet()){
			
			for(CellEntry c : entries.get(p).keySet()){
				if(getCellEntryPoint(c, p).equals(entry))
					return p;
			}
		}
		
		return null;
	}
	
	public int getLength(){
		return length;
	}
	
	protected HashMap<Point,HashMap<CellEntry,Road>> getPerceptionsEntries(ArrayList<Road> inR,ArrayList<Road> outR){
		HashMap<Point,HashMap<CellEntry,Road>> tmp = new HashMap<Point,HashMap<CellEntry,Road>>();
		inR.clear();
		outR.clear();
		
		for(Point area: entries.keySet()){
			
			HashMap<CellEntry, Road> tmp2 = new HashMap<CellEntry, Road>();
			
			for(CellEntry entry : entries.get(area).keySet()){
				
				Road r = entries.get(area).get(entry);
				Road roadPerc = null;
				
				if(r != null){
					if(r.getEndIntersection().equals(this)){		//inRoad
						roadPerc = r.getRoadPerceptionAtEnd();
						tmp2.put(entry,roadPerc);
						inR.add(roadPerc);
					}
					else	{										//endRoad
						roadPerc = r.getRoadPerceptionAtStart();
						tmp2.put(entry, roadPerc);
						outR.add(roadPerc);

					}
				}
				else
					tmp2.put(entry, null);

			}
			tmp.put(area, tmp2);
		}
		
		return tmp;
	}
	
	//getEntry
	public Point getOneEntry(){
		for(Point p : entries.keySet())
			return p;
		return null;
	}
	
	public String getName(){
		return name;
	}
}
