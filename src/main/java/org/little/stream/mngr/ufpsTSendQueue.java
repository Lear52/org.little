package org.little.stream.mngr;

import org.little.stream.cfg.commonChannel;
import org.little.stream.zmq.senderUFPS2ZMQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;

public class ufpsTSendQueue  extends tfork {
       private static final Logger logger = LoggerFactory.getLogger(ufpsTSendQueue.class);

       private senderUFPS2ZMQ  cln;
       private ufpsQueue       transfer;
       private String          name;
       private String          type;
       private String          subtype;
       private String          id;
       
       public ufpsTSendQueue(ufpsManager mngr,commonChannel cfg){
	      this.type=cfg.getType();
              this.subtype=cfg.getSubType();
              this.id=cfg.getID();
              this.cln=new senderUFPS2ZMQ(cfg.getClientHost(),cfg.getClientPort(),cfg.getTimeout());
              this.name=cfg.getName();
              this.transfer=mngr.getTQueue();/**/
       }

       public ufpsQueue   getQueue(){return transfer;} 

       public boolean   isOpen(){return false;}
       public String  getID(){return id;}
       public String  getType(){return type;}
       public String  getSubType(){return subtype;}

       @Override
       public void    run(){
           cln.open();
           logger.trace("open TranferQueue:"+name);

           stop();
           cln.close();
           logger.trace("close TranferQueue:"+name);
       }

}
