

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class deleteCourseServlet
 */
@WebServlet("/deleteCourseServlet")
public class deleteCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public deleteCourseServlet() {
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
			out.println("invalid session");
		}else{
		
		  try (
			    Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
			    Statement stmt = conn.createStatement();
			)
			{
				try {
					String secID = request.getParameter("secID");
					String courseID = request.getParameter("courseID");
					String year = request.getParameter("year");
					String semester = request.getParameter("semester");
					
						 String preString = "delete from takes where ( (id=?) "+
									"AND (course_id=?)"+
									"AND (sec_id=?)"+
									"AND (year='"+year+"')"+
									"AND (semester='"+semester+"')"+
								");";
						 
						 PreparedStatement prestmt = conn.prepareStatement(preString);
							prestmt.setString(2, courseID);
							prestmt.setString(3, secID);
							prestmt.setString(1, request.getSession().getAttribute("UserId").toString());
							prestmt.executeUpdate();
							out.println("Successfully deleted course !!");
					
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


