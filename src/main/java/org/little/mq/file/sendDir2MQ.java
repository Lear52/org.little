package org.little.mq.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.little.util.run.tfork;
import org.little.mq.file.util.localLog;
import org.little.mq.file.util.pathElement;
import org.little.mq.file.util.sendFile2MQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class sendDir2MQ extends tfork implements cmdElement  {
       private static final Logger log = LoggerFactory.getLogger(sendDir2MQ.class);
       public  static String getClassName(){return sendDir2MQ.class.getName();}

       private String                 connect_qm_name;
       private String                 send_q_name;
       private String                 host;
       private int                    port;
       private String                 channel;
       private String                 send_qm_name;
       private String                 mq_user;
       private String                 mq_passwd;
       private sendFile2MQ            sender_file;
       private String                 jms;
       private boolean                isPersistence;
       //------------------------------------------------------------------------------
       private String                 topic;
       private localLog               _log;
       //------------------------------------------------------------------------------
       private long                   count;
       private boolean                is_run;
       //------------------------------------------------------------------------------       
       private String                 global_dirname;
       private String                 local_dirname;
       private long                   time_range;
       private ArrayList<pathElement> list_file;
       private String                 arh_dirname;




       private static Hashtable<String,ArrayList<pathElement>> h_list = new Hashtable<String,ArrayList<pathElement>>();

       public sendDir2MQ() {
              clear();
       }
       public sendDir2MQ(String _connect_qm_name,String _jms,String _global_dirname,String _local_dirname,long _time_range,String _topic){
              clear();
              connect_qm_name=_connect_qm_name;
              global_dirname =_global_dirname ;
              local_dirname  =_local_dirname  ;
              time_range     =_time_range     ;
              topic          =_topic          ;
              jms            =_jms            ;

       }

       public sendDir2MQ(String _connect_qm_name, String _send_q_name,String _host,int _port,String _channel,String _send_qm_name,String _global_dirname,String _local_dirname,long _time_range,String _topic) {
              clear();
              connect_qm_name=_connect_qm_name;
              send_q_name    =_send_q_name    ;
              host           =_host           ;
              port           =_port           ;
              channel        =_channel        ;
              send_qm_name   =_send_qm_name   ;
              global_dirname =_global_dirname ;
              local_dirname  =_local_dirname  ;
              time_range     =_time_range     ;
              topic          =_topic          ;
       }
       public void clear() {
              super.clear();
              connect_qm_name=null;
              send_q_name    =null;
              host           =null;
              port           =0;
              channel        =null;
              send_qm_name   =null;
              global_dirname =null;
              local_dirname  =null;
              time_range     =0;
              isPersistence=false;
              //tps            =0;
              topic          =null;
              jms            =null;
              _log           =null;
              sender_file    =null;
              list_file      =null;
              count          =1;
              arh_dirname    =null;
              isRun(true);
       }
       public void setPersistence(boolean _isPersistence){isPersistence=_isPersistence;}
       public void setUser(String u,String p){
               mq_user=u;mq_passwd=p;
               log.info("init user:"+mq_user+" psw:"+mq_passwd);
       }
       public void setArhDir(String _arh_dirname){arh_dirname=_arh_dirname;}

       public void addCount() {count++;}
       public long getCount() {long c;c=count;return c;}

       public boolean isRun(){
              return is_run;
       }
       public void isRun(boolean r){
              is_run=r;
       }



       public void open(){
              //------------------------------------------------
              synchronized (h_list){
                       String key=global_dirname+"/"+local_dirname;
                       log.trace("search dir_list:"+key);
                       list_file=h_list.get(key);
                       if(list_file==null){
                          log.trace("begin create new dir_list:"+key);
                          list_file=pathElement.getDir(global_dirname,local_dirname,time_range);
                          if(list_file==null){
                             log.trace("dir_list:"+key+" is_empty");
                             clear();
                             return;
                          }
                          log.trace("end create new dir_list:"+key);
                       
                          h_list.put(key,list_file);
                       }
                       else{
                           log.trace("get dir_list:"+key);
                       }
              }
              //------------------------------------------------
              sender_file=new sendFile2MQ();
              if(jms==null){
                 sender_file.setUser(mq_user,mq_passwd);
		 sender_file.setPersistence(isPersistence);
                 if(sender_file.init(connect_qm_name,host,port,channel,send_q_name,send_qm_name)<0){
                    log.error("connect:"+connect_qm_name);
                    clear();
                    return;
                 }
              }
              else{
                 if(sender_file.init(connect_qm_name,jms)<0){
                    log.error("connect:"+connect_qm_name +" jms:"+jms);
                    clear();
                    return;
                 }
              }

              _log=new localLog(topic+".txt");
       }
       public void work(){
              if(list_file==null || sender_file==null)return;
              //------------------------------------------------
              long st=System.currentTimeMillis();

              count=1;

              while(list_file.size()>0 && isRun()){
                  pathElement obj_file=null;
                  long        t=0;

                  synchronized(list_file){
                     obj_file=list_file.get(0);
                     t=System.currentTimeMillis()-(obj_file.getStart()+st);
                     if(t>=0){
                        list_file.remove(0);
                     }
                     else obj_file=null;
                  }

                  if(obj_file!=null){
                     // посылаем сообщение из файла с именем (Path) полученного из o.get();

                     sender_file.run(obj_file.get());

                     log.info("file:"+obj_file.get());

                     long tt=System.currentTimeMillis()-st;
                     double tps;
                     if(tt==0) tps=0;
                     else      tps=((double)getCount())/((double)tt)*1000.0;

                     _log.print(" "+getCount()+" "+obj_file.getFullName()+" time:"+tt+" pts:"+tps + " dt:"+t);

                     addCount();
                     if(arh_dirname==null) {}
                     else
                     if(arh_dirname.equals("")) {
                    	 obj_file.get().toFile().delete();
                     } 
                     else {
                    	 File new_file=new File(arh_dirname);
                    	 obj_file.get().toFile().renameTo(new_file);
                     }
                     obj_file.clear();
                  }
                  else{
                     if(t<0)try{Thread.sleep(-t);}catch(Exception e){}
                  }
                  
              }
              isRun(false);
              //------------------------------------------------
              close();
       }
       public void close(){
              if(_log!=null)_log.close();
              if(sender_file!=null)sender_file.close();
              clear();
       }
       @Override
       public void run(){

              while(isRun()){
                 open();
                 work();
                 close();
                 delay(10);
              }

              stop();
      };
       public static void help(String[] args) {
              System.out.println("run java "+getClassName()+" manager_name_for_connect queue_name_for_send[@manager_name_for_send] [hostname port channel] global_dir_name time_range_sek [local_dir_name]");
       }
       public static void main(String[] args) {


              if((args.length<4) || (args.length>=4 && args.length<7)){
                 help(args);
                 return;
              }
              if(args.length==3)System.out.println("run java "+getClassName()+" ["+args.length+"] "+args[0]+" "+args[1]+" "+args[2]+" "+args[2]);
              else              System.out.println("run java "+getClassName()+" ["+args.length+"] "+args[0]+" "+args[1]+" "+args[2]+" "+args[3]+" "+args[4]+" "+args[5]+" "+args[6]);

              String con_manager_name =null;
              String send_qm_name     =null;
              String send_q_name      =null;
              String hostname         =null;
              String portname         =null;
              String channel          =null;
              String global_dirname   =null;
              String local_dirname    =null;
              String _range_time      =null;
              long   range_time       =0;
              int    port             =0;
              String _user            ="av"; 
              String _passwd          ="5tgbBGT%";   
              con_manager_name     =args[0];
              send_qm_name         =args[0];
              send_q_name          =args[1];
              global_dirname       =args[2];
              _range_time          =args[3];
              if(args.length>=4)   local_dirname=args[4];
              
              StringTokenizer parser_q;
              parser_q = new StringTokenizer(send_q_name, "@");
 
              if(parser_q.hasMoreTokens()) {
                 send_q_name=parser_q.nextToken();
                 if(parser_q.hasMoreTokens()) {
                    con_manager_name=parser_q.nextToken();
                 }
              }
              
              if(args.length>=6){
                 hostname           =args[2];
                 portname           =args[3];
                 channel            =args[4];
                 global_dirname     =args[5];
                 _range_time        =args[6];
                 _user              =args[7];
                 _passwd            =args[8];
                 local_dirname      ="";
                 if(args.length>9) local_dirname=args[9];
              }
              if(portname   !=null) try { port      =Integer.parseInt(portname   , 10);} catch (Exception e) {port=0;hostname=null;channel=null;}
              if(_range_time!=null) try { range_time=Long.parseLong  (_range_time, 10);} catch (Exception e) {range_time=0;}

              range_time*=1000;

              sendDir2MQ s=new sendDir2MQ(con_manager_name,send_q_name,hostname,port,channel,send_qm_name,global_dirname,local_dirname,range_time,"");
              s.setUser(_user,_passwd);
              s.setArhDir("");
              s.open();
              s.work();
              s.close();
       }

}
