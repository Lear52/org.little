package org.little.mq.file.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class sendFile2MQ{
       private static final Logger log = LoggerFactory.getLogger(sendFile2MQ.class);
       public  static String getClassName(){return sendFile2MQ.class.getName();}

       private connect2MQ  connect_mq;
       //private boolean     is_read_file;
       //private boolean     is_send_msg;
       //private int         size_msg;
       public sendFile2MQ() {
              clear();
       }
       public void clear() {
              connect_mq = new connect2MQ();
              //if(System.getProperty("prj0.stream.file.is_read","true").equals("true"))
              //       is_read_file=true;
              //else is_read_file=false;
              //if(System.getProperty("prj0.stream.file.is_send","true").equals("true"))
              //       is_send_msg=true; 
              //else is_send_msg=false;
              //String s=System.getProperty("prj0.stream.file.msg_size");
              //size_msg=0;
              /*
              if(s!=null){
                 try { size_msg=Integer.parseInt(s, 10);
                       log.trace("set prj0.stream.file.msg_size:"+size_msg);
                 } 
                 catch (Exception e) {
                       log.error("error parse prj0.stream.file.msg_size:"+s);
                       size_msg=0;
                 }
              }
              else log.trace("set prj0.stream.file.msg_size:0");
              */
       }
       public void setPersistence(boolean _isPersistence){connect_mq.setPersistence(_isPersistence);}
       public void setUser(String u,String p){ connect_mq.setUser(u,p);}
       public void close(){connect_mq.close(); connect_mq=null;}

       //private byte [] getBuffer() {             return null;      }


       private void run(String filename) {
               run(Paths.get(filename));
       }

       public void run(Path _file) {

              InputStream in=null;
              int         f_size=0;
              byte []     buf=null;

              //if(is_read_file){
              try{
                  f_size=(int)Files.size(_file);
                  in = Files.newInputStream(_file);
              }
              catch (Exception ex1){
                  log.error("open file:"+_file +" ex:"+Except.printException(ex1));
                  return;
              }
              buf=new byte[f_size];
              try{
                  in.read(buf, 0, buf.length);
              }
              catch (Exception ex1){
                  log.error("read file:"+_file +" ex:"+Except.printException(ex1));
                  return;
              }
              //}
              //else buf=getBuffer();

              //if(is_send_msg)
            	  connect_mq.put(buf);

              //if(is_read_file){
              try{
                  in.close();
              }
              catch (Exception ex1){
                  log.error("close file:"+_file+" ex:"+Except.printException(ex1));
              }
              //}
              //-------------------------------------------------------------
              _file=null;
       }
       public int init(String _connect_qm_name,String _jms) {
              return connect_mq.init(_connect_qm_name,_jms);
       }
       public int init(String _connect_qm_name,String _send_q_name,String _send_qm_name) {
              return connect_mq.init(_connect_qm_name,_send_q_name,_send_qm_name);
       }
       public int init(String _connect_qm_name,String _host,int _port,String _channel,String _send_q_name,String _send_qm_name) {
              return connect_mq.init(_connect_qm_name,_host,_port,_channel,_send_q_name,_send_qm_name);
       }


       private static void work(String _connect_qm_name,String _send_q_name,String host,int port,String channel,String _send_qm_name,String filename){

              sendFile2MQ r=new sendFile2MQ();

              if(_connect_qm_name!=null || channel==null){
                 if(r.connect_mq.init(_connect_qm_name,_send_q_name,_send_qm_name)<0)return;
              }
              else{
                 if(r.connect_mq.init(_connect_qm_name,host,port,channel,_send_q_name,_send_qm_name)<0)return;
              }
              r.run(filename);
              r.connect_mq.close();
       }

       public static void help(String[] args) {
              System.out.println("run java "+getClassName()+" manager_name queue_name||queue_name@manager_name [hostname port channel] filename");
       }
       public static void main(String[] args) {


              if((args.length<3) ||(args.length>=3 && args.length<6)){
                 help(args);
                 return;
              }
              String connect_qmanager_name=args[0];
              String send_qm_name=null;
              String send_q_name =args[1];
              String hostname    =null;
              String portname    =null;
              String channel     =null;
              String filename    =args[2];
              int    port=0;

              send_qm_name=connect_qmanager_name;
              StringTokenizer parser_q;
              parser_q = new StringTokenizer(send_q_name, "@");
              if(parser_q.hasMoreTokens()) {
                 send_q_name=parser_q.nextToken();
                 if(parser_q.hasMoreTokens()) {
                    send_qm_name=parser_q.nextToken();
                 }
              }

              if(args.length>=6){
                 hostname    =args[2];
                 portname    =args[3];
                 channel     =args[4];
                 filename    =args[5];
              }
              if(portname!=null) try { port=Integer.parseInt(portname, 10);} catch (Exception e) {port=0;hostname=null;channel=null;}

              sendFile2MQ.work(connect_qmanager_name,send_q_name,hostname,port,channel,send_qm_name,filename);

       }

}
