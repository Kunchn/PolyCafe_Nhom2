package poly.cafe.util;

import java.time.LocalDate;
import java.util.Date; // Đây là java.util.Date
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // Thêm NoArgsConstructor nếu bạn muốn có một constructor không tham số

@Data
@AllArgsConstructor // Tạo constructor public TimeRange(Date begin, Date end)
@NoArgsConstructor // Thêm nếu bạn cần constructor không tham số cho các mục đích khác
public class TimeRange {

    private Date begin = new Date(); // Kiểu java.util.Date
    private Date end = new Date();   // Kiểu java.util.Date

    /**
     * Constructor private để tạo TimeRange từ LocalDate.
     * Sửa lỗi bằng cách gán trực tiếp vào các trường.
     */
    private TimeRange(LocalDate beginDate, LocalDate endDate) {
        // Chuyển đổi LocalDate thành java.sql.Date, sau đó gán cho trường java.util.Date
        // Điều này hợp lệ vì java.sql.Date là một lớp con của java.util.Date.
        if (beginDate != null) {
            this.begin = java.sql.Date.valueOf(beginDate);
        }
        if (endDate != null) {
            this.end = java.sql.Date.valueOf(endDate);
        }
    }

    public static TimeRange today() {
        LocalDate today = LocalDate.now();
        // Gọi constructor private TimeRange(LocalDate, LocalDate)
        // Logic gốc của bạn dường như muốn tạo một khoảng từ 'today' đến 'today + 1 day (exclusive)'
        // Điều này thường được sử dụng để bao hàm tất cả các thời điểm trong ngày hôm nay.
        return new TimeRange(today, today.plusDays(1));
    }

    public static TimeRange thisWeek() {
        LocalDate now = LocalDate.now();
        // Lấy ngày đầu tuần (Thứ Hai, giả sử tuần bắt đầu từ Thứ Hai)
        LocalDate beginOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1L); // DayOfWeek.MONDAY is 1
        // Lấy ngày cuối tuần (Chủ Nhật) và cộng 1 để tạo khoảng [beginOfWeek, endOfWeekNextDay)
        LocalDate endOfWeek = beginOfWeek.plusDays(6); // Ngày cuối cùng của tuần hiện tại
        return new TimeRange(beginOfWeek, endOfWeek.plusDays(1));
    }

    public static TimeRange thisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate beginOfMonth = now.withDayOfMonth(1);
        // LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth()); // Ngày cuối cùng của tháng hiện tại
        // Để tạo khoảng [beginOfMonth, beginOfNextMonth), ta cộng 1 tháng vào ngày đầu tháng
        return new TimeRange(beginOfMonth, beginOfMonth.plusMonths(1));
    }

    public static TimeRange thisQuarter() {
        LocalDate now = LocalDate.now();
        // Xác định tháng đầu tiên của quý hiện tại
        int currentMonth = now.getMonthValue();
        int firstMonthOfQuarter;
        if (currentMonth <= 3) {
            firstMonthOfQuarter = 1;
        } else if (currentMonth <= 6) {
            firstMonthOfQuarter = 4;
        } else if (currentMonth <= 9) {
            firstMonthOfQuarter = 7;
        } else {
            firstMonthOfQuarter = 10;
        }
        LocalDate beginOfQuarter = LocalDate.of(now.getYear(), firstMonthOfQuarter, 1);
        // Để tạo khoảng [beginOfQuarter, beginOfNextQuarter), ta cộng 3 tháng vào ngày đầu quý
        return new TimeRange(beginOfQuarter, beginOfQuarter.plusMonths(3));
    }

    public static TimeRange thisYear() {
        LocalDate now = LocalDate.now();
        LocalDate beginOfYear = now.withDayOfYear(1);
        // Để tạo khoảng [beginOfYear, beginOfNextYear), ta cộng 1 năm vào ngày đầu năm
        return new TimeRange(beginOfYear, beginOfYear.plusYears(1));
    }
}
