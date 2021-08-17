package org.little.stream.ufps;

public class ufpsDocRefId {

       private String ed_no       ;
       private String ed_date     ;
       private String ed_autor    ;

       public ufpsDocRefId() {
              clear();
       }
       public void clear() {
              ed_no       =null;
              ed_date     =null;
              ed_autor    =null;
       }

       public void   setEDNO      (String arg){ed_no       =arg;}     
       public void   setEDDate    (String arg){ed_date     =arg;}     
       public void   setEDAutor   (String arg){ed_autor    =arg;}     

       public String getEDNO      (){return ed_no       ;}     
       public String getEDDate    (){return ed_date     ;}     
       public String getEDAutor   (){return ed_autor    ;}     


       public String toString(){
                               return "ref_id "
                               +" ed_no:"+              ((ed_no   ==null)?"":ed_no) 
                               +" ed_date:"+            ((ed_date ==null)?"":ed_date)
                               +" ed_autor:"+           ((ed_autor==null)?"":ed_autor)
                               ;
       }

}



/*
      <props:EDRefID EDNo="302146116" EDDate="2018-08-10" EDAuthor="4522222222"/>

*/