package agents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cityStructure.CityMap;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

public class MonitoredCarAgent extends CarAgent {

	//cor azul
	
	public MonitoredCarAgent(Grid<Object> space, CityMap map, Point origin, Point destination, Road startRoad) {
		super(space, map, origin, destination, startRoad, true);	
	}
	
	@Override
	public void takeDown() {
    	super.takeDown();
    	//serialize agent knowledge
    	try {
			String file = new File("").getAbsolutePath();
			file += "\\objs\\car.ser";
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.knowledge);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in car.ser\n");
         } catch (FileNotFoundException f) {
        	 System.out.print("File not loaded");
         }catch (IOException i) {
            i.printStackTrace();
         }
    }

}
