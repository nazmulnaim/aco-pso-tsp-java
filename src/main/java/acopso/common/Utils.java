package acopso.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class Utils {
  static InputStream propIS = null;
  static Properties props = null;

  public static String getReportConfigurationValue(String key, String defaultValue) {
      try {
          if (props == null) {
              initializePropertyFile("config.properties");
          }
          return props.getProperty(key, defaultValue);
      } catch (FileNotFoundException ex) {
          System.out.println("Property file not found");
      } catch (IOException ex) {
          System.out.println("Property file can not be loaded");
      } catch (Exception ex) {
          System.out.println(ex.getMessage());
      }
      return defaultValue;
  }

  public static String getReportConfigurationValue(String key) {
      return getReportConfigurationValue(key, "");
  }

  private static void initializePropertyFile(String propertyFile) throws Exception {
      propIS = Utils.class.getClassLoader().getResourceAsStream(propertyFile);
      props = new Properties();
      props.load(propIS);
  }

  /**
   * Read from a file and load it to a String.
   * @param fileName  the name of the file to read
   * @return          a String with the contents of the file
   */
  public static String read(String fileName) {
    Scanner scanner = null;
    try{
      InputStream stream = Utils.class.getClassLoader().getResourceAsStream(fileName);
      scanner = new Scanner(stream).useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }finally{
      if(scanner!=null){
        scanner.close();
      }
    }
  }

  /**
   * Removes duplicate what spaces in a String.
   * Example: "   2  3  3,2   " becomes " 2 3 3,2 "
   * @param s     the String to parse
   * @return      the String minus the duplicate white spaces
   */
  public static String removeWhiteSpace (String s) {
      for (int i = 1; i < s.length(); i++) {
          if (s.charAt(i) == ' ' && s.charAt(i-1) == ' ') {
              if (i != s.length()) {
                  s = s.substring(0, i) + s.substring(i+1, s.length());
                  i--;
              } else {
                  s = s.substring(0, i);
                  i--;
              }
          }
      }
      return s;
  }
}
