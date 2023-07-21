import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Runner {

	public static final int N_BIG = 3;
	public static final int N = N_BIG*N_BIG;
	
	public static final double PREpercent = 0.37;
	public static final int PREs = (int)(N*N*PREpercent); //how many numbers to show
	
	
	static ArrayList<Tile> list = new ArrayList<>();
	static ArrayList<Row> rowList = new ArrayList<>();
	
	public static Tile selected;
	public static int lastValOfSel;
	
	public static void main(String[] args){
		JFrame frame = new JFrame("Sudoku");
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(N,N,0,0));
		frame.add(mainPane);
		
		for (int i = 0; i<N*N; i++){
			Tile t = new Tile(i%N, i/N);
			list.add(t);
			mainPane.add(t);
		}
		
		frame.addKeyListener(
			new KeyListener(){
				public void keyTyped(KeyEvent e) {
					selected.typed(e);
					makeGreen(selected.x, selected.y);
					if (checkRow(selected.y) &&
					checkCol(selected.x) &&
					checkBox(selected.x, selected.y)){
						checkForWin(frame);
						recheck(selected, e);
					}
					if (e.getKeyChar() == 'd'){
						duplicate();
					}
				}

				public void keyPressed(KeyEvent e) {
				}

				public void keyReleased(KeyEvent e) {
				}
			}
		);
		
		setVals();
		cleanUp();
		
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		solve();
		
		for (int i=0; i<N; i++){
			rowList.add(new Row(i));
		}
		
	}
	
	public static void duplicate(){
		//TODO this. Note that this can't be made until list stops being static
	}
	
	public static void solve(){
		for (Tile t: list){
			if (!t.preSet){
				for (int i=0; i<N; i++){
					t.everythingIsPossible();
				}
				simpleCheck(t);
			}
		}
	}
	
	public static void simpleCheck(Tile t){
		int row = t.y;
		int col = t.x;
		
		for (int i=N*(row); i<N*(row+1); i++){
			int val = list.get(i).val;
			if (val != 0){
				t.notPossible(val-1, false);
			}
		}
		
		for (int i=col; i<N*N; i+= N){
			int val = list.get(i).val;
			if (val != 0){
				t.notPossible(val-1, false);
			}
		}
		
		while (col%N_BIG > 0){
			col--;
		}
		while (row%N_BIG > 0){
			row--;
		}
		for (int j=0; j<N_BIG; j++){
			for (int i=0; i<N_BIG; i++){
				int val = list.get(col+j+N*(row+i)).val;
				if (val != 0){
					t.notPossible(val-1, false);
				}
			}
		}
	}
	
	public static void recheck(Tile t, KeyEvent e){
		int row = t.y;
		int col = t.x;
	
		char key = e.getKeyChar();
		int val = 0;
		if (Character.isDigit(key)){
			val = Integer.parseInt(key + "");
		}
		else{
			System.out.println("Something is way wrong");
		}
		
		//rows
		for (int i=N*(row); i<N*(row+1); i++){
			if (val != 0) list.get(i).notPossible(val-1, true);
			if (lastValOfSel != 0) list.get(i).againPossible(lastValOfSel-1);
		}
		
		//cols
		for (int i=col; i<N*N; i+= N){
			if (val!= 0) list.get(i).notPossible(val-1, true);
			if (lastValOfSel != 0) list.get(i).againPossible(lastValOfSel-1);
		}
		
		//boxes
		while (col%N_BIG > 0){
			col--;
		}
		while (row%N_BIG > 0){
			row--;
		}
		for (int j=0; j<N_BIG; j++){
			for (int i=0; i<N_BIG; i++){
				int index = col+j+N*(row+i);
				if (val!= 0) list.get(index).notPossible(val-1, true);
				if (lastValOfSel != 0) list.get(index).againPossible(lastValOfSel-1);
			}
		}
	}
	
	public static void clicked(Tile t){
		selected = t;
		lastValOfSel = t.val;
		for (Tile tile: list){
			if (!tile.equals(t)){
				tile.deselect();
			}
		}
	}
	
	public static void setVals(){
		boolean allDone = false;
		int max = 0;
		while (!allDone){
			allDone = true;
			for (int i = 0; i<list.size(); i++){
				list.get(i).val = 0;
			}
			for (int i = 0; i<list.size(); i++){
				Tile t = list.get(i);
				boolean ok = false;
				int count = 0;
				boolean breakFlag = false;
				while (!ok){
					t.val = (int)(Math.random()*N+1);
					ok = checkRow(t.y) && checkCol(t.x) && checkBox(t.x, t.y);
					count++;
					if (count > N*20){
						breakFlag = true;
						break;
					}
				}
				if (breakFlag){
					if (i>max){
						System.out.println(i);
						max = i;
					}
					allDone = false;
					break;
				}
			}
		}
	}
	
	public static void cleanUp(){
		ArrayList<Integer> presets = new ArrayList<>();
		for (int i=0; i<PREs; i++){
			boolean waiting = true;
			while (waiting){
				int num = (int)(Math.random()*N*N+1);
				if (!presets.contains(num)){
					presets.add(num);
					waiting = false;
				}
			}
		}
		
		for (int i=0; i<list.size(); i++){
			Tile t = list.get(i);
			if (!presets.contains(i)){
				t.val = 0;
			}
			else{
				t.preSet = true;
			}
			t.makeGreen();
			t.deselect();
		}
	}
	
	public static boolean checkRow(int row){
		ArrayList<Integer> checkList = new ArrayList<>();
		ArrayList<Integer> checkListMap = new ArrayList<>();
		
		boolean flag = false;
		
		for (int i=N*(row); i<N*(row+1); i++){
			int v = list.get(i).val;
			if (v != 0){
				checkList.add(v);
				checkListMap.add(i);
			}
		}
		
		for (int i=0; i<checkList.size(); i++){
			for (int j=0; j<checkList.size(); j++){
				if (i!=j){
					if (checkList.get(i) == checkList.get(j)) {
						list.get(checkListMap.get(i)).makeRed();
						list.get(checkListMap.get(j)).makeRed();
						flag = true;
					}
				}
			}
		}
		if (flag) return false;
		return true;
	}
	
	public static boolean checkCol(int col){
		ArrayList<Integer> checkList = new ArrayList<>();
		ArrayList<Integer> checkListMap = new ArrayList<>();
		
		boolean flag = false;
		
		for (int i=col; i<N*N; i+= N){
			int v = list.get(i).val;
			if (v != 0){
				checkList.add(v);
				checkListMap.add(i);
			}
		}
		
		for (int i=0; i<checkList.size(); i++){
			for (int j=0; j<checkList.size(); j++){
				if (i!=j){
					if (checkList.get(i) == checkList.get(j)) {
						list.get(checkListMap.get(i)).makeRed();
						list.get(checkListMap.get(j)).makeRed();
						flag = true;
					}
				}
			}
		}
		if (flag) return false;
		return true;
	}

	public static boolean checkBox(int col, int row){
		ArrayList<Integer> checkList = new ArrayList<>();
		ArrayList<Integer> checkListMap = new ArrayList<>();
		
		boolean flag = false;
		
		while (col%N_BIG > 0){
			col--;
		}
		while (row%N_BIG > 0){
			row--;
		}
		for (int j=0; j<N_BIG; j++){
			for (int i=0; i<N_BIG; i++){
				int v = list.get(col+j+N*(row+i)).val;
				if (v != 0){
					checkList.add(v);
					checkListMap.add(col+j+N*(row+i));
				}
			}
		}
		
		for (int i=0; i<checkList.size(); i++){
			for (int j=0; j<checkList.size(); j++){
				if (i!=j){
					if (checkList.get(i) == checkList.get(j)) {
						list.get(checkListMap.get(i)).makeRed();
						list.get(checkListMap.get(j)).makeRed();
						flag = true;
					}
				}
			}
		}
		
		if (flag) return false;
		return true;
	}

	public static void makeGreen(int col, int row){
		for (int i=N*(row); i<N*(row+1); i++){
			list.get(i).makeGreen();
		}
		for (int i=col; i<N*N; i+= N){
			list.get(i).makeGreen();
		}
		
		while (col%N_BIG > 0){
			col--;
		}
		while (row%N_BIG > 0){
			row--;
		}
		for (int j=0; j<N_BIG; j++){
			for (int i=0; i<N_BIG; i++){
				list.get(col+j+N*(row+i)).makeGreen();
			}
		}
		
	}

	public static void checkForWin(Component frame){
		boolean flag = false;
		for (Tile t: list){
			if (t.val == 0){
				flag = true;
				break;
			}
		}
		if (!flag){
			JOptionPane.showConfirmDialog(frame, "You win! Play again?", "You win", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		}
	}
}
