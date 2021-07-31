package org.little.mq.file;

import org.little.mq.file.util.connect2MQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class cmdReceive4MQ implements cmdElement{
       private static final Logger log = LoggerFactory.getLogger(cmdReceive4MQ.class);
       public  static String getClassName(){return cmdReceive4MQ.class.getName();}

       private connect2MQ  q;
       private boolean     is_run;
       private String      _connect_qm_name;
       private String      _receive_q_name ;
       private String      host            ;
       private int         port            ;
       private String      channel         ;
       private String      _receive_qm_name;

       public cmdReceive4MQ() {
              clear();
       }
       @Override
       public void clear() {
              _connect_qm_name=null;
              _receive_q_name =null;
              host            =null;
              port            =0   ;
              channel         =null;
              _receive_qm_name=null;

              q=null;
              isRun(true);
       }
       public void setQMName (String _connect_qm_name){this._receive_qm_name =this._connect_qm_name = _connect_qm_name;}
       public void setQName  (String _receive_q_name) {      this._receive_q_name = _receive_q_name; }
       public void setHost   (String host)            { this.host = host;}
       public void setPort   (int port)               {        this.port = port;  }
       public void setChannel(String channel)         {this.channel = channel;}
       @Override
       public void open(){
              q = new connect2MQ();

              if(_connect_qm_name!=null || channel==null){
                 if(q.init(_connect_qm_name,_receive_q_name,_receive_qm_name)<0)return;
              }
              else{
                 if(q.init(_connect_qm_name,host,port,channel,_receive_q_name,_receive_qm_name)<0)return;
              }
       }
       @Override
       public void close(){if(q!=null)q.close(); q=null;}

       @Override
       public boolean isRun(){return is_run;}
       @Override
       public void isRun(boolean r){is_run=r;}

       public long getCount(){return 0;}

       @Override
       public void work() {
              while(isRun()){
                     byte [] buf=null;
                     buf=q.get();
                     if(buf!=null)log.trace("get msg size:"+buf.length);
              }
              isRun(false);
       }
       public int init(String _connect_qm_name,String _jms) {
              return q.init(_connect_qm_name,_jms);
       }
       public int init(String _connect_qm_name,String _receive_q_name,String _receive_qm_name) {
              return init(_connect_qm_name,_receive_q_name,_receive_qm_name);
       }
       public int init(String _connect_qm_name,String _host,int _port,String _channel,String _receive_q_name,String _receive_qm_name) {
              return init(_connect_qm_name,_host,_port,_channel,_receive_q_name,_receive_qm_name);
       }



       public static void help(String[] args) {
                 System.out.println("run java "+getClassName()+" manager_name queue_name||queue_name@manager_name [hostname port channel]");
       }
       public static void main(String[] args) {


              if((args.length<3) ||(args.length>=3 && args.length<6)){
                 help(args);
                 return;
              }
              String connect_qm_name=args[0];
              String receive_q_name =args[1];
              String host           =null;
              String _port          =null;
              String channel        =null;
              int    port           =0;

              if(args.length>=5){
                 host        =args[2];
                 _port       =args[3];
                 channel     =args[4];
              }
              if(_port!=null) try { port=Integer.parseInt(_port, 10);} catch (Exception e) {port=0;host=null;channel=null;}
              cmdReceive4MQ r=new cmdReceive4MQ();

              r.setQMName (connect_qm_name);
              r.setQName  (receive_q_name); 
              r.setHost   (host);            
              r.setPort   (port);               
              r.setChannel(channel);         
              
              
              r.open();
              r.work();
              r.close();

       }

}
