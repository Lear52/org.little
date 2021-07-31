package org.little.stream;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class server01{

    public static void main(String[] args){
        ZContext context;
        System.out.println("begin");
        try  {
            context = new ZContext();
            // Socket to talk to clients

            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            //ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
            socket.bind("tcp://*:5555");
              long end=System.currentTimeMillis()+1000;
              long count=0;


            while (!Thread.currentThread().isInterrupted()) {

                //System.out.println("Received ...");

                //byte[] reply = socket.recv(0);
                ZMsg msg = ZMsg.recvMsg(socket,10000);

                //System.out.println("Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]" );
                //System.out.println("Received " + ": [" + msg.toString() + "]" );
                //String response = "world";
                msg.duplicate().send(socket);
                count++;
                msg.destroy();
                //byte[] buf=reply;
                //byte [] buf=response.getBytes(ZMQ.CHARSET);
                //socket.send(buf, 0);
                //Thread.sleep(50); //  Do some 'work'
                if(System.currentTimeMillis()>end){
                   end=System.currentTimeMillis()+1000;                  
                   System.out.println("Received:" + count);
                   count=0;
                }

            }
        }
        catch(Exception ex){
              System.out.println("Error ex:" +ex);
        }
        System.out.println("end");
    }
}
