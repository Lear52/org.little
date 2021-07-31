package org.little.stream;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class receiveUFPS2ZMQ {
       private static final Logger logger = LoggerFactory.getLogger(receiveUFPS2ZMQ.class);
       private String       destinationName;
       private String       destinationURL;
       private ZContext     ctx;
       private ZMQ.Socket   server;
       private long         timeout;
       private ZMQ.Poller   poller;
       
       public receiveUFPS2ZMQ() {
              clear();
       }

       public void clear() {
              timeout        =25000;
              destinationName=null; 
              ctx            =null;             
              server         =null;
              poller         =null;
       }
       
       public void open() {
               destinationURL ="tcp://*:5555";
               destinationName="test_queue";
               ctx = new ZContext(1);
               try {
                  server = ctx.createSocket(SocketType.REP);
                  server.bind(destinationURL);
              }
              catch (Exception ex) {
                     logger.error("open sender ex:"+new Except("",ex));
              }
              int size_poller=1;
              poller = ctx.createPoller(size_poller);
              poller.register(server, ZMQ.Poller.POLLIN);
             
       }

       public void close(){
              if(poller!=null){
                 poller.close();
                 poller=null;
              }
              if (server != null) {
                  server.close();
                  server=null;
              }
              if(ctx!=null){
                 ctx.close();
                 ctx=null;
              }
       }
       public ZMsg receive(){
              
               logger.trace("receive() srv");
               while (!Thread.currentThread().isInterrupted()) {
                   //logger.trace("receive.poll 1");
                     if(poller.poll(timeout) == -1)continue; // Interrupted
                     //logger.trace("receive.poll 2");
                     if (poller.pollin(0)) {
                         //logger.trace("receive.poll 3");
                         ZMsg msg = ZMsg.recvMsg(server);
                         //if (msg == null)break; // Interrupted
                         if (msg == null)continue; // Interrupted
                         msg.duplicate().send(server);
                         return msg;
                     }
               }
              
               if(Thread.currentThread().isInterrupted())logger.trace("W: interrupt received, killing worker\n");
              
               return null;
        }
       
       public void run() {
              long end=System.currentTimeMillis()+1000;
              long count=0;
              while (!Thread.currentThread().isInterrupted()) {
                    //System.out.println("Received ....");
                    ZMsg msg = receive();
                    count++;
                    //System.out.println("Received " + msg);
                    msg.destroy();
                    if(System.currentTimeMillis()>end){
                       end=System.currentTimeMillis()+1000;                  
                       System.out.println("Received:" + count);
                       count=0;
                    }

              }
       }

       public static void main(String[] args) {
              receiveUFPS2ZMQ srv=new receiveUFPS2ZMQ();
              logger.trace("start srv");
              srv.open();
              logger.trace("open srv");
              srv.run();
              logger.trace("stop run");
              srv.close();
              logger.trace("stop srv");
       }
}
