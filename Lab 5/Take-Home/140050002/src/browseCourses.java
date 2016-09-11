

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

import javax.json.*;
import java.lang.Object;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Servlet implementation class browseCourses
 */
@WebServlet("/browseCourses")
public class browseCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public browseCourses() {
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
		
		out.println("<html><head><title>browseCourse</title>"
						+ "<script src='https://code.jquery.com/jquery-1.12.4.js'></script>"
						+"<script src='https://code.jquery.com/ui/1.12.0/jquery-ui.js'></script>"
						+"<style>"
						+"table {"
						    +"font-family: arial, sans-serif;"
						    +"border-collapse: collapse;"
						    +"width: 100%;"
						+"}"

						+"td, th {"
						    +"border: 1px solid #dddddd;"
						    +"text-align: left;"
						    +"padding: 8px;"
						+"}"

						+"tr:nth-child(even) {"
						    +"background-color: #dddddd;"
						+"}"
						+"</style>"
						+"<script>"
						+"$(document).ready(function(){"
						+ "$('#selectDept1').change(function(){"
						+"$.ajax({"
							+"url: 'browseCourses',"
							+"type: 'POST',"
							+"data: {deptName : $(this).val()},"
							+"success: function (data) {"
										+ "var courseIDArray = $.parseJSON(data).courseIDArray;"
										+ "var titleArray = $.parseJSON(data).titleArray;"
										+ "var creditsArray = $.parseJSON(data).creditsArray;"
										+ "$('#deptTable').html('');"
										+"$('#deptTable').append('<tr><th>course_id</th><th>title</th><th>credits</th></tr>');"
										+ "courseIDArray.forEach(function(v,i){"
											+"$('#deptTable').append('<tr><td>'+v+'</td><td>'+titleArray[i]+'</td><td>'+creditsArray[i]+'</td></tr>');"
										+ "});"
							+"},"
					+"});"
					    +"});"
					+"});"
					+"</script>"
					+ "</head>");
		
		if (session == null) {
			out.println("<body><br> <h3> ERROR : invalid session</h3><hr><p>You must be <a href='login.html'>logged in</a></body></html>");
		}else{
		
		out.println("<body><br> <h3> Select the department name. </h3><hr><br>");
		
		
		  try (
			    Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
			    Statement stmt = conn.createStatement();
			)
			{
				try {
					 String preString = "select dept_name from department";
					 
						 PreparedStatement prestmt = conn.prepareStatement(preString);
						 
						 
						ResultSet rset = prestmt.executeQuery();
					
					
					out.println("<select id='selectDept1'>"
									+ "<option value=''>None</option>");
					while (rset.next()) { out.println("<option value='"+rset.getString(1)+"'> "+rset.getString(1)+"</option>"); }
					out.println("</select>");
					out.println("<br><br><table id='deptTable'></table>");
					out.println("<br><br><a href='userhome.html'> User Home </a>"
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
					
					String deptName = request.getParameter("deptName");
					 String preString = "select course_id, title, credits "
								 		+ "from course "
								 		+ "where dept_name = ?";
					 PreparedStatement prestmt = conn.prepareStatement(preString);
					 prestmt.setString(1, deptName);
						 
						 
						ResultSet rset = prestmt.executeQuery();
					
						JsonArrayBuilder courseIDArray = Json.createArrayBuilder();
						JsonArrayBuilder titleArray = Json.createArrayBuilder();
						JsonArrayBuilder creditsArray = Json.createArrayBuilder();
						
						
						
//					
					while (rset.next()) {
						courseIDArray.add(rset.getString(1));
						titleArray.add(rset.getString(2));
						creditsArray.add(rset.getString(3));
					}
					
					out.println(Json.createObjectBuilder().add("courseIDArray",courseIDArray).add("titleArray", titleArray).add("creditsArray", creditsArray).build().toString());

					
					
				} catch ( SQLException sqle) {
					System.out.println("SQL erro : " + sqle);
				}
				
				
			}
			catch (Exception sqle)
			{ System.out.println("Exception : " + sqle); }
		}
		
		//doGet(request, response);
		
		
	}

}
