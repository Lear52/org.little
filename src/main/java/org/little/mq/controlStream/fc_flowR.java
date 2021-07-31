package org.little.mq.controlStream;

import org.json.JSONArray;
import org.json.JSONObject;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;

public class fc_flowR extends fc_flow{
       private static final Logger logger = LoggerFactory.getLogger(fc_flowR.class);

       @Override
       public JSONObject getStat(){
 	      JSONObject root= _getStat();  	   
              logger.trace("fc_flowR getStat():"+root);
              return root;
       }


       @Override
       public void work() {}

       @Override
       protected void setState(JSONObject root) {

                 logger.trace("setStat json:"+root); 

                 clear();

                 try{
                    String  type=root.getString("type");

                    if("flow".equalsIgnoreCase(type)==false){
                       logger.trace("setStat error type ("+type+"!=flow) for json:"+root); 
                       return;
                    }

                    setID  (root.getString("id"));
                    setName(root.getString("name"));
                    isAlarm(root.getBoolean("alarm"));

                    JSONArray  list=root.getJSONArray("list_queue");

                    for(int i=0;i<list.length();i++) {
                        fc_Q q=new fc_Q();
                        JSONObject q_json=list.getJSONObject(i);
                        q.setState(q_json);
                        getFlow().add(q);
                    }
                    JSONObject f_ctrl=root.getJSONObject("control");
                    if(f_ctrl!=null)flow_contrl.setState(f_ctrl);

                    JSONObject f_chnl=root.getJSONObject("channel");
                    if(f_chnl!=null)flow_channel.setState(f_chnl);

                 }
                 catch(Exception e){
                       logger.trace("setStat json:"+root+" ex:"+new Except("",e)); 
                 }
       }
       @Override
       protected JSONObject setFlag(boolean flag) {
                 flow_contrl.controlFlag(flag);
                 JSONObject root=new JSONObject();
                 root.put("id", getID());
                 root.put("name", getName());
                 root.put("control", flow_contrl.getState());
                 return root;
       }
       @Override
       protected JSONObject      setChannel(boolean is_run) {
                 flow_channel.controlChannel(is_run);
                 JSONObject root=new JSONObject();
                 root.put("id", getID());
                 root.put("name", getName());
                 root.put("channel", flow_channel.getState());
                 return root;
       }
 
       @Override
       public void init(Node n) {}

       
}