package �˹�����__������;
import java.awt.Point;
import java.util.ArrayList;
public class fiveInARow_Arithmetic implements fiveInARow_Macro {
	private int[][] board;             //���̵�ǰ��״̬
	private int[][] pc;                //���Եĸ���λ����
	private int[][] person;            //�˵ĸ���λ����
	private Point bestPoint,point; 
	private int bestScore=-intMax;
  public fiveInARow_Arithmetic() 
  {
    board=new int[15][15];  
	pc=new int[15][15];
	person=new int[15][15];
	point=new Point();
	bestPoint=new Point();
	for(int i=0;i<15;i++)
		for(int j=0;j<15;j++)
		{
			board[i][j]=EMPTY;
			person[i][j]=0;
			pc[i][j]=0;
		}
	}
    Point getbestP()
    {
      // alphaBeta(PC,DEPTH,-intMax,intMax);
    	 value();
    	 ArrayList<Point> list=getBestPlaces();
    	 if(list.size()==1) 
    		 return list.get(0);
    	 else
    	 {
    		 if(pc[list.get(0).x][list.get(0).y]>person[list.get(1).x][list.get(1).y])
    			 return list.get(0);
    		 else
    			 return list.get(1);
    	 }
           //return bestPoint;
    }
    boolean setPoint(int player,Point p)
    {
    	if(board[p.x][p.y]==EMPTY)
    	{
    		board[p.x][p.y]=player;
    		pc[p.x][p.y]=0;
    		person[p.x][p.y]=0;
    		return true;
    	}
    	return false;
    }
    int[][]getBoard()
    {
    	return board;
    }
 /********�������ݶ�����֦*************/
  int alphaBeta(int player,int depth, int alpha, int beta)
  {
   int value;
   int playerNow;
   if(player==PC)
	   playerNow=PERSON;
   else
	   playerNow=PC;
   if( depth==0||isEnded())
   {
        value = evaluate(board);
        return value;
   }
   ArrayList<Point>list=getBestPlaces();    //�����п��ܵĽڵ��Ͻ�����չ
   int best = -intMax;
   for(int i=0;i<list.size();i++)
   {
	   if(depth==DEPTH)
	   {
		   point.x=list.get(i).x;
		   point.y=list.get(i).y;
	   }
	   if(player==PC)
		   board[list.get(i).x][list.get(i).y]=PC;
	   else
		   board[list.get(i).x][list.get(i).y]=PERSON;
	   value = -alphaBeta(playerNow,depth-1,-beta,-alpha);
	   board[list.get(i).x][list.get(i).y]=EMPTY;
       if(value > best)
            best = value;
       if(best > alpha)
            alpha = best;
       if(best >= beta)
             break;
   }
    return best;
  }
  /****�ж��������Ƿ��Ѿ�����������*****/
   boolean isEnded()    
   {
	   for(int i=0;i<15;i++)
		   for(int j=0;j<15;j++)
			   if(board[i][j]==EMPTY)
				   return false;
	   return true;	    
   }
   /***����ڵ������*******/
   int evaluate(int[][] board)                  //�ڵ������
   {
	   int value=0;
	   value();
	   for(int i=0;i<15;i++)
		   for(int j=0;j<15;j++)
			if(board[i][j]==EMPTY)
   		    {
   			  value+=pc[i][j];
   			  value-=person[i][j];
   		    }
	   return value;
   }
   /**********�õ�������ߵ����ɽڵ�**************/
    ArrayList<Point> getBestPlaces()              
    {
    	ArrayList<Point> list=new ArrayList<>();
    	boolean[][] isVisit=new boolean[15][15];
    	int pcmax=-intMax,personmax=-intMax;
    	int pcx=0,pcy=0,personx=0,persony=0;
    	for(int i=0;i<15;i++)
    		for(int j=0;j<15;j++)
    			isVisit[i][j]=false;                  //��ʼ�����ʼ�¼�б�
        for(int x=0;x<pointNum;x++)
        {   pcmax=0;personmax=0;pcx=0;pcy=0;personx=0;persony=0;
        	for(int i=0;i<15;i++)
        		for(int j=0;j<15;j++)
        		{
        			if(pc[i][j]>pcmax&&!isVisit[i][j])
        			{
        				pcmax=pc[i][j];
        				pcx=i;pcy=j;
        			}
        			if(person[i][j]>personmax&&!isVisit[i][j])
        			{
        				personmax=person[i][j];
        				personx=i;persony=j;
        			}
        		}
        	if(pcmax!=-intMax)
        	{
        		list.add(new Point(pcx,pcy));
        		isVisit[pcx][pcy]=true;
        	}
        	if(personmax!=-intMax&&!isVisit[personx][persony])
        	{
        		list.add(new Point(personx,persony));	
        		isVisit[personx][persony]=true;
        	}	
        }
		return list;
    }
    /*************�Ե�ǰ�ڵ��������*************/
    void value()  
    {  
        int  cnt;                               //ͳ�����������ӵĸ���
        int tx, ty;                             //i+dx j+dy
        int barrier;                            //�м��Ƿ񱻶Է����ӻ��߽߱����  
        int[] win=new int[2];                   //��õ÷�
        int dx[]={0, 1, 1, 1, 0, -1, -1, -1};   //�˸�����
        int dy[]={-1, -1, 0, 1, 1, 1, 0, -1};  
        int[][] type=new int[8][2];              //ͳ������������������Ӹ���
        for (int i=0;i<15;i++ )    
        {  
            for (int j=0;j<15;j++ )   
            {  
                if (board[i][j]== 0)  
                {  
                    for (int ii=0;ii<8;ii++)  
                        for (int jj=0;jj<2;jj++ )  
                             type[ii][jj]= 0;  
                    for (int k=0;k<8;k++) 
                    {    
                        cnt=0;tx=i;ty=j;barrier=0;  
                        for (int t=0;t<5;t++)      
                        {  
                            tx += dx[k]; ty += dy[k];  
                            if (tx>14||tx<0||ty>14||ty<0) 
                                 {barrier = 1; break;}  
                            else if( board[tx][ty]== 1 ) 
                            	   cnt++;  
                            else  
                            {  
                                if ( board[tx][ty] == 2 ) 
                                	 barrier = 1;  
                                break;  
                            }  
                        }  
                        type[k][0]=cnt*2+barrier;  
                        cnt=0;tx=i;ty=j;barrier=0;  
                        for (int t=0;t<5;t++ )  
                        {  
                            tx += dx[k]; ty += dy[k];  
                            if ( tx > 14 || tx < 0 || ty > 14 || ty < 0 ) {barrier = 1; break;}  
                            if( board[tx][ty] == 2 ) cnt++;  
                            else  
                            {  
                                if (board[tx][ty] == 1 ) barrier = 1;  
                                break;  
                            }  
                        }  
                        type[k][1] = cnt * 2 + barrier;  
                    }  
                    // value  
                    for (int t = 0; t < 2; t++ )  
                    {  
                        win[t] = 0;  
                        for (int k = 0; k < 8; k++ )  // single line  
                        {  
                            int t1, s1;  
                            if (type[k][t] % 2 == 0 ) {t1 = type[k][t] / 2; s1 = 0;}  
                            else {t1 = ( type[k][t] - 1 ) / 2; s1 = 1;}  
                            if ( t1 >= 4 )  
                                win[t] += 100000;  
                            else if ( t1 == 3 && s1 == 0 )  //����������û�б�����
                                win[t] += 100000;  
                            else if ( t1 == 3 && s1 == 1 )  //����3���ұ�����
                                win[t] += 10000;  
                            else if ( t1 == 2 )  
                                win[t] += 10;  
                            else if(t1==1)  
                                win[t] += 1;  
                        }  
                        for (int k = 0; k < 8; k++ )  // two line combination, 45 degree, 90 degree and 135 degree  
                            for (int s = 1; s <= 3; s++ )  
                            {  
                                int t1, t2, s1, s2;       //t12Ϊ�����ĸ���     s12Ϊ��ϵķ�ʽ
                                if ( type[k][t] % 2 == 0 ) {t1 = type[k][t] / 2; s1 = 0;}  
                                else {t1 = ( type[k][t] - 1 ) / 2; s1 = 1;}  
                                if ( type[( k + s ) % 8][0] % 2 == 0 ) {t2 = type[( k + s ) % 8][t] / 2; s2 = 0;}  
                                else {t2 = ( type[( k + s ) % 8][t] - 1 ) / 2; s2 = 1;}  
                              
                                if ( t1 == 0 || t2 == 0 )   //���ĳһ����û������������
                                    continue;  
                                if(t1 >= 4 && s1 ==0 || t2>=4 && s2 >=4 ) { win[t] += 200000; continue;} //������ĸ���Ϊ��ʤ��
                                else if ( t1 >= 4 || t2 >= 4 ) {win[t] += 100000; continue;}    //����Ѿ��е����ĸ�����������
                                else if ( t1 == 3 || t2 == 3 )                  //�����������
                                {  
                                    if (( t1 == 3 && s1 == 0 ) || ( t2 == 3 && s2 == 0 )) {win[t] += 100000; continue;}      //����������˿�
                                    else if (( t1 == 2 && s1 == 0 ) || ( t2 == 2 && s2 == 0 )) {win[t] += 10000; continue;}  //����һ�˿պ�����
                                    else if (( t1 == 2 && s1 == 1 ) || ( t2 == 2 && s2 == 1 )) {win[t] += 100; continue;}    //����һ�� �ǿպ�����
                                    else if (( t1 == 1 ) || ( t2 == 1 )) {win[t] += 10; continue;}                           //������һ��
                                    else if (( t1 == 0 ) || ( t2 == 0 )) {win[t] += 1; continue;}                             //������
                                }  
                                else if ( t1 == 2 || t2 == 2 )  //������
                                {  
                                    if (( t1 == 2 && s1 == 0 ) && ( t2 == 2 && s2 == 0 )) {win[t] += 100000; continue;}      //˫�������˿�
                                    else if (( t1 == 2 && s1 == 1 ) && ( t2 == 2 && s2 == 1 )) {win[t] += 100; continue;}    //˫�������˶��ǿ�
                                    else if (( t1 == 2 && s1 == 1 ) && ( t2 == 2 && s2 == 0 ) || ( t1 == 2 && s1 == 0 ) && ( t2 == 2 && s2 == 1 ))  
                                        {win[t] += 10000; continue;}                                                         //˫����һ�����˿�һ�����˷ǿ�
                                    if (( t1 == 2 && s1 == 0 ) || ( t2 == 2 && s2 == 0 )) {win[t] += 10000; continue;}       //һ������������Ϊ��  
                                    else {win[t] += 100; continue;}  
                                }  
                                else if ( t1 == 1 || t2 == 1 )  
                                    {win[t] += 1; continue;}  
                            }  
                        for (int k = 0; k < 4; k++ )  // two line combination 180 degree  
                        {  
                            int t1, t2, s1, s2;  
                            if ( type[k][t] % 2 == 0 ) {t1 = type[k][t] / 2; s1 = 0;}  
                            else {t1 = ( type[k][t] - 1 ) / 2; s1 = 1;}  
                            if (type[( k + 4 ) % 8][0] % 2 == 0 ) {t2 = type[( k + 4 ) % 8][t] / 2; s2 = 0;}  
                            else {t2 = ( type[( k + 4 ) % 8][t] - 1 ) / 2; s2 = 1;}  
                            int tmp = t1 + t2;        //����һ�ߵĳ���
                            if ( t1 == 0 || t2 == 0 )  
                                continue;  
                            if ( tmp >= 4 )  
                            {win[t] += 100000;continue;}  
                            else if ( tmp == 3 )  
                            {  
                                if ( s1 == 0 && s2 == 0 ) {win[t] += 100000;continue;}  
                                else if ( s1 == 1 && s2 == 1 ) {win[t] += 1;continue;}  
                                else {win[t] += 10;continue;}  
                            }  
                            else if ( tmp == 2 )  
                            {  
                                if ( s1 == 0 && s2 == 0 ) {win[t] += 10000;continue;}  
                                else if ( s1 == 1 && s2 == 1 ) {win[t] += 1;continue;}  
                                else {win[t] += 10;continue;}  
                            }  
                        }  
                    }  
                    pc[i][j] = win[0];  
                    person[i][j] = win[1];  
                }  
            }  
        }  
    }  
} 
