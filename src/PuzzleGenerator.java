import java.awt.List;
import java.util.ArrayList;
import java.util.Random;

public class PuzzleGenerator {
	
	public static int[] puzzle2 = new int[]{3,1,0,0,0,0,5,0,8,0,0,0,0,0,1,0,9,0,7,0,4,3,0,5,0,0,2,0,0,0,9,0,0,0,0,7,0,0,0,2,0,0,0,3,4,4,0,0,0,0,0,0,0,0,5,0,0,0,0,3,0,0,0,1,0,0,4,8,0,0,6,0,0,6,0,7,0,9,0,8,0};
	public static int[] puzzle1 = new int[]{
			0,0,0,4,0,0,0,0,2,
			0,0,3,0,5,0,4,0,0,
			0,2,0,6,0,0,0,9,0,
			1,0,7,0,0,0,0,0,0,
			0,8,0,0,0,0,0,2,0,
			0,0,0,0,0,0,9,0,5,
			0,3,0,0,0,8,0,1,0,
			0,0,2,0,1,0,5,0,0,
			4,0,0,0,0,3,0,0,0
	};

	public static int[] betterGenerate(){
		int[] arr = new int[P.N2];
		for(int i=0; i<arr.length; i++){
			arr[i] = 0;
		}
		
		Random r = new Random();
		
		Board b = new Board();
		b.fill(arr);

		ArrayList<Tile> tilelist = new ArrayList<>();
		for (Tile t: b.list){
			tilelist.add(t);
		}
		
		while(!tilelist.isEmpty()){
			Tile t = tilelist.remove(0);
			if(t.val!=0) continue;
			int randPos = t.getRandPoss();
			if(randPos==-1) {tilelist.add(t); continue;}
			boolean change = true;
			while(change){
				while(b.queue.size()>0){
					b.nextOnQueue();
				}
				b.triples();
				if (b.queue.size()==0) change = false;
			}
			b.changeVal(t, randPos);
		}
		
		return arr;
	}
	
	public static int[] generate(){ 
		int[] arr = new int[P.N2];
		
		ArrayList<Integer> posns = new ArrayList<>();
		for (int i = 0; i<P.N2; i++){
			posns.add(i);
		}
		
		int max = 0;
		int resets = 0;
		
		outer:
		while(!posns.isEmpty()){
			if (P.N2-posns.size() > max ) {max = P.N2-posns.size(); System.out.println(resets+": "+max);}
			int i = getNextPosition(posns);
			int x = i%P.N;
			int y = i/P.N;
			int val = (int)(Math.random()*P.N)+1;
		
			int count = 0;
			while (arr[i] == 0){
				if (checkCol(x,y,val,arr) && checkRow(x,y,val,arr) && checkSqr(x,y,val,arr)){
					arr[i] = val;
				}
				else{
					val = (int)(Math.random()*P.N)+1;
					count++;
					if (count>P.N2){ //if does not work--reset
						for (int j=0; j<arr.length; j++){
							arr[j]=0;
						}
						posns.clear();
						for (int j = 0; j<P.N2; j++){
							posns.add(j);
						}
						resets++;
						continue outer;
					}
				}
			}
		}
		
		//now blank out everything that I don't want to show
		for (int i=0; i<arr.length; i++){
			if (Math.random()>P.PREpercent) arr[i] = 0;
		}
		return arr;
	}
	
	public int[] generate2(){
		return new int[0];
	}
	
	/**
	 * Gets a random position and removes it from the list
	 * @param posns
	 * @return
	 */
	private static int getPosition(ArrayList<Integer> posns){
		int index = (int)(Math.random()*posns.size());
		return posns.remove(index);
	}
	
	/**
	 * Exactly like getPosition, but rather than pulling a random position, does it in a linear order forward
	 * @param posns
	 * @return
	 */
	private static int getNextPosition(ArrayList<Integer> posns){
		return posns.remove(0);
	}

	private static boolean checkCol(int col, int row, int val, int[] arr){
		for (int i=col; i<P.N2; i+= P.N){
			if (i/P.N != row){
				if (arr[i] == val) return false;
			}
		}
		return true;
	}
	
	private static boolean checkRow(int col, int row, int val, int[] arr){
		for (int i=P.N*row; i<P.N*(row+1); i++){
			if (i%P.N != col){
				if (arr[i] == val) return false;
			}
		}
		return true;
	}
	
	private static boolean checkSqr(int col, int row, int val, int[] arr){
		int colOrig = col;
		int rowOrig = row;
		
		while (col%P.n> 0){
			col--;
		}
		while (row%P.n> 0){
			row--;
		}
		for (int j=0; j<P.n; j++){
			for (int i=0; i<P.n; i++){
				if (col+j != colOrig  ||  row+i != rowOrig){
					if (arr[col+j+P.N*(row+i)] == val) return false;
				}
			}
		}
		return true;
	}
}
