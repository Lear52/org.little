package org.little.web;

import java.security.Principal;

public class BasicPrincipal implements Principal {

       private String name;
       //private int nameType;

       public BasicPrincipal(final String name) {
              this.name = name;
              //nameType=0;

       }
       /*
       public BasicPrincipal(final String name, final int nameType) {
              this.name = name;
              this.nameType=nameType;
       }
       */
       @Override
       public String getName() {return name;}
       
       /*
       public int getNameType() {
           return nameType;
       }
       */
       //public String getRealm() { return ""; }
       
       @Override
       public int hashCode() {
           int result = 31;
           result = 31 * result + name.hashCode();
           return result;
       }
       
       @Override
       public boolean equals(final Object object) {
           if (object == this) {
               return true;
           }
           
           if (!(object instanceof BasicPrincipal)) {
               return false;
           }
           
           return this.hashCode() == object.hashCode();
       }
       
       @Override
       public String toString() { return name; }
}
