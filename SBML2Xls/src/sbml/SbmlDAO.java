package sbml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Species;

public class SbmlDAO {
	private Connection con;
	private String url="jdbc:postgresql://localhost:5432/sbml";
	private String user="postgres";
	private String password="root";

	/**
	 * 构造函数
	 * 载入postgres的驱动程序，并连接数据库
	 */
	public SbmlDAO(){
		try {
			Class.forName("org.postgresql.Driver");
			this.con=DriverManager.getConnection(url, user, password);
			System.out.println("Connection begin...");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接
	 */
	public void closeConnection(){
		if (this.con!=null){
			try {
				con.close();
				System.out.println("Connection closed!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据ID得到Metabolic的名字
	 * @param id Metabolic的ID
	 * @return
	 */
	public String getMetabolicName(String id){
		String name="";
		try {
			String querySQL="select metabolic_name from metabolic  where metabolic_id=?";
			PreparedStatement ps=con.prepareStatement(querySQL);
			ps.setString(1,id);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				name=rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 把所有的metabolic放入数据库中
	 * @param listOfSpecies metabolic的列表
	 */
	public void insertMetabolics(ListOf<Species> listOfSpecies){
		String deleteSql="delete from metabolic";
		String sql="insert into metabolic(metabolic_id,metabolic_name,compartment_id) values(?,?,?)";
		try {
			PreparedStatement deletePs=this.con.prepareStatement(deleteSql);
			deletePs.executeUpdate();
			PreparedStatement ps=this.con.prepareStatement(sql);
			for(Species specie:listOfSpecies){
				ps.setString(1, specie.getId());
				ps.setString(2, specie.getName());
				ps.setString(3, specie.getCompartment());
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



}
