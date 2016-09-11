

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class coursesTaken
 */
@WebServlet("/coursesTaken")
public class coursesTaken extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public coursesTaken() {
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
					String year = request.getParameter("year");
					String semester = request.getParameter("semester");
					String preString = "select course_id, sec_id, title, dept_name, credits "+
							"from takes NATURAL JOIN course "+
							"where ( (id = ?) "
									+ "AND (semester = '"+semester+"') "
									+ "AND (year ='"+year+"') "
									+ ");";
					 PreparedStatement prestmt = conn.prepareStatement(preString);
					 prestmt.setString(1, request.getSession().getAttribute("UserId").toString());
					 
						 
						ResultSet rset = prestmt.executeQuery();
					
						JsonArrayBuilder courseIDArray = Json.createArrayBuilder();
						JsonArrayBuilder titleArray = Json.createArrayBuilder();
						JsonArrayBuilder creditsArray = Json.createArrayBuilder();
						JsonArrayBuilder deptNameArray = Json.createArrayBuilder();
						JsonArrayBuilder secIDArray = Json.createArrayBuilder();
						
						
						
//					
					while (rset.next()) {
						courseIDArray.add(rset.getString(1));
						secIDArray.add(rset.getString(2));
						deptNameArray.add(rset.getString(3));
						titleArray.add(rset.getString(4));
						creditsArray.add(rset.getString(5));
					}
					
					out.println(Json.createObjectBuilder().add("courseIDArray",courseIDArray).add("titleArray", titleArray).add("creditsArray", creditsArray).add("secIDArray", secIDArray).add("deptNameArray", deptNameArray).build().toString());

					
					
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
