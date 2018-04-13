import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 当前万年历支持以下功能
 *  - 获得指定日期的前一天
 *  - 获得指定日期的后一天
 *  - 接收年月日，输出该日是星期几
 *  - 获取某年某月有多少天
 *  - 判断某年是否是闰年
 */
public class MyCalendar {

    /**
     * 获得指定日期的前一天
     * @param specifiedDay yy-MM-dd
     * @return String
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day-1);

        String dayBefore=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }
    /**
     * 获得指定日期的后一天
     * @param specifiedDay yy-MM-dd
     * @return String
     * @throws Exception
     */
    public static String getSpecifiedDayAfter(String specifiedDay){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day+1);

        String dayAfter=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayAfter;
    }

    /**
     * 接收年月日，输出该日是星期几
     * @param specifiedDay yy-MM-dd
     * @return int
     * @throws Exception
     */
    public static String getWeekOfDate(String specifiedDay) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取某年某月有多少天
     * @param year
     * @param month
     * @return int
     */
    public static int getDayOfMonth(int year,int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断某年是否是闰年
     * @param year
     * @return boolean
     */
    public static boolean isRun(int year) {
        boolean isRn = false;
        isRn = ((year % 4 == 0) && !(year % 100 == 0) || year % 400 == 0);
        return isRn;
    }

    public static void main(String[] args) {
        System.out.println(getWeekOfDate("2018-04-12"));
    }
}
