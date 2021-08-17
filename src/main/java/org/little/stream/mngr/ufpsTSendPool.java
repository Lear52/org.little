package org.little.stream.mngr;

import java.util.ArrayList;

import org.little.stream.cfg.commonChannel;
import org.little.stream.cfg.commonStream;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;

public class ufpsTSendPool extends tfork {
       private static final Logger logger = LoggerFactory.getLogger(ufpsTSendPool.class);

       private ufpsQueue                 transfer;
       private ArrayList<ufpsTSendQueue> send_channel;
       
       public ufpsTSendPool(ufpsQueue transfer,commonStream  cfg){
              this.transfer      =transfer;
              createChannels(cfg);
              logger.trace("create ufpsTSendPool size:"+send_channel.size());
       }

       @Override
       public void    run(){




       }

       private boolean createChannels(commonStream  cfg) {
               send_channel=new ArrayList<ufpsTSendQueue>();
               for(int i=0;i<cfg.getChannels().size();i++) {
                   commonChannel ch = cfg.getChannels().get(i);
                   if("client".equalsIgnoreCase(ch.getType()) && "trans".equalsIgnoreCase(ch.getSubType())){
                      ufpsTSendQueue channel = new ufpsTSendQueue(null,ch);
                      send_channel.add(channel);
                      break;
                   } 
               }
               return true;
       }

}
