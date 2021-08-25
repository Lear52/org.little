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
       private  String    frame_msg_id;  // for http
       private  boolean   is_local;
       private  int       http_ret_code;

       public ufpsFrame() {
              clear();
              create=new ufpsTime();
       }
       public ufpsFrame(ZMsg zmsg) {
              clear();
              setZMsg(zmsg);
       }
       public ufpsFrame(String user,String  queue,ufpsMsg msg) {
              clear();
              this.queue=queue;
              this.msg=msg;
              this.user=user;
       }
       protected void clear() {
              frame_msg_id ="empty_frame_msg_id";  
              node_id      ="empty_node_id";
              type         =ufpsDef.TYPE_DATA_MSG;
              queue        ="no_queue";
              user         ="no_user";
              create       =ufpsTime.empty; 
              arrive       =ufpsTime.empty; 
              receive      =ufpsTime.empty;
              msg          =null; 
              is_local     =true;
              http_ret_code=200;
       }
       protected void set(ufpsFrame f) {
              frame_msg_id =  f.frame_msg_id      ;
              node_id      =  f.node_id           ;
              type         =  f.type              ;
              queue        =  f.queue             ;
              user         =  f.user              ;
              create.setTime (f.create.getTime()) ;       
              arrive.setTime (f.arrive.getTime()) ;
              receive.setTime(f.receive.getTime());
              msg          =  f.msg               ;
              is_local     =  f.is_local          ;
              http_ret_code=  f.http_ret_code     ;
       }
       
       public String   getID     () {return frame_msg_id;   }
       public String   getNode   () {return node_id;        }


       public String   getType   () {return type;         }
       public String   getQueue  () {return queue;        }
       public String   getUser   () {return user;         }
       public ufpsMsg  getMsg    () {return msg;          }
       public ufpsTime getCreate () {return create;       }
       public ufpsTime getArrive () {return arrive;       }
       public ufpsTime getReceive() {return receive;      }
       public String   getNodeID () {return node_id;      }
       public String   getMsgID  () {return frame_msg_id; }
       public boolean  isLocal   () {return is_local; }

       
       public void     isLocal   (boolean is) {is_local=is; }
       public void     setQueue  (String q) {queue=q;       }
       public void     setUser   (String u) {user=u;        }
       public void     setType   (String t) {type=t;        }

       public void     setID     (String s) {frame_msg_id=s;}
       public void     setNode   (String s) {node_id     =s;}

       public void     setCreate () {create.setTime(System.currentTimeMillis()); }
       public void     setArrive () {arrive.setTime(System.currentTimeMillis()); }
       public void     setReceive() {receive.setTime(System.currentTimeMillis());}

       public void     setHTTPRetCode(int c) {http_ret_code=c;}
       public int      getHTTPRetCode() {return http_ret_code;}
       
       public void set(String _user,String _queue,String buf_msg,String _node_id,String _frame_msg_id) {
              set(_user,_queue,buf_msg.getBytes(),_node_id,_frame_msg_id);
       }
       public void set(String _user,String _queue,byte[] buf_msg,String _node_id,String _frame_msg_id) {
              create      =new ufpsTime();
              arrive      =null; 
              receive     =null;
              msg         =new ufpsMsg(buf_msg);
              user        =_user;
              queue       =_queue;
              node_id     =_node_id;
              frame_msg_id=_frame_msg_id;
       }

       private static Charset charset=ZMQ.CHARSET;

       public ZMsg getZMsg() {

              ZMsg zmsg       =new ZMsg();
              ZFrame _type    =new ZFrame(type);
              ZFrame _queue   =new ZFrame(queue);
              ZFrame _user    =new ZFrame(user);
              ZFrame _id      =new ZFrame(frame_msg_id);
              ZFrame _node    =new ZFrame(node_id);
              ZFrame _create  =new ZFrame(create.getText());
              ZFrame _arrive  =new ZFrame(arrive.getText());
              ZFrame _receive =new ZFrame(receive.getText());

              ZFrame _buf     =null;
              if(type.contentEquals(ufpsDef.TYPE_DATA_MSG)){
                 _buf=new ZFrame(msg.getBuf());
              }
              else {
                 _buf=new ZFrame("");
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

              type         =_type.getString(charset);    _type.destroy();
              queue        =_queue.getString(charset);   _queue.destroy();
              user         =_user.getString(charset);    _user.destroy();
              node_id      =_node.getString(charset);    _node.destroy();
              frame_msg_id =_id.getString(charset);      _id.destroy();

              String _time;
              _time  =_create.getString(charset);  _create.destroy();   create=new ufpsTime(_time);
              _time  =_arrive.getString(charset);  _arrive.destroy();   arrive=new ufpsTime(_time);
              _time  =_receive.getString(charset); _receive.destroy();  receive=new ufpsTime(_time);

              if(type.contentEquals(ufpsDef.TYPE_DATA_MSG)) {
                 byte[] buf =_buf.getData();          
                 msg    =new ufpsMsg(buf);
                 _buf.destroy();
              }
              else{
                 msg=null;
              }
              zmsg.destroy();/**/
              return true;
       }

       public String  toString(){return
              " type:"        +type        +
              " queue:"       +queue       +
              " user:"        +user        +
              " create:"      +create.getText() +
              " arrive:"      +arrive.getText() +
              " receive:"     +receive.getText()+ 
              " node_id:"     +node_id     +
              " frame_msg_id:"+frame_msg_id+
              " is_local:"    +is_local    +
              " msg:"         +(msg==null?"null":msg.toString())
              ;
       }


}


