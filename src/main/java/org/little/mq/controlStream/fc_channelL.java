package org.little.mq.controlStream;
        
import org.json.JSONObject;
import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_contrl;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_channelL extends fc_channel{
       private static final Logger logger = LoggerFactory.getLogger(fc_channelL.class);

       private mq_contrl cntrl;

       public fc_channelL(){
              clear();
              cntrl=null;
       }
       @Override
       protected void   clear() {
              super.clear();
       }
       protected void   reopen() {
           if(cntrl==null)
           try {
                cntrl=new mq_contrl();
                cntrl.open(cfg.getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());
           }
           catch (mqExcept m){
                  logger.error("cntrl.open ex:"+m);
                  try {cntrl.close();}catch (mqExcept m1){}
                  cntrl=null;
           }

       }

       @Override
       public void init(Node node_cfg) {
              cfg.init(node_cfg);

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("chl".equalsIgnoreCase(n.getNodeName())){ setName(n.getTextContent());logger.info("chl:"+getName()); }
              }
              reopen();
       }
       protected JSONObject setChannel(boolean _is_run) {
                 JSONObject root=new JSONObject();
                 try {
                      reopen();
                      if(cntrl!=null){
                         if(_is_run && !isRun())cntrl.startChannel(getName());
                         if(!_is_run && isRun())cntrl.stopChannel(getName(),true,true);
                      }
                      isRun(_is_run);
                 }
                 catch (mqExcept m){
                       logger.error("setChannel mngr:"+cfg.getNameMngr()+" channel:"+getName()+" ("+_is_run+") ex:"+m);
                       try {cntrl.close();}catch (mqExcept m1){}
                       cntrl=null;
                       isRun(false);
                       root.put("control", "ERROR");
                       return root;
                 }


                 root.put("channel", "OK");
                 logger.trace("channel:"+root);

                 return root;
       }

       @Override
       public void work(){
              if(getName()==null){
                 return;
              }
              if(cntrl==null){
                 reopen();
              }
              //logger.trace("1 work() mngr:"+cfg.getNameMngr()+" channel:"+getName()+" run:"+isRun());
              if(cntrl!=null)
              try {
                   String state=cntrl.statusChannel(getName());
                   if(state==null){
                      logger.error("cntrl.statusChannel return null");
                   }
                   else
                   if(state.startsWith("INACTIVE")){isRun(true);}
                   else
                   if(state.startsWith("MQCHS_STOP")){isRun(false);}
                   else
                   if(state.startsWith("MQCHS_")){isRun(true);}
              }
              catch (mqExcept m){
                    logger.error("work  mngr:"+cfg.getNameMngr()+" channel:"+getName()+"  ex:"+m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    cntrl=null;
                    isRun(false);
                    return;
              }

              logger.trace("2 work() mngr:"+cfg.getNameMngr()+" channel:"+getName()+" run:"+isRun());
       }
       @Override
       public void close(){
              super.close();
              if(cntrl==null)return;
              try {cntrl.close();}catch (mqExcept m1){}
              cntrl=null;
       }

}