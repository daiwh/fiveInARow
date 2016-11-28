package �˹�����__������;
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
			JOptionPane.showMessageDialog(null,"������������������ʧ��","error",JOptionPane.INFORMATION_MESSAGE);
			System.err.println(ex);
		}
	}
	@Override
	public void run() 
	{
		try {
			 fromeServer.readInt();
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("���ã����ǻ��������󣬻�ӭ��������");
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("����Է���   keyword + # + sentence ����˵��");
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
			chat("ѡ��Ӯ");
		}
		else if(status==PLAYER2_WON)
		{
			continueToPlay=false;
			chat("����Ӯ");
		}
		else if(status==DRAW)	
		{
			continueToPlay=false;
			chat("ƽ��");
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
				 JOptionPane.showMessageDialog(null,"���ݿ���ز���ʧ��","error",JOptionPane.INFORMATION_MESSAGE);
			}
	    	 toServer.writeInt(CHAT);
			 toServer.writeUTF("Ŷ�����Ժ����ô˵��");
	    }
		else if(s.equals("ѡ��Ӯ"))
		 {
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("����װ��ʧ��");
		 }
		 else if(s.equals("����Ӯ"))
		 {
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("����������Ҳ�²���");
		 }
		 else if(s.equals("ƽ��"))
		 {
			 toServer.writeInt(CHAT);
			 toServer.writeUTF("����Ҳ����");
		 }
		 else
			 {
			     fiveInARow_SQL f=new fiveInARow_SQL();
			     try {
					 String word=f.find(s);
					 if(word==null)
					 {
						 toServer.writeInt(CHAT);
						 toServer.writeUTF("����Է���   keyword + # + sentence ����˵��");
					 }
					 else
					 {
						 toServer.writeInt(CHAT);
						 toServer.writeUTF(word);
					 }
				     } catch (SQLException e) {
					         JOptionPane.showMessageDialog(null,"���ݿ���ز���ʧ��","error",JOptionPane.INFORMATION_MESSAGE);
				     }
			 }
		 toServer.flush();
	}
}