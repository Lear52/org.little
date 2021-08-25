package org.little.stream.mngr;
import java.util.HashMap;
        
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class ufpsUser {
       private static final Logger logger = LoggerFactory.getLogger(ufpsUser.class);

       private String       username;
       private int          max_size;
       private ufpsQueue    queue;   // user default queue
       private HashMap<String,ufpsQueue>  mngr_queue;

       public ufpsUser(String _username,int _max_size){
              username=_username;
              max_size=_max_size;
              mngr_queue=new HashMap<String,ufpsQueue>();
              queue   =new ufpsQueue(username,ufpsDef.DEF_NAME_IN_QUEUE,_max_size);
              mngr_queue.put(queue.getName(), queue);

       }
       public ufpsUser(String _username,int _max_size,ufpsQueue tranfer){
              max_size=_max_size;
              username=_username;
              mngr_queue=new HashMap<String,ufpsQueue>();
              queue   =new ufpsQueue(username,ufpsDef.DEF_NAME_IN_QUEUE,max_size);
              mngr_queue.put(queue.getName(), queue);
       }

       public String  getUser() {return username;}

       public boolean putQueue(ufpsFrame f_msg) throws InterruptedException {
              boolean ret=queue.put(f_msg);
              logger.trace("put "+ret+" f_msg to queue:"+queue.getName()+" size:"+queue.size());
              return ret;
       }
       private ufpsQueue _getQueue(String queue_name){
               if(queue_name==null)return queue;
               ufpsQueue q=mngr_queue.get(queue_name);
               logger.trace("find queue:"+queue_name+" "+((q!=null)?"Ok":"false"));
               if(q==null){
                  logger.trace("create queue:"+queue_name+ " for user:"+username);
                  q=new ufpsQueue(username,queue_name,max_size);
                  mngr_queue.put(queue.getName(), q);
               }
               return q;
       }
       public boolean putQueue(String queue_name,ufpsFrame f_msg) throws InterruptedException {
               ufpsQueue q=_getQueue(queue_name);
               boolean ret=q.put(f_msg);
               logger.trace("put "+ret+" f_msg to queue:"+q.getName()+" fullname:"+q.getFullName()+" size:"+queue.size());
               return ret;
       }
       
       public ufpsFrame getQueue() { 
              ufpsFrame f_msg=queue.get(); 
              logger.trace("get f_msg from queue:"+queue.getName()+" size:"+queue.size());
              return f_msg;
       }
       public ufpsFrame getQueue(int timeout) { 
              ufpsFrame f_msg=queue.get(timeout); 
              logger.trace("get f_msg from queue:"+queue.getName()+" size:"+queue.size());
              return f_msg;
       }

       public ufpsFrame getQueue(String queue_name) { 
              ufpsQueue q=_getQueue(queue_name);
              ufpsFrame f_msg=q.get(); 
              logger.trace("get f_msg from queue:"+queue.getName()+" size:"+queue.size());
              return f_msg;
       }
       public ufpsFrame getQueue(String queue_name, int timeout) {
              ufpsQueue q=_getQueue(queue_name);
              ufpsFrame f_msg=q.get(timeout); 
              logger.trace("get f_msg from queue:"+queue.getName()+" size:"+queue.size());
              return f_msg;
       }


}
