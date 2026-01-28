package entity;
import java.sql.*;

public class KhuMay {
    private String makhu;
    private String tenkhu;
    private double giacoso;
    private int somaytoida;
    private String trangthai;

    public KhuMay() {
    }

    public KhuMay(String makhu, String tenkhu, double giacoso, int somaytoida, String trangthai) {
        this.makhu = makhu;
        this.tenkhu = tenkhu;
        this.giacoso = giacoso;
        this.somaytoida = somaytoida;
        this.trangthai = trangthai;
    }

    public String getMakhu() {
        return makhu;
    }

    public void setMakhu(String makhu) {
        this.makhu = makhu;
    }

    public String getTenkhu() {
        return tenkhu;
    }

    public void setTenkhu(String tenkhu) {
        this.tenkhu = tenkhu;
    }

    public double getGiacoso() {
        return giacoso;
    }

    public void setGiacoso(double giacoso) {
        this.giacoso = giacoso;
    }

    public int getSomaytoida() {
        return somaytoida;
    }

    public void setSomaytoida(int somaytoida) {
        this.somaytoida = somaytoida;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
    public String generateMaMay() {
        String sql = "SELECT MaKH FROM khachhang ORDER BY MaKH DESC LIMIT 1";

        try (
                Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            if (rs.next()) {
                String lastMa = rs.getString("MaKH");  // VD: "KH015"
                int num = Integer.parseInt(lastMa.substring(2));  // 15
                return String.format("KH%03d", num + 1);  // "KH016"
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "MAY001";  // Nếu chưa có dữ liệu
    }
}
}