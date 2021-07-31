package org.little.mq.controlStream;

import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_flowL  extends fc_flow{
       private static final Logger logger = LoggerFactory.getLogger(fc_flowL.class);
       private Object  lock;
       private fc_node node;

       public fc_flowL(fc_node _node) {
              clear();
              lock=new Object();
              flow_contrl=new fc_control();              
              node=_node;
       }
       @Override 
       public JSONObject getStat() {
    	      JSONObject root= _getStat();  	   
              logger.trace("fc_flowL getStat():"+root);
              return root;
       }


       @Override
       public void work() {
              synchronized (lock){
                   for(int i=0;i<q_list.size();i++) {
                       fc_Q q=q_list.get(i);
                       q.work();
                   }
                   flow_contrl.work();
                   flow_channel.work();

                   
                   if(isAlarm()){
                      int len=0;
                      for(int i=0;i<q_list.size();i++) {
                          fc_Q q=q_list.get(i);
                          len+=q.getDeepQ();
                      }
                      if(len==0){
                         isAlarm(false);
                         if(node.isControlStream())flow_contrl.setFlag(fc_control.RUN);
                      }
                   }
                   else{
                      boolean is_alarm=false;
                      for(int i=0;i<q_list.size();i++) {
                          fc_Q q=q_list.get(i);
                          if(q.isAlarm()){is_alarm=true;break;}
                      }
                      if(is_alarm==false)setTimeAlarm(0);
                      else {
                          for(int i=0;i<q_list.size();i++) {
                              fc_Q q=q_list.get(i);
                              if(q.getTimeAlarm()>getTimeAlarm())setTimeAlarm(q.getTimeAlarm());
                          }
                      }
                      isAlarm(is_alarm);

                      if(is_alarm && node.isControlStream()){
                         flow_contrl.setFlag(fc_control.STOP);
                      }
                   }
                   
              }

       }
       
       @Override
       protected JSONObject setFlag(boolean flag) {
                 JSONObject ret=flow_contrl.setFlag(flag); 
                 work();
                 return ret;
       }
       @Override
       protected JSONObject      setChannel(boolean is_run) {
                 JSONObject ret=flow_channel.setChannel(is_run); 
                 work();
                 //flow_channel.work();
                 return ret;
       }

       @Override
       protected JSONObject ClearQ(String mngr_id,String q_id){
                 JSONObject root=new JSONObject();
                 logger.trace("flow.ClearQ(flow:"+getID()+",mngr:"+mngr_id+",q:"+q_id+")");
                 for(int i=0;i<q_list.size();i++){
                  if(q_list.get(i).getNameQ().equals(q_id) && q_list.get(i).getNameMngr().equals(mngr_id)) {
                     JSONObject ret=q_list.get(i).ClearQ(); 
                     root.put("resp", ret);
                     logger.trace("flow.ClearQ(flow:"+getID()+",mngr:"+mngr_id+",q:"+q_id+") ret:"+ret);
                     break;
                  }
              }
              
              return root;
       }

       @Override
       public void init(Node node_cfg) {
              clear();
              if(node_cfg==null)return;

              if(node_cfg.getAttributes().getNamedItem("id")==null) {
                 logger.error("The configuration group id:noname");
                 return;
              }
              else  setID(node_cfg.getAttributes().getNamedItem("id").getNodeValue());

              if(node_cfg.getAttributes().getNamedItem("name")==null) {
                 setName("");
              }
              else setName(node_cfg.getAttributes().getNamedItem("name").getNodeValue());

              logger.info("The configuration node:"+node_cfg.getNodeName()+" id:"+getID()+" name:"+getName());

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("q".equalsIgnoreCase(n.getNodeName()) ){           
                     fc_Q q=new fc_QL();
                     q.init(n);
                     q_list.add(q);
                  }
                  else
                  if("cntrl".equalsIgnoreCase(n.getNodeName()) ){           
                     flow_contrl=new fc_controlL();
                     flow_contrl.init(n);
                  }
                  else
                  if("channel".equalsIgnoreCase(n.getNodeName()) ){           
                     flow_channel=new fc_channelL();
                     flow_channel.init(n);
                  }
                         
              }
                 
       }
       @Override
       public    void close() {
                 super.close();
                 if(flow_contrl!=null)flow_contrl.close();
                 if(flow_channel!=null)flow_channel.close();
                 flow_contrl=null;
                 flow_channel=null;
                 node=null;
       }
       
}