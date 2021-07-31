package org.little.mq.file.util;


import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class localLog{
       private static final Logger log = LoggerFactory.getLogger(localLog.class);
       public  String getClassName(){return localLog.class.getName();}

       private FileWriter file;
       private String     filename;


       public localLog(String _filename) {
              open(_filename);
       }
       public localLog() {
              clear();
       }
       public void clear() {
              file=null;
              filename=null;
       }

       public void print(String msg) {
              if(file==null)return;
              try{
                  SimpleDateFormat sfd  =new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_S");
                  String           msg_t=sfd.format(new java.util.Date());
                  file.write(java.lang.System.currentTimeMillis()+" "+msg_t+" "+msg+"\n");
                  file.flush();
              } 
              catch (IOException e) {}
       
       }
       public boolean open(String _filename) {
              try{
                  filename=_filename;
                  file=new FileWriter(filename,true);
              } 
              catch (IOException e) {
                 log.error("open file:"+filename+" ex:"+e);clear();
                 return false;
              }

              log.info("open file:"+filename+" ret:ok");
              return true;
       }
       public void close() {
              if(file==null)return;
              try{file.close();} catch (IOException e) {}
              clear();
              return ;
       }
 
}
