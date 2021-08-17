package org.little.stream.mngr;

import java.util.ArrayList;
import java.util.HashMap;

import org.little.stream.cfg.commonChannel;
import org.little.stream.cfg.commonStream;
import org.little.stream.ufps.ufpsMsg;
import org.little.stream.ufps.ufpsTestGen;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;

public class ufpsManager extends tfork{
       private static final Logger logger = LoggerFactory.getLogger(ufpsManager.class);
       private HashMap<String,ufpsUser>  mngr_user;
       private int                       max_size_queue;
       private ufpsQueue                 transfer;
       private ufpsQueue                 command;
       private ufpsTSendPool             send_trans;
       private ArrayList<ufpsTSendQueue> send_local;
       private ufpsReceiveChannel         receive;
       private commonStream              cfg;

       public  ufpsManager(commonStream _cfg) {
                  cfg=_cfg;
               max_size_queue=1000;
               mngr_user  =new HashMap<String,ufpsUser>();
               
               send_trans=null;
               send_local =new ArrayList<ufpsTSendQueue>();
               receive    =null;
       }
 
       public void addUser(String user_name) {
              mngr_user.put(user_name,new ufpsUser(user_name,max_size_queue,transfer));
       }

       public ufpsUser     getUser(String user_name) {return mngr_user.get(user_name);}
       public commonStream getCfg() {return cfg;}
       public ufpsQueue    getTQueue() {return transfer;}
       public boolean      route(ufpsFrame msg) {return false;}
       
       public boolean createChannels() {
              send_local =new ArrayList<ufpsTSendQueue>();
              receive    =null;
              for(int i=0;i<cfg.getChannels().size();i++) {
                     commonChannel ch = cfg.getChannels().get(i);
                     if("server".equalsIgnoreCase(ch.getType())){
                        receive=new ufpsReceiveChannel(this,ch);
                        break;
                     } 
              }
              send_trans=new ufpsTSendPool(transfer,cfg);

              for(int i=0;i<cfg.getChannels().size();i++) {
                     commonChannel ch = cfg.getChannels().get(i);
                     if("client".equalsIgnoreCase(ch.getType()) && "local".equalsIgnoreCase(ch.getSubType())){
                            ufpsTSendQueue channel = new ufpsTSendQueue(this,ch);
                            send_local.add(channel);
                            break;
                     } 
              }
              
              return true;
       }
       
       public void runCommand() {
                 command.get();
       }


       @Override
       public void run(){
              receive.fork();
              send_trans.fork();

              while(this.isRun()){

                  delay(1);
                  logger.trace("empty RUN!!!! Override !!!!");
              }
              receive.stop();
              send_trans.stop();

              stop();

              clear();

       };


       public static void main(String args[]){
              commonStream cfg=new commonStream();
              ufpsManager  mngr=new ufpsManager(cfg);
              
              mngr.addUser("user1");
              mngr.addUser("user2");
              ufpsMsg msg = null;
              ufpsFrame frame = null;
              
              try {
                   msg = ufpsTestGen.getCalc();                  
                   frame=new ufpsFrame("user1","q1",msg);
                   mngr.getUser("user1").getStream().put(frame);
                   msg = ufpsTestGen.getCalc();                     
                   frame=new ufpsFrame("user1","q1",msg);
                   mngr.getUser("user1").getStream().put(frame);
                   msg = ufpsTestGen.getCalc();                     
                   frame=new ufpsFrame("user1","q1",msg);
                   mngr.getUser("user1").getStream().put(frame);


              } catch (InterruptedException e) {
                   e.printStackTrace();
              }
              
       
       }
}

