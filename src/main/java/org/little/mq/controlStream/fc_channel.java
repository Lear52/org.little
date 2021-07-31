package org.little.mq.controlStream;
        
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.little.mq.mqapi.commonMQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class fc_channel {
       private static final Logger logger = LoggerFactory.getLogger(fc_channel.class);

       protected commonMQ    cfg;
       private   boolean     is_run;
       private   String      name;
       private   String      mngr;

       public static boolean STOP=false;
       public static boolean RUN=true;

       public fc_channel(){
              clear();
       }
       protected void clear() {
    	      cfg   =new commonMQ(); 
              is_run=RUN;
              name  =null;
       }

       public void setState(JSONObject root) {
              try{
                  setName(root.getString("chl"));
                  isRun  (root.getBoolean("is_run"));
                  mngr=root.getString("mngr");
              }
              catch(Exception e){
                   logger.error("mngr:"+cfg.getNameMngr()+" channel:"+getName()+" run:"+isRun()+" ex:"+e);
              }
       }
       public JSONObject    getState() {
              JSONObject root=new JSONObject();

              root.put("type"     ,"channel");
              root.put("is_run"   ,isRun());
              root.put("chl"      ,getName());
              root.put("mngr"     ,cfg.getNameMngr());
              mngr=cfg.getNameMngr();

              return root;
       };
       public void controlChannel(boolean is_run) {
              isRun(is_run);
       }

       protected String  getName(){return name;}
       protected void    setName(String n){name=n;}
       protected boolean isRun  (){return is_run;}
       protected void    isRun  (boolean is_run){this.is_run = is_run;}

       protected JSONObject setChannel(boolean is_run){
                 JSONObject root=new JSONObject();
                 root.put("channel", "ERROR");
                 logger.error("can't set state  mq channel for remote control mngr:"+cfg.getNameMngr()+" channel:"+getName()+" run:"+isRun());

                 return root;
       }
       public void init(Node node_cfg) {}

       public    void       work(){mngr=cfg.getNameMngr();}

       public    void       close(){}


}