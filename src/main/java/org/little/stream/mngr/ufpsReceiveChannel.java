package org.little.stream.mngr;

import org.little.stream.cfg.commonChannel;
import org.little.stream.zmq.receiveUFPS2ZMQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;
import org.zeromq.ZMsg;

public class ufpsReceiveChannel extends tfork {
       private static final Logger logger = LoggerFactory.getLogger(ufpsReceiveChannel.class);

       private receiveUFPS2ZMQ srv;
       private String          name;
       private ufpsManager     mngr;
       private String          type;
       private String          subtype;
       private String          id;
       
       public ufpsReceiveChannel(ufpsManager mnge,commonChannel cfg){
	      this.type=cfg.getType();
              this.subtype=cfg.getSubType();
              this.id=cfg.getID();
              this.mngr=mnge;
              this.srv =new receiveUFPS2ZMQ(cfg.getLocalBindServer(),cfg.getPort(),cfg.getTimeout());
              this.name=cfg.getName();
       }
       public boolean   isOpen(){return false;}
       public String  getID(){return id;}
       public String  getType(){return type;}
       public String  getSubType(){return subtype;}

       @Override
       public void    run(){
           srv.open();
           logger.trace("open TranferQueue:"+name);
           while(isRun()) {
                 ZMsg z_msg=srv.receive();
                 ufpsFrame msg=new ufpsFrame();
                 if(msg.setZMsg(z_msg))mngr.route(msg);           
           }
           stop();
           srv.close();
           logger.trace("close TranferQueue:"+name);
       
       }

}
