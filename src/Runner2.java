import java.util.ArrayList;

public class Runner2 {

	public static void main(String[] args){
		makeABoard();
	}
	
	public static void makeABoard(){
		new Board();
	}
	
	public static void makeABoard(ArrayList<Tile> list){
		new Board(list);
	}
}
