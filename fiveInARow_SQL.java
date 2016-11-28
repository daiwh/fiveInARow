package 人工智能__五子棋;
import java.sql.*;
public class fiveInARow_SQL {
	public void insert(String keyword,String sentence) throws SQLException
	{
		 Connection con=null;
         Statement stmt=null;
         ResultSet rs=null;
         try {
			Class.forName("com.mysql.jdbc.Driver");
		    }  catch (ClassNotFoundException e1) {
			   System.out.println("数据库驱动加载失败");
		    }
         String url="jdbc:mysql://localhost:3306/fiveinarow?useUnicode=true&characterEncoding=gbk";
         try {
			con=DriverManager.getConnection(url,"root","");
		     } catch (SQLException e) {
			System.out.println("数据库连接失败");
		     }
         try {
			stmt=con.createStatement();
		} catch (SQLException e) {
			System.out.println("表获取失败");
		}
         String sql="select * from fiveinarow where keywords='"+keyword+"'";
         rs=stmt.executeQuery(sql);
         if(rs.next())
         {
        	 sql="update fiveinarow set sentence='"+sentence +"'where keywords='"+keyword+"'";
        	 stmt.executeUpdate(sql);
         }
         else
         {
        	 sql="insert into fiveinarow values('1','"+keyword+"','"+sentence+"')";
        	 stmt.executeUpdate(sql);
         }
         rs.close();
         stmt.close();
         con.close();
	}
	public String find(String word) throws SQLException
	{
		 Connection con=null;
         Statement stmt=null;
         ResultSet rs=null;
         try {
			Class.forName("com.mysql.jdbc.Driver");
		    }  catch (ClassNotFoundException e1) {
			   System.out.println("数据库驱动加载失败");
		    }
         String url="jdbc:mysql://localhost:3306/fiveinarow?useUnicode=true&characterEncoding=gbk";
         try {
			con=DriverManager.getConnection(url,"root","");
		     } catch (SQLException e) {
			System.out.println("数据库连接失败");
		     }
         try {
			stmt=con.createStatement();
		} catch (SQLException e) {
			System.out.println("表获取失败");
		}
         String sql="select * from fiveinarow";
         try {
			 rs=stmt.executeQuery(sql);
		   } catch (SQLException e) {
			 System.out.println("数据查询失败");
		  }
         while(rs.next())
         {
        	 String s=rs.getString("keywords");
        	 if(word.contains(s))
        		 return rs.getString("sentence");
         }
        	 return null;
	}
}
