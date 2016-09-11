

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet implementation class errorRegisterServlet
 */
@WebServlet("/errorRegisterServlet")
public class errorRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public errorRegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);
		if (session == null) {
			out.println("<html><body><p> <h3> ERROR : invalid session</h3><hr><p>You must be <a href='login.html'>logged in</a></body></html>");
		}else{
		
			String errorType = request.getParameter("errorType");
			out.println("<html>"+
						"<head><title>Register</title></head>"+
						"<body>"+
							"<h3> Error in Registretion for user : "+request.getSession().getAttribute("UserId")+"</h3>"+
							"Error is : ");
			if(errorType.equals("1")){out.println("Incorrect input. Course id or section id does not run in current semester.");}
			if(errorType.equals("2")){out.println("Course is already taken by user");}
			if(errorType.equals("3")){out.println("Course is NOT taken by user");}
			
			out.println("<br><br>"+
						"<a href='register'> Register </a> &nbsp &nbsp &nbsp &nbsp <a href='userhome.html'> User Home</a>"+
						"<br></body></html>"
					);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
