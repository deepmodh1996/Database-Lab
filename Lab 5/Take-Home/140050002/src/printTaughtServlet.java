

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

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
		HttpSession session = request.getSession(false);
		if (session == null) {
			out.println("<html><body><br> <h3> ERROR : invalid session</h3><hr><p>You must be <a href='login.html'>logged in</a></body></html>");
		}
		else{
		out.println("<html><body><br> <h3>Course id of All courses <b>Taught</b> by "+request.getSession().getAttribute("UserId")+"</h3><hr><br>");
		

		  try (
			    Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
			    Statement stmt = conn.createStatement();
			)
			{
				try {
					
					 String preString = "select course_id "+
								"from teaches "+
								"where id = ?;";
						 PreparedStatement prestmt = conn.prepareStatement(preString);
						 prestmt.setString(1, request.getSession().getAttribute("UserId").toString());
						 
						 
						ResultSet rset = prestmt.executeQuery();
					out.println("<ol type='1'>");
					while (rset.next()) { out.println("<li>"+rset.getString(1)+"</li>"); }
					out.println("</ol>"
							+ "<a href='userhome.html'> User Home</a>"
							+ "</body></html>");
					
					
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
