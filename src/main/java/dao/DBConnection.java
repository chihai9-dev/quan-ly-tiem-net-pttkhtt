package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection  {
    private static final String HOST = "127.0.0.1";
    private static final String PORT = "3306";
    private static  final  String USER = "root";
    private static final  String PASS = "12345";
    private static final String DATABASE = "quanlytiemnet_simple";
    private static final String URL = "jdbc:mysql://"+HOST+":"+PORT + "/"+DATABASE
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Ho_Chi_Minh"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";
    ;

    private static Connection conn = null;

    public static Connection getConnection()  {
        try {
            if(conn == null || conn.isClosed()){
                conn = DriverManager.getConnection(URL,USER,PASS);
                System.out.println("Kết nối database thành công !");
            }
        }catch (SQLException e ){
            System.out.println("Lỗi kết nối database !");
            e.printStackTrace();
        }
        return conn;
    }
    public static void closeConnection(){
        try {
            if(conn != null && !conn.isClosed()){
                conn.close();
                conn = null;
                System.out.println("Đã đống kết nối database ! ");
            }
        }catch (SQLException e ){
            System.out.println("Lỗi đống kết nối database ! ");
            e.printStackTrace();
        }
    }
    public static boolean testConnection(){
        try{
            if(conn != null && !conn.isClosed()){

                System.out.println("Database : " + DATABASE);
                return true;
            }
        } catch (SQLException e ){
            System.out.println("Lỗi test kết nối database !" + e.getMessage());
        }
        return false;
    }

}