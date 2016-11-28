package 人工智能__五子棋;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
public class  fiveInARow_client extends JApplet implements fiveInARow_constants,Runnable{
	private boolean myTurn=false;
	private char myTaken=' ';
	private char otherTaken=' ';
	private  Cell[][] cell=new Cell[15][15];
	private JLabel jlbTitle=new JLabel();
	private JLabel jlbStatus=new JLabel();
	private int rowSelected;
	private int columnSelected;
	private DataInputStream fromeServer;
	private DataOutputStream toServer;
	private boolean continueToPlay=true;
	private boolean waiting=true;
	private boolean ready=false;
	private boolean isfree=true;
	private boolean isrenji=true;
	private JMenuBar jmb=new JMenuBar();
	private String host="localhost";
	private Chat c=new Chat();
	public void init()
	{
		JPanel p =new JPanel();
		p.setLayout(new GridLayout(15,15,0,0));
		for(int i=0;i<15;i++)
			for(int j=0;j<15;j++)
				p.add(cell[i][j]=new Cell(i,j));
		p.setBorder(new LineBorder(java.awt.Color.black, 1));
		jlbTitle.setHorizontalAlignment(JLabel.CENTER);
		jlbTitle.setFont(new Font("SansSerif",Font.BOLD,16));
		jlbTitle.setBorder(new LineBorder(java.awt.Color.black,1));
		jlbStatus.setBorder(new LineBorder(java.awt.Color.black,1));
		add(jlbTitle,BorderLayout.NORTH);
		add(p,BorderLayout.CENTER);
		add(jlbStatus,BorderLayout.SOUTH);
		add(c,BorderLayout.EAST);
		JMenu newgame=new JMenu("新游戏");
		JMenu about=new JMenu("关于");
		JMenuItem renji=new JMenuItem("人机");JMenuItem pipei=new JMenuItem("匹配");JMenuItem guanyu=new JMenuItem("关于");
		newgame.add(renji);newgame.add(pipei);about.add(guanyu);
		guanyu.addActionListener(new ABOUT());
		renji.addActionListener(new RENJI());
		pipei.addActionListener(new PIPEI());
		jmb.add(newgame);
		jmb.add(about);
		this.setJMenuBar(jmb);
		host=JOptionPane.showInputDialog(null,"输入服务器IP地址","input",JOptionPane.OK_OPTION);
		jlbTitle.setText("点击新游戏开始游戏");
		setSize(950,600);
	}
	private void connectToServer()
	{
		try{
		 Socket socket;
		 socket=new Socket(host,8000);
		 fromeServer=new DataInputStream(socket.getInputStream());
		 toServer=new DataOutputStream(socket.getOutputStream());
	     }
	   catch(IOException ex)
		{
		   JOptionPane.showMessageDialog(null,"链接到服务器出错，请确认IP地址输入正确","error",JOptionPane.INFORMATION_MESSAGE);
		}
		Thread thread=new Thread(this);
		thread.start();
	}
	public void run()
	{
		try{
			isfree=false;
			if(isrenji==true)
				toServer.writeInt(RENJI);
			else
				toServer.writeInt(PIPEI); 
			int player=fromeServer.readInt();
			if(player==PLAYER1)
			{
				myTaken='X';
				otherTaken='O';
				jlbTitle.setText("玩家1，棋子为黑色");
				jlbStatus.setText("等待玩家2的加入......");
				fromeServer.readInt();
				jlbStatus.setText("玩家2已加入，游戏开始");
				ready=true;
				myTurn=true;	
			}
			else if(player==PLAYER2)
			{
				myTaken='O';
				otherTaken='X';
				jlbTitle.setText("玩家2，棋子为白色");
				jlbStatus.setText("等待玩家1落子");
				ready=true;
			}
			while(continueToPlay)
			{
				if(player==PLAYER1)
				{
					waitForPlayerAction();
					sendMove();
					receiveInfoFromeServer();
				}
				else if(player==PLAYER2)
				{
					receiveInfoFromeServer();
					waitForPlayerAction();
					sendMove();
				}
			}
			while(true)
			{
				fromeServer.readInt();
				String s=fromeServer.readUTF();
				c.setchat(s);
			}
		}
		catch(IOException ex)
		{
			System.err.println(ex);
		}
	}
	private void waitForPlayerAction() throws IOException
	{
		while(waiting==true)
			if(fromeServer.available()>0)
			{
				fromeServer.readInt();
				String s=fromeServer.readUTF();
				c.setchat(s);
			}
		waiting=true;
	}
	private void sendMove() throws IOException
	{
		toServer.writeInt(rowSelected);
		toServer.writeInt(columnSelected);
	}
	private void receiveInfoFromeServer() throws IOException
	{
		int status=fromeServer.readInt();	
		while(status==CHAT)
		{
				String s=fromeServer.readUTF();
			    c.setchat(s);
			    status=fromeServer.readInt();
		}
		if(status==PLAYER1_WON)
		{
			continueToPlay=false;
			if(myTaken=='X')
				jlbTitle.setText(jlbTitle.getText()+"   你赢了！");
			
			else if(myTaken=='O')
			{
				jlbTitle.setText(jlbTitle.getText()+"   你输了！");
				receiveMove();
			}
		}
		else if(status==PLAYER2_WON)
		{
			continueToPlay=false;
			if(myTaken=='O')
				jlbTitle.setText(jlbTitle.getText()+"   你赢了！");
			else if(myTaken=='X')
			{
				jlbTitle.setText(jlbTitle.getText()+"   你输了！");
				receiveMove();
			}
		}
		else if(status==DRAW)	
		{
			continueToPlay=false;
			jlbTitle.setText(jlbTitle.getText()+"   平局！");
			if(myTaken=='O')
				receiveMove();
		}
		else if(status==CONTINUE)
		{
			receiveMove();
			myTurn=true;
		}			
	}
	private void receiveMove() throws IOException
	{
		int row=fromeServer.readInt();
		int colnum=fromeServer.readInt();
		cell[row][colnum].setToken(otherTaken);
	}
	public class Cell extends JPanel
	{
		private int row;
		private int colnum;
		private char token=' ';
		public Cell(int row,int colnum)
		{
			this.row=row;
			this.colnum=colnum;
			setBackground(new java.awt.Color(139,71,38));
			addMouseListener(new ClickListenner());
		}
		public char getToken()
		{
			return token;
		}
		public void setToken(char c)
		{
			token=c;
			repaint();
		}
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if(token=='X')
			{
				g.setColor(java.awt.Color.BLACK.brighter());
				g.fillOval(getWidth()/2-15,getHeight()/2-15,30,30);
			}
			else if(token=='O')
			{
				g.setColor(java.awt.Color.WHITE.brighter());
				g.fillOval(getWidth()/2-15,getHeight()/2-15,30,30);
			}
			else
			{
				
				g.setColor(java.awt.Color.black);
				g.drawLine(0,getHeight()/2,getWidth(),getHeight()/2);
				g.drawLine(getWidth()/2,0,getWidth()/2,getHeight());
			}
		}
		class ClickListenner extends MouseAdapter
		{
			public void mouseClicked(MouseEvent e)
			{
				if(token==' '&&myTurn)
				{
					setToken(myTaken);
					myTurn=false;
					rowSelected=row;
					columnSelected=colnum;
					jlbStatus.setText("等待对方落子");
					waiting=false;
				}
			}
		}
	}
	class Chat extends JPanel
	{
		 private  JTextField jtf=new JTextField();
		 private  JTextArea jta=new JTextArea();
		 private  JScrollPane jpa;
		 private  JScrollBar jsb;
		public Chat()
		{
		
			JPanel p=new JPanel();
	    	p.setLayout(new BorderLayout());
	    	p.add(new JLabel("输入："),BorderLayout.WEST);
	    	p.add(jtf,BorderLayout.CENTER);
	    	jta.setColumns(30);
	    	jtf.setFont(new Font("SansSerif",Font.BOLD,16));
	    	jta.setFont(new Font("SansSerif",Font.BOLD,16));
	    	jtf.setHorizontalAlignment(JTextField.LEFT);
	    	jtf.addActionListener(new TextFieldListener());
	    	jta.setEditable(false);
	    	JLabel jlb=new JLabel("聊天室");
	    	jlb.setFont(new Font("SansSerif",Font.BOLD,25));
	    	jlb.setHorizontalAlignment(JLabel.CENTER);
	    	jta.setBackground(Color.GRAY.brighter());
	    	setLayout(new BorderLayout());
	    	jpa=new JScrollPane(jta);
	    	jsb=jpa.getVerticalScrollBar();
	    	jta.setLineWrap(true);
	    	jpa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    	add(jpa,BorderLayout.CENTER);
	    	add(p,BorderLayout.SOUTH);
	    	add(jlb,BorderLayout.NORTH);
		}
		public void setchat(String s)
		{
			jta.append("对方:\n");
			jta.append("            "+s+'\n');
			jsb.setValue(jsb.getMaximum());
		}
		private class TextFieldListener implements ActionListener
	    {
			@Override
			public void actionPerformed(ActionEvent e) {
				jta.append("我:\n");
				jta.append("            "+jtf.getText()+'\n');
				jsb.setValue(jsb.getMaximum());
				try {
					if(ready==true)
					{
					  toServer.writeInt(CHAT);
					  toServer.writeUTF(jtf.getText());
					  toServer.flush();
					}
					jtf.setText(null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
	    } 
	}
	private class ABOUT implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent a) {
			JOptionPane.showMessageDialog(null,"同济大学人工智能课作业\n代文海   尹俏   杨晓朋\n2016.5.28","about",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	private class RENJI implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(isfree)
			{
				isrenji=true;
				connectToServer();
			}
			else
			{
				JOptionPane.showMessageDialog(null,"您已经在游戏中了","error",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	private class PIPEI implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(isfree)
			{
				isrenji=false;
				connectToServer();
			}
			else
			{
				JOptionPane.showMessageDialog(null,"您已经在游戏中了","error",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}
