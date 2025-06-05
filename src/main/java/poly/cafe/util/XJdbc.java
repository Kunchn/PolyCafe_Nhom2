package poly.cafe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp tiện ích hỗ trợ làm việc với CSDL quan hệ
 *
 * @author NghiemN (được điều chỉnh dựa trên DBConnection của người dùng)
 * @version 1.1
 */
public class XJdbc {

    private static Connection connection;

    // Thông tin kết nối từ DBConnection.java của bạn
    private static final String DB_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=PolyCafeDB;encrypt=false"; // Đã cập nhật
    private static final String DB_USERNAME = "sa"; // Đã cập nhật
    private static final String DB_PASSWORD = "hung24725"; // Đã cập nhật

    /**
     * Mở kết nối nếu chưa mở hoặc đã đóng
     *
     * @return Kết nối đã sẵn sàng
     */
    public static Connection openConnection() {
        try {
            if (!XJdbc.isReady()) {
                Class.forName(DB_DRIVER); // Sử dụng hằng số driver
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); // Sử dụng thông tin kết nối đã cập nhật
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage()); // In ra lỗi để dễ debug
            // Cân nhắc throw new RuntimeException(e); nếu muốn dừng chương trình khi không kết nối được
            return null; // Hoặc throw lỗi tùy theo cách bạn muốn xử lý
        }
        return connection;
    }

    /**
     * Đóng kết nối
     */
    public static void closeConnection() {
        try {
            if (XJdbc.isReady()) {
                connection.close();
                connection = null; // Đặt lại connection thành null sau khi đóng
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối CSDL: " + e.getMessage());
            // throw new RuntimeException(e);
        }
    }

    /**
     * Kiểm tra kết nối đã sẵn sàng hay chưa
     * @return true nếu kết nối đã được mở và hợp lệ
     */
    public static boolean isReady() {
        try {
            // Kiểm tra thêm connection.isValid(1) để chắc chắn kết nối còn dùng được (timeout 1 giây)
            return (connection != null && !connection.isClosed() && connection.isValid(1));
        } catch (SQLException e) {
            // Nếu có lỗi khi kiểm tra isValid, coi như kết nối không sẵn sàng
            return false;
        }
    }

    /**
     * Thao tác dữ liệu
     *
     * @param sql câu lệnh SQL (INSERT, UPDATE, DELETE)
     * @param values các giá trị cung cấp cho các tham số trong SQL
     * @return số lượng bản ghi đã thực hiện
     * @throws RuntimeException không thực thi được câu lệnh SQL
     */
    public static int executeUpdate(String sql, Object... values) {
        try {
            var stmt = XJdbc.getStmt(sql, values);
            if (stmt == null) return -1; // Nếu không tạo được statement
            return stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Lỗi executeUpdate: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Truy vấn dữ liệu
     *
     * @param sql câu lệnh SQL (SELECT)
     * @param values các giá trị cung cấp cho các tham số trong SQL
     * @return tập kết quả truy vấn
     * @throws RuntimeException không thực thi được câu lệnh SQL
     */
    public static ResultSet executeQuery(String sql, Object... values) {
        try {
            var stmt = XJdbc.getStmt(sql, values);
            if (stmt == null) return null; // Nếu không tạo được statement
            return stmt.executeQuery();
        } catch (SQLException ex) {
            System.err.println("Lỗi executeQuery: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Truy vấn một giá trị
     *
     * @param <T> kiểu dữ liệu kết quả
     * @param sql câu lệnh SQL (SELECT)
     * @param values các giá trị cung cấp cho các tham số trong SQL
     * @return giá trị truy vấn hoặc null
     * @throws RuntimeException không thực thi được câu lệnh SQL
     */
    public static <T> T getValue(String sql, Object... values) {
        try {
            var resultSet = XJdbc.executeQuery(sql, values);
            if (resultSet != null && resultSet.next()) {
                return (T) resultSet.getObject(1);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println("Lỗi getValue: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Tạo PreparedStatement từ câu lệnh SQL/PROC
     *
     * @param sql câu lệnh SQL/PROC
     * @param values các giá trị cung cấp cho các tham số trong SQL/PROC
     * @return đối tượng đã tạo hoặc null nếu kết nối thất bại
     * @throws SQLException không tạo được PreparedStatement
     */
    private static PreparedStatement getStmt(String sql, Object... values) throws SQLException {
        var conn = XJdbc.openConnection();
        if (conn == null) {
            System.err.println("Không thể tạo PreparedStatement do kết nối CSDL thất bại.");
            return null; // Trả về null nếu không mở được kết nối
        }
        PreparedStatement stmt;
        if (sql.trim().startsWith("{")) { // Kiểm tra nếu là gọi stored procedure
            stmt = conn.prepareCall(sql);
        } else {
            stmt = conn.prepareStatement(sql);
        }
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
        return stmt;
    }

    // Các phương thức demo có thể giữ lại để bạn tự kiểm tra kết nối nếu cần
    // Hoặc xóa đi nếu không dùng đến trong phiên bản cuối
    public static void main(String[] args) {
        // Ví dụ kiểm tra kết nối
        Connection testConnection = XJdbc.openConnection();
        if (XJdbc.isReady()) {
            System.out.println("Kết nối CSDL thành công!");
            // Thử demo
            // demo1(); // Bạn cần có bảng Drinks và dữ liệu phù hợp để chạy demo
            // demo2();
            // demo3();
            XJdbc.closeConnection();
            System.out.println("Đã đóng kết nối.");
        } else {
            System.out.println("Kết nối CSDL thất bại!");
        }
    }

    private static void demo1() {
        // Giả sử bạn có bảng Drinks trong PolyCafeDB
        // String sql = "SELECT * FROM Drinks WHERE UnitPrice BETWEEN ? AND ?";
        // var rs = XJdbc.executeQuery(sql, 1.5, 5.0);
        // try {
        //     if(rs != null) {
        //         while(rs.next()){
        //             // Xử lý dữ liệu
        //             System.out.println(rs.getString(1)); // Ví dụ
        //         }
        //     }
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
        System.out.println("Chạy demo1 (cần điều chỉnh SQL và bảng cho PolyCafeDB)");
    }

    private static void demo2() {
        // String sql = "SELECT max(UnitPrice) FROM Drinks WHERE UnitPrice > ?";
        // var maxPrice = XJdbc.getValue(sql, 1.5);
        // System.out.println("Max price: " + maxPrice);
        System.out.println("Chạy demo2 (cần điều chỉnh SQL và bảng cho PolyCafeDB)");
    }

    private static void demo3() {
        // String sql = "DELETE FROM Drinks WHERE UnitPrice < ?";
        // var count = XJdbc.executeUpdate(sql, 0.0);
        // System.out.println("Rows deleted: " + count);
        System.out.println("Chạy demo3 (cần điều chỉnh SQL và bảng cho PolyCafeDB)");
    }
}