import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Board extends JFrame{
	
	boolean showHints = false;
	boolean minus = false;
	
	private static final long serialVersionUID = 5820789513365170788L;
	public Tile selected;

	boolean filled = false;
	ArrayList<Tile> list = new ArrayList<>();
	
	LinkedList<QueueEvent> queue = new LinkedList<>();
	ArrayList<Tile> tilesInQueue = new ArrayList<>();
	
	int markedCount = 0;
	public Board(){
		super("Sudoku2");
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(P.N,P.N,0,0));
		add(mainPane);
		
		//populate with tiles
		for (int i = 0; i<P.N*P.N; i++){
			Tile t = new Tile(i%P.N, i/P.N, this);
			list.add(t);
			mainPane.add(t);
		}
		
		addKeyListener(new KeyListener(){

		public void keyTyped(KeyEvent e) {
			System.out.println("keypressed");
			char c = e.getKeyChar();

			if (Character.isDigit(c)){ 
				if ( (selected!= null)) {
					if (minus) {
						selected.notPossible(Integer.parseInt(c+""));
						changeVal(selected, selected.val);
					}
					else {changeVal(selected, Integer.parseInt(c + ""));}
					repaint();
				}
				return;
			}
			else{
				switch (c){
				
				//new
				case 'n':
					Runner2.makeABoard();
					break;
					
				//fill with puzzle1
				case 'l':
					fill(PuzzleGenerator.puzzle1);
					break;
					
				//betterGeneration
				case 'b':
					fill(PuzzleGenerator.betterGenerate());
					break;
					
				case 't':
					triples();
					break;
					
				//fill
				case 'f':
					if (!filled){
						fill(PuzzleGenerator.generate());
						filled =true;
					}
					break;
					
				//set minus
				case '-':
					minus = !minus;
					repaint();
					break;
					
				//use hint
				case 'e':
					nextOnQueue();
					break;
					
				//duplicate
				case 'c':
					Runner2.makeABoard(list);
					
				//toggle hints
				case 'h':
					showHints = !showHints;
					repaint();
					break;
				
				//arrows
				case 'w':
					if (selected != null && selected.y !=0){
						select( list.get((selected.y-1)*P.N+selected.x) );
					}
					break;
				case 's':
					if (selected != null && selected.y != P.N-1){
						select( list.get((selected.y+1)*P.N+selected.x) );
					}
					break;
				case 'a':
					if (selected != null && selected.x !=0){
						select( list.get((selected.y)*P.N+selected.x-1) );
					}
					break;
				case 'd':
					if (selected != null && selected.x != P.N-1){
						select( list.get((selected.y)*P.N+selected.x+1) );
					}
					break;
				}
				if (e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE){
					changeVal(selected, 0);
				}
			}
		}
		
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {} });
		
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public Board(ArrayList<Tile> list){
		this();	
		for (int i =0; i<list.size(); i++){
			Tile t = list.get(i);
			if (t.val != 0){
				markedCount++;
				changeVal (this.list.get(i), t.val);
				if (t.preSet){
					this.list.get(i).preSet = true;
				}
			}
		}
		filled = true;
	}
	
	public void fill(int[] startList){
		for (int i=0; i<list.size(); i++){
			changeVal(list.get(i), startList[i]);
			if (startList[i] != 0) {list.get(i).preSet = true;}
		}
	}
	
	void changeVal(Tile selected, int val){
		int x = selected.x;
		int y = selected.y;
		
		if (selected.preSet) return;
		
		if (val != 0 && !(checkCol(x, y, val) && checkRow(x, y, val) && checkSqr(x, y, val))){
			//don't allow change
			System.out.println("illegal move");
			return;
		}

		int lastVal = selected.lastVal;
		selected.val = val;

		if (lastVal == 0){
			markedCount++;
		}
		if (val == 0){
			markedCount--;
		}
		if (markedCount == P.N2){
			checkForWin(this);
		}
		updatePsblCol(x, val, lastVal);
		updatePsblRow(y, val, lastVal);
		updatePsblSqr(x, y, val, lastVal);
		
		for (int i = 0; i<P.N; i++){
			updateUnqCol(x, val);
			updateUnqRow(y, val);			
		}
		for (int i=0; i<P.n; i++){
			for (int j=0; j<P.n; j++){
				updateUnqSqr(i*P.n, j*P.n, val);
			}
		}
		
		enqueueCol(selected.x);
		enqueueRow(selected.y);
		enqueueSqr(selected.x, selected.y);
		
		selected.lastVal = selected.val;
		
		repaint();
	}

		private void updatePsblCol(int col, int val, int lastVal){
			for (int i=col; i<P.N2; i+= P.N){
				Tile t = list.get(i);
				t.nowPossible(lastVal);
				t.notPossible(val);
			}
		}
		
		private void updatePsblRow(int row, int val, int lastVal){
			for (int i=P.N*(row); i<P.N*(row+1); i++){
				list.get(i).nowPossible(lastVal);
				list.get(i).notPossible(val);
			}
		}

		private void updatePsblSqr(int col, int row, int val, int lastVal){
			
			while (col%P.n> 0){
				col--;
			}
			while (row%P.n> 0){
				row--;
			}
			for (int j=0; j<P.n; j++){
				for (int i=0; i<P.n; i++){
					list.get(col+j+P.N*(row+i)).nowPossible(lastVal);
					list.get(col+j+P.N*(row+i)).notPossible(val);
				}
			}
		}
		
		private void updateUnqCol(int col, int val){
			//this is the slow version
			ArrayList<Integer> numlist = new ArrayList<>();
			for (int i =0; i<P.N; i++){
				numlist.add(i+1);
			}
			for (int i=col; i<P.N2; i+=P.N){
				int val2 = list.get(i).val;
				numlist.remove(Integer.valueOf(val2));
			}
			
			HashMap<Integer, ArrayList<Tile>> map = new HashMap<>();
			for (Integer i: numlist){
				map.put(i, new ArrayList<>());
			}
			for (int i=col; i<P.N2; i+=P.N){
				Tile t = list.get(i);
				if (t.val == 0){
					for (int j=0; j<P.N; j++){
						if (t.isPossible(j+1)){
							map.get(j+1).add(t);
						}
					}
				}
			}
			for (int i=0; i<P.N; i++){
				ArrayList<Tile> thisList = map.get(i+1);
				if (thisList!=null && thisList.size() == 1){
					thisList.get(0).isUqInCol = true;
					thisList.get(0).uqVal = i+1;
					addToQueue(i+1, thisList.get(0));
				}
			}			
		}
		
		private void updateUnqRow(int row, int val){
			//this is slow version
			ArrayList<Integer> numlist = new ArrayList<>();
			for (int i =0; i<P.N; i++){
				numlist.add(i+1);
			}
			for (int i=row*P.N; i<(row+1)*P.N; i++){
				int val2 = list.get(i).val;
				numlist.remove(Integer.valueOf(val2));
			}
			
			HashMap<Integer, ArrayList<Tile>> map = new HashMap<>();
			for (Integer i: numlist){
				map.put(i, new ArrayList<>());
			}
			for (int i=row*P.N; i<(row+1)*P.N; i++){
				Tile t = list.get(i);
				if (t.val == 0){
					for (int j=0; j<P.N; j++){
						if (t.isPossible(j+1)){
							map.get(j+1).add(t);
						}
					}
				}
			}
			for (int i=0; i<P.N; i++){
				ArrayList<Tile> thisList = map.get(i+1);
				if (thisList!=null && thisList.size() == 1){
					thisList.get(0).isUqInRow = true;
					thisList.get(0).uqVal = i+1;
					addToQueue(i+1, thisList.get(0));
				}
			}
		}

		private void updateUnqSqr(int col, int row, int val){
			ArrayList<Integer> numlist = new ArrayList<>();
			for (int i =0; i<P.N; i++){
				numlist.add(i+1);
			}
			
			while (col%P.n> 0){
				col--;
			}
			while (row%P.n> 0){
				row--;
			}
			for (int j=0; j<P.n; j++){
				for (int i=0; i<P.n; i++){
					int pos = col+j+P.N*(row+i);
					int val2 = list.get(pos).val;
					numlist.remove(Integer.valueOf(val2));
				}
			}
			
			HashMap<Integer, ArrayList<Tile>> map = new HashMap<>();
			for (Integer i: numlist){
				map.put(i, new ArrayList<>());
			}
			for (int j=0; j<P.n; j++){
				for (int i=0; i<P.n; i++){
					int pos = col+j+P.N*(row+i);
					Tile t = list.get(pos);
					if (t.val == 0){
						for (int k=0; k<P.N; k++){
							if (t.isPossible(k+1)){
								map.get(k+1).add(t);
							}
						}
					}
				}
			}
			for (int i=0; i<P.N; i++){
				ArrayList<Tile> thisList = map.get(i+1);
				if (thisList!=null && thisList.size() == 1){
					thisList.get(0).isUqInSqr = true;
					thisList.get(0).uqVal = i+1;
					addToQueue(i+1, thisList.get(0));
				}
			}
		}
		
		private boolean checkCol(int col, int row, int val){
			for (int i=col; i<P.N*P.N; i+= P.N){
				Tile t = list.get(i);
				if (t.y != row){
					if (t.val == val) return false;
				}
			}
			return true;
		}
		
		private boolean checkRow(int col, int row, int val){
			for (int i=P.N*(row); i<P.N*(row+1); i++){
				Tile t = list.get(i);
				if (t.x != col){
					if (t.val == val) return false;
				}
			}
			return true;
		}
		
		private boolean checkSqr(int col, int row, int val){
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
					Tile t = list.get(col+j+P.N*(row+i));
					if (t.y != rowOrig || t.x != colOrig){
						if (t.val == val) return false;
					}
				}
			}
			return true;
		}
		
		private void enqueueCol(int col){
			for (int i=col; i<P.N2; i+= P.N){
				Tile t = list.get(i);
				if (t.numPossible() == 1 && t.val==0){
					addToQueue(t.getSingularPoss(), t);
				}
			}
		}
		
		private void enqueueRow(int row){
			for (int i=P.N*(row); i<P.N*(row+1); i++){
				Tile t = list.get(i);
				if (t.numPossible() == 1 && t.val==0){
					addToQueue(t.getSingularPoss(), t);
				}
			}
		}
		
		private void enqueueSqr(int col, int row){
			
			while (col%P.n> 0){
				col--;
			}
			while (row%P.n> 0){
				row--;
			}
			for (int j=0; j<P.n; j++){
				for (int i=0; i<P.n; i++){
					Tile t = list.get(col+j+P.N*(row+i));
					if (t.numPossible() == 1 && t.val==0){
						addToQueue(t.getSingularPoss(), t);
					}
				}
			}
		}
		
		public void triples(){
			colTriples();
			rowTriples();
//			sqrTriples();
		}
		
		private void tripleHelper(int[] count, Set<Integer> nonzeroVals, ArrayList<Tile> orig_list){
			
			ArrayList<Integer> nums_to_check = new ArrayList<>(); //list of possible elements that show up <= 3 times
			for (int k=1; k<=P.N; k++){
				if (count[k]<=3 && !nonzeroVals.contains(k)){
					nums_to_check.add(k);
				}
			}

			//go through each permutation
			int[] it = new int[3]; //it short for iterators
			for (it[0]= 00000 ; it[0]<=nums_to_check.size()-3; it[0]++){
				for (it[1]=it[0]+1; it[1]<=nums_to_check.size()-2; it[1]++){
					for(it[2]=it[1]+1; it[2]<=nums_to_check.size()-1; it[2]++){
						ArrayList<Tile> new_list = new ArrayList<>();
						Set<Integer> val_set = new HashSet<>();
						int[] e = new int[3];
						for (int k =0; k<3; k++){
							e[k] = nums_to_check.get(it[k]);
						}
						for (Tile t: orig_list){
							for(int perm_num: e){
								if(t.possibles.contains(perm_num)){
									new_list.add(t);
									break;
								}
							}
						}
						for (int perm_num:e){
							val_set.add(perm_num);
						}
						if(new_list.size() == 3){
							for(Tile t: new_list){
								for(int k=1; k<=P.N; k++){
									if(!val_set.contains(k)){
										t.notPossible(k);
										changeVal(t, t.val);
									}
								}
							}
						}
					}
				}
			}
		}

		private void colTriples(){
			//j determines the column
			for (int j=0; j<P.N; j++){

				ArrayList<Tile> orig_col_list = new ArrayList<>(); //list of all blank tiles in col
				for (int i=j; i<P.N2; i+= P.N){ //run down the column
					Tile t = list.get(i);
					if(t.val==0) orig_col_list.add(t);
				}

				int[] count = new int[P.N+1]; //number of times a particular possible element shows up (init'zd to 0)
				for (int k=0; k<P.N; k++){
					count[k] = 0;
				}
				Set<Integer> nonzeroVals = new HashSet<>();
				for (int i=j; i<P.N2; i+= P.N){ //run down the column
					Tile t = list.get(i);
					if (t.val != 0){
						nonzeroVals.add(t.val);
						continue; //don't look at cells with writing
					}
 					Set<Integer> ps = t.possibles; //set of possibles
					for (int k=1; k<=P.N; k++){
						if(ps.contains(k)) count[k] +=1;
					}
				}
				tripleHelper(count, nonzeroVals, orig_col_list);
			}
			
		System.out.println("done here");
		}
	
	private void rowTriples(){
		//j determines the row
		for (int j=0; j<P.N2; j+=P.N){

			ArrayList<Tile> orig_row_list = new ArrayList<>(); //list of all blank tiles in row
			for (int i=j; i<j+P.N; i++){ //run across the row
				Tile t = list.get(i);
				if(t.val==0) orig_row_list.add(t);
			}

			int[] count = new int[P.N+1]; //number of times a particular possible element shows up (init'zd to 0)
			for (int k=0; k<P.N; k++){
				count[k] = 0;
			}
			Set<Integer> nonzeroVals = new HashSet<>();
			for (int i=j; i<j+P.N; i++){ //run down the column
				Tile t = list.get(i);
				if (t.val != 0){
					nonzeroVals.add(t.val);
					continue; //don't look at cells with writing
				}
				 Set<Integer> ps = t.possibles; //set of possibles
				for (int k=1; k<=P.N; k++){
					if(ps.contains(k)) count[k] +=1;
				}
			}
			tripleHelper(count, nonzeroVals, orig_row_list);
		}
		
	System.out.println("done here");
	}

	public void select(Tile t){
		if (selected!= null) {selected.deselect();}
		t.select();
		selected = t;
	}

	public void checkForWin(Component frame){
		boolean flag = false;
		for (Tile t: list){
			if (t.val == 0){
				flag = true;
				break;
			}
		}
		if (!flag){
			int ans = JOptionPane.showConfirmDialog(frame, "You win! Play again?", "You win", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (ans==JOptionPane.YES_OPTION){
				Runner2.makeABoard();
			}
			this.setVisible(false);
			this.dispose();
		}
	}
	
	public void addToQueue(int val, Tile t){
		if (!tilesInQueue.contains(t)){
			queue.add(new QueueEvent(val, t));
			tilesInQueue.add(t);
		}
	}
	
	/**
	 * 
	 * @return If the queue is empty, return false. Otherwise true
	 */
	public boolean nextOnQueue(){
//		System.out.println("queue\n"+queue);
		if (!queue.isEmpty()){
			QueueEvent e = queue.removeFirst();
			changeVal(e.tileToSet, e.valToSet);
			tilesInQueue.remove(e.tileToSet);
			return true;
		}
		return false;
	}
	
	public Tile getTile(int pos){
		return list.get(pos);
	}
	
}