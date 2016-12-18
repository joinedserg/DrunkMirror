package dev.drunkmirror.dao.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.autumn.annotaion.Component;
import dev.drunkmirror.dao.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DaoImpl4Db extends Dao {

	private String url="jdbc:postgresql://localhost:5432/testDB";
	private String usr="postgres";
	private String password="root";

	static Logger log = LogManager.getLogger(DaoImpl4Xml.class);
	
	
	public DaoImpl4Db() {
		log.warn("public DaoImpl4Db()");
	}


	@Override
	public void save(Object obj) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Object get() {
		// TODO Auto-generated method stub
		return null;
	}


	protected Object parse(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	private Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			log.info(e.getMessage());
		}
		try {
			dbConnection = DriverManager.getConnection(url,usr,password);
			return dbConnection;
		} catch (SQLException e) {
			log.info(e.getMessage());
		}
		return dbConnection;
	}

	private void createDbUserTable() throws SQLException {
		Connection dbConnection = null;
		Statement statement = null;

		String createTable1SQL = "CREATE TABLE PacketDescription("
				+ "ID_PacketDescription INT NOT NULL, "
				+ "type_facade VARCHAR(20) NOT NULL, "
				+ "CREATED_DATE DATE , "
				+ "PRIMARY KEY (ID_PacketDescription) "
				+ ")";


		String createTable2SQL = "CREATE TABLE Entities("
				+ "ID_Entities INT NOT NULL, "
				+ "nameClass VARCHAR(20) NOT NULL, "
				+ "ID_Parent INT , "
				+ "ID_PacketDescription INT, "
				+ "PRIMARY KEY (ID_Entities), "
				+"CONSTRAINT fkParent FOREIGN KEY (ID_Parent) references Entities(ID_Entities),"
				+"CONSTRAINT fkPacket FOREIGN KEY (ID_PacketDescription) references PacketDescription(ID_PacketDescription)"
				+ ")";

		String createTable3SQL = "CREATE TABLE Attribute("
				+ "ID_Attribute INT NOT NULL, "
				+ "value VARCHAR(20), "
				+ "type VARCHAR(20), "
				+ "ID_Entities INT, "
				+ "PRIMARY KEY (ID_Attribute ), "
				+"CONSTRAINT fkEntities foreign key (ID_Entities) references Entities(ID_Entities)"
				+ ")";

		try {
			dbConnection = getDBConnection();
			statement = dbConnection.createStatement();

			// выполнить SQL запрос
			try {statement.execute(createTable1SQL);}
			catch (SQLException e){
				log.info(e.getMessage());
			}
			try {statement.execute(createTable2SQL);}
			catch (SQLException e){
				log.info(e.getMessage());
			}
			try {statement.execute(createTable3SQL);}
			catch (SQLException e){
				log.info(e.getMessage());
			}
			log.info("Tables \"Entities Attribute PacketDescription\" are created!");
		} catch (SQLException e) {
			log.info(e.getMessage());
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}

	}

}
