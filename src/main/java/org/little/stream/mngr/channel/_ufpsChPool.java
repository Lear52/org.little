package org.little.stream.mngr.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.little.stream.cfg.commonChannel;
import org.little.stream.cfg.commonStream;
import org.little.stream.mngr.ufpsDef;
import org.little.stream.mngr.ufpsQueue;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;

public class _ufpsChPool extends tfork {
       private static final Logger logger = LoggerFactory.getLogger(_ufpsChPool.class);

       protected ufpsQueue                   transfer;
       protected ArrayList<ufpsTSendChannel> send_channel;
       protected String                      subtype_channel;
       
       public _ufpsChPool(ufpsQueue _transfer,commonStream  _cfg){
              subtype_channel="no_subtype_channel";
              transfer       =_transfer;
              createChannels(_cfg);
              logger.trace("create ufpsTSendPool size:"+send_channel.size());
       }
       public void open(){
              for(int i=0;i<send_channel.size();i++) {
                  ufpsTSendChannel ch = send_channel.get(i);
                  ch.open();
                  logger.trace("open ufpsTSendChannel("+i+") "+ch);
              }
       }
       public void close(){
              for(int i=0;i<send_channel.size();i++) {
                  ufpsTSendChannel ch = send_channel.get(i);
                  ch.close();
              }
       }

       @Override
       public void run(){
              logger.trace("begin ufpsTSendPool channels:"+send_channel.size());
              open();
              {

              }
              close();
              logger.trace("end ufpsTSendPool");

       }

       private boolean createChannels(commonStream  cfg) {
               logger.trace("All channels:"+cfg.getChannels().size());
               send_channel=new ArrayList<ufpsTSendChannel>();

               for(int i=0;i<cfg.getChannels().size();i++) {
                   commonChannel ch = cfg.getChannels().get(i);
                   if(ufpsDef.TYPE_CHANNEL_CLIENT.equalsIgnoreCase(ch.getType()) && subtype_channel.equalsIgnoreCase(ch.getSubType())){
                      ufpsTSendChannel channel = new ufpsTSendChannel(ch);
                      send_channel.add(channel);
                      continue;
                   } 
               }
               logger.trace("send trans channels:"+send_channel.size());
               Collections.sort(send_channel, new Comparator<ufpsTSendChannel>() { 
                           @Override 
                           public int compare(ufpsTSendChannel lhs, ufpsTSendChannel rhs) { 
                                 // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending 
                           return lhs.getID().compareToIgnoreCase(rhs.getID()); 
                           } 
              });
              logger.trace("send trans channels:"+send_channel.size());

              return true;
       }

}
