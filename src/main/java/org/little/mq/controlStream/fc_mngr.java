package org.little.mq.controlStream;
                     
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.run.scheduler;
import org.little.util.run.task;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_mngr  extends task{
       private static final Logger logger = LoggerFactory.getLogger(fc_mngr.class);

       private fc_common           cfg;
       private ArrayList<fc_node>  node_list;
       private ArrayList<task>     task_list;
       private static Object       LOCK=new Object();   
       private String              default_page;   
       private int                 task_timeout;
       public fc_mngr() {
              cfg               =new fc_common();
              node_list         =new ArrayList<fc_node>();
              task_list         =new ArrayList<task>();
              default_page      ="index.jsp";
              task_timeout      =10;
       }
       protected String             getDefaulPage() {return default_page;}
       protected ArrayList<fc_node> getListNode  () {return node_list;}
       public    int                getTimeout   (){return task_timeout;}
       public    ArrayList<task>    getListTask  () {return task_list;       }

       public boolean loadCFG(String xpath){
              return cfg.loadCFG(xpath);
       }
       public void init() {
              logger.info("init node:"+cfg.getNode().getNodeName());
              init(cfg.getNode());
       }
       
       /**
        * parsing configuration global section
        * @param _node_cfg 
        */
       private void init_global(Node _node_cfg) {

               if(_node_cfg==null)return;
              
               logger.info("The configuration node:"+_node_cfg.getNodeName());
               NodeList glist=_node_cfg.getChildNodes();
               if(glist==null) return;
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);
                   if("run_timeout".equalsIgnoreCase(n.getNodeName()) ){           
                      String s=n.getTextContent();try{task_timeout=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set run_timeout:"+s);task_timeout=10;}logger.info("run_timeout:"+task_timeout);
                   }else
                   if("default_page".equalsIgnoreCase(n.getNodeName()) ){           
                      default_page=n.getTextContent();logger.info("default_page:"+default_page);
                   }
               }
       }
       /**
        * parsing configuration  
        * @param _node_cfg
        */
       private void init(Node _node_cfg) {
              if(_node_cfg==null)return;

              node_list         =new ArrayList<fc_node>();
              task_list         =new ArrayList<task>();
              logger.info("The configuration node:"+_node_cfg.getNodeName());
              NodeList glist=_node_cfg.getChildNodes();

              if(glist==null) return;

              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("global".equalsIgnoreCase(n.getNodeName()) ){
                     init_global(n);
                  }
                  if("node".equalsIgnoreCase(n.getNodeName()) ){
                     fc_node fc_nd=new fc_node(this);
                     fc_nd.init(n,task_list);
                     node_list.add(fc_nd);
                  }
              }
              
       }
       public void close() {
              if(node_list!=null){
                 for(int i=0;i<node_list.size();i++) {
                     fc_node nd=node_list.get(i);
                     nd.close();
                 }       
                 node_list.clear();
              }
              node_list=null;

       }
       
       /**
         get number active flow

       */
       @Override
       public void work() {
              synchronized(LOCK){
                  for(int i=0;i<node_list.size();i++) {
                      fc_node nd=node_list.get(i);
                      nd.work();
                  }       
              }
       }

       /**
         executed command get state all node
       */
       public JSONObject getStatAll() {
              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              root.put("type", "all");
              for(int i=0;i<node_list.size();i++) {
                  fc_node node=node_list.get(i);
                  list.put(i,node.getStatAll());
              }
              root.put("list_node" , list);

              logger.trace("fc_mnmr getStat() size:"+node_list.size());
              
              return root;
       }
       /**
          executed command get state local group

       */
       public JSONObject getLocalStat() {
              JSONObject root=new JSONObject();
              JSONArray  list=new JSONArray();
              root.put("type", "local state");
              int count=0;
              for(int i=0;i<node_list.size();i++) {
                  fc_node node=node_list.get(i);
                  list.put(count,node.getLocalStat());
                  count++;
              }
              root.put("list_node", list);
              root.put("size"     , count);
             
              logger.trace("fc_mnmr getStat() group_list.size(local):"+count+" group_list.size(all):"+node_list.size());
              
              return root;
       }
       /**
          executed command set flag headbeat 
          node_id
          group_id
          flow_id
          is_flag - true                false
       */
       public JSONObject setFlag(String node_id, String group_id, String flow_id, boolean is_flag) {
              JSONObject root=new JSONObject();
              root.put("type", "flag");
              for(int i=0;i<node_list.size();i++){
                  if(node_list.get(i).getID().equalsIgnoreCase(node_id)) {
                     JSONObject ret=node_list.get(i).setFlag(group_id,flow_id,is_flag); 
                     root.put("resp", ret);
                     break;
                  }
              }
              return root;
       }
       public JSONObject setFlagAll(String node_id, boolean is_flag) {
              JSONObject root=new JSONObject();
              root.put("type", "flag");
              for(int i=0;i<node_list.size();i++){
                  if(node_list.get(i).getID().equalsIgnoreCase(node_id)) {
                     JSONObject ret=node_list.get(i).setFlagAll(is_flag); 
                     root.put("resp", ret);
                     break;
                  }
              }
              return root;
       }
       public JSONObject setChannel(String node_id, String group_id, String flow_id, boolean is_run) {
              JSONObject root=new JSONObject();
              root.put("type", "channel");
              logger.trace("setChannel");
              for(int i=0;i<node_list.size();i++){
                  if(node_list.get(i).getID().equalsIgnoreCase(node_id)) {
                     logger.trace("setChannel:"+node_id);
                     JSONObject ret=node_list.get(i).setChannel(group_id,flow_id,is_run); 
                     root.put("resp", ret);
                     break;
                  }
              }
              logger.trace("setChannel");
              return root;
       }
       public JSONObject setChannelAll(String node_id, boolean is_run) {
              JSONObject root=new JSONObject();
              root.put("type", "channel");
              logger.trace("setChannel");
              for(int i=0;i<node_list.size();i++){
                  if(node_list.get(i).getID().equalsIgnoreCase(node_id)) {
                     logger.trace("setChannel:"+node_id);
                     JSONObject ret=node_list.get(i).setChannelAll(is_run); 
                     root.put("resp", ret);
                     break;
                  }
              }
              logger.trace("setChannel");
              return root;
       }
       /**
          executed command clear queue
          node_id
          group_id
          flow_id
          mngr_id
          q_id

       */
       public JSONObject ClearQ(String node_id, String group_id, String flow_id, String mngr_id, String q_id) {
              JSONObject root=new JSONObject();
              root.put("type", "clear");
              for(int i=0;i<node_list.size();i++){
                  if(node_list.get(i).getID().equalsIgnoreCase(node_id)) {
                     JSONObject ret=node_list.get(i).ClearQ(group_id,flow_id,mngr_id,q_id); 
                     root.put("resp", ret);
                     break;
                  }
              }
              return root;
       }
       public static void main(String args[]){
              fc_mngr _mngr=new fc_mngr();
              String xpath=args[0];

              if(_mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+common.ver());
              _mngr.init();

              logger.info("RUN LITTLE.CONTROLSTREAM "+common.ver());
              scheduler runner = new scheduler(10);

              ArrayList<fc_node> _nd=_mngr.getListNode();
              runner.add(_mngr);
              for(int i=0;i<_nd.size();i++)runner.add(_nd.get(i));
              
              runner.fork();
              //mngr.run();

       }
       
}
