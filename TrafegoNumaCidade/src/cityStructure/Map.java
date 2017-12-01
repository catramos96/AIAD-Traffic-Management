package cityStructure;

import java.util.ArrayList;

import agents.CarAgent;
import agents.Semaphore;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import resources.Resources.Direction;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

/**
 * Class that represents the city structure and the connections
 * between roads and intersections.
 */
public class Map extends Agent 
{
	private ArrayList<Intersection> intersections = new ArrayList<Intersection>();
	private ArrayList<Road> roads = new ArrayList<Road>();

	public Map() {
		
		//Intersections top->bottom left->right
	    Intersection i1 = new SimpleIntersection(new Point(1,20), "A");
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
	 
	    roads.add(r1);
	    roads.add(r2);
	    roads.add(r3);
	    roads.add(r4);
	    roads.add(r5);
	    roads.add(r6);
	    roads.add(r7);
	    roads.add(r8);
	    roads.add(r9);
	    roads.add(r10);
	    roads.add(r11);
	    roads.add(r12);
	    roads.add(r13);
	    roads.add(r14);
	    roads.add(r15);
	    roads.add(r16);
	    roads.add(r17);
	    roads.add(r18);
	    roads.add(r19);
	    roads.add(r20);
	    
	    intersections.add(i1);
	    intersections.add(i2);
	    intersections.add(i3);
	    intersections.add(i4);
	    intersections.add(i5);
	    intersections.add(i6);
	    intersections.add(i7);
	    intersections.add(i8);
	    intersections.add(i11);
	    intersections.add(i12);
	    intersections.add(i15);
	    intersections.add(i16);
	    intersections.add(i17);
	    intersections.add(i18);
	    intersections.add(i19);
	    intersections.add(ci1);
	}
	
	public ArrayList<Intersection> getIntersections(){
		return intersections;
	}
	
	public ArrayList<Road> getRoads(){
		return roads;
	}
}
