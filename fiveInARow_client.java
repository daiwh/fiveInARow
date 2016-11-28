package �˹�����__������;

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
		JMenu newgame=new JMenu("����Ϸ");
		JMenu about=new JMenu("����");
		JMenuItem renji=new JMenuItem("�˻�");JMenuItem pipei=new JMenuItem("ƥ��");JMenuItem guanyu=new JMenuItem("����");
		newgame.add(renji);newgame.add(pipei);about.add(guanyu);
		guanyu.addActionListener(new ABOUT());
		renji.addActionListener(new RENJI());
		pipei.addActionListener(new PIPEI());
		jmb.add(newgame);
		jmb.add(about);
		this.setJMenuBar(jmb);
		host=JOptionPane.showInputDialog(null,"���������IP��ַ","input",JOptionPane.OK_OPTION);
		jlbTitle.setText("�������Ϸ��ʼ��Ϸ");
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
		   JOptionPane.showMessageDialog(null,"���ӵ�������������ȷ��IP��ַ������ȷ","error",JOptionPane.INFORMATION_MESSAGE);
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
				jlbTitle.setText("���1������Ϊ��ɫ");
				jlbStatus.setText("�ȴ����2�ļ���......");
				fromeServer.readInt();
				jlbStatus.setText("���2�Ѽ��룬��Ϸ��ʼ");
				ready=true;
				myTurn=true;	
			}
			else if(player==PLAYER2)
			{
				myTaken='O';
				otherTaken='X';
				jlbTitle.setText("���2������Ϊ��ɫ");
				jlbStatus.setText("�ȴ����1����");
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
				jlbTitle.setText(jlbTitle.getText()+"   ��Ӯ�ˣ�");
			
			else if(myTaken=='O')
			{
				jlbTitle.setText(jlbTitle.getText()+"   �����ˣ�");
				receiveMove();
			}
		}
		else if(status==PLAYER2_WON)
		{
			continueToPlay=false;
			if(myTaken=='O')
				jlbTitle.setText(jlbTitle.getText()+"   ��Ӯ�ˣ�");
			else if(myTaken=='X')
			{
				jlbTitle.setText(jlbTitle.getText()+"   �����ˣ�");
				receiveMove();
			}
		}
		else if(status==DRAW)	
		{
			continueToPlay=false;
			jlbTitle.setText(jlbTitle.getText()+"   ƽ�֣�");
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
					jlbStatus.setText("�ȴ��Է�����");
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
	    	p.add(new JLabel("���룺"),BorderLayout.WEST);
	    	p.add(jtf,BorderLayout.CENTER);
	    	jta.setColumns(30);
	    	jtf.setFont(new Font("SansSerif",Font.BOLD,16));
	    	jta.setFont(new Font("SansSerif",Font.BOLD,16));
	    	jtf.setHorizontalAlignment(JTextField.LEFT);
	    	jtf.addActionListener(new TextFieldListener());
	    	jta.setEditable(false);
	    	JLabel jlb=new JLabel("������");
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
			jta.append("�Է�:\n");
			jta.append("            "+s+'\n');
			jsb.setValue(jsb.getMaximum());
		}
		private class TextFieldListener implements ActionListener
	    {
			@Override
			public void actionPerformed(ActionEvent e) {
				jta.append("��:\n");
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
			JOptionPane.showMessageDialog(null,"ͬ�ô�ѧ�˹����ܿ���ҵ\n���ĺ�   ����   ������\n2016.5.28","about",JOptionPane.INFORMATION_MESSAGE);
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
				JOptionPane.showMessageDialog(null,"���Ѿ�����Ϸ����","error",JOptionPane.INFORMATION_MESSAGE);
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
				JOptionPane.showMessageDialog(null,"���Ѿ�����Ϸ����","error",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}
