
public class QueueEvent {

	int valToSet;
	Tile tileToSet;
	
	public QueueEvent(int val, Tile t){
		valToSet = val;
		tileToSet = t;
	}
	
	public String toString(){
		return "{" + tileToSet.x + "," + tileToSet.y + ": " + valToSet + "}\n";
	}
}
