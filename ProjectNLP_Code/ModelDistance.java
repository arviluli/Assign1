/**
 * A model class that encapsulates a NGram model and distance calculated
 * from mystery model.
 * 
 * @author
 *
 */
public class ModelDistance {
	// the n-gram language model
	private NGram model;
	
	// distance of model from mystery model
	private double distance;
	
	/**
	 * Class Constructor
	 * @param model n-gram model
	 * @param distance distance of model from mystery
	 */
	public ModelDistance(NGram model, double distance) {
		this.model = model;
		this.distance = distance;
	}
	
	/**
	 * Get the model
	 * @return the model
	 */
	public NGram getModel() {
		return model;
	}
	
	/**
	 * Get the distance
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}	

}
