import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class Guppy extends Fish {

	public Guppy(PApplet p, int x, int y) {
		super(p, x, y);
		// TODO Auto-generated constructor stub
	}
	
	// applies various swarm forces
	protected void school(ArrayList<Guppy> guppies, float aC, float cC, float sC, float fC,
			float pullDist, float desiredSep, float scareDist, Predator p) {
		// init. each force
		PVector a = align(guppies, pullDist);
		PVector s = separate(guppies, desiredSep);
		PVector c = cohesion(guppies, pullDist);
		PVector f = flight(scareDist, p);
		// apply weights
		a.mult(aC);
		s.mult(sC);
		c.mult(cC);
		f.mult(fC);
		// apply each force
		applyForce(c);
		applyForce(s);
		applyForce(a);
		applyForce(f);

	}
	
	// run method for fish
	public void run(ArrayList<Guppy> guppies, float aC, float cC, float sC, float fC, float pullDist,
			float desiredSep, float scareDist, Predator p) {
		school(guppies, aC, cC, sC, fC, pullDist, desiredSep, scareDist, p);
		update();
		borders();
		render();
	}
	
	@Override
	public void render() {
		parent.pushMatrix();
		parent.fill(127);
		parent.ellipse(position.x,position.y,10,10);
		parent.popMatrix();
	}

	// keeps fish moving in similar direction
	private PVector align(ArrayList<Guppy> guppies, float pullDist) {
		// PVector to hold sum of direction
		PVector sum = new PVector(0,0);
		// counter to divide by
		int count = 0;
		// loop through all fish
		for (Guppy other : guppies) {
			// measure distance between current fish and others
			float d = PVector.dist(position,other.position);
			// if between 0 and pull distance
			if ((d > 0) && (d < pullDist)) {
				// add vector of neighbor to sum
				sum.add(other.velocity);
				count++;
			}
		}
		// if other fish w/in pull distance
		if (count > 0) {
			// divide sum by count
			sum.div((float)count);
			sum.normalize();
			sum.mult(maxspeed);
			// get new steering vector
			PVector steer = PVector.sub(sum,velocity);
			steer.limit(maxforce);
			return steer;
		} else {
			return new PVector(0,0);
		}
	}

	// steers away from fish that are too close
	private PVector separate(ArrayList<Guppy> guppies, float desiredSep) {
		PVector steer = new PVector();
		int count = 0;
		// loop through all fish
		for (Guppy other : guppies) {
			// check distance like in align()
			float d = PVector.dist(position, other.position);
			// if d is within range
			if ((d > 0) && (d < desiredSep)) {
				// get vector away from neighbor 
				// USE THIS FOR FLEE METHOD??
				PVector diff = PVector.sub(position,  other.position);
				diff.normalize();
				// weight by distance ???
				diff.div(d*2);
				steer.add(diff);
				count++;
			}
		}
		// get average
		if (count > 0) {
			steer.div(count);
		}
		// if there is a direction
		if (steer.mag() > 0) {
			// steering = desired - velocity
			// Reynolds
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		return steer;
	}

	// steers fish towards center of mass
	private PVector cohesion(ArrayList<Guppy> guppies, float pullDist) {
		PVector sum = new PVector(0,0);
		int count = 0;
		// loop thru all fish
		for (Guppy other : guppies) {
			// if within pull distance (
			float d = PVector.dist(position, other.position);
			if ((d > 0 ) && (d < pullDist)) {
				sum.add(other.position);
				count++;
			}
		}
		if (count > 0 ) {
			sum.div(count);
			return seek(sum);
		} else {
			return new PVector(0,0);
		}
	}

	// steers away from fish that are too close to mouse
	private PVector flight(float scareDist, Predator p) {
		PVector steer = new PVector();
		//PVector predator = new PVector(parent.mouseX, parent.mouseY);
		// check distance like in align()
		float d = PVector.dist(position, p.position);
		// if d is within range
		if ((d > 0) && (d < scareDist)) {
			// get vector away from mouse 
			PVector diff = PVector.sub(position, p.position);
			diff.normalize();
			// weight by distance ???
			diff.div(d);
			steer.add(diff);
			
		}
		if (steer.mag() > 0) {
			// steering = desired - velocity
			// Reynolds
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
			return steer; 
		} else {
		return new PVector(0,0);
		}
	}

}
