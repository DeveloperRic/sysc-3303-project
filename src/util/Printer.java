package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Printer {

	public static void print(String s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(boolean s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(int s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(float s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(double s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(long s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(Object s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(char s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ s);
	}
	
	public static void print(char[] s) {
		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);
		System.out.println(dateFormatted +": "+ new String());
	}
	
	public static void print() {
		System.out.println();
	}
}
