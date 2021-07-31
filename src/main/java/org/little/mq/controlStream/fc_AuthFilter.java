package org.little.mq.controlStream;
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

import org.little.auth.authUserXML;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class fc_AuthFilter implements Filter {
       private static final Logger logger = LoggerFactory.getLogger(fc_AuthFilter.class);

       private static authUserXML   auth=null;
       private static fc_commonAuth cfg=null;

       @Override
       public void init(FilterConfig filterConfig) throws ServletException {
              logger.trace("init fc_AuthFilter");
              if(auth==null){
                 String _cfg_fiename=filterConfig.getInitParameter("config");
                 if(_cfg_fiename==null)return;

                 String xpath=filterConfig.getServletContext().getRealPath("")+_cfg_fiename;

                 if(cfg==null)cfg=new fc_commonAuth();
                 logger.trace("set config file:"+xpath);
                 if(cfg.loadCFG(xpath)==false){
                    logger.error("error read config file:"+xpath);
                    return;
                 }

                 logger.trace("load config file:"+xpath);
                 cfg.init();
                 if(cfg.getUserList()==null){
                    logger.error("error get user list");
                 }
                 else auth=new authUserXML("local",cfg.getUserList());
              }
              logger.trace("init fc_AuthFilter ok");
      
       }
      
       @Override
       public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      
           HttpServletRequest req = (HttpServletRequest) request;
           HttpServletResponse res = (HttpServletResponse) response;
      
           boolean authorized = false;
      
           String authHeader = req.getHeader("Authorization");
           if (authHeader != null) {
               logger.trace("authHeader:"+authHeader);
      
               String[] authHeaderSplit = authHeader.split("\\s");
      
               for (int i = 0; i < authHeaderSplit.length; i++) {
                   String token = authHeaderSplit[i];
                   if (token.equalsIgnoreCase("Basic")) {
      
                       String credentials = new String(Base64.getDecoder().decode(authHeaderSplit[i + 1]));
                       int index = credentials.indexOf(":");
                       if (index != -1) {
                           String username = credentials.substring(0, index).trim();
                           String password = credentials.substring(index + 1).trim();
                           if(auth==null)authorized = false;
                           else  authorized = auth.checkUser(username,password);
                           logger.trace("user:"+username+" pswd:"+password+" ret:"+authorized);
                       }
                   }
               }
           }
           else logger.trace("authHeader:null");
      
           if (!authorized) {
               res.setHeader("WWW-Authenticate", "Basic realm=\"Insert credentials\"");
               res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
               logger.trace("HttpServletResponse.SC_UNAUTHORIZED");
           } else {
               chain.doFilter(req, res);
           }
       }
      
       @Override
       public void destroy() {
       }
}