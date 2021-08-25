package org.little.stream.zmq;
       
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
       private String     destinationURL;
       private ZContext   ctx;
       private ZMQ.Socket client;
       private long       timeout;
       private ZMQ.Poller poller;

       public senderUFPS2ZMQ() {
              clear();
       }

       public senderUFPS2ZMQ(String clientHost, int clientPort, int _timeout) {
              clear();
              timeout        =_timeout;
              destinationURL ="tcp://"+clientHost+":"+clientPort;
       }

          private void clear() {
              timeout        =25000;
              destinationURL ="tcp://localhost:5555";
              ctx            =null;   
              client         =null;
              poller         =null;
       }
       
       public boolean open() {
              ctx    = new ZContext();
              client = ctx.createSocket(SocketType.REQ);
              client.connect(destinationURL);
              int size_poller=1;
              poller = ctx.createPoller(size_poller);
              poller.register(client, ZMQ.Poller.POLLIN);
              return true;
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
       
       private void test_run(String txt_msg) {
              ZMsg msg = new ZMsg();
              msg.add(txt_msg);
              
              ZMsg reply = send(msg);

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

       
       public ZMsg send( ZMsg request){
               logger.trace("send cln 0");
               ZMsg reply = null;
               while(!Thread.currentThread().isInterrupted()) {
                     try{
                         request.duplicate().send(client);
                         while(true){
                               if (poller.poll(timeout) == -1) break; // Interrupted
                               //logger.trace("send cln 3");
                               if(poller.pollin(0)) {
                                   //logger.trace("send cln 4 poling ok");
                                   ZMsg msg = ZMsg.recvMsg(client);
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
                   sender.test_run("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                   logger.trace("send i:"+i+" ok");
                   //try{Thread.sleep(100);}catch(Exception e){}
              }
              logger.trace("stop cln");
              sender.close();
       }
}
