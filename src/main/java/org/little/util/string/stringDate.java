package org.little.util.string;

import java.text.SimpleDateFormat;
import java.util.Date;

public class stringDate{

    public static Date str2date(String s){
           if(s==null) return null;
           try{
               SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
               return new Date(sfd.parse(s).getTime());
           } catch (Exception e) { return null; }
        
    }
    public static String date2str(Date d){
           if(d==null) return null;
           try{
               SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
               return sfd.format(d);
           } catch (Exception e) { return null; }
    }
    public static String date2filename(Date d){
           if(d==null) return null;
           try{
               SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
               return sfd.format(d);
           } catch (Exception e) { return null; }
    }
    public static String date2Z(Date d){
           if(d==null) return null;
           try{
               SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
               return sfd.format(d);
           } catch (Exception e) { return null; }
    }
    public static String date2prn(Date d){
           if(d==null) return "";
           try{
               SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               return sfd.format(d);
           } catch (Exception e) { return null; }
    }

}
