package org.little.mq.controlStream;

import java.util.ArrayList;

import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.task;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class fc_groupL extends task implements fc_group{
       private static final Logger logger = LoggerFactory.getLogger(fc_groupL.class);
       private ArrayList<fc_flow> flow_list;
       private String             id;
       private String             name;
       private int                count_active_flow; 
       private boolean            is_alarm; 
       private long               time_alarm; 
       private fc_node            node;

       public fc_groupL(fc_node _node){
              clear();
              node=_node;
       }

       @Override
       public void clear() {
              count_active_flow=0;
              is_alarm=false;
              time_alarm=0;
              flow_list  =new ArrayList<fc_flow>();
              id         =null;  
              name       =null;
       }

       @Override
       public String             getNodeID() {return node.getID();}
       @Override
       public String             getID() {return id;}
       @Override
       public String             getRID() {return getID();}
       @Override
       public void               setID(String id) {this.id = id;       }
       @Override
       public String             getName() {return name;}
       @Override
       public void               setName(String name) {this.name = name;}
       @Override
       public int                getActiveFlow(){return count_active_flow;}
       @Override
       public boolean            isAlarm() {return is_alarm;};
       @Override
       public long               getTimeAlarm() {return time_alarm;};
       
       @Override
       public ArrayList<fc_flow> getFlowList(){return flow_list;}

       @Override
       public void work(){
              count_active_flow      =0;
              is_alarm=false;
              time_alarm=0;
              for(int i=0;i<flow_list.size();i++) {
                  fc_flow flow=flow_list.get(i);
                  logger.trace("group id:"+id+" name:"+name+" flow:"+i);
                  flow.work();  // get state flow
              }
              for(int i=0;i<flow_list.size();i++) {
                  fc_flow flow=flow_list.get(i);
                  if(flow.isAlarm())is_alarm=true;else count_active_flow++; 
                  if(flow.getTimeAlarm()>time_alarm)time_alarm=flow.getTimeAlarm();
              }
       }
       @Override
       public JSONObject getStat(){
              JSONObject root=fc_group.getStat(this);
              logger.trace("fc_groupL getStat() id:"+id+" name:"+name+" size:"+flow_list.size());
              return root;

       }

       @Override
       public void init(Node node_cfg) {
              if(node_cfg==null)return;

              if(node_cfg.getAttributes().getNamedItem("id")==null) {
                 logger.error("The configuration group id:noname");
                 return;
              }
              else{
                 id=node_cfg.getAttributes().getNamedItem("id").getNodeValue();
                 logger.info("group(l) id:"+id);

              }

              if(node_cfg.getAttributes().getNamedItem("name")==null) {
                 name="NO NAME";
                 logger.info("group(l) name:"+name);
              }
              else{
                 name=node_cfg.getAttributes().getNamedItem("name").getNodeValue();
                 logger.info("group(l) name:"+name);
              }

              flow_list=new ArrayList<fc_flow>();
              logger.info("The configuration node:"+node_cfg.getNodeName()+" id:"+id+" name:"+name);

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("flow".equalsIgnoreCase(n.getNodeName()) ){           
                     fc_flow flow=new fc_flowL(node);
                     flow.init(n);
                     flow_list.add(flow);
                  }
              }
              
       }

       @Override
       public JSONObject setFlag(String flow_id, boolean is_flag) {

              JSONObject root=new JSONObject();

              for(int i=0;i<flow_list.size();i++){
                  if(flow_list.get(i).getID().equalsIgnoreCase(flow_id)) {
                     JSONObject ret=flow_list.get(i).setFlag(is_flag); 
                     root.put("resp", ret);
                     break;
                  }
              }
              
              return root;
       }
       @Override
       public JSONObject setFlagAll(boolean is_flag) {

              JSONObject root=new JSONObject();

              for(int i=0;i<flow_list.size();i++){
                     JSONObject ret=flow_list.get(i).setFlag(is_flag); 
                     root.put("resp", ret);
              }
              
              return root;
       }
       @Override
       public JSONObject setChannel(String flow_id, boolean is_channel) {

              JSONObject root=new JSONObject();
              //logger.trace("setChannel");

              for(int i=0;i<flow_list.size();i++){
                  if(flow_list.get(i).getID().equalsIgnoreCase(flow_id)) {
                     logger.trace("setChannel flow_id:"+flow_id);
                     JSONObject ret=flow_list.get(i).setChannel(is_channel); 
                     root.put("resp", ret);
                     break;
                  }
              }
              
              //logger.trace("setChannel");

              return root;
       }
       @Override
       public JSONObject setChannelAll(boolean is_channel) {

              JSONObject root=new JSONObject();

              for(int i=0;i<flow_list.size();i++){
                     JSONObject ret=flow_list.get(i).setChannel(is_channel); 
                     root.put("resp", ret);
              }

              return root;
       }

       @Override
       public JSONObject ClearQ(String flow_id,String mngr_id,String q_id){
              JSONObject root=new JSONObject();

              //logger.trace("group.ClearQ(group:"+id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+")");
              for(int i=0;i<flow_list.size();i++){
                  if(flow_list.get(i).getID().equals(flow_id)) {
                     JSONObject ret=flow_list.get(i).ClearQ(mngr_id,q_id); 
                     root.put("resp", ret);
                     logger.trace("group.ClearQ(group:"+id+",flow:"+flow_id+",mngr:"+mngr_id+",q:"+q_id+") ret:"+ret);
                     break;
                  }
              }
              
              return root;

       }
       @Override
       public void close() {
              if(flow_list==null)return;
              for(int i=0;i<flow_list.size();i++){
                  flow_list.get(i).close();
              }
              flow_list.clear();
              flow_list=null;
       }

}