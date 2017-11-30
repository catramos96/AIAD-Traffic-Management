package trafegoNumaCidade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.math3.analysis.function.Add;

import trafegoNumaCidade.Point;

import trafegoNumaCidade.Semaphore;

/*
 * Area with 4 points		Area with 1 point
 * 8 Possible Roads			4 Possible Roads
 * 
 * 	_|||_						_| |_
 * 	_X|X_						_ X _		
 *  _X|X_	 					 | |
 * 	 |||
 */
public abstract class Intersection extends CityElement{
	enum EntryType {In,Out,NotPart}
	enum CellEntry {North, South, East, West}
	
	protected ArrayList<Road> inRoads = new ArrayList<Road>();
	protected ArrayList<Road> outRoads = new ArrayList<Road>();
	protected Semaphore semaphore = null;
	protected HashMap<Point,HashMap<CellEntry,Road>> entries = new HashMap<Point,HashMap<CellEntry,Road>>();
							
	
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
	}
	
	public Intersection(Point area, String name){
		this.name = name;
		
		HashMap<CellEntry,Road> entries_tmp = new HashMap<CellEntry,Road>();
		entries_tmp.put(CellEntry.North, null);
		entries_tmp.put(CellEntry.South, null);
		entries_tmp.put(CellEntry.East, null);
		entries_tmp.put(CellEntry.West, null);
			
		entries.put(area, entries_tmp);
	}
	
	public abstract ArrayList<Point> getRouteToRoad(Road roadEntry,Road roadOut);

	/*
	 * GETS & SETS
	 */
	public boolean isOutRoad(Road r){
		for(int i = 0; i < outRoads.size(); i++){
			if(outRoads.get(i).equals(r))
				return true;
		}
		return false;
	}
	
	public EntryType insertRoad(Road r){
		Point startPoint = r.getStartPoint();
		Point endPoint = r.getEndPoint();

		//Search for the cell of the intersection where the road ends/starts
		for(Point p : entries.keySet()){
			
			HashMap<CellEntry, Road> entries_tmp = entries.get(p);

			for(CellEntry key : entries_tmp.keySet()){
				
				if(startPoint.equals(getCellEntryPoint(key, p))){
					outRoads.add(r);
					entries_tmp.put(key, r);
					return EntryType.Out;
				}
				else if(endPoint.equals(getCellEntryPoint(key, p))){
					inRoads.add(r);
					entries_tmp.put(key, r);
					return EntryType.In;
				}
			}
		}
		System.out.println("COULDN'T INSERT ROAD");
		return EntryType.NotPart;
	}
	
	public void setSemaphore(Semaphore s){
		semaphore = s;
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
	
	public Semaphore getSemaphore(){
		return semaphore;
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
}
