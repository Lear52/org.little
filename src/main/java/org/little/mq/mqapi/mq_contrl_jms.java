package org.little.mq.mqapi;

import java.util.ArrayList;
import java.util.Hashtable;

import org.little.util.Logger;

import com.ibm.mq.MQMessage;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.MQCFH;
import com.ibm.mq.headers.pcf.MQCFIL;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;
import com.ibm.mq.headers.pcf.PCFParameter;

public class mq_contrl_jms extends mq_contrl{
       final private static String CLASS_NAME="prj0.stream.mq.mqContrl";
       final private static int    CLASS_ID  =1702;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private mq_mngr_jms        queueManager;
       private PCFMessageAgent agent;
       private String          jms_string;
       private boolean         isUsed;

       public mq_contrl_jms(){
              clear();
       }

       public void clear() {
              queueManager   = new mq_mngr_jms();
              jms_string       = null;
              isUsed           = false;
       }
       public boolean isUse(){return isUsed;}
       public void    isUse(boolean is){isUsed=is;}

       // JmsChannel:SYSTEM.ADMIN.SVRCONN/Port:1414/Host:10.70.112.86/Queue:UTP@QM_cc/User:av/Passwd:av123/Report:1792!UTP@QM_cc/Persistence:true
       public boolean isURL(String _jms_string) {  
                       return jms_string.equals(_jms_string);
       }

       public int init(String _jms_string) throws  mqExcept {
              jms_string=_jms_string;
              queueManager.initJMS(jms_string);
              queueManager.open();
              return 0;
       }



       public  void open() throws  mqExcept{
               try {
                      agent = new PCFMessageAgent(queueManager.getQM());
              }
              catch (MQDataException mqde) {
                     mqExcept e1=new mqExcept("pcf agent connect",mqde);
                     if(!queueManager.isLocal()) log.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                     else       log.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                     throw e1;
              }
              
       }
      
       public void close() throws  mqExcept{
              try {
                  if(agent!=null)agent.disconnect();
              }
              catch (MQDataException e) {
                     mqExcept e1=new mqExcept("pcf agent disconnect",e);
                     if(!queueManager.isLocal()) log.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                     else       log.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                     throw e1;
              }
              try {
                  if(queueManager!=null)queueManager.close();
              }
              catch (Exception e2) {
                     mqExcept e1=new mqExcept("pcf agent disconnect",e2);
                     if(!queueManager.isLocal()) log.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                     else       log.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                     throw e1;
              }

       }
       private PCFMessage[] sendCmd(String cmd,PCFMessage pcfCmd) throws  mqExcept{
               PCFMessage[] pcfResponse =null;
               try {
                 pcfResponse=agent.send(pcfCmd);
               }
               catch (PCFException e10) {
                      if((e10.reasonCode == 3065) || (e10.reasonCode == 3200)) {/**/
                         log.trace("mngr:"+queueManager.getQMName()+" cmd:"+cmd+" rc:"+e10.reasonCode);
                         return null;//pcfResponse; 
                      } 
                      if(e10.reasonCode == 4064) {
                         log.trace("mngr:"+queueManager.getQMName()+" cmd:"+cmd+" rc:"+e10.reasonCode);
                         return null; 
                      } 
                      if(e10.reasonCode == 4031) {
                         log.trace("mngr:"+queueManager.getQMName()+" cmd:"+cmd+" rc:"+e10.reasonCode);
                         return null; 
                      } 
                      mqExcept e1=new mqExcept(cmd,e10);
                      if(!queueManager.isLocal()) log.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                      else                        log.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                      throw e1;
               }
               catch (MQDataException e11) {
                      mqExcept e1=new mqExcept(cmd,e11);
                      if(!queueManager.isLocal()) log.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                      else       log.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                      throw e1;
               }
               catch (Exception e12) {
                      mqExcept e1=new mqExcept(cmd,e12);
                      if(!queueManager.isLocal()) log.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                      else       log.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                      throw e1;
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
              return alterChannel(pcfChannel,type,ipChannel,null);
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
       public int alterChannel(String pcfChannel,String type,String ipChannel,String localChannel) throws  mqExcept{

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
              if(is_stop)pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_STATUS, MQConstants.MQCHS_INACTIVE);
              else pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_STATUS, MQConstants.MQCHS_INACTIVE);

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
                  //System.out.println("Channel status is " + chStatusText[chStatus]);
                  return chStatusText[chStatus];
              }
              //else System.out.println("Channel status is " + chStatusText[0]);
              return chStatusText[0];
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

       }
       public void displayQueueManager()   throws  mqExcept {
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

       }

      
              
       public static void main(String[] args) {
              mq_contrl_jms cntrl=new mq_contrl_jms();
              try {
                   System.out.println("create");
                   cntrl.init("JmsChannel:SYSTEM.ADMIN.SVRCONN/Port:1515/Host:10.70.112.150/QM:KCOI_NN/User:av/Passwd:483886413409");

                   System.out.println("init");
                   cntrl.open();
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
                   cntrl.DisplayActiveLocalQueues("APSDH_PS");
              }
              catch (mqExcept m){
                    System.out.println(m);
              }
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

              try {
                   cntrl.close();
                   System.out.println("close");
              }
              catch (mqExcept m){
                   System.out.println(m);
              }

       }


}


