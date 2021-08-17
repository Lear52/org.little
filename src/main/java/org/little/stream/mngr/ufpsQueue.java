package org.little.stream.mngr;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class ufpsQueue {
       private static final Logger logger = LoggerFactory.getLogger(ufpsQueue.class);

       private String                name;
       private TransferQueue<ufpsFrame> queue;
       private int max_size;

       public ufpsQueue(String name,int max_size){
              this.queue = new LinkedTransferQueue<ufpsFrame>();
              this.name=name;
              this.max_size=max_size;
              logger.trace("queue:"+name);
       }

       public String getName() {return name;}

       public boolean put(ufpsFrame msg) throws InterruptedException {if(queue.size()>=max_size)return false; else queue.put(msg); return true;}
       public ufpsFrame get() { return queue.poll(); }

}
