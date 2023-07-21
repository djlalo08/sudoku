import java.util.ArrayList;

public class Number {

	public int val;
	public ArrayList<Tile> list = new ArrayList<>();
	
	public Number(int val, ArrayList<Tile> list){
		this.val = val;
		this.list = list;
	}
	
	private void myRemove(Tile t){
		if (!list.contains(t)){
			list.remove(t);
			if (list.size() == 1){
				t.mustBe = true;
			}
		}
	}
	
	public String toString(){
		String str = val + "(";
		for (int i =0; i<list.size()-1; i++){
			str += list.get(i).x + "-" + list.get(i).y + ", ";
		}
		str += list.get(list.size()-1).x + "-" + list.get(list.size()-1).y + ")";
		return str;
	}
}
