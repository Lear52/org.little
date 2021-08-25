package org.little.stream.mngr;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class ufpsQueue {
       private static final Logger logger = LoggerFactory.getLogger(ufpsQueue.class);

       private String                   name;
       private String                   full_name;
       private TransferQueue<ufpsFrame> queue;
       private int                      max_size;
       private long                     last_put;// time
       private long                     last_get; // time

       public ufpsQueue(String _prefix,String _name,int _max_size){
              this.queue = new LinkedTransferQueue<ufpsFrame>();
              name=_name;
              full_name=_prefix+"_"+name;
              max_size=_max_size;
              logger.trace("queue name:"+name+" ");
              last_get=System.currentTimeMillis();
              last_put=System.currentTimeMillis();
       }

       public String getName() {return name;}
       public String getFullName() {return full_name;}

       public boolean put(ufpsFrame f_msg) throws InterruptedException {
              logger.trace("put ("+full_name+") f_msg  f_msg:"+((f_msg==null)?"OK":"NULL")+" queue.size()>=max_size:"+(queue.size()>=max_size)+" size:"+queue.size());
              if(queue.size()>=max_size)return false; 

              queue.put(f_msg); 

              logger.trace("put ("+full_name+") f_msg size:"+queue.size());
              last_put=System.currentTimeMillis();
              return true;
       }
       public ufpsFrame get() { 
              last_get=System.currentTimeMillis();
              return queue.poll(); 
       }
       
       public ufpsFrame get(int timeout) { 
              last_get=System.currentTimeMillis();
              try {
                  return queue.poll((long)timeout,TimeUnit.MILLISECONDS);
              } catch (InterruptedException e) {
                  return null;
              } 
       }
       
       public long    getLastPut() {return last_put;}
       public long    getLastGet() {return last_get;}
       public int     size()       {return queue.size();}

}
