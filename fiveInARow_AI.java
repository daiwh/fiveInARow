package 人工智能__五子棋;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import javax.swing.JOptionPane;
public class fiveInARow_AI  implements fiveInARow_constants, Runnable{
    private DataInputStream fromeServer;
    private DataOutputStream toServer;
    private String host="localhost";
    private boolean continueToPlay=true;
    private int row=0,col=0;
    private fiveInARow_Arithmetic ar;
	public fiveInARow_AI() throws IOException
	{
		connectToServer();
		ar=new fiveInARow_Arithmetic();
	}
	private void connectToServer() throws IOException
	{
		try{
		Socket socket;
		socket=new Socket(host,8000);
		fromeServer=new DataInputStream(socket.getInputStream());
		toServer=new DataOutputStream(socket.getOutputStream());
		toServer.writeInt(PIPEI);
	     }
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(null,"机器人链接至服务器失败","error",JOptionPane.INFORMATION_MESSAGE);
			System.err.println(ex);
		}
	}
	@Override
	public void run() 
	{
		try {
			 fromeServer.readInt();
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("您好，我是机器人朋朋，欢迎和我下棋");
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("你可以发送   keyword + # + sentence 教我说话");
		   } catch (IOException e1) {
			 e1.printStackTrace();
		}
		while(continueToPlay)
		{
			try {
				receiveInfoFromeServer();
				getplace();
				sendMove();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while(true)
		{
			try {
				fromeServer.readInt();
				String s=fromeServer.readUTF();
				chat(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void sendMove() throws IOException
	{
		ar.setPoint(2,new Point(row,col));
		toServer.writeInt(row);
		toServer.writeInt(col);
	}
	private void receiveInfoFromeServer() throws IOException
	{
		int status=fromeServer.readInt();	
		while(status==CHAT)
		{
				String s=fromeServer.readUTF();
			    chat(s);
			    status=fromeServer.readInt();
		}
		if(status==PLAYER1_WON)
		{
			continueToPlay=false;
			receiveMove();
			chat("选手赢");
		}
		else if(status==PLAYER2_WON)
		{
			continueToPlay=false;
			chat("机器赢");
		}
		else if(status==DRAW)	
		{
			continueToPlay=false;
			chat("平局");
		}
		else if(status==CONTINUE)
		{
			receiveMove();
		}			
	}
	private void receiveMove() throws IOException
	{
		int row=fromeServer.readInt();
		int col=fromeServer.readInt();
		ar.setPoint(1,new Point(row,col));
	}
	private void getplace()
	{
		Point p=ar.getbestP();
		row=p.x;
		col=p.y;
	}
	private void chat(String s) throws IOException
	{
	    if(s.contains("#"))
	    {
	    	 String[] ss=new String[10];
	    	 ss=s.split("#"); 
	    	 fiveInARow_SQL f=new fiveInARow_SQL();
	    	 try {
				f.insert(ss[0],ss[1]);
			} catch (SQLException e) {
				 JOptionPane.showMessageDialog(null,"数据库相关操作失败","error",JOptionPane.INFORMATION_MESSAGE);
			}
	    	 toServer.writeInt(CHAT);
			 toServer.writeUTF("哦，我以后就这么说吧");
	    }
		else if(s.equals("选手赢"))
		 {
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("算我装逼失败");
		 }
		 else if(s.equals("机器赢"))
		 {
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("垃圾，连我也下不过");
		 }
		 else if(s.equals("平局"))
		 {
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("和我也差不多吧");
		 }
		 else
			 {
			     fiveInARow_SQL f=new fiveInARow_SQL();
			     try {
					 String word=f.find(s);
					 if(word==null)
					 {
						 toServer.writeInt(CHAT);
						 toServer.writeUTF("你可以发送   keyword + # + sentence 教我说话");
					 }
					 else
					 {
						 toServer.writeInt(CHAT);
						 toServer.writeUTF(word);
					 }
				     } catch (SQLException e) {
					         JOptionPane.showMessageDialog(null,"数据库相关操作失败","error",JOptionPane.INFORMATION_MESSAGE);
				     }
			 }
		 toServer.flush();
	}
}