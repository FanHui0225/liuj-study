package com.stereo.study.util;

/**
 日期类
 * @date
 * @version 1.0
 */

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class DateKit {


	/**
	 * 判断两个日期是否是同一天
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
				.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth
				&& cal1.get(Calendar.DAY_OF_MONTH) == cal2
				.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}

	/**
	 * 获取现在时间
	 *
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static Date getNowDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(0);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}
	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * @return String
	 */
	public static String getCurrTime() {
		return getCurrTime("yyyyMMddHHmmss");
	}

	public static String getCurrTime(String format) {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat(format);
		String s = outFormat.format(now);
		return s;
	}

	/**
	 * 获取简单的时间格式 yyyyMMdd
	 * @param date
	 * @return
	 */
	public static String getSimpleDate(Date date) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			return formatter.format(date);
		}else {
			return null;
		}
	}

	public static String getNowSimpleDate()
	{
		return getSimpleDate(new Date());
	}

	/**
	 * 获取现在时间
	 *
	 * @return返回短时间格式 yyyy-MM-dd
	 */
	public static Date getNowDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(0);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	/**
	 * 获取现在时间
	 *
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public static String getStringTimeDate(long time, String format) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 获取现在时间
	 *
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	public static String getStringDateShort(String params) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(params);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取时间 小时:分;秒 HH:mm:ss
	 *
	 * @return
	 */
	public static String getTimeShort() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 *
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate, String params) {
		SimpleDateFormat formatter = new SimpleDateFormat(params);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
	/**
	 *
	 * <p>
	 * Title: 字符串日期格式化
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 *
	 * @param strDate
	 * @param afterDateType
	 *            格式化之前日期格式
	 * @param beforeDateType
	 *            格式化之后日期格式
	 * @return
	 */
	public static String strToFormatting(String strDate, String afterDateType, String beforeDateType) {
		return DateTime.parse(strDate, DateTimeFormat.forPattern(afterDateType)).toString(beforeDateType);
	}

	/**
	 * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 *
	 * @param dateDate
	 * @return
	 */
	public static String dateToStrLong(Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将短时间格式时间转换为字符串 yyyy-MM-dd
	 *
	 * @param dateDate
	 * @return
	 */
	public static String dateToStr(Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 *
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		if (strDate == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	public static Date strToDate(String strDate,String format) {
		if (strDate == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	public static String dateToString(Date date,String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	/**
	 * 得到现在时间
	 *
	 * @return
	 */
	public static Date getNow() {
		Date currentTime = new Date();
		return currentTime;
	}

	/**
	 * 提取一个月中的最后一天
	 *
	 * @param day
	 * @return
	 */
	public static Date getLastDate(long day) {
		Date date = new Date();
		long date_3_hm = date.getTime() - 3600000 * 34 * day;
		Date date_3_hm_date = new Date(date_3_hm);
		return date_3_hm_date;
	}

	/**
	 * 得到现在时间
	 *
	 * @return 字符串 yyyyMMdd HHmmss
	 */
	public static String getStringToday() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 得到现在小时
	 */
	public static String getHour() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String hour;
		hour = dateString.substring(11, 13);
		return hour;
	}

	public static int getHour(long dateTime) {
		Date date = new Date(dateTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		String hour;
		hour = dateString.substring(11, 13);
		return Integer.valueOf(hour);
	}

	/**
	 * 得到现在分钟
	 *
	 * @return
	 */
	public static String getMinute() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String min;
		min = dateString.substring(14, 16);
		return min;
	}

	/**
	 * 得到现在分钟
	 *
	 * @return
	 */
	public static int getMinute(long dateTime) {
		Date currentTime = new Date(dateTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String min;
		min = dateString.substring(14, 16);
		return Integer.valueOf(min);
	}

	/**
	 * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
	 *
	 * @param sformat
	 *            yyyyMMddhhmmss
	 * @return
	 */
	public static String getUserDate(String sformat) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(sformat);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
	 */
	public static String getTwoHour(String st1, String st2) {
		String[] kk = null;
		String[] jj = null;
		kk = st1.split(":");
		jj = st2.split(":");
		if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
			return "0";
		else {
			double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
			double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
			if ((y - u) > 0)
				return y - u + "";
			else
				return "0";
		}
	}

	/**
	 * 得到二个日期间的间隔天数
	 */
	public static String getTwoDay(String sj1, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		long day = 0;
		try {
			Date date = myFormatter.parse(sj1);
			Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "";
		}
		return day + "";
	}

	/**
	 * 时间前推或后推分钟,其中JJ表示分钟.
	 */
	public static String getPreTime(String sj1, String jj) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mydate1 = "";
		try {
			Date date1 = format.parse(sj1);
			long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
			date1.setTime(Time * 1000);
			mydate1 = format.format(date1);
		} catch (Exception e) {
		}
		return mydate1;
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static Date getNextTime(Date date, int delay) {
		try {
			Date d = (Date)date.clone();
			long myTime = (d.getTime() / 1000) + delay * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断是否润年
	 *
	 * @param ddate
	 * @return
	 */
	public static boolean isLeapYear(String ddate) {

		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 3.能被4整除同时能被100整除则不是闰年
		 */
		Date d = strToDate(ddate);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(d);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) {
			if ((year % 100) == 0)
				return false;
			else
				return true;
		} else
			return false;
	}

	/**
	 * 返回美国时间格式 26 Apr 2006
	 *
	 * @param str
	 * @return
	 */
	public static String getEDate(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(str, pos);
		String j = strtodate.toString();
		String[] k = j.split(" ");
		return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
	}

	/**
	 * 获取一个月的最后一天
	 *
	 * @param dat
	 * @return
	 */
	public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
		String str = dat.substring(0, 8);
		String month = dat.substring(5, 7);
		int mon = Integer.parseInt(month);
		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
			str += "31";
		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
			str += "30";
		} else {
			if (isLeapYear(dat)) {
				str += "29";
			} else {
				str += "28";
			}
		}
		return str;
	}

	/**
	 * 判断二个时间是否在同一个周
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeekDates(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	/**
	 * 产生周序列,即得到当前时间所在的年度是第几周
	 *
	 * @return
	 */
	public static String getSeqWeek() {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
		if (week.length() == 1)
			week = "0" + week;
		String year = Integer.toString(c.get(Calendar.YEAR));
		return year + week;
	}

	/**
	 * 获得一个日期所在的周的星期几的日期，如要找出2002年2月3日所在周的星期一是几号
	 *
	 * @param sdate
	 * @param num
	 * @return
	 */
	public static String getWeek(String sdate, String num) {
		// 再转换为时间
		Date dd = strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(dd);
		if (num.equals("1")) // 返回星期一所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		else if (num.equals("2")) // 返回星期二所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		else if (num.equals("3")) // 返回星期三所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		else if (num.equals("4")) // 返回星期四所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		else if (num.equals("5")) // 返回星期五所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		else if (num.equals("6")) // 返回星期六所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		else if (num.equals("0")) // 返回星期日所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}

	public static int getWeekNum(long dateTime)
	{
		return getWeekNum(new Date(dateTime));
	}

	public static int getWeekNum(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int week = c.get(Calendar.DAY_OF_WEEK);
		if (week == 1) {
			return 7;
		} else if (week == 2) {
			return 1;
		} else if (week == 3) {
			return 2;
		} else if (week == 4) {
			return 3;
		} else if (week == 5) {
			return 4;
		} else if (week == 6) {
			return 5;
		} else if (week == 7) {
			return 6;
		}else
			return 0;
	}

	public static int getWeekNum(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int week = c.get(Calendar.DAY_OF_WEEK);
		if (week == 1) {
			return 7;
		} else if (week == 2) {
			return 1;
		} else if (week == 3) {
			return 2;
		} else if (week == 4) {
			return 3;
		} else if (week == 5) {
			return 4;
		} else if (week == 6) {
			return 5;
		} else if (week == 7) {
			return 6;
		}else
			return 0;
	}

	/**
	 * 两个时间之间的天数
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDays(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// 转换为标准时间
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		Date mydate = null;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
		} catch (Exception e) {
		}
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}

	/**
	 * 形成如下的日历 ， 根据传入的一个时间返回一个结构 星期日 星期一 星期二 星期三 星期四 星期五 星期六 下面是当月的各个时间
	 * 此函数返回该日历第一行星期日所在的日期
	 *
	 * @param sdate
	 * @return
	 */
	public static String getNowMonth(String sdate) {
		// 取该时间所在月的一号
		sdate = sdate.substring(0, 8) + "01";

		// 得到这个月的1号是星期几
		Date date = strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int u = c.get(Calendar.DAY_OF_WEEK);
		String newday = getNextDay(sdate, (1 - u) + "");
		return newday;
	}

	/**
	 * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
	 *
	 * @param k
	 *            表示是取几位随机数，可以自己定
	 */

	public static String getNo(int k) {

		return getUserDate("yyyyMMddHHmmss") + getRandom(k);
	}

	/**
	 * 返回一个随机数
	 *
	 * @param i
	 * @return
	 */
	public static String getRandom(int i) {
		Random jjj = new Random();
		// int suiJiShu = jjj.nextInt(9);
		if (i == 0)
			return "";
		String jj = "";
		for (int k = 0; k < i; k++) {
			jj = jj + jjj.nextInt(9);
		}
		return jj;
	}

	/**
	 *
	 * @param date
	 */
	public static boolean RightDate(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if (date == null)
			return false;
		if (date.length() > 10) {
			sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		} else {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		try {
			sdf.parse(date);
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	/**
	 * 获取今天所在周的星期一
	 * @method name: getMondyOfToday
	 * @return type: String
	 *
	 * @return
	 */
	public static String getMondyOfToday(String format){
		Calendar c = Calendar.getInstance();
		int step = c.get(Calendar.DAY_OF_WEEK);
		//星期天
		if(step == 1)
			step = 6;
		else
			step -= 2;

		c.add(Calendar.DAY_OF_YEAR, -step);

		return toString(c.getTime(), format);
	}

	/**
	 *
	 * @method name: toString
	 * @return type: String
	 *	@param date
	 *	@param format
	 *	@return
	 */
	public static String toString(Date date, String format){
		String strTime = null;
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
			strTime = simpledateformat.format(date);
		} catch (Exception ex) {
			System.err.println("格式化日期错误 : " + ex.getMessage());
		}
		return strTime;
	}

	/**
	 * <p>以现在的时间为基准,获取前num天的日期,返回字符格式yyyy-MM-dd HH:mm:ss</p>
	 * @param num
	 * @return
	 */
	public static String getPreviousDate(int num){
		String date = null;
		try{
			Date nowTime = DateKit.getNow();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowTime);
			calendar.add(Calendar.DATE, -num); //日期向前减去num天
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = dateFormat.format( calendar.getTime() );
		}catch(Exception e){
			System.err.println("获取前" + num + "天日期异常!  " + e.getMessage());
		}
		return date;
	}

	public static boolean isValidDate(String sDate) {
		String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
		String datePattern2 = "^((\\d{2}(([02468][048])|([13579][26]))"
				+ "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
				+ "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
				+ "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		if ((sDate != null)) {
			Pattern pattern = Pattern.compile(datePattern1);
			Matcher match = pattern.matcher(sDate);
			if (match.matches()) {
				pattern = Pattern.compile(datePattern2);
				match = pattern.matcher(sDate);
				return match.matches();
			}
			else {
				return false;
			}
		}
		return false;
	}

	public static boolean isValidDateStr(String sDate) {
		if (StringUtils.length(sDate) != 8)
			return false;
		String datePattern1 = "\\d{4}\\d{2}\\d{2}";
		Pattern pattern = Pattern.compile(datePattern1);
		Matcher match = pattern.matcher(sDate);
		if (!match.matches())
			return false;

		String format = "yyyyMMdd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			dateFormat.parse(sDate);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * 获取季度
	 *
	 * @param date
	 * @return
	 */
	public static int getSeason(Date date) {
		int season = 0;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		switch (month) {
			case Calendar.JANUARY:
			case Calendar.FEBRUARY:
			case Calendar.MARCH:
				season = 1;
				break;
			case Calendar.APRIL:
			case Calendar.MAY:
			case Calendar.JUNE:
				season = 2;
				break;
			case Calendar.JULY:
			case Calendar.AUGUST:
			case Calendar.SEPTEMBER:
				season = 3;
				break;
			case Calendar.OCTOBER:
			case Calendar.NOVEMBER:
			case Calendar.DECEMBER:
				season = 4;
				break;
			default:
				break;
		}
		return season;
	}

	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		return year;
	}

	public static int getYear()
	{
		return getYear(new Date());
	}

	public static int getMonthNum()
	{
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(new Date());
		int month=calendar.get(Calendar.MONTH) + 1;
		return month;
	}

	public static String getMonth()
	{
		int month = getMonthNum();
		switch (month)
		{
			case 1:
				return "一";
			case 2:
				return "二";
			case 3:
				return "三";
			case 4:
				return "四";
			case 5:
				return "五";
			case 6:
				return "六";
			case 7:
				return "七";
			case 8:
				return "八";
			case 9:
				return "九";
			case 10:
				return "十";
			case 11:
				return "十一";
			case 12:
				return "十二";
			default:
				return "";
		}
	}

	public static String getSeason() {
		int season = getSeason(new Date());
		switch (season)
		{
			case 1:
				return "一";
			case 2:
				return "二";
			case 3:
				return "三";
			case 4:
				return "四";
			default:
				return "";
		}
	}

	public static int getSeasonNum() {
		return getSeason(new Date());
	}

	public static Date getSeasonStartDate()
	{
		Date cur = new Date();
		int year = getYear(cur);
		int s = getSeason(cur);
		switch (s)
		{
			case 1:
				return strToDateLong(String.valueOf(year) +"-01-01","yyyy-MM-dd");
			case 2:
				return strToDateLong(String.valueOf(year) +"-04-01","yyyy-MM-dd");
			case 3:
				return  strToDateLong(String.valueOf(year) +"-07-01","yyyy-MM-dd");
			case 4:
				return strToDateLong(String.valueOf(year) +"-10-01","yyyy-MM-dd");
			default:
				return cur;
		}
	}

	public static Date getSeasonEndDate()
	{
		Date cur = new Date();
		int year = getYear(cur);
		int s = getSeason(cur);
		switch (s)
		{
			case 1:
				return strToDateLong(String.valueOf(year) +"-03-31 23:59:59","yyyy-MM-dd HH:mm:ss");
			case 2:
				return strToDateLong(String.valueOf(year) +"-06-30 23:59:59","yyyy-MM-dd HH:mm:ss");
			case 3:
				return  strToDateLong(String.valueOf(year) +"-09-30 23:59:59","yyyy-MM-dd HH:mm:ss");
			case 4:
				return strToDateLong(String.valueOf(year) +"-12-31 23:59:59","yyyy-MM-dd HH:mm:ss");
			default:
				return cur;
		}
	}

	public static Date getYearStartDate()
	{
		return strToDateLong(String.valueOf(getYear(new Date())) +"-01-01","yyyy-MM-dd");
	}

	public static Date getYearEndDate()
	{
		return strToDateLong(String.valueOf(getYear(new Date())) +"-12-31 23:59:59","yyyy-MM-dd HH:mm:ss");
	}

	public static Date getMonthStartDate()
	{
		int m = getMonthNum();
		return strToDateLong(String.valueOf(getYear(new Date())) + "-" + (m < 10 ? "0"+m : m) + "-01",  "yyyy-MM-dd") ;
	}

	public static Date getMonthEndDate()
	{
		String date = getEndDateOfMonth(getCurrTime("yyyy-MM-dd"));
		return strToDateLong(date + " 23:59:59",  "yyyy-MM-dd HH:mm:ss") ;
	}

	public static Date getTodayStartDate()
	{
		String date = getCurrTime("yyyy-MM-dd");
		return strToDateLong(date + " 00:00:00",  "yyyy-MM-dd HH:mm:ss") ;
	}

	public static Date getTodayEndDate()
	{
		String date = getCurrTime("yyyy-MM-dd");
		return strToDateLong(date + " 23:59:59",  "yyyy-MM-dd HH:mm:ss") ;
	}

	public static void main(String[] args) {
		System.out.println(getTodayStartDate().getTime());
		System.out.println(getTodayEndDate().getTime());
	}

	public static long getDayTime(String day, String format){
		return strToDateLong(day, format).getTime();
	}

	public static List<Date> getWeek(Date tm, int firstday){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(tm);
		if(calendar.get(Calendar.DAY_OF_WEEK) == 1)
			calendar.add(Calendar.DATE, -1);
		List<Date> list = new ArrayList<Date>();
		Calendar cf = Calendar.getInstance();
		cf.setTime(calendar.getTime());
		cf.set(Calendar.DAY_OF_WEEK, cf.getFirstDayOfWeek());
		cf.add(Calendar.DATE, firstday);
		Calendar ce = Calendar.getInstance();
		ce.setTime(calendar.getTime());
		ce.set(Calendar.DAY_OF_WEEK, cf.getFirstDayOfWeek()+6);
		ce.add(Calendar.DATE, firstday);
		list.add(cf.getTime());
		list.add(ce.getTime());
		return list;
	}

	public static long[] getWeekStartAndEndTime()
	{
		List<Date> dateList = getWeek(new Date(),1);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateList.get(0));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date start = calendar.getTime();
		calendar.setTime(dateList.get(1));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date end = calendar.getTime();
		return new long[]{start.getTime(),end.getTime()};
	}

	public static List<Date> getBetweenDates(Date start, Date end)
	{
		List<Date> result = new ArrayList<Date>();
		Calendar tempStart = Calendar.getInstance();
		tempStart.setTime(start);
		tempStart.add(Calendar.DAY_OF_YEAR, 1);

		Calendar tempEnd = Calendar.getInstance();
		tempEnd.setTime(end);
		while (tempStart.before(tempEnd))
		{
			result.add(tempStart.getTime());
			tempStart.add(Calendar.DAY_OF_YEAR, 1);
		}
		return result;
	}

	public static List<Date> getBetweenDates(long start, long end)
	{
		return getBetweenDates(new Date(start),new Date(end));
	}

	public static long getPreviousDay(int num){
		Calendar start = Calendar.getInstance();
		//从0点开始计算
		start.add(Calendar.DATE, -num);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		return start.getTimeInMillis();
	}

	/**
	 * 获取当年的第一天
	 * @return
	 */
	public static Date getCurrYearFirst(){
		Calendar currCal=Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearFirst(currentYear);
	}

	/**
	 * 获取当年的最后一天
	 * @return
	 */
	public static Date getCurrYearLast(){
		Calendar currCal=Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearLast(currentYear);
	}

	/**
	 * 获取某年第一天日期
	 * @param year 年份
	 * @return Date
	 */
	public static Date getYearFirst(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * @param year 年份
	 * @return Date
	 */
	public static Date getYearLast(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();

		return currYearLast;
	}

	/**
	 * 获取当年某月第一天的时间
	 * @param month 月份
	 * @return 时间戳
	 */
	public static long getMonthFirst(int month){
		Calendar firstDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String date = getYear() + "-"  + month + "-" + "01";
			firstDate.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 设置为这个月的第 1 天
		firstDate.set(Calendar.DATE, 1);
		return firstDate.getTimeInMillis();
	}

	/**
	 * 获取当年某月第一天的时间
	 * @param month 月份
	 * @return 时间戳
	 */
	public static long getMonthLast(int month){
		Calendar lastDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String date = getYear() + "-"  + month + "-" + "01";
			lastDate.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 设置为这个月的第 1 天
		lastDate.set(Calendar.DATE, 1);

		lastDate.add(Calendar.MONTH, 1);
		return lastDate.getTimeInMillis() - 1;
	}

	public static String getDateString(long time) {
		SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd");
		return format.format(time);
	}
}