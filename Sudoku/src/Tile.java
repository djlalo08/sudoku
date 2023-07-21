import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	
	private int possibleCount = Runner.N;
	private boolean[] possibles = new boolean[Runner.N];
	
	int x;//0 is left, 9 is right
	int y;//0 is top,  9 is bot
	
	boolean red;
	boolean preSet;
	int val;
	
	boolean isSelected;

	public boolean mustBe = false;
	
	public Tile(int x, int y){
		super();
		this.x = x;
		this.y = y;
		Tile thisOne = this;
		setBackground(new Color(250,250,250));
		addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					select();
					Runner.clicked(thisOne);
					repaint();
				}
			}
		);
	}
	
	public void typed(KeyEvent e){
		if ((!preSet)){
			char key = e.getKeyChar();
			if (e.getExtendedKeyCode() == 8) key = '0';
			if (Character.isDigit(key)){
				val = Integer.parseInt(key + "");
				repaint();
			}
		}
	}
	
	public void deselect(){
		isSelected = false;
		setBG();
	}
	
	public void select(){
		isSelected = true;
		setBG();
	}
	
	public void makeRed(){
		red = true;
		setBackground(Color.red);
		repaint();
	}
	
	public void makeGreen(){
		red = false;
		setBG();
		repaint();
	}
	
	public void setBG(){
		if (!red){
			if (isSelected){
				setBackground(backGSel);
			}
			else{
				setBackground(backG);
			}
		}
	}

	public void everythingIsPossible(){ //🌈🦄
		for (int i=0; i<possibles.length; i++){
			possibles[i] = true;
		}
	}
	
	public void notPossible(int number, boolean checkRow){
		if (possibles[number]){
			possibleCount--;
			possibles[number] = false;
			if (possibleCount == 1){
				//TODO this thing
			}
		}
		if (checkRow){
			System.out.println("tile " + x + ", " + y + "   - " + (number+1));
			Runner.rowList.get(y).numberRemoved(number);
		}
	}
	
	public void againPossible(int number){
		if (preSet) return;
		if (!possibles[number]){
			possibleCount++;
			possibles[number] = true;
		}
	}
	public boolean isPossible(int number){
		return possibles[number];
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
	
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		if (x%Runner.N_BIG == 0){
			g.fillRect(0, 0, barWidth, getHeight());
		}
		if (y%Runner.N_BIG == 0){
			g.fillRect(0, 0, getWidth(), barWidth);
		}
		
		
		if (!preSet){
			g.setColor(new Color(180,180,180));
			if (red){
				g.setColor(redText);
			}
		}
		g.setFont(new Font("Arial", Font.BOLD, 20));
		if (val != 0){
			g.drawString(Integer.toString(val), getWidth()/2-3, getHeight()/2+3);
		}
		g.setColor(new Color(100, 50, 100));
		if (val == 0)
			g.drawRect(startX-1, startY-1, (dotWidth+1)*Runner.N_BIG+1, (dotHeight+1)*Runner.N_BIG+1);
		for (int i =0; i<Runner.N; i++){
			if (possibles[i] && val == 0)
			g.fill3DRect(startX+i%Runner.N_BIG*(dotWidth+1), 
						 startY+i/Runner.N_BIG*(dotHeight+1), 
						 dotWidth, dotHeight, possibles[i]);
		}
		if ((possibleCount == 1) && val == 0){
			setBackground(Color.green);
		}
	}
}
