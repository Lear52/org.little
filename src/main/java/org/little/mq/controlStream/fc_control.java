package org.little.mq.controlStream;
        
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.little.mq.mqapi.commonMQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class fc_control {
       private static final Logger logger = LoggerFactory.getLogger(fc_control.class);

       protected commonMQ    cfg;
       private boolean       is_manual;
       private boolean       is_set_flag;

       public static boolean STOP=true;
       public static boolean RUN=false;

       public fc_control(){
              clear();
       }
       protected void   clear() {
    	      cfg=new commonMQ(); 
              is_manual  =false;
              is_set_flag=false;
       }

       public void setState(JSONObject root) {
           isManual(root.getBoolean("is_manual"));
           isFlag  (root.getBoolean("is_flag"  ));
       }
       public JSONObject    getState() {
              JSONObject root=new JSONObject();
              root.put("type"     ,"cntrl");
              root.put("is_manual",isManual());
              root.put("is_flag"  ,isFlag());
              root.put("queue"    ,cfg.getNameQ());
              root.put("mngr"     ,cfg.getNameMngr());

              return root;
       };
       public void controlFlag(boolean is_flag) {
              if(isManual())return;
              isFlag(is_flag);
       }

       protected boolean isManual() {return is_manual;}
       protected void    isManual(boolean is_manual) {this.is_manual = is_manual;}
       protected boolean isFlag() {return is_set_flag;}
       protected void    isFlag(boolean is_set_flag) {this.is_set_flag = is_set_flag;}

       protected JSONObject setFlag(boolean flag) {
                 JSONObject root=new JSONObject();
                 root.put("control", "ERROR");
                 logger.error("can't set local flag for remote control");
                 return root;
       }
       public void init(Node node_cfg) {}
       public    void       work(){}
       public    void       close(){}

}