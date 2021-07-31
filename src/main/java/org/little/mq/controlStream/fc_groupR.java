package org.little.mq.controlStream;

import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.http.lHttpCLN;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.task;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_groupR  extends task implements fc_group{
       private static final Logger logger = LoggerFactory.getLogger(fc_groupR.class);

       private ArrayList<fc_flow> flow_list;
       private String             url_r_group;
       private String             userURL;
       private String             passwordURL;
       private String             id;
       private String             remote_id;
       private String             name;
       private fc_node            node;

       private int                count_active_flow;
       private boolean            is_alarm; 
       private long               time_alarm; 

       public fc_groupR(fc_node  _node){
              clear();
              node=_node;
       }

       @Override
       public void clear() {
              flow_list  =new ArrayList<fc_flow>();
              url_r_group=null;
              userURL    =null;    
              passwordURL=null;
              id         =null;  
              remote_id  =null;
              name       =null;
              count_active_flow=0;
              is_alarm=false;
              time_alarm=0;
       }
       @Override
       public String             getNodeID()          {return node.getID();}
       @Override
       public String             getID()              {return id;         }
       @Override
       public String             getRID()             {return remote_id;  }
       @Override
       public void               setID(String id)     {this.id = id;      }
       @Override
       public String             getName()            {return name;       }
       @Override
       public void               setName(String name) {this.name = name;  }
       @Override
       public int                getActiveFlow()      {return count_active_flow;}
       @Override
       public boolean            isAlarm() {return is_alarm;};
       @Override
       public long               getTimeAlarm() {return time_alarm;};
       @Override
       public ArrayList<fc_flow> getFlowList(){return flow_list;}

       @Override
       public void work(){

              //logger.trace("begin run group");

              count_active_flow=0;
              is_alarm         =false;
              time_alarm       =0;

              String _url_state=url_r_group+webMngr.url_cmd_base+webMngr.url_i_get_stat+"?node="+getNodeID()+"&group="+getRID()+"&_="+System.currentTimeMillis();
              lHttpCLN cln=null;
              try {cln=new lHttpCLN(_url_state,userURL,passwordURL); } catch (URISyntaxException e) { logger.error("create lHttpCLN("+_url_state+") ex:"+e);return; }

              logger.trace("new lHttpCLN("+_url_state+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_state+")",ex));
                    return;
              }

              try{
                  //logger.trace("getJSON("+_url_state+")");
                 
                  if(root==null)return;
                 
                  JSONArray nlist=root.getJSONArray("list_node");//group 

                  JSONObject _root=nlist.getJSONObject(0);

                  JSONArray glist=_root.getJSONArray("list_group");//group 
                 
                  logger.trace("getJSON("+_url_state+")  size list group json:"+glist.length());
                 
                  if(glist.length()<1){
                     return;
                  }
                 
                  for(int ii=0;ii<glist.length();ii++) {
                      JSONObject groot=glist.getJSONObject(ii);
                      String _rid=groot.getString("id");
                      if(_rid.equalsIgnoreCase(getRID())==false)continue;
                      
                      //----------------------------------------------------------------------------------------------------------
                      JSONArray flist=groot.getJSONArray("list_flow");// list flow
                      //logger.trace("getJSON("+_url_state+") group id:"+getID()+" name:"+getName() +" list flow json:"+flist);
                     
                      for(int i=0;i<flist.length();i++) {
                          fc_flow    flow;
                          if(i>=flow_list.size()){
                             flow=new fc_flowR();
                             flow_list.add(flow);
                          }
                          else{
                             flow=flow_list.get(i);
                          }
                          
                          JSONObject f_json=flist.getJSONObject(i);
                          flow.setState(f_json);
                          flow.work();
                      }
                      //----------------------------------------------------------------------------------------------------------
                      count_active_flow=groot.getInt("state");
                      is_alarm         =groot.getBoolean("alarm"); 
                      time_alarm       =groot.getLong("time_alarm");  
                  }
              } 
              catch (Exception ex1) {
                    logger.error("ex:"+new Except("parse json:"+root,ex1));
                    return;
              }

              //logger.trace("end run group id:"+getID()+" name:"+getName());
              
       }
       @Override
       public JSONObject setFlag(String flow_id, boolean is_flag) {
              String _url_control=url_r_group+webMngr.url_cmd_base+webMngr.url_i_set_flag+"?node="+getNodeID()+"&group="+getRID()+"&flow="+flow_id+"&state="+is_flag+"&_="+System.currentTimeMillis();

              lHttpCLN cln=null;
              try {cln=new lHttpCLN(_url_control,userURL,passwordURL); } catch (URISyntaxException e) { logger.error("create lHttpCLN("+_url_control+") ex:"+e);return null; }

              logger.trace("new lHttpCLN("+_url_control+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_control+")",ex));
                    return null;
              }

              logger.trace("getJSON("+_url_control+")");

              if(root==null)return null;

              logger.trace("setFlag("+_url_control+" group id:"+getID()+" flow:"+flow_id +" flag:"+is_flag+") ret:"+root);

              //logger.trace("end setFlag groupR id:"+getID()+" name:"+getName());

              return root;
       }
       @Override
       public JSONObject setFlagAll(boolean is_flag) {
              String _url_control=url_r_group+webMngr.url_cmd_base+webMngr.url_i_set_flag_all+"?node="+getNodeID()+"&group="+getRID()+"&state="+is_flag+"&_="+System.currentTimeMillis();


              lHttpCLN cln=null;
              try {cln=new lHttpCLN(_url_control,userURL,passwordURL); } catch (URISyntaxException e) { logger.error("create lHttpCLN("+_url_control+") ex:"+e);return null; }

              logger.trace("new lHttpCLN("+_url_control+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_control+")",ex));
                    return null;
              }

              logger.trace("getJSON("+_url_control+")");

              if(root==null)return null;

              logger.trace("setFlagAll("+_url_control+" group id:"+getID() +" flag:"+is_flag+") ret:"+root);

              //logger.trace("end setFlag groupR id:"+getID()+" name:"+getName());

              return root;
       }
       @Override
       public JSONObject setChannel(String flow_id, boolean is_run) {
              String _url_control=url_r_group+webMngr.url_cmd_base+webMngr.url_i_exec_chl+"?node="+getNodeID()+"&group="+getRID()+"&flow="+flow_id+"&state="+is_run+"&_="+System.currentTimeMillis();

              lHttpCLN cln=null;
              try {cln=new lHttpCLN(_url_control,userURL,passwordURL); } catch (URISyntaxException e) { logger.error("create lHttpCLN("+_url_control+") ex:"+e);return null; }

              logger.trace("new lHttpCLN("+_url_control+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_control+")",ex));
                    return null;
              }

              logger.trace("getJSON("+_url_control+")");

              if(root==null)return null;

              logger.trace("setChannel("+_url_control+" group id:"+getID()+" flow:"+flow_id +" is_run:"+is_run+") ret:"+root);

              //logger.trace("end setChannel groupR id:"+getID()+" name:"+getName());

              return root;
       }
       @Override
       public JSONObject setChannelAll(boolean is_run) {
              String _url_control=url_r_group+webMngr.url_cmd_base+webMngr.url_i_exec_chl_all+"?node="+getNodeID()+"&group="+getRID()+"&state="+is_run+"&_="+System.currentTimeMillis();

              lHttpCLN cln=null;
              try {cln=new lHttpCLN(_url_control,userURL,passwordURL); } catch (URISyntaxException e) { logger.error("create lHttpCLN("+_url_control+") ex:"+e);return null; }

              logger.trace("new lHttpCLN("+_url_control+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_control+")",ex));
                    return null;
              }

              logger.trace("getJSON("+_url_control+")");

              if(root==null)return null;

              logger.trace("setChannelAll("+_url_control+" group id:"+getID() +" is_run:"+is_run+") ret:"+root);

              //logger.trace("end setChannelAll groupR id:"+getID()+" name:"+getName());

              return root;
       }
       @Override
       public JSONObject ClearQ(String flow_id,String mngr_id,String q_id){
              String _url_control=url_r_group+webMngr.url_cmd_base+webMngr.url_i_exec_clr+"?node="+getNodeID()+"&group="+getRID()+"&mngr="+mngr_id+"&q="+q_id+"&_="+System.currentTimeMillis();

              lHttpCLN cln=null;
              try {cln=new lHttpCLN(_url_control,userURL,passwordURL); } catch (URISyntaxException e) { logger.error("create lHttpCLN("+_url_control+") ex:"+e);return null; }

              logger.trace("new lHttpCLN("+_url_control+")");

              JSONObject root=null;
              try{
                  root=cln.getJSON();
              } 
              catch (Exception ex) {
                    logger.error("ex:"+new Except("cln.getJSON("+_url_control+")",ex));
                    return null;
              }

              logger.trace("getJSON("+_url_control+")");

              if(root==null)return null;


              logger.trace("ClearQ("+_url_control+" group id:"+getID()+" flow:"+flow_id + " ret:"+root);


              //state_group      =1;

              //logger.trace("end ClearQ groupR id:"+getID()+" name:"+getName());

              return root;
       }
       @Override
       public JSONObject getStat(){
              JSONObject root=fc_group.getStat(this);
              logger.trace("fc_groupR getStat() id:"+id+" name:"+name+" size:"+flow_list.size());
              return root;
       }

       @Override
       public void init(Node node_cfg) {
              if(node_cfg==null)return;
              //
              if(node_cfg.getAttributes().getNamedItem("id")==null) {
                 logger.error("The configuration group(r) id:noname");
                 return;
              }
              else{
                 id=node_cfg.getAttributes().getNamedItem("id").getNodeValue();
                 logger.info("group(r) id:"+id);
              }
              //
              if(node_cfg.getAttributes().getNamedItem("name")==null) {
                 name="NO NAME";
                 logger.info("group(r) name:"+name);
              }
              else{
                 name=node_cfg.getAttributes().getNamedItem("name").getNodeValue();
                 logger.info("group(r) name:"+name);
              }
              //
              if(node_cfg.getAttributes().getNamedItem("remote_id")==null) {
                 remote_id=id;
                 logger.info("group(r) remote_id:"+remote_id);
              }
              else{
                   remote_id=node_cfg.getAttributes().getNamedItem("remote_id").getNodeValue();
                   logger.info("group(r) remote_id:"+remote_id);
              }

              //
              flow_list=new ArrayList<fc_flow>();
              logger.trace("The configuration node:"+node_cfg.getNodeName()+" id:"+id+" name:"+name);

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("url_r_group".equalsIgnoreCase(n.getNodeName())){url_r_group=n.getTextContent(); logger.info("group(r) url_r_group:" +url_r_group  );}else
                  if("url_user"  .equalsIgnoreCase(n.getNodeName())){userURL    =n.getTextContent(); logger.info("group(r) url_user:"  +userURL    );}else
                  if("url_passwd".equalsIgnoreCase(n.getNodeName())){passwordURL=n.getTextContent(); logger.info("group(r) url_passwd:"+passwordURL);}
                  if("remote_id" .equalsIgnoreCase(n.getNodeName())){remote_id=n.getTextContent();   logger.info("group(r) remote_id:" +remote_id);}
              }
              
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

