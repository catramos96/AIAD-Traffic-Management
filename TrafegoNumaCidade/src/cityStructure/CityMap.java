package cityStructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import resources.MessagesResources;
import resources.Point;
import sajas.core.Agent;

/**
 * Class that represents the city structure and the connections
 * between roads and intersections.
 */
public class CityMap extends Agent implements Serializable
{
	private static final long serialVersionUID = 1L;
	private HashMap<String,Intersection> intersections = new HashMap<String,Intersection>();
	private HashMap<String,Road> roads = new HashMap<String,Road>();

	public CityMap() {
	
	}
	
	public HashMap<String,Intersection> getIntersections(){
		return intersections;
	}
	
	public HashMap<String,Road> getRoads(){
		return roads;
	}
	
	public Road isPartOfRoad(Point p){
		for(Road r : roads.values()){
			if(r.partOfRoad(p))
				return r;
		}
		
		return null;
	}
	
	/*
	 * SAVE & LOAD
	 */
	
	public boolean load(String path){
		
		boolean inIntersections = false;
		boolean inRoads = false;
		try {
			File f = new File(path);
			
			if(!f.exists())
				System.out.println("Couldn't load map! Path not valid!");
			else{
				
				FileReader fr = new FileReader(path);
				@SuppressWarnings("resource")
				BufferedReader reader = new BufferedReader(fr);
				
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					
					
					if(line.equals("INTERSECTIONS"))
						inIntersections = true;
					else if(line.equals("ROADS")){
						inRoads = true;
						inIntersections = false;
					}
					else{
						if(inIntersections){
							String[] parts = line.split(MessagesResources.SEPARATOR);
							
							//String type = parts[0];
							String name = parts[1];
							
							if(parts[0].equals("SIMPLE")){
								Point p = Point.getPoint(parts[2]);
								
								SimpleIntersection i = new SimpleIntersection(p, name);
								
								if(intersections.containsKey(name)){
									System.out.println("Intersection with repeated Name: " + name);
									return false;
								}
								
								intersections.put(name, i);
							}
							else if(parts[0].equals("COMPLEX")){
								
								ArrayList<Point> area = new ArrayList<Point>();
								
								for(int i = 2; i < parts.length; i++){
									Point p = Point.getPoint(parts[i]);
									area.add(p);
								}
								
								ComplexIntersection i = new ComplexIntersection(area, name);
								
								if(intersections.containsKey(name)){
									System.out.println("Intersection with repeated Name");
									return false;
								}
									
								intersections.put(name, i);
							}
						}
						else if(inRoads){
							String[] parts = line.split(MessagesResources.SEPARATOR);
							
							String name = parts[0];
							Point start = Point.getPoint(parts[1]);
							Point end = Point.getPoint(parts[2]);
							String i1Name = parts[3];
							String i2Name = parts[4];
							
							if(intersections.containsKey(i1Name) && intersections.containsKey(i2Name)){
								Road r = new Road(start, end, intersections.get(i1Name), intersections.get(i2Name), name);
								
								if(roads.containsKey(name)){
									System.out.println("Roads with the same name!");
									return false;
								}
								
								roads.put(name, r);
									
							}
							else{
								System.out.println("Road " + name + " doesn't match intersections " + i1Name + " and " + i2Name);
								return false;
							}
						
						}
					
					}

				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	public void save(String path){
		try {
			File f = new File(path);
			
			if(!f.exists())
				f.createNewFile();
			
			PrintWriter out = new PrintWriter(path);
			
			out.println("INTERSECTIONS");
			
			for(Intersection i : intersections.values()){
				if(i.getClass().equals(SimpleIntersection.class)){
					out.println("SIMPLE" + 
							MessagesResources.SEPARATOR + i.name + 
							MessagesResources.SEPARATOR + 
							((SimpleIntersection)i).getArea().print());
				}
				else if(i.getClass().equals(ComplexIntersection.class)){
					String s = "";
					
					s += "COMPLEX" + MessagesResources.SEPARATOR + i.name;
					
					for(Point p : ((ComplexIntersection)i).getArea())
						s+= MessagesResources.SEPARATOR + p.print();
					
					out.println(s);
				}
			}
			
			out.println("ROADS");
			
			for(Road r : roads.values()){
				String s = "";
				
				s += r.getName() + MessagesResources.SEPARATOR + 
						r.getStartPoint().print() + MessagesResources.SEPARATOR + 
						r.getEndPoint().print() + MessagesResources.SEPARATOR + 
						r.getStartIntersection().name + MessagesResources.SEPARATOR + 
						r.getEndIntersection().name + MessagesResources.SEPARATOR;
				
				out.println(s);
			}
			
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
