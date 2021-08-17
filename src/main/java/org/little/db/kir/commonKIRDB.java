package org.little.db.kir;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.commonDB;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonKIRDB extends commonDB{
       private static final Logger logger = LoggerFactory.getLogger(commonKIRDB.class);

       private String      db_table_fromkbr;
       private String      db_table_fromkbr_coa;
       private String      db_table_fromkbr_cod;

       private String      db_table_tokbr;
       private String      db_table_tokbr_coa;
       private String      db_table_tokbr_cod;


       public commonKIRDB() {
              clear();
              logger.info("create object commonKIRDB");
       }

       public void clear() {
              super.clear();
       }

       public String    getTableFromKBR   () {return db_table_fromkbr;}
       public String    getTableFromKBRCOA() {return db_table_fromkbr_coa;}
       public String    getTableFromKBRCOD() {return db_table_fromkbr_cod;}
       public String    getTableToKBR     () {return db_table_fromkbr;}
       public String    getTableToKBRCOA  () {return db_table_fromkbr_coa;}
       public String    getTableToKBRCOD  () {return db_table_fromkbr_cod;}


       public void  init(Node _node_cfg) {
              super.init(_node_cfg);

              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());

                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("db_table_fromkbr"    .equalsIgnoreCase(n.getNodeName())){db_table_fromkbr    =n.getTextContent();logger.info("db_table_fromkbr:"    +db_table_fromkbr    );}else
                     if("db_table_fromkbr_coa".equalsIgnoreCase(n.getNodeName())){db_table_fromkbr_coa=n.getTextContent();logger.info("db_table_fromkbr_coa:"+db_table_fromkbr_coa);}else
                     if("db_table_fromkbr_cod".equalsIgnoreCase(n.getNodeName())){db_table_fromkbr_cod=n.getTextContent();logger.info("db_table_fromkbr_cod:"+db_table_fromkbr_cod);}else
                     if("db_table_tokbr"      .equalsIgnoreCase(n.getNodeName())){db_table_tokbr      =n.getTextContent();logger.info("db_table_tokbr:"      +db_table_tokbr      );}else
                     if("db_table_tokbr_coa"  .equalsIgnoreCase(n.getNodeName())){db_table_tokbr_coa  =n.getTextContent();logger.info("db_table_tokbr_coa:"  +db_table_tokbr_coa  );}else
                     if("db_table_tokbr_cod"  .equalsIgnoreCase(n.getNodeName())){db_table_tokbr_cod  =n.getTextContent();logger.info("db_table_tokbr_cod:"  +db_table_tokbr_cod  );}else
                     {}
                }

              }
              else{
                  logger.error("The configuration node:null");
              }                 

	
       }

}

