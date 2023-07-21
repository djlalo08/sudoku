
public class Test {

	public static void main(String[] args){
		int count = 0;
		for (int i=0; i<200000; i++){
			int k = (int)(Math.random()*9+1);
			if (k == 8) count++;
		}
		System.out.println(count);
	}
}
