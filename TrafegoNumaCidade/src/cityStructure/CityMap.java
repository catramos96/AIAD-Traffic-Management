package cityStructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import agents.CarAgent;
import agents.Semaphore;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.grid.Grid;
import resources.MessagesResources;
import resources.Point;
import resources.Resources;
import resources.Resources.Direction;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

/**
 * Class that represents the city structure and the connections
 * between roads and intersections.
 */
public class CityMap extends Agent 
{
	private HashMap<String,Intersection> intersections = new HashMap<String,Intersection>();
	private HashMap<String,Road> roads = new HashMap<String,Road>();

	public CityMap() {
		
		//Intersections top->bottom left->right
	   /* Intersection i1 = new SimpleIntersection(new Point(1,20), "A");
	    Intersection i2 = new SimpleIntersection(new Point(19,20), "B");
	    Intersection i3 = new SimpleIntersection(new Point(1,17), "C");
	    Intersection i4 = new SimpleIntersection(new Point(4,17), "D");
	    Intersection i5 = new SimpleIntersection(new Point(9,17), "E");
	    Intersection i6 = new SimpleIntersection(new Point(10,15), "F");
	    Intersection i7 = new SimpleIntersection(new Point(19,15), "G");
	    Intersection i8 = new SimpleIntersection(new Point(4,12), "H");
	    
	    ArrayList<Point> cInt = new ArrayList<Point>();
	    cInt.add(new Point(9,12));		//i9
	    cInt.add(new Point(10,12));		//i10
	    cInt.add(new Point(9,11));		//i13
	    cInt.add(new Point(10,11));		//i14
	    Intersection ci1 = new ComplexIntersection(cInt, "CX");
	    
	    Intersection i11 = new SimpleIntersection(new Point(19,12), "K");
	    Intersection i12 = new SimpleIntersection(new Point(2,11), "L");
	    Intersection i15= new SimpleIntersection(new Point(16,11), "O");
	    Intersection i16= new SimpleIntersection(new Point(2,7), "P");
	    Intersection i17= new SimpleIntersection(new Point(9,7), "Q");
	    Intersection i18= new SimpleIntersection(new Point(10,1), "R");
	    Intersection i19 = new SimpleIntersection(new Point(16,1), "S");
	    
	    //Horizontal (top-> bottom, left->right)
	    Road r1 = new Road(new Point(18,20),new Point(2,20),i1,i2,"RH1");
	    Road r2 = new Road(new Point(2,17), new Point(3,17),i3,i4,"RH2");
	    Road r3 = new Road(new Point(8,17), new Point(5,17),i4,i5,"RH3");
	    Road r4 = new Road(new Point(18,15),new Point(11,15),i6,i7,"RH4");
	    Road r5 = new Road(new Point(5,12), new Point(8,12),i8,ci1,"RH5");
	    Road r6 = new Road(new Point(11,12), new Point(18,12),ci1,i11,"RH6");
	    Road r7 = new Road(new Point(3,11), new Point(8,11),i12,ci1,"RH7");
	    Road r8 = new Road(new Point(11,11), new Point(15,11),ci1,i15,"RH8");
	    Road r9 = new Road(new Point(8,7),new Point(3,7),i16,i17,"RH9");
	    Road r10 = new Road(new Point(15,1),new Point(11,1),i18,i19,"RH10");
	    //vertical
	    Road r11= new Road(new Point(1,19), new Point(1,18),i1,i3,"RV1");
	    Road r12= new Road(new Point(2,8),new Point(2,10),i12,i16,"RV2");
	    Road r13= new Road(new Point(4,16), new Point(4,13),i4,i8,"RV3");
	    Road r14= new Road(new Point(9,13),new Point(9,16),i5,ci1,"RV4");
	    Road r15= new Road(new Point(9,10), new Point(9,8),ci1,i17,"RV5");
	    Road r16= new Road(new Point(10,14), new Point(10,13),i6,ci1,"RV6");
	    Road r17= new Road(new Point(10,2),new Point(10,10),ci1,i18,"RV7");
	    Road r18= new Road(new Point(16,10), new Point(16,2),i15,i19,"RV8");
	    Road r19= new Road(new Point(19,16),new Point(19,19),i2,i7,"RV9");
	    Road r20= new Road(new Point(19,13), new Point(19,14),i7,i11,"RV10");
	 
	    roads.put(r1.getName(),r1);
	    roads.put(r2.getName(),r2);
	    roads.put(r3.getName(),r3);
	    roads.put(r4.getName(),r4);
	    roads.put(r5.getName(),r5);
	    roads.put(r6.getName(),r6);
	    roads.put(r7.getName(),r7);
	    roads.put(r8.getName(),r8);
	    roads.put(r9.getName(),r9);
	    roads.put(r10.getName(),r10);
	    roads.put(r11.getName(),r11);
	    roads.put(r12.getName(),r12);
	    roads.put(r13.getName(),r13);
	    roads.put(r14.getName(),r14);
	    roads.put(r15.getName(),r15);
	    roads.put(r16.getName(),r16);
	    roads.put(r17.getName(),r17);
	    roads.put(r18.getName(),r18);
	    roads.put(r19.getName(),r19);
	    roads.put(r20.getName(),r20);
	    
	    intersections.put(i1.name,i1);
	    intersections.put(i2.name,i2);
	    intersections.put(i3.name,i3);
	    intersections.put(i4.name,i4);
	    intersections.put(i5.name,i5);
	    intersections.put(i6.name,i6);
	    intersections.put(i7.name,i7);
	    intersections.put(i8.name,i8);
	    intersections.put(i11.name,i11);
	    intersections.put(i12.name,i12);
	    intersections.put(i15.name,i15);
	    intersections.put(i16.name,i16);
	    intersections.put(i17.name,i17);
	    intersections.put(i18.name,i18);
	    intersections.put(i19.name,i19);
	    intersections.put(ci1.name,ci1);*/
	}
	
	public HashMap<String,Intersection> getIntersections(){
		return intersections;
	}
	
	public HashMap<String,Road> getRoads(){
		return roads;
	}
	
	
	public boolean load(String path){
		
		boolean inIntersections = false;
		boolean inRoads = false;
		try {
			File f = new File(path);
			
			if(!f.exists())
				System.out.println("Couldn't load map! Path not valid!");
			else{
				
				FileReader fr = new FileReader(path);
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
							
							String type = parts[0];
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
