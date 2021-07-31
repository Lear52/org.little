package org.little.mq.controlStream;

import org.json.JSONObject;
import org.little.mq.mqapi.mq_contrl;
import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.clearQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_QL extends fc_Q{
       private static final Logger logger = LoggerFactory.getLogger(fc_QL.class);
       
       //private String    mq_host;
       //private int       mq_port;
       //private String    mq_user;
       //private String    mq_passwd;
       private int       deep_alarm;
       //private String    mq_channel;

       private mq_contrl cntrl;

       @Override
       public void   clear() {
    	      super.clear();
              //mq_host   =null;
              //mq_port   =1414;
              //mq_user   =null;
              //mq_passwd =null;
              deep_alarm=150;
              //mq_channel="SYSTEM.ADMIN.SVRCONN";
              cntrl     =new mq_contrl();
       }

       @Override
       public JSONObject getState() {
              JSONObject q=super.getState();
              //logger.info("getState() queue:"+getNameQ()+" len:"+getDeepQ());
              return q;
       }
       @Override
       protected JSONObject ClearQ(){
                 JSONObject root=new JSONObject();
                 if(cfg.getHost()==null){
                    //logger.trace("QL.ClearQ(mngr:"+getNameMngr()+",q:"+getNameQ()+") ");
                    clearQ.clear(getNameMngr(),getNameQ());
                 }
                 else{
                    //logger.trace("QL.ClearQ(host:"+cfg.getHost()+":"+cfg.getPort()+",mngr:"+cfg.getNameMngr()+",q:"+cfg.getNameQ()+") ");
                    clearQ.clear(getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),getNameQ(),cfg.getUser(),cfg.getPasswd());
                 }
                 root.put("clear","Ok");

                 logger.trace("QL.ClearQ(mngr:"+getNameMngr()+",q:"+getNameQ()+") ret:"+root);
                
                 return root;
       }
       @Override
       public void close() {
              try {
                   if(cntrl!=null)cntrl.close();

              }
              catch (mqExcept m){
                    //logger.error("close()  ex:"+m);
                    //return;
              }
              cntrl=null;
              clear();
       }

       @Override
       public void work() {
              //logger.info("1 work() queue:"+getNameQ()+" len:"+getDeepQ());
              //mq_contrl cntrl=new mq_contrl();
              int len=0;
              try {
                   if(!cntrl.isOpen())cntrl.open(getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());

                   len=cntrl.lengthLocalQueues(getNameQ());

                   if(deep_alarm<len)isAlarm(true);
                   else isAlarm(false);

              }
              catch (mqExcept m){
                    logger.error("work() ex:"+m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    setDeepQ(-1);
                    return;
              }
              setDeepQ(len);
              //logger.info("2 work() queue:"+getNameQ()+" len:"+getDeepQ());
       }
       @Override
       public void init(Node node_cfg) {
              if(node_cfg==null)return;
              logger.info("The configuration node:"+node_cfg.getNodeName());

              cfg.init(node_cfg);
              
              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("deep"    .equalsIgnoreCase(n.getNodeName())){String          s=n.getTextContent();try{deep_alarm=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set deep:"+s);deep_alarm=150;}logger.info("deep:"+deep_alarm);}
              }


       }



}

