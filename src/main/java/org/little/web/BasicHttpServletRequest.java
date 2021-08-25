package org.little.web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;




public class BasicHttpServletRequest extends HttpServletRequestWrapper {
    
      private final transient BasicPrincipal principal;
      
      /**
       * Creates Servlet Request specifying KerberosPrincipal of util.
       * 
       * @param request
       * @param _principal 
       */
      BasicHttpServletRequest(final HttpServletRequest request, final BasicPrincipal _principal) {
          super(request);
          this.principal=_principal;
      }
      
      
      @Override
      public String getAuthType() {
          
          final String authType;
          final String header = this.getHeader("Authorization");
          
          if (null == header) {
              authType = super.getAuthType();
          }  
          else if (header.startsWith("Basic")) {
              authType = "Basic";
              
          } else {
              authType = super.getAuthType();
          }
          
          return authType;
      }
      
      
      @Override
      public String getRemoteUser() {
             if(null == this.principal) {
                return super.getRemoteUser();
             } else {
                final String[] username = this.principal.getName().split("@", 2);
                return username[0];
             }
      }
      
      @Override
      public Principal getUserPrincipal() {
          return this.principal;
      }
      
}
