package org.little.stream.mngr;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class ufpsUser {
       private static final Logger logger = LoggerFactory.getLogger(ufpsUser.class);

       private String       username;
       private ufpsStream   stream;

       public ufpsUser(String username,int max_size){
              this.username=username;
              this.stream  =new ufpsLocalStream("default",max_size);
       }
       public ufpsUser(String username,int max_size,ufpsQueue tranfer){
              this.username=username;
              this.stream  =new ufpsLocalStream("default",max_size,tranfer);
       }

       public String     getUser() {return username;}
       public ufpsStream getStream() {return stream;}
       public ufpsStream getStream(String stream_name) {return stream;}


}
