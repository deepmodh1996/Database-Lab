

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;
import static java.lang.System.out;

/**
 * Servlet implementation class printTakenServlet
 */
@WebServlet("/printTaughtServlet")
public class printTaughtServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public printTaughtServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.println("<html><body><p> <h3>Course id of All courses <b>Taught</b> by "+request.getSession().getAttribute("UserId")+"</h3><hr><p>");
		

		  try (
			    Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
			    Statement stmt = conn.createStatement();
			)
			{
				try {
					
					ResultSet rset = stmt.executeQuery( "select course_id "+
							"from teaches "+
							"where id = '"+request.getSession().getAttribute("UserId")+"';" );
					out.println("<ol type='1'>");
					while (rset.next()) { out.println("<li>"+rset.getString(1)+"</li>"); }
					out.println("</ol></body></html>");
					
					
				} catch ( SQLException sqle) {
					System.out.println("SQL erro : " + sqle);
				}
				
				
			}
			catch (Exception sqle)
			{ System.out.println("Exception : " + sqle); }
			
				    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
