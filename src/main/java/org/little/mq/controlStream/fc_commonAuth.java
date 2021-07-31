package org.little.mq.controlStream;
       
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_commonAuth  extends common{
       private static final Logger logger = LoggerFactory.getLogger(fc_commonAuth.class);
       private Node   user_list;

       public fc_commonAuth() {
              setNodeName("littlestat");
              user_list=null;
              logger.trace("fc_commonAuth:littlestat");
       }

       @Override
       public void init() {
            logger.trace("init():"+this.getNode());
            init(this.getNode());
       }
       @Override
       public void init(Node _node_cfg) {
            //init(this.getNode());
            logger.info("init configuration node:"+_node_cfg.getNodeName());
            NodeList glist=_node_cfg.getChildNodes();

            if(glist==null) return;

            for(int i=0;i<glist.getLength();i++){
                Node n=glist.item(i);
                if("auth".equalsIgnoreCase(n.getNodeName()) ){
                   NodeList alist=n.getChildNodes();
                   //---------------------------------------------------
                   for(int a=0;a<alist.getLength();a++){
                       Node nn=alist.item(a);
                       if("user".equalsIgnoreCase(nn.getNodeName()) ){
                          user_list=nn;
                       }
                   }
                   //---------------------------------------------------
                }
            }


       }

       public Node getUserList(){return user_list;}

       public static void main(String args[]){
              fc_commonAuth cfg=new fc_commonAuth();
              String xpath  =args[0];

              if(cfg.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM.AUTH "+ver());
              cfg.init();
              logger.info("RUN LITTLE.CONTROLSTREAM.AUTH "+ver());

       }

}
