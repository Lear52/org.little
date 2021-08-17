package org.little.mq.stream.ufps;

import java.io.FileInputStream;
import java.util.ArrayList;

public class ufps_sl {

       private ArrayList<String> to;
       private String from        ;
       private String type        ;
       private String priority    ;
       private String id          ;
       private String creat_time  ;
       private String app_id      ;
       private String cor_id      ;
       private String send_time   ;
       private String receive_time;
       private String accept_time ;
       private String ack_request ;
     

       public ufps_sl() {
    	      clear();
    	    
       }
       public void clear() {
              to          =new ArrayList<String>(4);  
              from        ="";
              type        ="";
              priority    ="";
              id          ="";
              creat_time  ="";
              app_id      ="";
              cor_id      ="";
              send_time   ="";
              receive_time="";
              accept_time ="";
              ack_request ="";
       }

       public void   addTO        (String arg){to.add(arg);     }       
       public void   setFROM      (String arg){from        =arg;}     
       public void   setType      (String arg){type        =arg;}      
       public void   setPriority  (String arg){priority    =arg;}  
       public void   setID        (String arg){id          =arg;}        
       public void   setCreateTime(String arg){creat_time  =arg;}    
       public void   setAppID     (String arg){app_id      =arg;}     
       public void   setCorID     (String arg){cor_id      =arg;}     
       public void   setSendTime  (String arg){send_time   =arg;}  
       public void   setReceveTime(String arg){receive_time=arg;}
       public void   setAcceptTime(String arg){accept_time =arg;}
       public void   setAckRequest(String arg){ack_request =arg;}
       public ArrayList<String>   getTO()     {return to;}
       public String getFROM      (){return from        ;}     
       public String getType      (){return type        ;}      
       public String getPriority  (){return priority    ;}  
       public String getID        (){return id          ;}        
       public String getCreateTime(){return creat_time  ;}    
       public String getAppID     (){return app_id      ;}     
       public String getCorID     (){return cor_id      ;}     
       public String getSendTime  (){return send_time   ;}  
       public String getReceveTime(){return receive_time;}
       public String getAcceptTime(){return accept_time ;}
       public String getAckRequest(){return ack_request ;}


       public String toString(){
                               String _to="";
                               for(int i=0;i<to.size();i++) _to+=" to:"+to.get(i);
                               return 
                               _to
                               +" from:"+           from        
                               +" type:"+           type        
                               +" priority:"+       priority    
                               +" id:"+             id          
                               +" creat_time:"+     creat_time  
                               +" app_id:"+         app_id      
                               +" cor_id:"+         cor_id      
                               +" send_time:"+      send_time   
                               +" receive_time:"+   receive_time
                               +" accept_time:"+    accept_time 
                               +" ack_request:"+    ack_request 
                               ;
       }

       public static void main(String[] args) throws Exception {

              FileInputStream fis = new FileInputStream(args[0]);
              byte[] data = new byte[fis.available()];
              fis.read(data);
              fis.close();

      }
}



/*
<?xml version="1.0" encoding="WINDOWS-1251"?>
<env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
  <env:Header>
      <props:MessageInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
           <props:To>uic:040700199900</props:To>
           <props:From>uic:049999900000</props:From>
           <props:MessageID>450295413-0000004099013</props:MessageID>
           <props:MessageType>1</props:MessageType>
           <props:Priority>5</props:Priority>
           <props:CreateTime>2015-12-16T19:50:08Z</props:CreateTime>
      </props:MessageInfo>
      <props:DocInfo xmlns:props="urn:cbr-ru:msg:props:v1.3">
           <props:DocFormat>2</props:DocFormat>
           <props:DocType>MT865</props:DocType>
           <props:DocID>450295413-0000004099013</props:DocID>
      </props:DocInfo>
   </env:Header>
   <env:Body>
   <Object xmlns="urn:cbr-ru:dc:v1.0">ezE6DQo6VFlQRTo4NjUNCjpUTzo5OTk5OTk5OTkwMDANCjpGUk9NOjA0MDQ5OTk5OTAxMw0KOklEOjA0MDQ5OTk5OTAxMzIwMTUxMjE2NDUwMjk1NDEzLTAwMDAwMDQwOTkwMTMvMDAwMDAwMDAwMQ0KOlJFRklEOjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwLzAwMDAwMDAwMDB9DQp7NDoNCjozMDA6OTYwMDAxDQo6MzQ0OjIxj66r4+eo4uwgpKCtreulfXs1OntGQUM6MzA4MjAxNjQwNjA5MkE4NjQ4ODZGNzBEMDEwNzAyQTA4MjAxNTUzMDgyMDE1MTAyMDEwMTMxMEYzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDAzMDBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMxODIwMTJDMzA4MjAxMjgwMjAxMDEzMDU4MzA0NDMxMEIzMDA5MDYwMzU1MDQwNjEzMDI1MjU1MzEwQjMwMDkwNjAzNTUwNDA4MTMwMjMwMzQzMTBDMzAwQTA2MDM1NTA0MEExMzAzNDM0MjUyMzEwRDMwMEIwNjAzNTUwNDBCMTMwNDU1NDI1QTQ5MzEwQjMwMDkwNjAzNTUwNDAzMTMwMjQzNDEwMjEwNDAzNjEwQjc3RDBCRDVBRUI3NDI3N0QxNTUyMUYxRTAzMDBEMDYwOTJCMDYwMTA0MDE5QzU2MDEwMTA1MDBBMDY5MzAxODA2MDkyQTg2NDg4NkY3MEQwMTA5MDMzMTBCMDYwOTJBODY0ODg2RjcwRDAxMDcwMTMwMUMwNjA5MkE4NjQ4ODZGNzBEMDEwOTA1MzEwRjE3MEQzMTM1MzEzMjMxMzYzMTM5MzUzMDMxMzM1QTMwMkYwNjA5MkE4NjQ4ODZGNzBEMDEwOTA0MzEyMjA0MjAzREFBNzk0OUM2MzE0NkNGRThCQkI3MEM2NUQ5RDQ5NkUxODlFNTgyNTk1Q0ZEMzUyNjkxRDAzQjBCQUQzQTQwMzAwRDA2MDkyQjA2MDEwNDAxOUM1NjAxMDIwNTAwMDQ0MDkyOEUwRDlEOTUyOEE2Nzc2RUVGRkU5NjcwNzlFNEE2ODI2OTQ0MDcyOTFGNkMwMzRDMTU5MjVGQjAwRTUwNzM3MDQyQjVBRkE0NDYxRDJENEZDQzg0NkQzQjRBM0E4NkVGQUE0ODhDODgxRjdFOEJEODVCMEJGMEY1QjlCQUU0fX0=</Object>
   </env:Body>

</env:Envelope>

*/