package dev.drunkmirror.dao.impl;

import dev.drunkmirror.annotation.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.autumn.annotaion.Component;
import dev.drunkmirror.dao.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Component
public class DaoImpl4Db extends Dao {

    private int idEnty, idAtr, idForField;
    int level;
    private List<Object> list = new ArrayList<Object>();

    static Logger log = LogManager.getLogger(DaoImpl4Xml.class);


    public DaoImpl4Db() throws SQLException {
        log.warn("public DaoImpl4Db()");
        level = 0;
        idAtr = 1;
        idEnty = 1;
        idForField = 1;
        createDbUserTable();
    }


    @Override
    public void save(Object obj) {
        log.info("\n\n\n");
        log.info("DaoImpl4Db save");

        try {

            Class c = obj.getClass();
            log.info(getSpaces() + "Type: " + c.getName());

            research(c, obj, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public Object get() {
        // TODO Auto-generated method stub
        return null;
    }

    public void getfromDB() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        getFromEntities();
    }

    protected Object parse(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    private void getFromEntities() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        try {
            FileInputStream fis;
            Properties property = new Properties();
            Connection dbConnection = getDBConnection();
            PreparedStatement statement = dbConnection.prepareStatement(
                    "SELECT * FROM Entities");
            ResultSet rs = statement.executeQuery();

            fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);

            String path = property.getProperty("example.path");

            while (rs.next()) {
                int id_entity = rs.getInt("ID_Entities");
                String nameClass = rs.getString("nameClass");
                log.info(nameClass);
                //Integer idParent = rs.getInt("idParent");
                if (id_entity < 3) {
                    Class clazz = Class.forName(path + nameClass);
                    Object o = clazz.getDeclaredConstructor().newInstance();
                    getFromAttribute(id_entity, o);
                    list.add(o);
                }
                log.info("entity_name : " + id_entity);
                log.info("nameClass : " + nameClass);
            }
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getFromAttribute(int id, Object o) throws SQLException, NoSuchFieldException, IllegalAccessException {
        try {
            Connection dbConnection = getDBConnection();
            PreparedStatement statement = dbConnection.prepareStatement(
                    "SELECT * FROM Attribute where ID_Entities=?");
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id_attribute = rs.getInt("ID_Attribute");
                String nameAttr = rs.getString("nameAttr");
                String value = rs.getString("value");
                String type = rs.getString("type");
                log.info(value + " " + type);
                Class c = o.getClass();
                Field field = o.getClass().getDeclaredField(nameAttr);
                field.setAccessible(true);
                if ((field.getType().getName()).equals("java.lang.String")) {
                    field.set(o, value);
                } else if ((field.getType().getName()).equals("java.lang.Integer"))
                    field.set(o, Integer.parseInt(value));

            }
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertIntoEntities(int id, String nameclass, int idParent) throws SQLException {
        try {
            Connection dbConnection = getDBConnection();
            PreparedStatement statement = dbConnection.prepareStatement(
                    "INSERT INTO Entities(ID_Entities, nameClass, ID_Parent) VALUES(?,?,?)");
            statement.setInt(1, id);
            statement.setString(2, nameclass);
            statement.setInt(3, idParent);
            statement.executeUpdate();
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertIntoEntities(int id, String nameclass) throws SQLException {
        try {
            Connection dbConnection = getDBConnection();
            PreparedStatement statement = dbConnection.prepareStatement(
                    "INSERT INTO Entities(ID_Entities, nameClass) VALUES(?,?)");
            statement.setInt(1, id);
            statement.setString(2, nameclass);
            statement.executeUpdate();
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertIntoAttribute(int id, String name, String value, String type, int id_entity) throws SQLException {
        try {
            Connection dbConnection = getDBConnection();
            PreparedStatement statement = dbConnection.prepareStatement(
                    "INSERT INTO Attribute(ID_Attribute,nameAttr, value,type, ID_Entities) VALUES(?,?,?,?,?)");
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, value);
            statement.setString(4, type);
            statement.setInt(5, id_entity);
            statement.executeUpdate();
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getSpaces() {
        String space = "    ";
        String spaces = "";
        for (int i = 0; i < level; i++) {
            spaces += space;
        }

        return spaces;
    }

    private void research(Class c, Object obj, boolean isSuper) throws SQLException, IllegalAccessException {
        level++;
        String nameType = c.getName();
        String simpleName = c.getSimpleName();

        log.info(getSpaces() + "Type ins: " + nameType);
        if (simpleName.equals("Object")) {
            level--;
            return;
        }


        if (isSuper == true) {
            insertIntoEntities(idEnty, simpleName, 1);
            idForField = idEnty;
            idEnty++;
        } else {
            insertIntoEntities(idEnty, simpleName);
            idForField = idEnty;
            idEnty++;
        }


        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            log.info(getSpaces() + "Field: " + f.getName());

            Class type = f.getType();
            level++;
            log.info(getSpaces() + "Field type: " + type.getName());

            SecondName sn = (SecondName) c.getAnnotation(SecondName.class);
            if (sn != null) {
                log.info(getSpaces() + "SecondName: " + sn.value());
            }
            Transient tn = (Transient) c.getAnnotation(Transient.class);
            if (tn == null) {
                f.setAccessible(true);
                try {
                    EmbeddedCollection embCol = (EmbeddedCollection) f.getAnnotation(EmbeddedCollection.class);
                    EmbeddedElement embType = (EmbeddedElement) f.getAnnotation(EmbeddedElement.class);

                    if (embType != null) {
                        research(f.get(obj).getClass(), f.get(obj), false);
                    } else if (embCol != null) {
                        //log.info(getSpaces() + "Collection handler");

                        Class researchedClass = f.get(obj).getClass();
                        if (Map.class.isAssignableFrom(researchedClass)) {
                            //log.info(getSpaces() + "Field type: map");
                            //log.info(getSpaces() + "Field concrete type: " + researchedClass);
                        } else if (List.class.isAssignableFrom(researchedClass)) {
                            //log.info(getSpaces() + "Field type: list");
                            //log.info(getSpaces() + "Field concrete type: " + researchedClass);
                        }
                    } else {
                        log.info(getSpaces() + "Field value=" + f.get(obj));

                        insertIntoAttribute(idAtr, f.getName(), f.get(obj).toString(), type.getName(), idForField);
                        idAtr++;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                f.setAccessible(false);
            } else {
                log.info(getSpaces() + "Trancient");
            }
            level--;
        }
        level--;
        research(c.getSuperclass(), obj, true);
    }

    private Connection getDBConnection() {
        Connection dbConnection = null;
        FileInputStream fis;
        Properties property = new Properties();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.info(e.getMessage());
        }
        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);

            String host = property.getProperty("db.host");
            String login = property.getProperty("db.login");
            String password = property.getProperty("db.password");
            dbConnection = DriverManager.getConnection(host, login, password);
            return dbConnection;
        } catch (SQLException e) {
            log.info(e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dbConnection;
    }

    private void createDbUserTable() throws SQLException {
        Connection dbConnection = null;
        Statement statement = null;

        String createTable1SQL = "CREATE TABLE PacketDescription("
                + "ID_PacketDescription INT NOT NULL, "
                + "type_facade VARCHAR(100) NOT NULL, "
                + "CREATED_DATE DATE , "
                + "PRIMARY KEY (ID_PacketDescription) "
                + ")";


        String createTable2SQL = "CREATE TABLE Entities("
                + "ID_Entities INT NOT NULL, "
                + "nameClass VARCHAR(100) NOT NULL, "
                + "ID_Parent INT , "
                + "ID_PacketDescription INT, "
                + "PRIMARY KEY (ID_Entities), "
                + "CONSTRAINT fkParent FOREIGN KEY (ID_Parent) references Entities(ID_Entities),"
                + "CONSTRAINT fkPacket FOREIGN KEY (ID_PacketDescription) references PacketDescription(ID_PacketDescription)"
                + ")";

        String createTable3SQL = "CREATE TABLE Attribute("
                + "ID_Attribute INT NOT NULL, "
                + "nameAttr VARCHAR(100) NOT NULL, "
                + "value VARCHAR(100), "
                + "type VARCHAR(100), "
                + "ID_Entities INT, "
                + "PRIMARY KEY (ID_Attribute ), "
                + "CONSTRAINT fkEntities foreign key (ID_Entities) references Entities(ID_Entities)"
                + ")";

        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();

            try {
                statement.execute(createTable1SQL);
            } catch (SQLException e) {
                log.info(e.getMessage());
            }
            try {
                statement.execute(createTable2SQL);
            } catch (SQLException e) {
                log.info(e.getMessage());
            }
            try {
                statement.execute(createTable3SQL);
            } catch (SQLException e) {
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
