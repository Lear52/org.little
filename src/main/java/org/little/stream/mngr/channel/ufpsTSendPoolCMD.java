package org.little.stream.mngr.channel;

import org.little.stream.cfg.commonStream;
import org.little.stream.mngr.ufpsFrame;
import org.little.stream.mngr.ufpsQueue;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class ufpsTSendPoolCMD extends _ufpsChPool {
       private static final Logger logger = LoggerFactory.getLogger(ufpsTSendPoolData.class);

       
       public ufpsTSendPoolCMD(ufpsQueue _command,commonStream  _cfg){
              super(_command,_cfg);
              subtype_channel="local";
              logger.trace("create ufpsTSendPool size:"+send_channel.size());
       }

       @Override
       public void    run(){
              logger.trace("begin ufpsTSendPoolCMD channels:"+send_channel.size());
              open();

              while(isRun()) {
                   //logger.trace(" transfer("+transfer.getFullName()+") size:"+transfer.size());
                   ufpsFrame f_msg = transfer.get();
                   //logger.trace("get f_msg:"+((f_msg==null)?"null":"OK")+" transfer("+transfer.getFullName()+") size:"+transfer.size());
                   if(f_msg!=null) {
                      logger.trace("begin send f_msg:"+f_msg.toString());

                      for(int i=0;i<send_channel.size();i++) {
                          ufpsTSendChannel ch = send_channel.get(i);
                          if(ch.isOpen()==false)continue;
                          ch.send(f_msg);
                      }

                   }
                   else{
                     delayMs(100);
                   }
              }
              close();
              logger.trace("end ufpsTSendPoolCMD");

       }


}
