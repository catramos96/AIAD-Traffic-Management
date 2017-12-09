package agents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

/**
 * The car that is supervised. 
 * Any debug car messages showing in the console are regarding this subclass.
 * It's an extended class of car just with the purpose of handling the debug
 * messages in a different way and to enable the choice of a different icon
 * in the repast display, such that it can be easily distinguished in the space 
 * from the other cars.
 * The color of this car is blue.
 */
public class CarMonitored extends Car {
	
	/**
	 * Contructor.
	 * @param space
	 * @param origin
	 * @param startRoad
	 * @param destination
	 * @param knowledge
	 * @param mode
	 */
	public CarMonitored(Grid<Object> space, Point origin, Road startRoad, Point destination,CarSerializable knowledge, LearningMode mode) {
		super(space, origin, startRoad, destination,knowledge, mode);	
	}
	
	@Override
	public String print() {
		String ret = "-- Monitored Car Agent Informations --\n";
		ret += "Learning Mode : "+learningMode+"\n";
		ret += "Origin : "+position.print()+"\n";
		ret += "Destination : "+destination.print()+"\n";
		return ret;
	}
	
	@Override
	public void takeDown() {
    	super.takeDown();
    	//serialize agent knowledge    	
    	knowledge.setQualityValues(this.getQLearning().getQualityValues());
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
