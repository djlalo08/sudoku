import java.util.ArrayList;
import java.util.HashMap;

public class Row extends Sector{

	HashMap<Integer, Number> list = new HashMap<>();
	int rowNum;
	
	public Row(int rowNum){
		this.rowNum = rowNum;
		for (int i=0; i<Runner.N; i++){
			ArrayList<Tile> listForNum = new ArrayList<>();
			for (int j=rowNum*Runner.N; j<(rowNum+1)*Runner.N; j++){
				if (Runner.list.get(j).isPossible(i)){
					listForNum.add(Runner.list.get(j));
				}
			}
			if (!listForNum.isEmpty()){
				Number newNum = new Number(i+1, listForNum);
				list.put(i+1, newNum);
			}
		}
		System.out.println("Row" + rowNum + " size: " + list.size());
	}
	
	public void numberRemoved(int number){
		
	}
	
	
	
}
