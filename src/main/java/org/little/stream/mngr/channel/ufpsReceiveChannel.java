package org.little.stream.mngr.channel;

import org.little.stream.cfg.commonChannel;
import org.little.stream.mngr.ufpsFrame;
import org.little.stream.mngr.ufpsManager;
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
       private boolean         is_open;
       
       public ufpsReceiveChannel(ufpsManager mnge,commonChannel cfg){
              this.type   =cfg.getType();
              this.subtype=cfg.getSubType();
              this.id     =cfg.getID();
              this.mngr   =mnge;
              this.srv    =new receiveUFPS2ZMQ(cfg.getLocalBindServer(),cfg.getPort(),cfg.getTimeout());
              this.name   =cfg.getName();
              is_open     =false;
       }
       public boolean isOpen    (){return is_open;}
       public String  getID     (){return id;}
       public String  getType   (){return type;}
       public String  getSubType(){return subtype;}

       @Override
       public void    run(){
           srv.open();
           is_open=true;
           logger.trace("open ufpsReceiveChannel:"+name);
           while(isRun()) {
                 ZMsg z_msg=srv.receive();
                 ufpsFrame f_msg=new ufpsFrame();
                 if(f_msg.setZMsg(z_msg)){
                    f_msg.isLocal();
                    logger.trace("receive f_msg:"+f_msg);
                    mngr.route(f_msg);           
                    logger.trace("route receive f_msg:"+f_msg);
                 }

           }
           is_open=false;
           stop();
           srv.close();
           logger.trace("close ufpsReceiveChannel:"+name);
       }

}
