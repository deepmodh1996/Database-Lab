// assuming only one year, semester currently going on

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet implementation class register
 */
@WebServlet("/register")
public class register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		try (
			    Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
			    Statement stmt = conn.createStatement();
			)
			{
				try {
		
					PrintWriter out = response.getWriter();
					HttpSession session = request.getSession(false);
					if (session == null) {
						out.println("<html><body><p> <h3> ERROR : invalid session</h3><hr><p>You must be <a href='login.html'>logged in</a></body></html>");
					}else{
					
					// all the courses of student in his department in string
						 String preString2 = "select *"+
								 "from course "+
								 "where course_id IN ((select course_id "+
								 	"from course) "+
								 	"except "+
								 	"( "+
								 	"select course_id "+
								 	"from takes "+ 
								 	"where id = ? "+
								 	")"+
								 ")";
						 	
							 PreparedStatement prestmt2 = conn.prepareStatement(preString2);
							 prestmt2.setString(1, request.getSession().getAttribute("UserId").toString()); 
							 
						ResultSet rset2 = prestmt2.executeQuery();
						
						String availableCourses = "[";
						while (rset2.next()) { availableCourses = availableCourses + " '" + rset2.getString(1) +" "+rset2.getString(2) +" "+rset2.getString(3) +" "+rset2.getString(4)+ "' "+ ","; }
						availableCourses = availableCourses.substring(0, availableCourses.length() - 1);
						availableCourses = availableCourses + "]";
						
					out.println("<html>"+
								"<head>"
								+ "<script src='https://code.jquery.com/jquery-1.12.4.js'></script>"
								+"<script src='https://code.jquery.com/ui/1.12.0/jquery-ui.js'></script>"
								+ "<script>"
									  +"$( function() {"
									    +"var availableCourses2 = "+availableCourses+";"
									    +"$( '#autocompleteHtmlID' ).autocomplete({"
									      +"source: availableCourses2"
									    +"});"
								+"	  } );"
								+"</script>"
								//+"<script src='https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js'></script>"
								+"<script>"
								+"$(document).ready(function(){"
								+ "$('#addButton').click(function(){"
									    +"$.post('register',"
									    +"{secID: $('#addSecID').val(),"
									    + "courseID: $('#autocompleteHtmlID').val(),"
									    + "action: 'add'"
									    + "},"
									    +"function(data, status){"
									        +"alert('Status: ' + status);"
										+" });"
								    +"});"
								+"});"
								+"</script>"
								+ "<title>Register</title></head>"+
								"<body>"+
									"<h3>Registretion for "+request.getSession().getAttribute("UserId")+"</h3>"
								);
						
					// start print all current courses
					
					ResultSet rset = stmt.executeQuery("SELECT year, semester "+
														"FROM   regnDates "+
														"WHERE  now()::timestamp BETWEEN startTS AND endTS;");
					String year = "noYear";
					String semester = "";
					while (rset.next()) { year = rset.getString(1); semester = rset.getString(2); }
					
					if(year.equals("noYear")){out.println("There is no current semester !!! <br> <h2>Can not register</h2><br><br><a href='userhome.html'> User Home </a></body></html>");}
					else{out.println("List of all courses taken :<br><br>course_id, title, dept_name, credits<br>");}
					
					 String preString = "select course_id, sec_id, title, dept_name, credits "+
								"from takes NATURAL JOIN course "+
								"where ( (id = ?) "
										+ "AND (semester = '"+semester+"') "
										+ "AND (year ='"+year+"') "
										+ ");";
						 PreparedStatement prestmt = conn.prepareStatement(preString);
						 prestmt.setString(1, request.getSession().getAttribute("UserId").toString());
						 
						 
					rset = prestmt.executeQuery();
					
					out.println("<ol type='1'>");
					while (rset.next()) { out.println("<li>"+rset.getString(1)+", "+rset.getString(2)+", "+rset.getString(3)+", "+rset.getString(4)+", "+rset.getString(5)+"</li>"); }
					out.println("</ol>");
					// done printing of all courses
					
					out.println("<div>"
//							+ "<div style = 'float:left'>"
							+ "<h4> Register for New Course </h4>"+
									
//									"<form action='register' method='post'>"+
										"Enter course_id of course :  <input type='text' name = 'courseID' id='autocompleteHtmlID'>"+
										"<br>"+
										"Enter sec_id of course :  <input type='text' name = 'secID' id='addSecID'>"+
										"<br>"+
										"<button id='addButton'>Register</button>"+
//										"<input type='hidden' name='action' value='add'>"+
//									"</form>"
									"</div>");
					
					out.println(
//							 "<div style='float:left'>"+
							 "<h4> Drop current Course </h4>"+
							
							"<form action='register' method='post'>"+
								"Enter course_id of course :  <input type='text' name = 'courseID'>"+
								"<br>"+
								"Enter sec_id of course :  <input type='text' name = 'secID'>"+
								"<br>"+
								"<input type='submit' value = 'Drop'>"+
								"<input type='hidden' name='action' value='delete'>"+
							"</form></div></div>");
					out.println("<br><br><br><br><br><br><br><br><br>"
							+ "<a href='userhome.html'>User Home</a>"
							+ "</body></html>");
					
					}
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
		try (
			    Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
			    Statement stmt = conn.createStatement();
			)
			{
				try {
		
					PrintWriter out = response.getWriter();
					HttpSession session = request.getSession(false);

					
					ResultSet rset = stmt.executeQuery("SELECT year, semester "+
														"FROM   regnDates "+
														"WHERE  now()::timestamp BETWEEN startTS AND endTS;");
					String year = "";
					String semester = "";
					while (rset.next()) { year = rset.getString(1); semester = rset.getString(2); }
					
					
					String ID = request.getSession().getAttribute("UserId").toString();
					String secID = request.getParameter("secID");
					String courseID = request.getParameter("courseID");
					String actionType = request.getParameter("action");
					
					
					// checking if course exists in database
					 String preString = "SELECT count(distinct course_id) "+
								"FROM section "+
								"WHERE ( (course_id = ?) "+
								    "AND (sec_id = ?) "+
								    "AND (year = '"+year+"') "+
								    "AND (semester = '"+semester+"') "+
								");";
						 PreparedStatement prestmt = conn.prepareStatement(preString);
						 prestmt.setString(1, courseID);
						 prestmt.setString(2, secID);
						 
					rset = prestmt.executeQuery();
					
					String number = "";
					while (rset.next()) { number = rset.getString(1); }
					
					if(number.equals("0")){
						response.sendRedirect("errorRegisterServlet?errorType=1");
					}
					else{
					
						// check if course already taken
						preString = "SELECT count(distinct course_id) "+
								"FROM takes "+
								"WHERE ( (course_id = ?) "+
								    "AND (sec_id = ?) "+
								    "AND (year = '"+year+"') "+
								    "AND (semester = '"+semester+"') "+
								    "AND (id = ?)"+
								");";
						 prestmt = conn.prepareStatement(preString);
						 prestmt.setString(1, courseID);
						 prestmt.setString(2, secID);
						 prestmt.setString(3, request.getSession().getAttribute("UserId").toString());
						 rset = prestmt.executeQuery();
						 
						
						number = "";
						while (rset.next()) { number = rset.getString(1); }
						
						if(actionType.equals("add")){
							if(number.equals("1")){response.sendRedirect("errorRegisterServlet?errorType=2");}
							else{
								// update courses taken
								preString = "insert into takes values (?, ?, ?, '"+semester+"', '"+year+"', NULL);";
								 prestmt = conn.prepareStatement(preString);
								 prestmt.setString(2, courseID);
								 prestmt.setString(3, secID);
								 prestmt.setString(1, request.getSession().getAttribute("UserId").toString());
								 prestmt.executeUpdate();
								 
								doGet(request, response);
							}
							
						}else{
							if(number.equals("0")){response.sendRedirect("errorRegisterServlet?errorType=3");
							}else{
								preString = "delete from takes where ( (id=?) "+
																		"AND (course_id=?)"+
																		"AND (sec_id=?)"+
																		"AND (year='"+year+"')"+
																		"AND (semester='"+semester+"')"+
																	");";
								 prestmt = conn.prepareStatement(preString);
								 prestmt.setString(2, courseID);
								 prestmt.setString(3, secID);
								 prestmt.setString(1, request.getSession().getAttribute("UserId").toString());
								 prestmt.executeUpdate();
								
								doGet(request, response);
							}
						
						}
					}
		
				} catch ( SQLException sqle) {
					System.out.println("SQL erro : " + sqle);
				}
				
			
	
		
			}
			catch (Exception sqle)
			{ System.out.println("Exception : " + sqle); }
		  
			
	}

}
