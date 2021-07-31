package org.little.stream;
       
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
             
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class senderUFPS2ZMQ {
       private static final Logger logger = LoggerFactory.getLogger(senderUFPS2ZMQ.class);
       private String     destinationName;
       private String     destinationURL;
       private ZContext   ctx;
       private ZMQ.Socket client;
       private long       timeout;
       private ZMQ.Poller poller;

       //private clientAPI session;
       
       public senderUFPS2ZMQ() {
              clear();
       }

       public void clear() {
              destinationName=null;
              destinationURL =null;
              ctx            =null;   
              client         =null;
              poller         =null;
       }
       
       public void open() {
              timeout        =25000;
              destinationURL ="tcp://localhost:5555";
              destinationName="test_queue";

              ctx    = new ZContext();
              client = ctx.createSocket(SocketType.REQ);
              client.connect(destinationURL);
              int size_poller=1;
              poller = ctx.createPoller(size_poller);
              poller.register(client, ZMQ.Poller.POLLIN);
       }
       public void close(){
              if(poller!=null){
                 poller.close();
                 poller=null;
              }
               if(client!=null){
                  client.close();
                  client=null;
              }
              if(ctx!=null){
                 ctx.destroy();
                 ctx=null;
              }
       }
       
       public void run(String txt_msg) {
              ZMsg msg = new ZMsg();
              msg.add(txt_msg);
              
              ZMsg reply = send(destinationName, msg);

              if(reply != null) {
                 ZFrame status = reply.pop();
                 if(status.streq("200")) {
                    status.destroy();
                    logger.trace("OK");
                 }
                 else{
                    status.destroy();
                    logger.trace("ERROR");
                 }
                 reply.destroy();
              }
       }

       
       private ZMsg send(String queue, ZMsg request){

               logger.trace("send cln 0");

               request.push(new ZFrame(queue));
               request.push(new ZFrame("MDPC01".getBytes(ZMQ.CHARSET)));

               ZMsg reply = null;

               while(!Thread.currentThread().isInterrupted()) {
                     try{
                         request.duplicate().send(client);
                        
                         while(true){
                               if (poller.poll(timeout) == -1) break; // Interrupted
                               //logger.trace("send cln 3");
                               if(poller.pollin(0)) {
                                   logger.trace("send cln 4 poling ok");
                              
                                   ZMsg msg = ZMsg.recvMsg(client);
                                   // Don't try to handle errors, just assert noisily
                                   /*
                                   if(msg.size() < 3){
                                      logger.error("msg.size() < 3");
                                      break;
                                   }
                               
                                   ZFrame header = msg.pop();
                                   if("MDPC01".equals(header.toString())){
                                      logger.error("no MDPC01");
                                      break;
                                   }
                                   header.destroy();
                               
                                   ZFrame replyService = msg.pop();
                                   if(queue.equals(replyService.toString())){
                                      logger.error("no queue!="+queue);
                                      break;
                                   }
                                   replyService.destroy();
                                   */
                                   reply = msg;
                                   break;
                               }
                         }
                         //poller.close();
                     }
                     catch(Exception ex){
                        logger.error("ex:"+new Except("send msg",ex));
                     }
                     break;
               }
               request.destroy();
               return reply;
       }




       public static void main(String[] args) {
              senderUFPS2ZMQ sender=new senderUFPS2ZMQ();
              logger.trace("start cln");
              sender.open();
              logger.trace("open cln");
              for(int i=0;i<100000;i++){
                   logger.trace("send i:"+i);
                   sender.run("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                   logger.trace("send i:"+i+" ok");
                   //try{Thread.sleep(100);}catch(Exception e){}
              }
              logger.trace("stop cln");
              sender.close();
       }
}
