package org.little.stream.mngr;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
        
public class ufpsTime {

       private static     SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
       private  long      time;
       private  String    text;  
       private  Timestamp timestamp;
       public   final static ufpsTime  empty=new ufpsTime(0);
       public ufpsTime() {
              time     =System.currentTimeMillis();
              text     =null;
              timestamp=null;
       }
       public ufpsTime(long _time) {
           time     =_time;
           text     =null;
           timestamp=null;
       }
       public ufpsTime(Timestamp _time) {
              setTimestamp(_time);
       }
       public ufpsTime(String _time) {
              setText(_time);
       }
       public long getTime() {return time;}
       public String getText() {
                 if(time==0)return "-";
              if(text==null){
                 try{
                     text=format.format(new Date(time));
                 } catch (Exception e) { return null; }
              }
              return text;
       }
       public Timestamp getTimestamp() {
              if(timestamp==null){
                    timestamp=new Timestamp(time);
              }
              return timestamp;
       }

       public void setTime(long _time) {
              time      =_time;
              text      =null;
              timestamp =null;
       }
       public void setText(String _text) {
              this.timestamp =null;
              this.text      =null;
              this.time      =0;
              if("-".contentEquals(_text))return;
              try{
                  time=format.parse(_text).getTime();
              } catch (Exception e) { time=0;return; }
              this.text = _text;
       }
       public void setTimestamp(Timestamp timestamp) {
              this.timestamp = timestamp;
              time=timestamp.getTime();
              text=null;
       }

       public static String getText(long _time) {
              try{
                  return format.format(new Date(_time));
              } catch (Exception e) { return null; }
              //return null;
       }



}


