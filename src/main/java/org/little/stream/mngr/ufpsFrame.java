package org.little.stream.mngr;

import java.nio.charset.Charset;

import org.little.stream.ufps.ufpsMsg;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class ufpsFrame {

       private  ufpsMsg   msg;
       private  String    type;
       private  String    queue;
       private  String    user;
       private  ufpsTime  create;
       private  ufpsTime  arrive;
       private  ufpsTime  receive;
       private  String    node_id; // for http
       private  String    msg_id;  // for http

       public ufpsFrame() {
              clear();
       }
       public ufpsFrame(ZMsg zmsg) {
              clear();
              setZMsg(zmsg);
       }
       public ufpsFrame(String user,String  queue,ufpsMsg msg) {
              this.queue=queue;
              this.msg=msg;
              this.user=user;
       }
       protected void clear() {
              type   ="clr";
              msg    =null; 
              queue  ="def";
              user   ="null";
              node_id=null;
              msg_id =null; 
              create =ufpsTime.empty; 
              arrive =ufpsTime.empty; 
              receive=ufpsTime.empty;
       }
       
       public String   getType   () {return type;  }
       public String   getQueue  () {return queue;  }
       public String   getUser   () {return user;   }
       public ufpsMsg  getMsg    () {return msg;    }
       public ufpsTime getCreate () {return create; }
       public ufpsTime getArrive () {return arrive; }
       public ufpsTime getReceive() {return receive;}
       public String   getNodeID () {return node_id;}
       public String   getMsgID  () {return msg_id; }
       
       public void set(String user,String queue,String buf_msg,String _node_id,String _msg_id) {
              set(user,queue,buf_msg.getBytes(),_node_id,_msg_id);
       }
       public void set(String _user,String _queue,byte[] buf_msg,String _node_id,String _msg_id) {
              create =new ufpsTime();
              arrive =null; 
              receive=null;
              msg    =new ufpsMsg(buf_msg);
              user   =_user;
              queue  =_queue;
       }

       private static Charset charset=ZMQ.CHARSET;
       public ZMsg getZMsg() {
              ZMsg zmsg       =new ZMsg();
              ZFrame _type    =new ZFrame(type);
              ZFrame _queue   =new ZFrame(queue);
              ZFrame _user    =new ZFrame(user);
              ZFrame _id      =new ZFrame(msg_id);
              ZFrame _node    =new ZFrame(node_id);
              ZFrame _create  =new ZFrame(create.getText());
              ZFrame _arrive  =new ZFrame(arrive.getText());
              ZFrame _receive =new ZFrame(receive.getText());
              ZFrame _buf     =null;
              if(type.contentEquals("msg")){
                 _buf     =new ZFrame(msg.getBuf());
              }
              else {
                 _buf     =new ZFrame("");
              }              
              zmsg.push(_buf);
              zmsg.push(_receive);
              zmsg.push(_arrive);
              zmsg.push(_create);
              zmsg.push(_id);
              zmsg.push(_node);
              zmsg.push(_queue);
              zmsg.push(_user);
              zmsg.push(_type);

              return zmsg;
       }

       public boolean setZMsg(ZMsg zmsg) {
              ZFrame _type    =zmsg.pop();
              ZFrame _user    =zmsg.pop();
              ZFrame _queue   =zmsg.pop();
              ZFrame _node    =zmsg.pop();
              ZFrame _id      =zmsg.pop();
              ZFrame _create  =zmsg.pop();
              ZFrame _arrive  =zmsg.pop();
              ZFrame _receive =zmsg.pop();
              ZFrame _buf     =zmsg.pop();

              type  =_type.getString(charset);   _type.destroy();
              queue  =_queue.getString(charset);   _queue.destroy();
              user   =_user.getString(charset);    _user.destroy();
              node_id=_node.getString(charset);    _node.destroy();
              msg_id =_id.getString(charset);      _id.destroy();

              String _time;
              _time  =_create.getString(charset);  _create.destroy();
              create=new ufpsTime(_time);
              _time  =_arrive.getString(charset);  _arrive.destroy();
              arrive=new ufpsTime(_time);
              _time  =_receive.getString(charset); _receive.destroy();
              receive=new ufpsTime(_time);

              if(type.contentEquals("msg")) {
                 byte[] buf =_buf.getData();          
                 msg    =new ufpsMsg(buf);
                 _buf.destroy();
              }
              zmsg.destroy();/**/
              return true;
       }

}


