// 140050002 
// database contains edge table as given in sample data
// printing only reachable nodes

// if needed; modify edge from database

import java.sql.*;
import java.io.*; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class  Main{
	
	public static void printReachable(){
		 
		try (
		    Connection conn = DriverManager.getConnection(
		    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
		    Statement stmt = conn.createStatement();
		)
		{
			try {
		
				
				//// create table
				String createReachable = " CREATE TABLE reachable (node int);";
				stmt.executeUpdate(createReachable + "insert into reachable values (0);"); 
				
				//// base case
				stmt.executeUpdate("INSERT INTO reachable (" +
						"SELECT distinct edge.dest as node " +
						"FROM edge " +
						"WHERE (src = 0)" +
						");");
				
				
				String currCount = "";
				ResultSet rset = stmt.executeQuery( "SELECT count(node) FROM reachable; " );
				while (rset.next()) { currCount = (rset.getString(1)); }
				

				//// recursion
				String prevCount = "0";
				
				while( ! currCount.equals(prevCount)){
					String modifyReachable = "insert into reachable ( "+ 
													"(select dest " +
													"FROM reachable, edge "+
													"where (node = src)) "+
												"except "+
													"(select node "+
													"from reachable) "+
												");";
					stmt.executeUpdate(modifyReachable);
					
					prevCount = currCount;
					rset = stmt.executeQuery( "SELECT count(node) FROM reachable; " );
					while (rset.next()) { currCount = (rset.getString(1)); }
				}
				
				rset = stmt.executeQuery("SELECT node FROM reachable ORDER BY node;");
				while(rset.next()){System.out.println(rset.getString("node"));}

				// deleting tables
				
				stmt.executeUpdate("drop table reachable;");
				
				
				
			} catch ( SQLException sqle) {
				System.out.println("SQL erro : " + sqle);
			}
			
			
		}
		catch (Exception sqle)
		{ System.out.println("Exception : " + sqle); }

	}
	
	public static void printReachableC(){
		 
		try (
		    Connection conn = DriverManager.getConnection(
		    		"jdbc:postgresql://localhost:5020/postgres", "deepmodh", "");
		    Statement stmt = conn.createStatement();
		)
		{
			try {
				
				//// create table
				stmt.executeUpdate(" CREATE TABLE reachableC (node int, cost int);");
				stmt.executeUpdate( "insert into reachableC values (0,0);"); 
				
				//// base case
				stmt.executeUpdate("INSERT INTO reachableC (" +
						"SELECT distinct edge.dest as node, edge.weight as cost " +
						"FROM edge " +
						"WHERE (src = 0)" +
						");");

				/// create temporary table to compair
				stmt.executeUpdate(" create table prevReachableC (node int,cost int);");
				stmt.executeUpdate( "insert into prevReachableC values (0,0);");
				
				while(true){
					// checking exit condition
					// check if temporary table and cuurentTable are same or not
					ResultSet rset2 = stmt.executeQuery( "select count(*) "+
														"from ( "+
															"(select node, cost "+
															"from reachableC )"+
														"except "+
															"(select node, cost "+
															"from prevReachableC )"+
														") as temppppp ;" );
					
					String checkAnswer = "";
					while (rset2.next()) { checkAnswer = (rset2.getString(1)); }
					if(checkAnswer.equals("0")){break;}
					
					// updating prevReachableC
					stmt.executeUpdate("delete from prevReachableC;" +
							" insert into prevReachableC (select node, cost from reachableC);");
					
					// including possible new entries in reachableC
					stmt.executeUpdate("insert into reachableC( "+
											"select edge.dest as node, (prevReachableC.cost + edge.weight) as cost " +
											"From prevReachableC, edge "+
											"where (prevReachableC.node = edge.src) "+
										");");
					
					// keep only minimum costs in reachableC
					stmt.executeUpdate("create table tempTableToStoreMinCost(node int, cost int);");
					stmt.executeUpdate("insert into tempTableToStoreMinCost "+
											"(select node, min(cost) as cost "+
												"from reachableC "+
												"group by node " +
											");");
					
					stmt.executeUpdate("delete from reachableC;"+
										"insert into reachableC "+
										"(select node, cost "+
											"from tempTableToStoreMinCost "+
										");" +
										"drop table tempTableToStoreMinCost;");
					
				}
				
				// printing answer
				
				ResultSet rset = stmt.executeQuery("SELECT node,cost FROM reachableC ORDER BY node;");
				while(rset.next()){
					System.out.println(rset.getString("node")+", "+rset.getString("cost"));
				}
				
				// deleting tables
				
				stmt.executeUpdate(
						"drop table reachableC;" +
						"drop table prevReachableC;"
						);
				
				
				
			} catch ( SQLException sqle) {
				System.out.println("SQL erro : " + sqle);
			}
			
			System.out.println();
			
		
		    
		}
		catch (Exception sqle)
		{ System.out.println("Exception : " + sqle); }
	}
	

	
	
	public static void main(String[] args) {
	
		try{
			
			//System.out.print("Reachable Nodes are :\n");
			printReachable();
			
			System.out.println();
			
			//System.out.println("\nList of reachable node along with cost is  : \n" +"node, cost");
			printReachableC();
			
		}
		catch (Exception sqle)
			{System.out.println("Exception : " + sqle);}
		}
	
	}


