package dao;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public static Connection getConnection() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn == null || conn.isClosed()) {
            conn = DBConnection.getConnection(); // tái sử dụng file có sẵn
            threadLocal.set(conn);
        }
        return conn;
    }

    public static void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public static void commit() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn != null) conn.commit();
    }

    public static void rollback() {
        Connection conn = threadLocal.get();
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        Connection conn = threadLocal.get();
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            threadLocal.remove();
        }
    }
}