

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet implementation class Servelet5
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
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
		String id = request.getParameter("name");
		String password = request.getParameter("password");
	    if((id.equals(""))&&password.equals("")){ response.sendRedirect("signup.html"); }
	    else{
	 
		    try (
				    Connection conn = DriverManager.getConnection(
				    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
				    Statement stmt = conn.createStatement();
				)
				{
					try {
						 String preString = "select count(distinct ID)as num "+
								"from password "+
								"where (id=?) and (password=?); ";
						 PreparedStatement prestmt = conn.prepareStatement(preString);
						 prestmt.setString(1, id);
						 prestmt.setString(2, password);
						 
						ResultSet rset = prestmt.executeQuery();
						String number = "";
						while (rset.next()) { number = rset.getString(1); }
						
						if(number.equals("0")){//failed authentication
							response.sendRedirect("errorlogin.html");
						}else{
							request.getSession().setAttribute("UserId", id);
							response.sendRedirect("userhome.html");
						}
						
						
					} catch ( SQLException sqle) {
						System.out.println("SQL erro : " + sqle);
					}
					
					
				}
				catch (Exception sqle)
				{ System.out.println("Exception : " + sqle); }
	
		    
		    
		   //response.sendRedirect("login.html");
		  //out.println("<html><body>Hello world "+id+"<p>"+password+"</body></html>");
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
