package agents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

public class MonitoredCarAgent extends Car {

	//cor azul
	
	public MonitoredCarAgent(Grid<Object> space, Point origin, Road startRoad, Point destination,CarSerializable knowledge, LearningMode mode) {
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
