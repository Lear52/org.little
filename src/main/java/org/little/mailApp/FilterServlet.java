package org.little.mailApp;
       
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This servlet is used to determine whether the user is logged in before
 * forwarding the request to the selected URL.
 */
public class FilterServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8813396427860061521L;

	/**
     * This method handles the "POST" submission from two forms: the
     * login form and the message compose form.
     */
    public void doPost(HttpServletRequest request,HttpServletResponse  response) throws IOException, ServletException {

       String servletPath = request.getServletPath();
       servletPath = servletPath.concat(".jsp");
       getServletConfig().getServletContext().
       getRequestDispatcher("/" + servletPath).forward(request, response);
    }

    /**
     * This method handles the GET requests from the client.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse  response)throws IOException, ServletException {
       // check to be sure we're still logged in 
       // before forwarding the request.
       HttpSession session = request.getSession();
       MailUserBean mailuser = (MailUserBean)session.getAttribute("mailuser");
       String servletPath = request.getServletPath();
       servletPath = servletPath.concat(".jsp");
       
       if (mailuser.isLoggedIn())getServletConfig().getServletContext().getRequestDispatcher("/" + servletPath).forward(request, response);
       else getServletConfig().getServletContext().getRequestDispatcher("/index.html").forward(request, response);
    }
}

