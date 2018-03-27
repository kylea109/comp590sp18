package tcc;

public class TemporalPixel {
	
	private int intensity;
	private int lifespan;
	private int current_age;
	private TemporalPixel successor;

	public TemporalPixel(int intensity, int lifespan, TemporalPixel successor) {
		this.intensity = intensity;
		this.lifespan = lifespan;
		this.current_age = 0;
		this.successor = successor;
	}
	
	public TemporalPixel(int intensity, int lifespan) {
		this.intensity = intensity;
		this.lifespan = lifespan;
		this.current_age = 0;
		this.successor = null;
	}
	
	public int getIntensity() {
		if (current_age == lifespan) {
			return successor.getIntensity();
		}
		return intensity;
	}
	
	public int getLifespan() {
		return lifespan;
	}
	
	public TemporalPixel getNext() {
		return successor;
	}
	
	public void addState(TemporalPixel new_state) {
		if (successor == null) {
			successor = new_state;
		} else {
			successor.addState(new_state);
		}
	}
	
	public void ageByFrame() {
		if (current_age == lifespan) {
			successor.ageByFrame();
		} else {
			current_age += 1;
		}
	}
	
}
