package org.little.stream.mngr;

import org.zeromq.ZMsg;

public class ufpsCMD extends ufpsFrame {

       public ufpsCMD(ufpsFrame f_msg) {
              set(f_msg);
       }

       public ufpsCMD(ZMsg zmsg) {
              setZMsg(zmsg);
       }
       public ufpsCMD(String _node,String _user,String  _queue) {
              setQueue (_queue);
              setUser  (_user);
              isLocal  (false);
              setCreate();
              setID    ("id_empty");
              setNode  (_node);
              setType  (ufpsDef.TYPE_CMD_MSG);
       }


}


