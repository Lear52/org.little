package org.little.stream.http.auth;
import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.little.auth.authUser;
import org.little.stream.cfg.commonStream;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class BasicAuthUser implements Filter {
       private static final Logger logger = LoggerFactory.getLogger(BasicAuthUser.class);

       private static authUser     auth=null;
       private static commonStream cfg =null;


       @Override
       public void init(FilterConfig filterConfig) throws ServletException {
              
                 logger.trace("init start"+":"+filterConfig.getFilterName()+" : "+filterConfig.getServletContext().getServerInfo());

              if(cfg==null){
                 cfg=new commonStream();
              
                 String xpath=filterConfig.getServletContext().getRealPath("");
                 String _xpath=filterConfig.getInitParameter("config");
                 xpath+=_xpath;

                 if(cfg.loadCFG(xpath)==false){
                    logger.error("error read config file:"+xpath);
                    return;
                 }
                 logger.info("read config file:"+xpath);
                 cfg.init();
                 auth=cfg.getAuth();
                 logger.trace("load:"+filterConfig.getFilterName()+" : "+filterConfig.getServletContext().getServerInfo());
              }
              logger.trace("init end"+":"+filterConfig.getFilterName()+" : "+filterConfig.getServletContext().getServerInfo());
      
       }
      
       @Override
       public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      
           HttpServletRequest  req = (HttpServletRequest)  request;
           HttpServletResponse res = (HttpServletResponse) response;
      
           boolean authorized = false;
           String username=null;
      
           String authHeader = req.getHeader("Authorization");

           logger.trace("start doFilter");

           if (authHeader != null) {
      
               String[] authHeaderSplit = authHeader.split("\\s");
      
               for (int i = 0; i < authHeaderSplit.length; i++) {
                   String token = authHeaderSplit[i];
                   if (token.equalsIgnoreCase("Basic")) {
      
                       String credentials = new String(Base64.getDecoder().decode(authHeaderSplit[i + 1]));
                       int index = credentials.indexOf(":");
                       if (index != -1) {
                           username = credentials.substring(0, index).trim();
                           String password = credentials.substring(index + 1).trim();
                           if(auth==null)authorized = false;
                           else          authorized = auth.checkUser(username,password);
                           logger.trace("user:"+username+" pswd:"+password+" authorized:"+authorized);
                       }
                   }
               }
           }
      
           if (!authorized) {
               res.setHeader("WWW-Authenticate", "Basic realm=\"Insert credentials\"");
               res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
           } else {

               BasicHttpServletRequest new_request = new BasicHttpServletRequest(req,new BasicPrincipal(username));
               chain.doFilter(new_request, res);
           }
           logger.trace("end doFilter");
       }
      
       @Override
       public void destroy() {

       }
}
