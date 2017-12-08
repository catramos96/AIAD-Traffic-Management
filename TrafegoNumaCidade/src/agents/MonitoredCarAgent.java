package agents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cityStructure.CityMap;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

public class MonitoredCarAgent extends CarAgent {

	//cor azul
	
	public MonitoredCarAgent(Grid<Object> space, CityMap map, Point origin, Point destination, Road startRoad, CarAgent.LearningMode mode) {
		super(space, map, origin, destination, startRoad, mode);	
	}
	
	@Override
	public void takeDown() {
    	super.takeDown();
    	//serialize agent knowledge    	
    	try {
    		String path = new File("").getAbsolutePath();
			path += "\\objs\\"+this.knowledge.getFilename();
			File ser = new File(path);
    		ser.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(ser);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.knowledge);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data in car.ser\n");
         }catch (IOException i) {
            i.printStackTrace();
         }
    }

}
