package org.little.db.kir;
                    
import java.sql.Timestamp;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;

public class loadKIR{
       private static final Logger logger = LoggerFactory.getLogger(loadKIR.class);

       private static connection_pool db=null;
       private String                 querySelect;
       private commonKIR              cfg;
       private listKIR                list;
       private Timestamp              time1;
       private Timestamp              time2;


       
       public loadKIR(commonKIR  _cfg) {
              clear();
              cfg=_cfg;
              list=new listKIR(_cfg.getWorkPath());
              logger.info("create commonArh");
       }
       public loadKIR(commonKIR  _cfg,listKIR  _list) {
              clear();
              cfg=_cfg;
              list=_list;
              logger.info("create commonArh");
       }

       public void clear() {
              querySelect="ERROR";
              cfg        =null;
              list       =null;
       }
       
       public synchronized void open() {

              if(db==null){
                 logger.trace("begin init_db");
                 db=new connection_pool();
                 db.init(cfg.getDB().getDrv(),cfg.getDB().getURL(),cfg.getDB().getUser(),cfg.getDB().getPasswd());
                 logger.trace("init connection_pool db");
              }
              String fields_select ="";
                                   
              querySelect = "SELECT "+fields_select+" FROM "+cfg.getDB().getTableFromKBR()+" F,"+cfg.getDB().getTableFromKBRCOA()+" A,"+cfg.getDB().getTableFromKBRCOD()+" D" ;

       }
       
       public synchronized void close() {
              if(db!=null){try {db.close();} catch (dbExcept e) {}} 
              db=null;
              logger.trace("close db");
       }
 
       private objKIR getAll(query q) throws dbExcept{
               int s=0;
               objKIR obj=new objKIR();
              
               try {
                    obj.setMsgID          (q.Result().getString   ( 2+s)); 
                    obj.setTime          (q.Result().getTimestamp( 7+s)); 
               }
               catch (Exception ex2) {
                       logger.error("setAll ex:"+ex2);
                       throw new dbExcept("setAll",ex2);
               }
               return obj;
       }

       public void load() {
               if(db==null){
                  logger.error("db is not init");
                  return ;
               }
               else{
                  query q=null;
                  time1=new Timestamp(cfg.getStart());
                  time2=new Timestamp(time1.getTime()+cfg.getTimeout());
                  try{
                       q=db.open();
                       logger.trace("open db q_id:"+q.getId());
              
                       q.creatPreSt(querySelect);
                       q.setTimestamp ( 1,time1);
                       q.setTimestamp ( 2,time2);
              
                       q.executeQuery();
                       while(q.isNextResult()) {
                             objKIR obj=getAll(q);
                             list.write(obj);
                       } 
              
                  }catch(dbExcept ex){
                       if(q!=null){
                          logger.error("db q_id:"+q.getId()+" error sql:"+querySelect);
                          logger.error("db q_id:"+q.getId()+" ex:"+ex);
                       }
                       else logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
                  cfg.setStart(time2.getTime());
               }
       }


       public static void main(String args[]){
              commonKIR cfg = new commonKIR();
              String xpath=args[0];
              boolean ret=cfg.loadCFG(xpath);
              cfg.init();
              if(ret==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              listKIR   list=new listKIR(xpath+cfg.getWorkPath());
              loadKIR   load=new loadKIR(cfg,list);

       }

}
