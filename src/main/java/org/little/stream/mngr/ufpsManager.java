package org.little.stream.mngr;

import java.util.HashMap;

import org.little.stream.cfg.commonChannel;
import org.little.stream.cfg.commonStream;
import org.little.stream.mngr.channel.ufpsReceiveChannel;
import org.little.stream.mngr.channel.ufpsTSendPoolCMD;
import org.little.stream.mngr.channel.ufpsTSendPoolData;
import org.little.stream.mngr.channel.ufpsTSendPoolLocal;
import org.little.stream.ufps.ufpsMsg;
import org.little.stream.ufps.ufpsTestGen;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;

public class ufpsManager extends tfork{
       private static final Logger logger = LoggerFactory.getLogger(ufpsManager.class);
       private HashMap<String,ufpsUser>  mngr_user;
       private int                       max_size_queue;
       private ufpsQueue                 transfer;
       private ufpsQueue                 command;
       private ufpsQueue                 local;
       private ufpsTSendPoolData         send_trans;
       private ufpsTSendPoolCMD          send_cmd;
       private ufpsTSendPoolLocal        send_local;
       private ufpsReceiveChannel        receive;
       private commonStream              cfg;

       public  ufpsManager(commonStream _cfg) {
               cfg=_cfg;
               max_size_queue=cfg.getMaxSizeQueue();
               mngr_user  =new HashMap<String,ufpsUser>();
               transfer=new ufpsQueue("Manager","transfer",max_size_queue);
               command =new ufpsQueue("Manager","command",max_size_queue);
               local   =new ufpsQueue("Manager","local",max_size_queue);

               createChannels();
       }
 
       public synchronized ufpsUser addUser(String user_name) {
              ufpsUser user=new ufpsUser(user_name,max_size_queue,transfer);
              mngr_user.put(user_name,user);
              return user;
       }

       public synchronized ufpsUser getUser(String user_name) {
              ufpsUser user=mngr_user.get(user_name);
              if(user!=null)return user;
              if(cfg.isAutoCreatUser()) user=addUser(user_name);
              return user;
       }
       public commonStream getCfg   () {return cfg;}
       public ufpsQueue    getTQueue() {return transfer;}
       
       public boolean createChannels() {
              send_trans =null;
              send_local =null;
              receive    =null;
              for(int i=0;i<cfg.getChannels().size();i++) {
                  commonChannel ch = cfg.getChannels().get(i);
                  if(ufpsDef.TYPE_CHANNEL_SERVER.equalsIgnoreCase(ch.getType())){
                     receive=new ufpsReceiveChannel(this,ch);
                     break;
                  } 
              }
              send_trans=new ufpsTSendPoolData (transfer,cfg);
              send_cmd  =new ufpsTSendPoolCMD  (command ,cfg);
              send_local=new ufpsTSendPoolLocal(this,local   ,cfg);

              return true;
       }
       
       public boolean runCommandGet(String _username,String _queue) {
              try {
                   command.put(new ufpsCMD(cfg.getManagerName(),_username,_queue));
              } catch (InterruptedException e) {
                   return false;
              }
              return true;
       }
       
       public boolean route(ufpsMsg msg){
              if(msg==null){
                 logger.error("route: msg is null");
                 return false;
              }
              logger.trace("begin msg route");
              //
              ufpsFrame f_msg=cfg.getRoute().set(msg);
              f_msg.setNode(cfg.getFrameNodeName());
              f_msg.setCreate();
              //
              boolean ret=route(f_msg);
              logger.trace("end msg route");
              return ret;
       }
       public boolean route(ufpsFrame f_msg) {
              boolean ret=false;
              if(f_msg==null){
                 logger.error("route: f_msg is null");
                 return false;
              }

              if(f_msg.isLocal()==false){
                 logger.trace("f_msg put to transfer queue (front2back)");
                 try {
                     ret=transfer.put(f_msg);
                     logger.trace("put "+ret+" to transfer f_msg user:"+f_msg.getUser()+" queue:"+f_msg.getQueue());
                     logger.trace("transfer("+transfer.getFullName()+") size:"+transfer.size());
                 } catch (InterruptedException e) {
                     logger.error("error put ufpsFrame in  transfer queue"+new Except("ex:",e));
                     return false;
                 }
                 logger.trace("f_msg route(trans) return:"+ret);
                 return  ret;
              }

              logger.trace("f_msg put to local queue");
              if(f_msg.getType().equals(ufpsDef.TYPE_CMD_MSG)){
                 //-----------------------------------------------


                 //-----------------------------------------------
              }
              else{
                 ufpsUser user=getUser(f_msg.getUser());
                 if(user==null)return false;
                 try {
                     f_msg.setArrive();
                     logger.trace("get user:"+f_msg.getUser()+" queue:"+f_msg.getQueue());
                     ret=user.putQueue(f_msg.getQueue(),f_msg);
                     logger.trace("put "+ret+" f_msg user:"+f_msg.getUser()+" queue:"+f_msg.getQueue());
                 } 
                 catch (InterruptedException e) {
                      logger.error("error put ufpsFrame in local queue:"+f_msg.getQueue()+" user:"+f_msg.getUser()+new Except("ex:",e));
                      return false;                            
                 }
              }

              logger.trace("f_msg route(local) return:"+ret);
              return ret;
       }


       @Override
       public void run(){
              logger.trace("start manager");
              receive.fork();
              logger.trace("run receive channel");
              send_trans.fork();
              logger.trace("run trans channel");
              send_cmd.fork();
              logger.trace("run cmd channel");
              send_local.fork();
              logger.trace("run local channel");


              while(this.isRun()){
                    //ufpsFrame f_msg=transfer.get();
                    //if(f_msg!=null){
                    //}
                    logger.trace("empty ");
                    delay(100);
              }
              receive.stop();
              send_cmd.stop();
              send_trans.stop();
              send_local.stop();

              stop();

              clear();
              logger.trace("stop manager");

       };


       public static void main(String args[]){
              commonStream cfg =new commonStream();
              ufpsManager  mngr=new ufpsManager(cfg);
              
              mngr.addUser("av");
              mngr.addUser("iap");

              ufpsMsg   msg   = null;
              ufpsFrame frame = null;
              
              try {
                   msg  = ufpsTestGen.getCalc();                  
                   frame=new ufpsFrame("user1","q1",msg);
                   mngr.getUser("user1").putQueue(frame);
                   msg  = ufpsTestGen.getCalc();                     
                   frame=new ufpsFrame("user1","q1",msg);
                   mngr.getUser("user1").putQueue(frame);;
                   msg  = ufpsTestGen.getCalc();                     
                   frame=new ufpsFrame("user1","q1",msg);
                   mngr.getUser("user1").putQueue(frame);;


              } catch (InterruptedException e) {
                   e.printStackTrace();
              }
              
       
       }

}

