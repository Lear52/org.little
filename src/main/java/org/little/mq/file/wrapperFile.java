package org.little.mq.file;

import java.util.StringTokenizer;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.iWrapper;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


public class wrapperFile implements WrapperListener{
       private static final Logger logger = LoggerFactory.getLogger(wrapperFile.class);

       private sendDir2MQ     serverFile;
       protected  wrapperFile(String args[]){
                  logger.trace("create FILE2MQ");
                  WrapperManager.start(this, args);
       }

       public Integer start(String args[]){ 
              String con_manager_name =args[0];
              String send_qm_name     =args[0];
              String send_q_name      =args[1];
              String hostname         =args[2];
              String portname         =args[3];
              String channel          =args[4];
              String global_dirname   =args[5];
              String local_dirname    ="";
              String _range_time      =args[6];
              long   range_time       =0;
              int    port             =0;
              String _user            =args[7]; 
              String _passwd          =args[8];   
              
              StringTokenizer parser_q;
              parser_q = new StringTokenizer(send_q_name, "@");
              
              if(parser_q.hasMoreTokens()) {
                 send_q_name=parser_q.nextToken();
                 if(parser_q.hasMoreTokens()) {
                    con_manager_name=parser_q.nextToken();
                 }
              }
              if(portname   !=null) try { port      =Integer.parseInt(portname   , 10);} catch (Exception e) {port=0;hostname=null;channel=null;}
              if(_range_time!=null) try { range_time=Long.parseLong  (_range_time, 10);} catch (Exception e) {range_time=0;}
              range_time*=1000;

              serverFile=new sendDir2MQ(con_manager_name,send_q_name,hostname,port,channel,send_qm_name,global_dirname,local_dirname,range_time,"");
              serverFile.setUser(_user,_passwd);
              serverFile.setArhDir("");
              serverFile.fork();

              return null; 
       } 
       public int stop(int exitCode){
              serverFile.isRun(false);
              return 0;
       }
 
       public void controlEvent(int event){
         
              logger.trace("Event CSKI:"+event);

              if(event == 202 && WrapperManager.isLaunchedAsService()){
                 if(WrapperManager.isDebugEnabled())logger.trace("wrapper FILE2MQ: controlEvent(" + event + ") Ignored");
              } 
              else {
                 if(WrapperManager.isDebugEnabled())logger.trace("wrapper FILE2MQ: controlEvent(" + event + ") Stopping");
                 WrapperManager.stop(0);
              }  
       }    

       public static void main(String args[]){
              logger.trace("start FILE2MQ");
              String xpath=iWrapper.getFileanme(args);
              if(xpath==null)return;
              new wrapperFile(args);
       }

}
