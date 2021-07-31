package org.little.mq.controlStream;

import org.w3c.dom.Node;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


public interface fc_group {

       public void       clear();
                        
       public void       init(Node n);
                        
       public String     getNodeID();
       public String     getID();
       public String     getRID();

       public void       setID(String id);
       public String     getName();
       public void       setName(String name);

       public int        getActiveFlow();
       public boolean    isAlarm();
       public long       getTimeAlarm();

       public JSONObject getStat();

       public void       work();
       
       public JSONObject setFlag(String flow_id,boolean is_flag);
       public JSONObject setFlagAll(boolean is_flag);

       public JSONObject setChannel(String flow_id,boolean is_channel);
       public JSONObject setChannelAll(boolean is_channel);

       public JSONObject ClearQ(String flow_id,String mngr_id,String q_id);

       public void       close();

       ArrayList<fc_flow> getFlowList();

       public static JSONObject getStat(fc_group grp){

              JSONArray  list=new JSONArray();
              for(int i=0;i<grp.getFlowList().size();i++) {
                  fc_flow flow=grp.getFlowList().get(i);
                  list.put(i,flow.getStat());
              }
              JSONObject root=new JSONObject();
              root.put("type"      , "group");
              root.put("node"      , grp.getNodeID());
              root.put("id"        , grp.getID());
              root.put("rid"       , grp.getRID());
              root.put("state"     , grp.getActiveFlow());
              root.put("alarm"     , grp.isAlarm());
              root.put("time_alarm", grp.getTimeAlarm());
              root.put("name"      , grp.getName());
              root.put("list_flow" , list);
              root.put("size"      , grp.getFlowList().size());

              return root;

       }
       

}
