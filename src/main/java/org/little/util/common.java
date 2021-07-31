package org.little.util;
        
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class common {

    private static final Logger logger = LoggerFactory.getLogger(common.class);
    private String        node_name                      ;
    private String        cfg_filename                   ;
    private Document      doc                            ;
    private Node          node_cfg                       ;

    public  static String  ver(){ return "v"+Version.getVer()+" "+Version.getDate();};

    public common(){clear();}


    public void clear(){
           node_name   = "";
           cfg_filename= "";
           doc         = null;
           node_cfg    = null;
    }

    public boolean loadCFG(String xpath){
           cfg_filename = xpath;
           return loadCFG();
    }
    public String       getDefNodeName(){ return "little";};
    public String       getOldNodeName(){return "<>old<>";}//for Backward compatibility
    public String       getNodeName(){return node_name;}
    public void         setNodeName(String n_name){node_name=n_name;}
    public Node         getNode(){return node_cfg;}

    public void         preinit(){
                        System.setProperty("java.net.preferIPv4Stack","true");
                        System.setProperty("content.types.user.table","content-types.properties"); 
                        logger.trace("Set java property:java.net.preferIPv4Stack=true"); 
    }
    public void         init(){}
    public void         init(Node _node_cfg){node_cfg=_node_cfg;}
    public void         reinit(){ }
    public void         initMBean(){ }

    public boolean loadCFG(){
           DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
           try {
                DocumentBuilder builder;
                builder  = factory.newDocumentBuilder();
                doc      = builder.parse(cfg_filename);
                logger.trace("open doc:"+cfg_filename);
                //doc.getFirstChild().getNodeValue();
                node_cfg = doc.getFirstChild();              
               
                logger.trace("compare node name:"+node_cfg.getNodeName()+" with name:"+getOldNodeName()+" or "+getNodeName()+" or "+getDefNodeName());
               
                if(getOldNodeName().equals(node_cfg.getNodeName())){
                   logger.info("config structure OLD name:"+getOldNodeName());
                   return true;
                }
                if(getNodeName().equals(node_cfg.getNodeName())){
                   logger.trace("config structure name:"+getNodeName());
                   return true;
                }
                if(getDefNodeName().equals(node_cfg.getNodeName())){
                   logger.trace("default config structure name:"+getDefNodeName());
                   logger.trace("seach topic name:"+getNodeName());
                   NodeList glist=node_cfg.getChildNodes();     
                   for(int i=0;i<glist.getLength();i++){
                       Node n=glist.item(i);
                       if(getNodeName().equals(n.getNodeName())){
                          node_cfg=n;
                          logger.trace("set topic name:"+node_cfg.getNodeName());
                          return true;
                       }
                   }
                   logger.error("can't find topic name:"+getNodeName());
                   node_cfg = null;
                   return false;
                }
                logger.error("can't find config structure name:"+getNodeName()+" in config file:"+cfg_filename);
                node_cfg = null;
                return false;
           }catch (Exception e) {
                   logger.error("Could not load xml config file:"+cfg_filename, e);
                   return false;
           }
         //return true;
    }


}

