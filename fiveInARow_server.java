package 人工智能__五子棋;
import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
public class fiveInARow_server extends JFrame implements fiveInARow_constants
{
	private JTextArea jtalog=new JTextArea();
	private fiveInARow_AI x;
	private  Socket waitPlayer=null;
	public static void main() {
		new  fiveInARow_server();
	}
	public fiveInARow_server()
	{
		jtalog.setEditable(false);
	    setLayout(new BorderLayout());
	    add(new JScrollPane(jtalog),BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,500);
		setTitle("server");
		setVisible(true);
		try{
			ServerSocket serverSocket=new ServerSocket(8000);
			jtalog.append("服务开始:"+new Date()+'\n');
			jtalog.append("端口号：8000\n");
			int sessionNO=1;
			while(true)
			{
				jtalog.append("等待第"+sessionNO+"个玩家链接......\n");
				Socket play1= serverSocket.accept();
				jtalog.append("第"+sessionNO+"个玩家的IP地址为:"+play1.getInetAddress().getHostAddress()+'\n');
				sessionNO++;
			    DataInputStream in= new DataInputStream(play1.getInputStream());
			    int type=in.readInt();
				if(type==RENJI)
				{
					new DataOutputStream(play1.getOutputStream()).writeInt(PLAYER1);
					fiveInARow_AI ai=new fiveInARow_AI();
					new Thread(ai).start();
					jtalog.append("等待第"+sessionNO+"个玩家链接......\n");
					Socket play2= serverSocket.accept();
					jtalog.append("第"+sessionNO+"个玩家的IP地址为:"+play2.getInetAddress().getHostAddress()+'\n');
					sessionNO++;
					new DataOutputStream(play2.getOutputStream()).writeInt(PLAYER2);
				    DataInputStream in2= new DataInputStream(play2.getInputStream());
					type=in2.readInt();
				    HandleASession task=new HandleASession(play1,play2);
					 new Thread(task).start();
				}
				else
				{
					if(waitPlayer!=null)
					{
						 new DataOutputStream(waitPlayer.getOutputStream()).writeInt(PLAYER1);
						 new DataOutputStream(play1.getOutputStream()).writeInt(PLAYER2);
						 HandleASession task=new HandleASession(waitPlayer,play1);
						 new Thread(task).start();
						 waitPlayer=null;
					}
					else
					{
						waitPlayer=play1;
					}
				}	
			 }
		}
		catch(IOException ex)
		{
			System.err.println("错误");
		}
	}
	public static void main(String[] s)
	{
		fiveInARow_server t=new fiveInARow_server();
	}
}
class HandleASession implements Runnable,fiveInARow_constants
{
	private Socket player1;
	private Socket player2;
	private DataInputStream fromePlayer1;
	private DataInputStream fromePlayer2;
	private DataOutputStream toplay1;
	private DataOutputStream toplay2;
	private char[][]cell=new char[15][15];
	private boolean continueToPlay=true;
	public HandleASession(Socket player1,Socket player2)
	{
		this.player1=player1;
		this.player2=player2;
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++)
				cell[i][j]=' ';
	}
	@Override
	public void run() {	
		try {
			DataInputStream fromePlayer1 = new DataInputStream(player1.getInputStream());
			DataInputStream fromePlayer2 = new DataInputStream(player2.getInputStream());
			DataOutputStream toPlayer1 =new DataOutputStream(player1.getOutputStream());
			DataOutputStream toPlayer2 =new DataOutputStream(player2.getOutputStream());
			toPlayer1.writeInt(1);
			while(true)
			{
				int row;
				while(true)
				{
					if(fromePlayer1.available()>0)
					{
						row=fromePlayer1.readInt();
					 if(row==CHAT)
						{
							String s=fromePlayer1.readUTF();
							toPlayer2.writeInt(CHAT);
							toPlayer2.writeUTF(s);
						}
					 else
						 break;
					}
					if(fromePlayer2.available()>0)
					{
						fromePlayer2.readInt();
						String s=fromePlayer2.readUTF();
						toPlayer1.writeInt(CHAT);
						toPlayer1.writeUTF(s);
					}   
				}
				int colnum=fromePlayer1.readInt();
				cell[row][colnum]='X';
				if(isWon('X'))
				{
					toPlayer1.writeInt(PLAYER1_WON);
					toPlayer2.writeInt(PLAYER1_WON);
					sendMove(toPlayer2,row,colnum);
					break;
				}
				else if(isFull())
				{
					toPlayer1.writeInt(DRAW);
					toPlayer2.writeInt(DRAW);
					sendMove(toPlayer2,row,colnum);
					break;
				}
				else
				{
					toPlayer2.writeInt(CONTINUE);
					sendMove(toPlayer2,row,colnum);
				}
				
				while(true)
				{
					if(fromePlayer2.available()>0)
					{
						row=fromePlayer2.readInt();
					 if(row==CHAT)
						{
							String s=fromePlayer2.readUTF();
							toPlayer1.writeInt(CHAT);
							toPlayer1.writeUTF(s);
						}
					 else
						 break;
					}
					if(fromePlayer1.available()>0)
					{
						fromePlayer1.readInt();
						String s=fromePlayer1.readUTF();
						toPlayer2.writeInt(CHAT);
						toPlayer2.writeUTF(s);
					}   
				}
				 colnum=fromePlayer2.readInt();
				cell[row][colnum]='O';
				if(isWon('O'))
				{
					toPlayer1.writeInt(PLAYER2_WON);
					toPlayer2.writeInt(PLAYER2_WON);
					sendMove(toPlayer1,row,colnum);
					break;
				}
				else
				{
					toPlayer1.writeInt(CONTINUE);
					sendMove(toPlayer1,row,colnum);
				}
				
			}
			while(true)
			{
				if(fromePlayer2.available()>0)
				{
					    fromePlayer2.readInt();
						String s=fromePlayer2.readUTF();
						toPlayer1.writeInt(CHAT);
						toPlayer1.writeUTF(s);
				}
				if(fromePlayer1.available()>0)
				{
					fromePlayer1.readInt();
					String s=fromePlayer1.readUTF();
					toPlayer2.writeInt(CHAT);
					toPlayer2.writeUTF(s);
				}   
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sendMove(DataOutputStream out,int row,int colnum) throws IOException
	{
		out.writeInt(row);
		out.writeInt(colnum);
	}
	private boolean isFull()
	{
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++)
				if(cell[i][j]==' ')
					return false;
		 return true;
	}
	private boolean isWon(char token)
	{
		int same=0;
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++)
			{
				if(cell[i][j]==token)
					same++;
				else
					same=0;
				if(same==5)
					return true;	
			}
		same=0;
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++)
			{
				if(cell[j][i]==token)
					same++;
				else
					same=0;
				if(same==5)
					return true;
			}
		same=0;
		for(int k=14;k>=0;k--)
			for(int i=0;i+k<15;i++)
				{
				 if(cell[k+i][i]==token)
					same++;
					else
						same=0;
					if(same==5)
						return true;
				}
		same=0;
		for(int k=0;k<15;k++)
			for(int i=0;i+k<15;i++)
				{
				 if(cell[i][k+i]==token)
					same++;
					else
						same=0;
					if(same==5)
						return true;
				}
		same=0;
		for(int i=0;i<15;i++)
			for(int j=0;i-j>=0;j++)
			{
				if(cell[i-j][j]==token)
					same++;
				else
					same=0;
				if(same==5)
					return true;
			}
		same=0;
		for(int i=0;i<15;i++)
			for(int j=0;i+j<15;j++)
			{
				if(cell[14-j][i+j]==token)
					same++;
				else
					same=0;
				if(same==5)
					return true;
			}
		return false;		
	}	
}