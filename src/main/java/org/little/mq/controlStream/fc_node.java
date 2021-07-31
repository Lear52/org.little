package org.little.mq.controlStream;
              
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
//import org.little.util.common;
//import org.little.util.run.scheduler;
import org.little.util.run.task;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_node  extends task{
       private static final Logger logger = LoggerFactory.getLogger(fc_node.class);

       private ArrayList<fc_group> group_list;               // список групп
       private int                 count_active_flow;
       private boolean             is_alarm; 
       private long                time_alarm; 

       private boolean             is_control_stream;
       private int                 min_count_group;
       private static Object       LOCK=new Object();   

       private String              node_name;   
       private String              node_id;   

       public fc_node(fc_mngr fc_mngr) {
              group_list        =new ArrayList<fc_group>();
              count_active_flow =0;
              is_alarm          =false;
              time_alarm        =0;
              is_control_stream =false;
              min_count_group   =2;
              node_name         ="MAIN"; 
              node_id           ="0";   

       }
       
       /**
        * parsing configuration global section
        * @param _node_cfg 
        */
       private void init_set(Node _node_cfg) {

               if(_node_cfg==null)return;
              
               logger.info("The configuration node:"+_node_cfg.getNodeName());
               NodeList glist=_node_cfg.getChildNodes();
               if(glist==null) return;
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);
                   //if("default_page".equalsIgnoreCase(n.getNodeName()) ){           
                   //   default_page=n.getTextContent();logger.info("default_page:"+default_page);
                   //}else
                   //if("run_timeout".equalsIgnoreCase(n.getNodeName()) ){           
                   //   String s=n.getTextContent();try{task_timeout=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set run_timeout:"+s);task_timeout=10;}logger.info("run_timeout:"+task_timeout);
                   //}else
                   if("control_stream".equalsIgnoreCase(n.getNodeName()) ){           
                      String s=n.getTextContent();try{is_control_stream=Boolean.parseBoolean(s);}catch(Exception e){logger.error("error set control_stream:"+s);is_control_stream=false;}logger.info("control_stream:"+is_control_stream);
                   }else
                   if("min_count_group".equalsIgnoreCase(n.getNodeName()) ){           
                      String s=n.getTextContent();try{min_count_group=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set min_count_group:"+s);min_count_group=2;}logger.info("min_count_group:"+min_count_group);
                   }
               }
       }
       /**
        * parsing configuration  
        * @param _node_cfg
        */
       protected void init(Node _node_cfg,ArrayList<task> task_list) {
              if(_node_cfg==null)return;

              group_list=new ArrayList<fc_group>();
              //task_list =new ArrayList<task>();

              logger.info("The configuration node:"+_node_cfg.getNodeName());
              NodeList glist=_node_cfg.getChildNodes();

              if(_node_cfg.getAttributes().getNamedItem("id")==null) {
                 logger.error("The configuration group id:noname");
                 return;
              }
              else  setID(_node_cfg.getAttributes().getNamedItem("id").getNodeValue());

              if(_node_cfg.getAttributes().getNamedItem("name")==null) {
                 setName("");
              }
              else setName(_node_cfg.getAttributes().getNamedItem("name").getNodeValue());

              if(glist==null) return;

              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  //if("global".equalsIgnoreCase(n.getNodeName()) ){
                  if("node_set".equalsIgnoreCase(n.getNodeName()) ){
                     init_set(n);
                  }
                  if("local".equalsIgnoreCase(n.getNodeName()) ){
                     fc_groupL fc_grp=new fc_groupL(this);
                     fc_grp.init(n);
                     group_list.add(fc_grp);
                     task_list.add(fc_grp);
                  }
                  else
                  if("remote".equalsIgnoreCase(n.getNodeName())){
                     fc_groupR fc_grp=new fc_groupR(this);
                     fc_grp.init(n);
                     group_list.add(fc_grp);
                     task_list.add(fc_grp);
                  }
              }
              
       }
       public void close() {
              if(group_list!=null){
                 for(int i=0;i<group_list.size();i++) {
                     fc_group gr=group_list.get(i);
                     gr.close();
                 }       
                 group_list.clear();
              }
              group_list=null;

       }
       
       public int       getActiveFlow   (){return count_active_flow;}
       public boolean   isAlarm         (){return is_alarm;}
       public long      getTimeAlarm   (){return time_alarm;}
       public boolean   isControlStream (){return is_control_stream && ((getActiveFlow()-getMinCountFlow())>=0);}
       public int       getMinCountFlow(){return min_count_group;}
       public String    getID           (){return node_id;}
       public void      setID           (String id) {node_id = id;       }
       public String    getName         (){return node_name;}
       public void      setName         (String name) {node_name = name;}

       /**
          get number active flow
       */
       @Override
       public void work() {
              synchronized(LOCK){
                  int _count_flow=0;
                  is_alarm=false;
                  time_alarm=0;
                  for(int i=0;i<group_list.size();i++) {
                      fc_group gr=group_list.get(i);
                      _count_flow+=gr.getActiveFlow();
                      if(gr.isAlarm())is_alarm=true;
                      if(gr.getTimeAlarm()>time_alarm)time_alarm=gr.getTimeAlarm();
                  }       
                  count_active_flow=_count_flow;
              }
       }
       
       //protected ArrayList<task> getListTask() {return task_list;}

       public JSONObject ClearQ(String group_id,String flow_id,String mngr_id,String q_id){
              JSONObject root=new JSONObject();
              root.put("type", "clear");
              for(int i=0;i<group_list.size();i++){
                  if(group_list.get(i).getID().equalsIgnoreCase(group_id)) {
                     JSONObject ret=group_list.get(i).ClearQ(flow_id,mngr_id,q_id); 
                     root.put("resp", ret);
                     break;
                  }
              }
              return root;
       }
       public JSONObject setFlag(String group_id,String flow_id,boolean is_flag) {
              JSONObject root=new JSONObject();
              root.put("type", "flag");
              for(int i=0;i<group_list.size();i++){
                  if(group_list.get(i).getID().equalsIgnoreCase(group_id)) {
                     JSONObject ret=group_list.get(i).setFlag(flow_id,is_flag); 
                     root.put("resp", ret);
                     break;
                  }
              }
              return root;
       }
       public JSONObject setFlagAll(boolean is_flag) {
           JSONObject root=new JSONObject();
           root.put("type", "flag");
           for(int i=0;i<group_list.size();i++){
                  JSONObject ret=group_list.get(i).setFlagAll(is_flag); 
                  root.put("resp", ret);
           }
           return root;
    }
       public JSONObject setChannel(String group_id,String flow_id,boolean is_channel) {
              JSONObject root=new JSONObject();
              root.put("type", "channel");
              logger.trace("setChannel");
              for(int i=0;i<group_list.size();i++){
                  if(group_list.get(i).getID().equalsIgnoreCase(group_id)) {
                     logger.trace("setChannel:"+group_id);
                     JSONObject ret=group_list.get(i).setChannel(flow_id,is_channel); 
                     root.put("resp", ret);
                     break;
                  }
              }
              logger.trace("setChannel");
              return root;
       }
       public JSONObject setChannelAll(boolean is_channel) {
              JSONObject root=new JSONObject();
              root.put("type", "channel");
              logger.trace("setChannelAll");
              for(int i=0;i<group_list.size();i++){
                     JSONObject ret=group_list.get(i).setChannelAll(is_channel); 
                     root.put("resp", ret);
              }
              logger.trace("setChannelAll");
              return root;
       }
       /**
           get state local group 
           return JSON 
       */
       public JSONObject getLocalStat() {
              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              int count=0;
              for(int i=0;i<group_list.size();i++) {
                  fc_group group=group_list.get(i);
                  if(group instanceof fc_groupL){
                     list.put(count,group.getStat());
                     count++;
                  }
              }
              root.put("type"      , "local state");
              root.put("list_group", list);
              root.put("size"      , count);

              logger.trace("fc_mnmr getStat() group_list.size(local):"+count+" group_list.size(all):"+group_list.size());
              
              return root;
       }
       /**
          get state all group 

       */
       public JSONObject getStatAll() {
              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              for(int i=0;i<group_list.size();i++) {
                  fc_group group=group_list.get(i);
                  list.put(i,group.getStat());
              }
              root.put("type"      , "node all");
              root.put("node_name" , getName());
              root.put("node_id"   , getID());
              root.put("active"    , getActiveFlow());
              root.put("list_group", list);
              root.put("auto"      , isControlStream());
              root.put("size"      , group_list.size());
              root.put("state"     , getActiveFlow());
              root.put("alarm"     , isAlarm());
              root.put("time_alarm", getTimeAlarm());

              logger.trace("fc_mnmr getStat() size:"+group_list.size());
              
              return root;
       }
/*
       public static void main(String args[]){
              fc_node mngr=new fc_node();
              String xpath=args[0];

              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+common.ver());
              mngr.init();
              logger.info("RUN LITTLE.CONTROLSTREAM "+common.ver());
              scheduler runner = new scheduler(10);

              ArrayList<task> _task=mngr.getListTask();
              runner.add(mngr);
              for(int i=0;i<_task.size();i++)runner.add(_task.get(i));
              
              runner.fork();
              //mngr.run();

       }
       */
}
