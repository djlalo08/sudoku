import java.util.ArrayList;
import java.util.HashMap;

public class Row {

	int rowNum;
	HashMap<Integer, Number> list = new HashMap<>();
	
	Board parent;
	
	public Row(int rowNum, Board parent){
		this.rowNum = rowNum;
		this.parent = parent;
		for (int i=0; i<P.N; i++){
			ArrayList<Tile> numList = new ArrayList<>();
			for (int j=rowNum*(P.N); j<P.N; j++){
				numList.add(parent.getTile(j));
			}
			list.put(i+1, new Number(i+1, numList));
		}
	}
	
	public void numAdded(int x, int val){
		list.remove(val);
		for (Number n: list.values()){
			n.numberAdded(x, rowNum, val);
		}
	}
}
