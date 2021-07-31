package org.little.mq.mqapi;

import java.util.ArrayList;
import java.util.Hashtable;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

//import com.ibm.mq.MQC;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.MQCFH;
import com.ibm.mq.headers.pcf.MQCFIL;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;
import com.ibm.mq.headers.pcf.PCFParameter;

public class mq_contrl{
       private static final Logger logger = LoggerFactory.getLogger(mq_contrl.class);

       private mq_mngr         queueManager;
       private PCFMessageAgent agent;
       private int             reason;
       private boolean         isUsed;
       private boolean         isOpen;

       public mq_contrl(){
              clear();
       }

       public void clear() {
              queueManager   = new mq_mngr();
              isUsed           = false;
              isOpen           = false;
              reason           =0;
       }
       public boolean isUse(){return isUsed;}
       public void    isUse(boolean is){isUsed=is;}

       public boolean isOpen(){return isOpen;}
       public void    isOpen(boolean is){isOpen=is;}

       public int     getReason(){return reason;}

       public int open(String _qmname,String _host,int _port,String _channel,String user,String passwd) throws  mqExcept {
              queueManager=new mq_mngr(_qmname,_host,_port, _channel);
              queueManager.setUser    (user);
              queueManager.setPassword(passwd);

              queueManager.open();
              try {
                  agent = new PCFMessageAgent(queueManager.getQM());
              }
              catch (MQDataException mqde) {
                 mqExcept e1=new mqExcept("pcf agent connect",mqde);
                 if(!queueManager.isLocal()) logger.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                 else       logger.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                 throw e1;
             }
             isOpen(true); 
             reason =0;
             return 0;
       }

      
       public void close() throws  mqExcept{
              reason =0;
              isOpen(false); 
              try {
                  if(agent!=null)agent.disconnect();
              }
              catch (MQDataException e) {
                     mqExcept e1=new mqExcept("pcf agent disconnect",e);
                     if(!queueManager.isLocal()) logger.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                     else       logger.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                     throw e1;
              }
              try {
                  if(queueManager!=null)queueManager.close();
              }
              catch (Exception e2) {
                     mqExcept e1=new mqExcept("pcf agent disconnect",e2);
                     if(!queueManager.isLocal()) logger.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                     else       logger.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                     throw e1;
              }

       }
       private PCFMessage[] sendCmd(String cmd,PCFMessage pcfCmd) throws  mqExcept{
               reason=0;
               isUse(true);
               PCFMessage[] pcfResponse =null;
               try {
                 pcfResponse=agent.send(pcfCmd);
               }
               catch (PCFException e10) {
                      reason =e10.reasonCode;
                      if((e10.reasonCode == 3065) || (e10.reasonCode == 3200)) {/**/
                         logger.trace("mngr:"+queueManager.getQMName()+" cmd:"+cmd+" rc:"+e10.reasonCode);
                         return null;//pcfResponse; 
                      } 
                      if(e10.reasonCode == 4064) {
                         logger.trace("mngr:"+queueManager.getQMName()+" cmd:"+cmd+" rc:"+e10.reasonCode);
                         return pcfResponse;//null; 
                      } 
                      if(e10.reasonCode == 4031) {
                         logger.trace("mngr:"+queueManager.getQMName()+" cmd:"+cmd+" rc:"+e10.reasonCode);
                         return null; 
                      } 
                      mqExcept e1=new mqExcept(cmd,e10);
                      if(!queueManager.isLocal()) logger.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                      else                        logger.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                      throw e1;
               }
               catch (MQDataException e11) {
                      reason =-1;
                      mqExcept e1=new mqExcept(cmd,e11);
                      if(!queueManager.isLocal()) logger.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                      else                        logger.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                      throw e1;
               }
               catch (Exception e12) {
                      reason =-1;
                      mqExcept e1=new mqExcept(cmd,e12);
                      if(!queueManager.isLocal()) logger.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                      else                        logger.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                      throw e1;
               }
               finally{
                  isUse(false);
               }
               return pcfResponse;

       }
       public int startChannel(String pcfChannel) throws  mqExcept{

              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_START_CHANNEL);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, pcfChannel);

              sendCmd("start channel:"+pcfChannel,pcfCmd);
              return getReason();
       }
       public int alterChannel(String pcfChannel,String type,String ipChannel) throws  mqExcept{
              alterChannel(pcfChannel,type,ipChannel,null);
              return getReason();
       }
       public int  alterChannel(String pcfChannel,String type,String ipChannel,String localChannel) throws  mqExcept{

              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_CHANGE_CHANNEL);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, pcfChannel);
              pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE, typeChannel(type));
              pcfCmd.addParameter(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE,MQConstants.MQXPT_TCP ); // "TCP"
              pcfCmd.addParameter(MQConstants.MQCACH_CONNECTION_NAME,ipChannel);
              if(localChannel!=null){
                 pcfCmd.addParameter(MQConstants.MQCACH_LOCAL_ADDRESS,localChannel);
              }
              sendCmd("alter channel:"+pcfChannel,pcfCmd);
              return getReason();
       }
       public int stopChannel(String pcfChannel,boolean force,boolean is_stop) throws  mqExcept{
              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_STOP_CHANNEL);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, pcfChannel);


              if(force) {
                 pcfCmd.addParameter(MQConstants.MQIACF_MODE, MQConstants.MQMODE_FORCE);
              }
              if(is_stop)pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_STATUS, MQConstants.MQCHS_STOPPED);
              else       pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_STATUS, MQConstants.MQCHS_INACTIVE);

              sendCmd("stop channel:"+pcfChannel,pcfCmd);

              return getReason();
       }
       public int resetChannel(String pcfChannel, int value) throws  mqExcept{

              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_RESET_CHANNEL);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, pcfChannel);
              pcfCmd.addParameter(MQConstants.MQIACH_MSG_SEQUENCE_NUMBER,value);
              sendCmd("reset channel:"+pcfChannel,pcfCmd);

              return getReason();
       }

       public int commitChannel(String pcfChannel) throws  mqExcept{
              return resolveChannel(pcfChannel,MQConstants.MQIDO_COMMIT);
       }
       public int backoutChannel(String pcfChannel) throws  mqExcept{
              return resolveChannel(pcfChannel,MQConstants.MQIDO_BACKOUT);
       }
       public int resolveChannel(String pcfChannel, int value) throws  mqExcept{
              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_RESOLVE_CHANNEL);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, pcfChannel);
              pcfCmd.addParameter(MQConstants.MQIACH_IN_DOUBT,value);
              sendCmd("resolve channel:"+pcfChannel,pcfCmd);
              return getReason();
       }
       public String statusChannel(String pcfChannel) throws  mqExcept{
              String[] chStatusText = {"INACTIVE", "MQCHS_BINDING", "MQCHS_STARTING", "MQCHS_RUNNING","MQCHS_STOPPING", "MQCHS_RETRYING", "MQCHS_STOPPED", "MQCHS_REQUESTING", "MQCHS_PAUSED","", "", "", "", "MQCHS_INITIALIZING"};

              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_STATUS);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, pcfChannel);

              PCFMessage[] pcfResponse = sendCmd("status channel:"+pcfChannel,pcfCmd);
              if((pcfResponse != null) && (pcfResponse.length > 0)) {
                  int          chStatus = ((Integer) (pcfResponse[0].getParameterValue(MQConstants.MQIACH_CHANNEL_STATUS))).intValue();
                  logger.trace("mngr:"+queueManager.getQMName()+" channel:"+pcfChannel+" statusChannel:"+chStatus+" statusChannel:"+chStatusText[chStatus]);
                  //System.out.println("Channel status is " + chStatusText[chStatus]);
                  return chStatusText[chStatus];
              }
              else{
                  logger.trace("mngr:"+queueManager.getQMName()+" channel:"+pcfChannel+" pcfResponse = null");
              }
              return chStatusText[0];
              //else System.out.println("Channel status is " + chStatusText[0]);
       }
       public void displayChannels(String channel_name) throws  mqExcept{
              // Create the PCF message type for the channel names inquire.
              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_NAMES);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, channel_name);
              pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE, MQConstants.MQCHT_ALL);

              PCFMessage[] pcfResponse = sendCmd("display channel:"+channel_name,pcfCmd);

              System.out.println("+-----+------------------------------------------------+----------+");
              System.out.println("|Index|                  Channel Name                  |   Type   |");
              System.out.println("+-----+------------------------------------------------+----------+");

              for(int responseNumber = 0; responseNumber < pcfResponse.length; responseNumber++) {
                  String[] names = (String[]) pcfResponse[responseNumber].getParameterValue(MQConstants.MQCACH_CHANNEL_NAMES);
                  if (names != null) {
                     int[] types = (int[]) pcfResponse[responseNumber].getParameterValue(MQConstants.MQIACH_CHANNEL_TYPES);
                     String[] channelTypes = {"", "SDR", "SVR", "RCVR", "RQSTR", "", "CLTCN", "SVRCN","CLUSRCVR", "CLUSSDR", ""};
                     for(int index = 0; index < names.length; index++) {
                         System.out.println("|" + index+ "|" + names[index] + "|" + channelTypes[types[index]] + "|");
                     }
                  }
              }
       }
       public void displayChannels(Hashtable<String,String> tab,String channel_name) throws  mqExcept{
              // Create the PCF message type for the channel names inquire.
              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_NAMES);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, channel_name);
              pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE, MQConstants.MQCHT_ALL);

              PCFMessage[] pcfResponse = sendCmd("display channel:"+channel_name,pcfCmd);

              for(int responseNumber = 0; responseNumber < pcfResponse.length; responseNumber++) {
                  String[] names = (String[]) pcfResponse[responseNumber].getParameterValue(MQConstants.MQCACH_CHANNEL_NAMES);
                  if (names != null) {
                     int[] types = (int[]) pcfResponse[responseNumber].getParameterValue(MQConstants.MQIACH_CHANNEL_TYPES);
                     String[] channelTypes = {"", "SDR", "SVR", "RCVR", "RQSTR", "", "CLTCN", "SVRCN","CLUSRCVR", "CLUSSDR", ""};
                     for(int index = 0; index < names.length; index++) {
                         tab.put(names[index],channelTypes[types[index]]);
                     }
                  }
              }
       }
       /**
        * ¬озвращает список всех каналов у менеджера очередей
        * @param mqConnection соединение с менеджером MQ
        * @return список всех каналов
        * @throws MQSPIException если при получении списка каналов произошла ошибка
        */
       public ArrayList<String> getAllChannelNames () throws mqExcept {
              ArrayList<String> channelNames = new ArrayList<String>();

              //if ((mqConnection == null)||(mqConnection.getMQAgent() == null)) {
              //    return channelNames;
              //}
             
              PCFMessage pcfCmd  = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_NAMES);
              pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME,"*");
              pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE, MQConstants.MQCHT_ALL);
             
              PCFMessage[] pcfResponse = sendCmd("display all channel",pcfCmd);
             
              for (PCFMessage responseMsg : pcfResponse) {
                  String namesFromMessage[] =  (String[])responseMsg.getParameterValue(MQConstants.MQCACH_CHANNEL_NAMES);
                  if (namesFromMessage == null) {
                      continue;
                  }
                  for (String qName : namesFromMessage) {
                      channelNames.add(qName.trim());
                  }
              }
              return channelNames;
       }


       public void DisplayActiveLocalQueues(String queue_name) throws mqExcept{

              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
              pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, queue_name);//"*"
              pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);
              // Queue depth filter = "WHERE depth > 0".
              pcfCmd.addFilterParameter(MQConstants.MQIA_CURRENT_Q_DEPTH, MQConstants.MQCFOP_GREATER, 0);
              // Execute the command. The returned object is an array of PCF messages.
              PCFMessage[] pcfResponse = sendCmd("display queues:"+queue_name,pcfCmd);

             System.out.println("+-----+------------------------------------------------+-----+");
             System.out.println("|Index|                    Queue Name                  |Depth|");
             System.out.println("+-----+------------------------------------------------+-----+");

             if(pcfResponse!=null)
             for(int i = 0; i < pcfResponse.length; i++) {
                 PCFMessage response = pcfResponse[i];
                 System.out.println("|" + i + "|" + response.getParameterValue(MQConstants.MQCA_Q_NAME).toString() + "|" + (response.getParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH)+""));
              }
     
       }
       public int lengthLocalQueues1(String queue_name) throws mqExcept{

              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
              pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, queue_name);//"*"
              pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);
              // Queue depth filter = "WHERE depth > 0".
              pcfCmd.addFilterParameter(MQConstants.MQIA_CURRENT_Q_DEPTH, MQConstants.MQCFOP_GREATER, 0);
              // Execute the command. The returned object is an array of PCF messages.
              PCFMessage[] pcfResponse = sendCmd("display queues:"+queue_name,pcfCmd);


             if(pcfResponse!=null){
                if(pcfResponse.length>0){
                   String  v = pcfResponse[0].getParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH).toString();
                   try{return Integer.parseInt(v,10);}catch(Exception e){return 0;}
                }
             }
             return 0;
     
       }
       public int lengthLocalQueues(String queue_name) throws mqExcept{
              isUse(true);
              try{
            	  int op1=MQConstants.MQOO_INQUIRE;
            	  //int op2=MQC.MQOO_INQUIRE;
                  MQQueue destQueue = queueManager.getQM().accessQueue(queue_name, op1);
                  int depth = destQueue.getCurrentDepth();
                  destQueue.close();
                  return depth;
              }
              catch(Exception e){
                    return 0;
              }
              finally{
                  isUse(false);
              }
     
       }

       public void DisplayActiveLocalQueues(Hashtable <String,String> tab,String queue_name) throws mqExcept{

              PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
              pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, queue_name);//"*"
              pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);
              // Queue depth filter = "WHERE depth > 0".
              pcfCmd.addFilterParameter(MQConstants.MQIA_CURRENT_Q_DEPTH, MQConstants.MQCFOP_GREATER, 0);
              // Execute the command. The returned object is an array of PCF messages.
              PCFMessage[] pcfResponse = sendCmd("display queues:"+queue_name,pcfCmd);

             if(pcfResponse!=null)
             for(int i = 0; i < pcfResponse.length; i++) {
                 PCFMessage response = pcfResponse[i];
                 tab.put(response.getParameterValue(MQConstants.MQCA_Q_NAME).toString(),(response.getParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH)+""));
              }
     
       }

       public void displayQueueManager(Hashtable <String,String> tab)   throws  mqExcept {
              isUse(true);
              try {
                   int[] pcfParmAttrs = {MQConstants.MQIACF_ALL};

                   PCFParameter[] pcfParameters = {new MQCFIL(MQConstants.MQIACF_Q_MGR_ATTRS, pcfParmAttrs)};
                   MQMessage[]    mqResponse    = agent.send(MQConstants.MQCMD_INQUIRE_Q_MGR, pcfParameters);

                   MQCFH        mqCFH   = new MQCFH(mqResponse[0]);
                   PCFParameter pcfParam;

                   if (mqCFH.getReason() == 0) {
                  
                       for (int index = 0; index < mqCFH.getParameterCount(); index++) {
                            pcfParam = PCFParameter.nextParameter(mqResponse[0]);
                            tab.put(pcfParam.getParameterName().toString(),pcfParam.getValue().toString());
                       }
                   }
                   else {
                       StringBuffer buf=new StringBuffer(10); 
                       buf.append(" PCF error:\n" + mqCFH);
                       for (int index = 0; index < mqCFH.getParameterCount(); index++) {
                            buf.append(PCFParameter.nextParameter(mqResponse[0]));
                       }
                       //System.out.println(buf.toString());
                   }
             }
             catch (Exception ioe){
                     throw new mqExcept("display mqmngr",ioe); 
             }
             finally{
                isUse(false);
             }

       }
       public void displayQueueManager()   throws  mqExcept {
              isUse(true);
              try {
                   int[] pcfParmAttrs = {MQConstants.MQIACF_ALL};
                  
                   PCFParameter[] pcfParameters = {new MQCFIL(MQConstants.MQIACF_Q_MGR_ATTRS, pcfParmAttrs)};
                   MQMessage[]    mqResponse    = agent.send(MQConstants.MQCMD_INQUIRE_Q_MGR, pcfParameters);
                  
                   MQCFH        mqCFH   = new MQCFH(mqResponse[0]);
                   PCFParameter pcfParam;
                  
                   if (mqCFH.getReason() == 0) {
                       System.out.println("Queue manager attributes:");
                       System.out.println("+--------------------------------+----------------------------------------------------------------+");
                       System.out.println("|Attribute Name                  |                            Value                               |");
                       System.out.println("+--------------------------------+----------------------------------------------------------------+");
                  
                       for (int index = 0; index < mqCFH.getParameterCount(); index++) {
                            pcfParam = PCFParameter.nextParameter(mqResponse[0]);
                            System.out.println("|" + pcfParam.getParameterName().toString() + "|" + pcfParam.getValue().toString() + "|" );   
                       }
                  
                       System.out.println("+--------------------------------+----------------------------------------------------------------+");
                   }
                   else {
                       System.out.println(" PCF error:\n" + mqCFH);
                       for (int index = 0; index < mqCFH.getParameterCount(); index++) {
                            System.out.println(PCFParameter.nextParameter(mqResponse[0]));
                       }
                   }
             }
             catch (Exception ioe){
                     throw new mqExcept("display mqmngr",ioe); 
             }
             finally{
                isUse(false);
             }

       }
       private int  typeChannel(String pcfChannel){
              if("SENDER"   .equals(pcfChannel))return MQConstants.MQCHT_SENDER   ;
              if("SERVER"   .equals(pcfChannel))return MQConstants.MQCHT_SERVER   ;
              if("RECEIVER" .equals(pcfChannel))return MQConstants.MQCHT_RECEIVER ;
              if("REQUESTER".equals(pcfChannel))return MQConstants.MQCHT_REQUESTER;
              if("SVRCONN"  .equals(pcfChannel))return MQConstants.MQCHT_SVRCONN  ;
              if("CLNTCONN" .equals(pcfChannel))return MQConstants.MQCHT_CLNTCONN ;
              if("CLUSRCVR" .equals(pcfChannel))return MQConstants.MQCHT_CLUSRCVR ;
              if("CLUSSDR"  .equals(pcfChannel))return MQConstants.MQCHT_CLUSSDR  ;
              return MQConstants.MQCHT_SENDER;
       }

      
              
       public static void main1(String[] args) {
              mq_contrl cntrl=new mq_contrl();
              try {
                   System.out.println("create");
                   cntrl.open("QM_cc","10.93.134.211",1414,"SYSTEM.ADMIN.SVRCONN","av","5tgbBGT%");
                   System.out.println("init");
                   System.out.println("open");
              }
              catch (mqExcept m){
                    System.out.println(m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    return;
              }
              /*
              try {
                   cntrl.displayQueueManager();
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              
              try {
                   cntrl.statusChannel("SRV.CLN");
                   System.out.println("status 2");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }

              try {
                   cntrl.statusChannel("SYSTEM.DEF.SVRCONN");
                   System.out.println("status 3");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              */
              try {
                   //cntrl.DisplayActiveLocalQueues("Q*");
                   System.out.println("Q1:"+cntrl.lengthLocalQueues("Q1"));
                   System.out.println("---------------------------------");
                   System.out.println("Q2:"+cntrl.lengthLocalQueues("Q2"));
                   System.out.println("---------------------------------");
                   System.out.println("Q3:"+cntrl.lengthLocalQueues("Q3"));
                   System.out.println("---------------------------------");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              /*
              try {
                   cntrl.displayChannels("SNTNN.TO.KBRGATE");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              try {
                   cntrl.statusChannel("SNTNN.TO.KBRGATE");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              try {
                   cntrl.startChannel("SNTNN.TO.KBRGATE");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              try {
                   cntrl.statusChannel("SNTNN.TO.KBRGATE");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              try {
                   cntrl.stopChannel("SNTNN.TO.KBRGATE",true,false);
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              try {
                   cntrl.statusChannel("SNTNN.TO.KBRGATE");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              */
              try {
                   cntrl.close();
                   System.out.println("close");
              }
              catch (mqExcept m){
                   System.out.println(m);
              }

       }

       public static void main(String[] args) {
              mq_contrl cntrl=new mq_contrl();
              try {
                   System.out.println("create");
                   cntrl.open("SBPBACK_SNT_TU","10.70.112.150",2702,"SYSTEM.ADMIN.SVRCONN","av","483886416409");
                   System.out.println("init");
                   System.out.println("open");
              }
              catch (mqExcept m){
                    System.out.println(m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    return;
              }
              
              String channel="SBP.SVRCONN";

              try {
                   System.out.println("status:"+channel+" "+cntrl.statusChannel(channel));
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              /*
              try {
                    System.out.println("start:"+channel+" "+cntrl.startChannel(channel));
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              */
              try {
                   System.out.println("stop:"+channel+" "+cntrl.stopChannel(channel,true,true));
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
              try {
                   cntrl.close();
                   System.out.println("close");
              }
              catch (mqExcept m){
                   System.out.println(m);
              }

       }


}


