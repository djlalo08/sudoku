import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JPanel;

public class Tile extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1253465418676318723L;

	private static final int barWidth = 2;
	
	private static final Color backG = new Color(250,250,250);
	private static final Color backGSel = new Color(250,250,200);
	private static final Color redText = new Color(200,50,50);
	
	private static final int startX = 45;
	private static final int startY = 45;
	private static final int dotWidth = 3;
	private static final int dotHeight = 3;
	
	//TODO set this back to private when done testing
	Set<Integer> possibles = new HashSet<>(); 
	
	int x;//0 is left, 9 is right
	int y;//0 is top,  9 is bot
	
	boolean red;
	boolean preSet = false;
	int val;
	
	boolean isSelected;
	int lastVal;
	
	boolean isUqInRow = false;
	boolean isUqInCol = false;
	boolean isUqInSqr = false;
	int uqVal = 0;
	
	Board parent;
	
	public Tile(int x, int y, Board parent){
		super();
		this.x = x;
		this.y = y;
		this.parent = parent;
		Tile thisOne = this;
		everythingIsPossible();
		setBackground(backG);
		addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					parent.select(thisOne);
					parent.requestFocus();
					repaint();
				}
			}
		);
	}
	
	public void deselect(){
		isSelected = false;
		repaint();
	}
	
	public void select(){
		isSelected = true;
		repaint();
	}
	
	private void setBG(){
		if (red){
			if (isSelected)
				setBackground(Color.red);
			else{
				setBackground(Color.red.darker());
			}
		}
		else if (possibles.size() == 1 && val == 0 && parent.showHints){
			if (isSelected)
				setBackground(Color.green);
			else{
				setBackground(Color.green.darker());
			}
		}
		else if ((isUqInCol || isUqInRow || isUqInSqr) && val == 0 && parent.showHints){
			if (isSelected)
				setBackground(Color.pink);
			else{
				setBackground(Color.pink.darker());
			}
		}
		else{
			if (isSelected){
				if (parent.minus){setBackground(Color.orange);}
				else {setBackground(backGSel);}
			}
			else{
				setBackground(backG);
			}
		}
	}

	public void everythingIsPossible(){ //🌈🦄
		for (int i=1; i<=P.N; i++){
			nowPossible(i);
		}
	}
	
	public void notPossible(int number){
		possibles.remove(number);
	}
	
	public void nowPossible(int number){
		if (preSet) return;
		if (number == 0) return;
		possibles.add(number);
	}
	
	public int numPossible(){
		return possibles.size();
	}
	
	/**
	 * This should only be used when You know that possibles.size()==1
	 * @return
	 */
	public int getSingularPoss(){
		Integer[] lst = new Integer[1];
		lst = possibles.toArray(lst);
		return lst[0];
	}
	
	public int getRandPoss(){
		if(possibles.isEmpty()) return -1;
		Random r = new Random();
		Integer[] lst = new Integer[1];
		lst = possibles.toArray(lst);
		int rand =r.nextInt(lst.length);
		return lst[rand];
	}

	public boolean isPossible(int number){
		return (possibles.contains(number));
	}
	
	private void drawPossible(Graphics g){
		g.setColor(new Color(100, 50, 100));
		if (val == 0)
			g.drawRect(startX-1, startY-1, (dotWidth+1)*P.n+1, (dotHeight+1)*P.n+1);
		for (int i=0; i<P.N; i++){
			if (possibles.contains(i+1) && val == 0)
			g.fill3DRect(startX+i%P.n*(dotWidth+1), 
						 startY+i/P.n*(dotHeight+1), 
						 dotWidth, dotHeight, true);
		}
	}
	
	private void drawUnique(Graphics g){
		g.setColor(Color.BLACK);
		if (val == 0){
			final int offset = 3;
			final int size = 11;
			final int sizeSqr = 5;
			final int offsetSqr = 6;
			final int thick = 1;
			if (isUqInCol){
				g.fillRect(offset+size/2, offset, thick, size);
			}
			if (isUqInRow){
				g.fillRect(offset, offset+size/2, size, thick);
			}
			if (isUqInSqr){
				g.fillRect(offsetSqr, offsetSqr, sizeSqr, sizeSqr);
			}
			if (uqVal != 0){
				g.setFont(new Font("Arial", Font.BOLD, 9));
				g.drawString(uqVal + "", (int)(offset*2)+size, size);
			}
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
	
		setBG();
		
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		if (x%P.n == 0){
			g.fillRect(0, 0, barWidth, getHeight());
		}
		if (y%P.n == 0){
			g.fillRect(0, 0, getWidth(), barWidth);
		}
		
		if (!preSet){
			g.setColor(new Color(150,150,150));
			if (red){
				g.setColor(redText);
			}
		}
		g.setFont(new Font("Arial", Font.BOLD, 20));
		if (val != 0){
			g.drawString(Integer.toString(val), getWidth()/2-3, getHeight()/2+3);
		}
		if (parent.showHints){
			drawPossible(g);
			drawUnique(g);
		}
	}
}
