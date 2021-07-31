package org.little.mq.file.util;
       
import java.util.StringTokenizer;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.mq.stream.def;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import com.ibm.mq.MQException;

public class connect2MQ{
       private static final Logger log = LoggerFactory.getLogger(connect2MQ.class);
       public  static String getClassName(){return connect2MQ.class.getName();}

       private mq_mngr_jms  qm;
       private mq_queue     q;
       private String       connect_qm_name;
       private String       host;
       private int          port;
       private String       channel;
       private String       send_q_name;
       private String       send_qm_name;
       private String       user;
       private String       password;
       private int          report;
       private String       report_q;
       private String       report_qm;
       private boolean      isPersistence;

       public connect2MQ() {
              clear();
              user           =null;
              password       =null;
       }
       public void clear() {
              qm             =null;   
              q              =null;
              MQException.log=null;

              connect_qm_name=null;
              host           =null;
              port           =0;
              channel        =null;
              send_q_name    =null;
              send_qm_name   =null;
              report         =0;
              report_q       =null;
              report_qm      =null;
              isPersistence  =false;
        
       }
       public void setPersistence(boolean _isPersistence){isPersistence=_isPersistence;}
       //public void commit()  {qm.commit();}
       //public void backout() {qm.backout();}
       public byte [] get() {
              int ret;
              byte [] buf=null;
              mq_msg m=new mq_msg();
              try {
                  ret = q.get(m);
                  if(ret==def.RET_OK){
                     int len=m.getLen();
                     buf = new byte[len];
                     m.get(buf);
                     log.trace("get q:"+q.getQName()+" qm:"+q.getQMName()+" size:"+len);
                  }
                  qm.commit();
              }
              catch (mqExcept ex1) {
                log.error("run:error q:"+q.getQName()+" qm:"+q.getQMName()+" ex:"+ex1);
                return null;
              }

              return buf;
       }
       public void put(byte [] buf) {
              int ret;
              mq_msg m=new mq_msg();
              //-------------------------------------------------------------
              try {
                  //m.setMaxLen(10000);
                  if(report   !=0){
                     m.setReport(report);
                  }
                  if(report_q !=null){
                     m.setReportQ(report_q);
                  }
                  if(report_qm!=null){
                     m.setReportQM(report_qm);
                  }
                  m.setPersistence(isPersistence);/**/
                  m.put(buf);
                  ret = q.put(m);
                  if(ret==def.RET_OK){
                       //log.trace("run:put q:"+q.getQName()+" qm:"+q.getQMName()+" file:"+_file);
                  }
                  qm.commit();
              } 
              catch (mqExcept ex1) {
                log.error("run:error q:"+q.getQName()+" qm:"+q.getQMName()+" ex:"+ex1);
              }
       }

// #define MQRO_COA                       0x00000100
// #define MQRO_COA_WITH_DATA             0x00000300
// #define MQRO_COA_WITH_FULL_DATA        0x00000700
// #define MQRO_COD                       0x00000800
// #define MQRO_COD_WITH_DATA             0x00001800
// #define MQRO_COD_WITH_FULL_DATA        0x00003800



       // JmsChannel:SYSTEM.ADMIN.SVRCONN/Port:1414/Host:10.70.112.86/Queue:UTP@QM_cc/User:av/Passwd:av123/Report:1792!UTP@QM_cc/Persistence:true
       public int init(String _connect_qm_name,String _jms) {
              StringTokenizer          parser_str;
              String _port             =null;
              String _report           =null;
        
              connect_qm_name=_connect_qm_name;
        
              parser_str = new StringTokenizer(_jms, "/");


              while(parser_str.hasMoreTokens()){
                    String field=parser_str.nextToken();          

                    StringTokenizer parser_field = new StringTokenizer(field, ":");

                    if(parser_field.hasMoreTokens()==false)continue;

                    field=parser_field.nextToken();

                    if("JmsChannel".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          channel=parser_field.nextToken();
                          log.trace("channel:"+channel);
                       }
                       else{
                           log.error("error parse channel:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("QM".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          _connect_qm_name=parser_field.nextToken();
                          log.trace("qm:"+_connect_qm_name);
                          connect_qm_name=_connect_qm_name;
                       }
                       else{
                           log.error("error parse qm:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Port".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          _port=parser_field.nextToken();
                          log.trace("port:"+_port);
                       }
                       else{
                           log.error("error parse port:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Persistence".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          String p=parser_field.nextToken();
                          if(p!=null){
                             try { isPersistence=Boolean.parseBoolean(p);} 
                             catch (Exception e) {
                                    log.error("error parse persistence:"+p);
                                    return def.RET_ERROR;
                             }
                          }
                          log.trace("persistence:"+isPersistence);
                       }
                       else{
                           log.error("error parse persistence:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Host".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          host=parser_field.nextToken();
                          log.trace("host:"+host);
                       }
                       else{
                           log.error("error parse host:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Queue".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          send_q_name=parser_field.nextToken();
                          log.trace("send_q_name:"+send_q_name);
                       }
                       else{
                           log.error("error parse queue:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("User".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          user=parser_field.nextToken();
                          log.trace("util:"+user);
                       }
                       else{
                           log.error("error parse util:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Passwd".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          password=parser_field.nextToken();
                          log.trace("passwd:*****");
                       }
                       else{
                           log.error("error parse passwd:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Report".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          _report=parser_field.nextToken();
                          log.trace("report:"+_report);
                       }
                       else{
                           log.error("error parse report:"+field);
                           return def.RET_ERROR;
                       }
                    }
              }

              if(channel==null || _port ==null || host ==null || send_q_name  ==null){
                 log.error("null field error parse jms:"+_jms);
                 return def.RET_ERROR;
              }

              //----------------------------------------------------------------------------------

              parser_str = new StringTokenizer(send_q_name, "@");
              if(parser_str.hasMoreTokens()){
                    send_q_name=parser_str.nextToken();
                    if(parser_str.hasMoreTokens()){
                       send_qm_name=parser_str.nextToken();
                    }
                    else{
                       log.error("error parse send_q_name:"+send_qm_name);
                       return def.RET_ERROR;
                    }
              }
              else{
                 log.error("error parse send_q_name:"+send_q_name);
                 return def.RET_ERROR;
              }
              //----------------------------------------------------------------------------------
              if(_report!=null){
                 parser_str = new StringTokenizer(_report, "!");
                 if(parser_str.hasMoreTokens()){
                       _report=parser_str.nextToken();
                       if(parser_str.hasMoreTokens()){
                          report_q=parser_str.nextToken();
                          parser_str = new StringTokenizer(report_q, "@");

                          report_q=parser_str.nextToken();
                          if(parser_str.hasMoreTokens()){
                             report_qm=parser_str.nextToken();
                          }
                       }
                       else{
                          log.error("error parse queue report:"+_report);
                          return def.RET_ERROR;
                       }
                 }
                 else{
                    log.error("error parse text code report:"+_report);
                    return def.RET_ERROR;
                 }
              }

              if(_port!=null){
                 try { port=Integer.parseInt(_port, 10);} 
                 catch (Exception e) {
                       log.error("error parse port:"+_port);
                       return def.RET_ERROR;
                 }
              }

              if(_report!=null){
                 try { report=Integer.parseInt(_report, 10);} 
                 catch (Exception e) {
                       log.error("error parse code report:"+_report);
                       return def.RET_ERROR;
                 }
              }

              return init();
       }
       public int init(){
              if(channel==null)qm=new mq_mngr_jms(connect_qm_name);
              else             qm=new mq_mngr_jms(connect_qm_name,host,port,channel);

              if(user    !=null){qm.setUser(user);          log.info("init user:"+user);}
              if(password!=null){qm.setPassword(password);  log.info("init psw:"+password);}


              log.info("init router qm:"+connect_qm_name+" queue:"+send_q_name+" bind host:"+host+" port:"+port+" channel:"+channel+" user:"+user+" psw:"+password);

              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     close();
                     return def.RET_FATAL;
              }
              log.info("open manager:"+qm.getQMName());
               try {
                 q=qm.openWriteQ(send_q_name,send_qm_name);
                 if(q!=null)log.info("open q:"+send_q_name+" qm:"+q.getQMName());
                 else      {log.error("can't open q:"+send_q_name);return def.RET_ERROR;}
               } 
               catch(mqExcept e1){
                    log.error("can't open q:"+send_q_name+" error:"+e1.getError()+" ex:"+e1);
                    close();
                    return def.RET_ERROR;
               }
               return def.RET_OK;
       }
       public int init(String _connect_qm_name,String _send_q_name,String _send_qm_name) {
              return init(_connect_qm_name,null,0,null,_send_q_name,_send_qm_name);
       }
       public int init(String _connect_qm_name,String _host,int _port,String _channel,String _send_q_name,String _send_qm_name) {
              clear();
              connect_qm_name=_connect_qm_name;
              host           =_host;
              port           =_port;
              channel        =_channel;
              send_q_name    =_send_q_name; 
              send_qm_name   =_send_qm_name;
              return init();
       }
       public void close(){
               try{if(q !=null)qm.closeQ(q); q=null; } catch (mqExcept e1){q=null; }
               try{if(qm!=null)qm.close();   qm=null;} catch (mqExcept e1){qm=null;}
       }



       private static void work(String _connect_qm_name,String _send_q_name,String host,int port,String channel,String _send_qm_name){

              connect2MQ r=new connect2MQ();

              if(_connect_qm_name!=null || channel==null){
                 if(r.init(_connect_qm_name,_send_q_name,_send_qm_name)<0)return;
              }
              else{
                 if(r.init(_connect_qm_name,host,port,channel,_send_q_name,_send_qm_name)<0)return;
              }
              r.close();
       }

       private static void help(String[] args) {
                 System.out.println("run java "+getClassName()+" manager_name queue_name||queue_name@manager_name [hostname port channel]");
       }
       public static void main(String[] args) {


              if((args.length<3) ||(args.length>=3 && args.length<6)){
                 help(args);
                 return;
              }
              String connect_qmanager_name=null;
              String send_qm_name=null;
              String send_q_name =null;
              String hostname    =null;
              String portname    =null;
              String channel     =null;
              int    port=0;

              connect_qmanager_name=args[0];
              send_qm_name=connect_qmanager_name;
              send_q_name  =args[1];
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
              }
              if(portname!=null) try { port=Integer.parseInt(portname, 10);} catch (Exception e) {port=0;hostname=null;channel=null;}

              connect2MQ.work(connect_qmanager_name,send_q_name,hostname,port,channel,send_qm_name);

       }
       public void setUser(String u, String p) {
              user=u;
              password=p;
              log.info("init user:"+user+" psw:"+password);
              
       }

}
