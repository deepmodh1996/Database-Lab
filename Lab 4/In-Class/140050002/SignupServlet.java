

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

/**
 * Servlet implementation class Servelet5
 */
@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignupServlet() {
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
		String name = request.getParameter("name");
		String password = request.getParameter("password");
	    if((name.equals(""))||password.equals("")){ out.println("<html><body> <b><h2>Error !!!</h2></b><p> Can not sign up, name or passward is <b>empty</b></body></html>"); }
	    else{
	    	 
		    try (
				    Connection conn = DriverManager.getConnection(
				    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
				    Statement stmt = conn.createStatement();
				)
				{
					try {
						stmt.executeUpdate( "insert into password values ('"+name+"', '"+password+"');" );
						out.println("<html><body>  <b><h2>Succeful !!!</h2></b><p> Succefully entered name and password.</body></html>");
						
					} catch ( SQLException sqle) {
						System.out.println("SQL erro : " + sqle);
					}
					
					
				}
				catch (Exception sqle)
				{ System.out.println("Exception : " + sqle); }
	
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
