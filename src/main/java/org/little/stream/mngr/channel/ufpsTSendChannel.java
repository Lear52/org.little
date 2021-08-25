package org.little.stream.mngr.channel;

import org.little.stream.cfg.commonChannel;
import org.little.stream.mngr.ufpsFrame;
import org.little.stream.zmq.senderUFPS2ZMQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.zeromq.ZMsg;


public class ufpsTSendChannel {
       private static final Logger logger = LoggerFactory.getLogger(ufpsTSendChannel.class);

       private senderUFPS2ZMQ  cln;
       //private ufpsQueue       transfer;
       private String          name;
       private String          type;
       private String          subtype;
       private String          id;
       private boolean         is_open;
       
       public ufpsTSendChannel(commonChannel cfg){
              this.type=cfg.getType();
              this.subtype=cfg.getSubType();
              this.id=cfg.getID();
              this.cln=new senderUFPS2ZMQ(cfg.getClientHost(),cfg.getClientPort(),cfg.getTimeout());
              this.name=cfg.getName();
              logger.trace("create channel type:"+type+" subtype:"+subtype+" id:"+id+" name:"+name);
              is_open     =false;
       }


       public boolean isOpen(){return is_open;}
       public String  getID(){return id;}
       public String  getName(){return name;}
       public String  getType(){return type;}
       public String  getSubType(){return subtype;}
       public boolean open() {
              boolean ret=cln.open();
              logger.trace("open "+toString()+" ret:"+ret);
              is_open=true;
              return ret;
       }
       public void    close() {
              is_open=false;
              cln.close();
              logger.trace("close TranferQueue:"+name);
       }
          
       public void send(ufpsFrame f_msg) {
              ZMsg request=f_msg.getZMsg();
              cln.send(request);
              logger.trace(toString()+" send f_msg:"+f_msg);
              
       }
       public String toString(){return "channel type:"+type+" subtype:"+subtype+" id:"+id+" name:"+name;}


}
