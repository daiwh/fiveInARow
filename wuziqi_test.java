package �˹�����__������;

import java.awt.Point;
import java.util.Scanner;

public class wuziqi_test implements fiveInARow_Macro{
	private int[][] board;
	private fiveInARow_Arithmetic ar;
	private int x,y;
	public static void main(String[] args) {
		 wuziqi_test test=new wuziqi_test();
	}
	public  wuziqi_test()
	{
		ar=new fiveInARow_Arithmetic();
		while(true)
		{
			System.out.println("����Ҫ���ӵ�λ��");
			Scanner in=new Scanner(System.in);
			x=in.nextInt();
			y=in.nextInt();
			ar.setPoint(PERSON, new Point(x,y));
			Point p=ar.getbestP();
			ar.setPoint(PC,p);
			System.out.println("���Ե�����λ��Ϊ:"+p.x+" "+p.y);
			int[][] board=ar.getBoard();
			for(int i=0;i<15;i++)
			{
				for(int j=0;j<15;j++)
					System.out.printf(" %7d",board[i][j]);
				System.out.println();
			}
		}
	}

}
