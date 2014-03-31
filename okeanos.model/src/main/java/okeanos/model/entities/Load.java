package okeanos.model.entities;

public interface Load {
	int getId();
	
	double getConsumption();
	
	double getConsumption(long minutes);
}
