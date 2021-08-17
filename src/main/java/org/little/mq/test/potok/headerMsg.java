package org.little.mq.test.potok;

import java.sql.Timestamp;

//import org.little.util.Logger;
import org.little.util.db.dbExcept;
import org.little.util.db.query;

public class headerMsg {


       private String     id;        
       private String     msg_id;    
       private Timestamp  dt;        
       private int        size;       
       private String     to;        
       private String     from;      
       private String     processor; 
       private String     to_url;    
       private String     app_id;    
       private String     corr_id;   
       private String     file_id;   
       private int        doc_format;
       private String     doc_type;
       private long       ed_no;     
       private String     ed_dt;     
       private long       ed_autor;  
       private String     doc_id;    
       private String     user;
       private int        priority;
       private String     qm;        
       //                                                   
       private final static String query_s1="INSERT INTO SBP ";
       //                                                   1  2      3  4        5  6    7         8      9      10      11      12         13       14    15    16        17     18       19       20         1 2 3 4 5 6 7 8 9 1011121314151617181920
       private final static String query_s2="(ID,MSG_ID,DT,MSG_SIZE,TO,FROM,PROCESSOR,TO_URL,APP_ID,CORR_ID,FILE_ID,DOC_FORMAT,DOC_TYPE,ED_NO,ED_DT,ED_AUTHOR,DOC_ID,USERNAME,PRIORITY,QM) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ";


       public headerMsg() {
              clear();
       }
       public void clear() {
              id        =null;      
              msg_id    =null;  
              dt        =null;      
              size      =0;     
              to        =null;      
              from      =null;    
              processor =null;
              to_url    =null;  
              app_id    =null;  
              corr_id   =null; 
              file_id   =null; 
              doc_format=0;
              doc_type  =null;
              ed_no     =0;   
              ed_dt     =null;   
              ed_autor  =0;
              doc_id    =null;  
              user      =null;    
              priority  =0;
              qm        =null;      
       }

       public void setID       (String    n){id         =n;}      
       public void setMsgID    (String    n){msg_id     =n;}  
       public void setDT       (Timestamp n){dt         =n;}      
       public void setSize     (int       n){size       =n;}     
       public void setTo       (String    n){to         =n;}      
       public void setFrom     (String    n){from       =n;}    
       public void setProcessor(String    n){processor  =n;}
       public void setToUrl    (String    n){to_url     =n;}  
       public void setAppID    (String    n){app_id     =n;}  
       public void setCorrID   (String    n){corr_id    =n;} 
       public void setFileID   (String    n){file_id    =n;} 
       public void setFocFormat(int       n){doc_format =0;}
       public void setDocType  (String    n){doc_type   =n;}
       public void setFocEDNO  (long      n){ed_no      =n;}   
       public void setEDDT     (String    n){ed_dt      =n;}   
       public void setEDAUTOR  (long      n){ed_autor   =n;}
       public void setDocID    (String    n){doc_id     =n;}  
       public void setUser     (String    n){user       =n;}    
       public void setPriority (int       n){priority   =n;}
       public void setQM       (String    n){qm         =n;}      

       public String    getID       (){return id         ;}      
       public String    getMsgID    (){return msg_id     ;}  
       public Timestamp getDT       (){return dt         ;}      
       public int       getSize     (){return size       ;}     
       public String    getTo       (){return to         ;}      
       public String    getFrom     (){return from       ;}    
       public String    getProcessor(){return processor  ;}
       public String    getToUrl    (){return to_url     ;}  
       public String    getAppID    (){return app_id     ;}  
       public String    getCorrID   (){return corr_id    ;} 
       public String    getFileID   (){return file_id    ;} 
       public int       getFocFormat(){return doc_format ;}
       public String    getDocType  (){return doc_type   ;}
       public long      getFocEDNO  (){return ed_no      ;}   
       public String    getEDDT     (){return ed_dt      ;}   
       public long      getEDAUTOR  (){return ed_autor   ;}
       public String    getDocID    (){return doc_id     ;}  
       public String    getUser     (){return user       ;}    
       public int       getPriority (){return priority   ;}
       public String    getQM       (){return qm         ;}      


       public void initQ(query q) throws dbExcept {
              q.creatPreSt(query_s1+query_s2);
       }
       public void closeQ(query q) throws dbExcept {
              q.closePreSt();
       }
       public void execQ(query q) throws dbExcept {
              q.executeUpdate();
       }
                                

       public void setQ(query q) throws dbExcept {

              q.setString   (1, getID       () );                           //"ID"         
              q.setString   (2, getMsgID    () );                           //"MSG_ID"     
              q.setTimestamp(3, getDT       () );                           //"DT"         
              q.setInt      (4, getSize     () );                           //"MSG_SIZE"   
              q.setString   (5, getTo       () );                           //"TO"         
              q.setString   (6, getFrom     () );                           //"FROM"       
              q.setString   (7, getProcessor() );                           //"PROCESSOR"  
              q.setString   (8, getToUrl    () );                           //"TO_URL"     
              q.setString   (9, getAppID    () );                           //"APP_ID"     
              q.setString   (10,getCorrID   () );                           //"CORR_ID"    
              q.setString   (11,getFileID   () );                           //"FILE_ID"    
              q.setInt      (12,getFocFormat() );                           //"DOC_FORMAT" 
              q.setString   (13,getDocType  () );                           //"DOC_TYPE"   
              q.setLong     (14,getFocEDNO  () );                           //"ED_NO"      
              q.setString   (15,getEDDT     () );                           //"ED_DT"      
              q.setLong     (16,getEDAUTOR  () );                           //"ED_AUTHOR"  
              q.setString   (17,getDocID    () );                           //"DOC_ID"     
              q.setString   (18,getUser     () );                           //"USERNAME"   
              q.setInt      (19,getPriority () );                           //"PRIORITY"   
              q.setString   (20,getQM       () );                           //"QM"         

      }

       public void initMsg(){
              String t_id=""+( new java.util.Date().getTime())+""+java.lang.System.currentTimeMillis()+""+java.lang.System.nanoTime();
              initMsg(t_id);
       }
       public void initMsg(String t_id){
              String id="ID="+t_id;
              String msg_id="MSGID="+t_id;
              String corr_id="CORRID="+t_id;
              long _dt=System.currentTimeMillis();
              Timestamp dt=new Timestamp(_dt);

              setID       (id     );                                  //"ID"         
              setMsgID    (msg_id );                                  //"MSG_ID"     
              setDT       (dt);                                       //"DT"         
              setSize     (4096);                                     //"MSG_SIZE"   
              setTo       ("TO");                                     //"TO"         
              setFrom     ("FROM");                                   //"FROM"       
              setProcessor("PROCESSOR");                              //"PROCESSOR"  
              setToUrl    ("TO_URL");                                 //"TO_URL"     
              setAppID    ("APP"+msg_id);                             //"APP_ID"     
              setCorrID   ( corr_id);                                 //"CORR_ID"    
              setFileID   ( "FILE_ID");                               //"FILE_ID"    
              setFocFormat( 9);                                       //"DOC_FORMAT" 
              setDocType  ( "ED000");                                 //"DOC_TYPE"   
              setFocEDNO  ( 123456);                                  //"ED_NO"      
              setEDDT     ( "0000000000");                            //"ED_DT"      
              setEDAUTOR  ( 12);                                      //"ED_AUTHOR"  
              setDocID    ( "123456789");                             //"DOC_ID"     
              setUser     ( "util");                                  //"USERNAME"
              setPriority ( 1);                                       //"PRIORITY"   
              setQM       ( "QM_MANAMGER");                           //"QM"         

      }
      public String toString(){
          return getID       ()+" "+
                 getMsgID    ()+" "+
                 getDT       ()+" "+
                 getSize     ()+" "+
                 getTo       ()+" "+
                 getFrom     ()+" "+
                 getProcessor()+" "+
                 getToUrl    ()+" "+
                 getAppID    ()+" "+
                 getCorrID   ()+" "+
                 getFileID   ()+" "+
                 getFocFormat()+" "+
                 getDocType  ()+" "+
                 getFocEDNO  ()+" "+
                 getEDDT     ()+" "+
                 getEDAUTOR  ()+" "+
                 getDocID    ()+" "+
                 getUser     ()+" "+
                 getPriority ()+" "+
                 getQM       () ;
      }
}

/*
CREATE TABLE SBP (
		"ID"        VARCHAR(64 ) NOT NULL PRIMARY KEY,  
		"MSG_ID"    VARCHAR(128 ) NOT NULL, 
		"DT"        TIMESTAMP NOT NULL, 
		"MSG_SIZE"  INTEGER, 
		"TO"        VARCHAR(20 ), 
		"FROM"      VARCHAR(20 ) NOT NULL, 
		"PROCESSOR" VARCHAR(50 ), 
		"TO_URL"    VARCHAR(50 ), 
		"APP_ID"    VARCHAR(128 ), 
		"CORR_ID"   VARCHAR(128 ), 
		"FILE_ID"   VARCHAR(128 ), 
		"DOC_FORMAT" INTEGER, 
		"DOC_TYPE"   VARCHAR(128 ), 
		"ED_NO"      INTEGER, 
		"ED_DT"      VARCHAR(12 ), 
		"ED_AUTHOR"  BIGINT, 
		"DOC_ID"     VARCHAR(128 ), 
		"USERNAME"   VARCHAR(20 ), 
		"PRIORITY"   INTEGER, 
		"QM"         VARCHAR(32 )

);
CREATE TABLE SBP_REPORTS (
		"ID" VARCHAR(64 ) NOT NULL, 
		"DT" TIMESTAMP NOT NULL, 
		"SOURCE" VARCHAR(128 ) NOT NULL, 
		"QM" VARCHAR(32 )
);
truncate table SBP_REPORTS  IMMEDIATE;
truncate table SBP IMMEDIATE ;

*/