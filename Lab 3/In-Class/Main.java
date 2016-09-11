// 140050002 ; Main.java

// 140050002 ; Main.java


import java.sql.*;
import java.io.*; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class Main {
	public static void main(String[] args) {
		 
		// In the JDBC API 4.0, the DriverManager.getConnection method loads
		// JDBC drivers automatically. As a result you do not need to call the 
		// Class.forName method 
		// try {
		//	  Class.forName ("org.postgresql.Driver");
		// }
		// catch (Exception e) {
		//	System.out.println("Could not load driver: " + e);
		//}
		
	
		// The following syntax is called try with resources which can be used with any resource
		// that supports the java.lang.AutoCloseable interface. 
		// It ensures that the resources get closed at the end of the try block.  
		// It is **MUCH** preferred to the old style to avoid connection leakage.
		// Note the URL syntax below:  jdbc:postgresql tells the DriverManager to use the 
		// postgresql JDBC driver.  
		// localhost can be replaced with a host name if the postgresql is running on a remote machine.
		// Replace 6432 with the port number you are using, and dbis with your database name
		// Similarly, replace sudarsha with the user name you are using for your database.  
		try (
		    Connection conn = DriverManager.getConnection(
		    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
		    Statement stmt = conn.createStatement();
		)
		{
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Enter a command : ");
				 
				String commandString = reader.readLine();
				String[] commandParts = commandString.split(" ");
				int sizeCommand = commandParts.length;
				
				
				switch (commandParts[0]) {
	            	case "selectall":  
	            		if((sizeCommand == 2) & !(anyQuotes(commandParts[1]))){
							ResultSet rset = stmt.executeQuery( "select * from "+commandParts[1]);
							ResultSetMetaData rsmd = rset.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
						    while (rset.next()) {
						    	for (int i = 1; i <= columnsNumber; i++) {
						            if (i > 1) System.out.print(",  ");
						            String columnValue = rset.getString(i);
						            System.out.print(columnValue);
						        }
						        System.out.println("");
						    }
						}
	            		break;
	            		
	            	case "insert":
	            		if(!(anyQuotes(commandParts[1]))){
	            			 ResultSet res=stmt.executeQuery("select * from "+commandParts[1]+" where 1<0");
	            			 ResultSetMetaData rsmd=res.getMetaData();
	            			 int columnsNumber = rsmd.getColumnCount();
	            			 
	            			 String columnType = "";
					    	 for (int i = 1; i <= columnsNumber; i++) {
					            columnType = columnType + rsmd.getColumnTypeName(i) + " ";
					        }
					    	String[] columnTypeArray = columnType.split(" ");
							
					    	String query = "insert into " + commandParts[1] + " values ( ";
					    	boolean isBreak = false;
					    	for ( int i = 1; i<= columnsNumber; i++){
					    		
					    		if(anyQuotes(commandParts[i+1])){
					    			System.out.println("Program error : Incorrect Input; not sending to sql");
					    			isBreak = true;
					    			break;
					    		}
					    		else{
					    			if(columnTypeArray[i-1].equals("varchar")){
					    				query = query + "'" + commandParts[i+1] + "'";
					    			}
					    			else{query = query + commandParts[i+1] ;}
					    			
					    			if(i!=columnsNumber){query += ", ";}
					    		}
					    	}
					    	
					    	if(!isBreak){
					    		query += ")";
					    		if (stmt.executeUpdate(  query ) > 0) {
									System.out.println("Successfully inserted tuple into instructor");
								} ;
					    	}
	            			 
	            		}
	            		break;
	            		
	            	case "select":
	            		if(!(anyQuotes(commandParts[1]))){
	            			DatabaseMetaData dm = conn.getMetaData( );
	            			ResultSet rs = dm.getPrimaryKeys( "" , "" , commandParts[1] );
	            			ResultSet res=stmt.executeQuery("select * from "+commandParts[1]+" where 1<0");
	            			ResultSetMetaData rsmd=res.getMetaData();
	            			
	            			String primaryKeys = "";
	            			String columnType = "";
	            			while( rs.next( ) ) 
	            			{ 
	            				 String columnName = rs.getString("COLUMN_NAME");
	            			      primaryKeys =  primaryKeys + columnName + " ";
	            			      columnType = columnType + rsmd.getColumnTypeName(rs.getInt("KEY_SEQ")) + " ";
	            			}
	            			String[] primaryKeyArray = primaryKeys.split(" ");
	            			int numPrimaryKey = primaryKeyArray.length;
	            			String[] columnTypeArray = columnType.split(" ");
	            				            			
	            			String query = "select * from " + commandParts[1] + " where ( ";
	            			for(int i = 0; i< numPrimaryKey;i++){
	            				query = query + "( " + primaryKeyArray[i] + " = " ;
	            				
	            				if(columnTypeArray[i].equals("varchar")){
				    				query = query + "'" + commandParts[i+2] + "'";
				    			}
				    			else{query = query + commandParts[i+2] ;}
				    			
	            				query = query + ") " ;
				    			if(i!=(numPrimaryKey-1)){query += "AND ";}
				    			
	            				
	            			}
	            			
	            			//if(!isBreak){
					    		query = query + " )";
					    		System.out.println(query);
					    		if (stmt.executeUpdate(  query ) > 0) {
									System.out.println("Success on sql query");
					    		}
							//	} ;
					    	
	            		}
	            		
	            		
	            		break;
						
					default : System.out.println("Incorrect Input");
	                    break;
				}
				
				
				
			} catch ( SQLException sqle) {
				System.out.println("SQL erro : " + sqle);
			}
			
			System.out.println();
			
		    
		    // The following are not required anymore since the connections were opened
		    // with the try with resources feature:
		    // stmt.close();
		    // conn.close();
		}
		catch (Exception sqle)
		{
		System.out.println("Exception : " + sqle);
		}
	}
	
	
	
	
	public static boolean anyQuotes(String s1) {
		boolean answer = false;   
		if(s1.contains("'")){answer = true;}
		return answer;
		}
}



