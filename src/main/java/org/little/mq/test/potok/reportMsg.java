package org.little.mq.test.potok;

import java.sql.Timestamp;

//import org.little.util.Logger;
import org.little.util.db.dbExcept;
import org.little.util.db.query;

public class reportMsg {
       final private static String CLASS_NAME="prj0.potok.reportMsg";
       final private static int    CLASS_ID  =2404;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}

       private String     id;        
       private String     source;    
       private Timestamp  dt;        
       private String     qm;        
       private final static String query_r="INSERT INTO SBP_REPORTS(ID,DT,SOURCE,QM) VALUES(?,?,?,?) ";


       public reportMsg() {
              clear();
       }
       public void clear() {
              id        =null;      
              dt        =null;      
              source    =null;
              qm        =null;      
       }

       public void setID       (String    n){id         =n;}      
       public void setDT       (Timestamp n){dt         =n;}      
       public void setSource   (String    n){source     =n;}
       public void setQM       (String    n){qm         =n;}      

       public String    getID       (){return id         ;}      
       public Timestamp getDT       (){return dt         ;}      
       public String    getSource   (){return source     ;}
       public String    getQM       (){return qm         ;}      


       public void initQ(query q) throws dbExcept {
              q.creatPreSt(query_r);
       }
       public void closeQ(query q) throws dbExcept {
              q.closePreSt();
       }
       public void setQ(query q) throws dbExcept {

              q.setString   (1, getID       () );                             
              q.setTimestamp(2, getDT       () );                                  
              q.setString   (3, getSource   () );                             
              q.setString   (4, getQM       () );                          

       }
       public void initMsg(){
              //String t_id=""+( new java.util.Date())+java.lang.System.currentTimeMillis()+""+java.lang.System.nanoTime();
              String t_id=""+( new java.util.Date().getTime())+java.lang.System.currentTimeMillis()+""+java.lang.System.nanoTime();
              initMsg(t_id);
       }
       public void initMsg(String t_id){
                   initMsg(t_id,"test");
       }
       public void initMsg(String t_id,String _source){
              String id="ID="+t_id;
              //String msg_id="MSGID="+t_id;
              //String corr_id="CORRID="+t_id;
              long _dt=System.currentTimeMillis();
              Timestamp dt=new Timestamp(_dt);

              setID       (id     );                                  
              setSource   (_source);                                   
              setDT       (dt);                                       
              setQM       ( "QM_MANAMGER");                           

      }

      public String toString(){
             return getID       ()+" "+
                    getDT       ()+" "+
                    getSource   ()+" "+
                    getQM       ();
     }

}

