/* SimpleApp.java */
import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import java.lang.*;
import java.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;




//////////////////////////////////////////////////////////////////////////////////////////////////////
/*
 * 		Idea Used : for each word, create pairs that contain ( (prefix, word) , 1) ) for all prefix with length > 3
 * 						Combine pairs using combiner which adds frequency.
 * 						Then sort and find 3 most occuring words and store them in listPrint
 * 
 * 					Same way for calculating following words.
 * 						Note : for last word in line, no following word is considered
 */





public class SimpleApp {
  public static void main(String[] args) {
    String dataFile = "/home/deepmodh1/Downloads/neon-eclipse/sample_io/input/*"; // Should be some file on your system
    SparkConf conf = new SparkConf().setAppName("Simple Application");
    JavaSparkContext sc = new JavaSparkContext(conf);
    JavaRDD<String> lines = sc.textFile(dataFile).cache();
    
    JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
    	public Iterator<String> call(String s) {
    		return Arrays.asList(s.toLowerCase().split("[^a-z0-9]+")).iterator(); }
    });
    
    // temporary pair so that flatmaptopair can be used
    JavaPairRDD<Tuple2<String, String> , Integer > pairsTemp1 = words.mapToPair(new PairFunction<String, Tuple2<String, String> , Integer >() {
    	public Tuple2<Tuple2<String, String> , Integer > call(String s) {
    		Tuple2 pReturn = new Tuple2(s, s);
    		return new Tuple2<Tuple2<String, String> , Integer  >(pReturn,1); 
  	  	}
  	});
    
    
    // pairs contain ( (prefix, word) , 1) )  
    JavaPairRDD<Tuple2<String, String> , Integer > pairs1 = pairsTemp1.flatMapToPair(new PairFlatMapFunction<Tuple2<Tuple2<String, String> , Integer >, Tuple2<String, String> , Integer>() {
    	  public Iterator<Tuple2<Tuple2<String, String> , Integer>> call(Tuple2<Tuple2<String, String> , Integer > t) { 
    		  List<Tuple2<Tuple2<String, String> , Integer>> list = new ArrayList<Tuple2<Tuple2<String, String> , Integer>>();
    		  String s = t._1()._1();
    		  for(int i=3;i<=s.length();i++){
        		  Tuple2 pairTemp = new Tuple2(s.substring(0,i), s); 
    			  Tuple2<Tuple2<String, String> , Integer> tupleTemp = new Tuple2<Tuple2<String, String> , Integer>(pairTemp,1);
    			  list.add(tupleTemp);
    		  }
    	  	  return list.iterator(); }
    });// if without combiner   .reduceByKey( (x,y) -> x+y );
    
    
    // combining, now pair contians ( (prefix, word) , freqyency ) )
    JavaPairRDD<Tuple2<String, String> , Integer > pairs1Combined = pairs1.combineByKey(
		v -> v, // v stands for value
		(v1,v2) -> (v1+v2),
		(v1,v2) -> (v1+v2)
	);
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // 					for last word in line, no following word is considered              //
    //////////////////////////////////////////////////////////////////////////////////////////
    
    // pair contains ( (word, followingWord), 1 )
    JavaPairRDD<Tuple2<String, String> , Integer > pairs2 = lines.flatMapToPair(new PairFlatMapFunction<String, Tuple2<String, String> , Integer>() {
	  	  public Iterator<Tuple2<Tuple2<String, String> , Integer>> call(String s) { 
			 
	  		  List<Tuple2<Tuple2<String, String> , Integer>> list = new ArrayList<Tuple2<Tuple2<String, String> , Integer>>();	  
    		  List <String> listTemp = new LinkedList<> (Arrays.asList(s.toLowerCase().split("[^a-z0-9+]")));
    		  for (Iterator<String> iter = listTemp.listIterator(); iter.hasNext(); ) {
    			    String a = iter.next();
    			    if (a.equals("")) {
    			        iter.remove();
    			    }
    		  }
    		  
    		  Integer counter = 0;
    		  while(counter<(listTemp.size()-1)){
    			  Tuple2 pairTemp = new Tuple2(listTemp.get(counter), listTemp.get(counter+1));
    			  list.add(new Tuple2<Tuple2<String, String> , Integer>(pairTemp,1));
    			  counter++;
    		  }
    		  
		  	  return list.iterator(); }
    });// if without combiner   .reduceByKey( (x,y) -> x+y );
    
    
	// combining, now pair contains ( (word, followingWord), frequency )
	JavaPairRDD<Tuple2<String, String> , Integer > pairs2Combined = pairs2.combineByKey(
		v -> v, // v stands for value
		(v1,v2) -> (v1+v2),
		(v1,v2) -> (v1+v2));

    
    // comparator
    //( (prefix, word), n ) or ( (word, following word), frequency)
    Comparator<Tuple2<Tuple2<String, String> , Integer>> comp = new Comparator<Tuple2<Tuple2<String, String> , Integer>>(){
    	@Override
    	public int compare(Tuple2<Tuple2<String, String> , Integer> t1, Tuple2<Tuple2<String, String>, Integer> t2){
    		if(t2._1()._1().compareTo(t1._1()._1()) == 0){
				return t2._2().compareTo(t1._2());
			}else{
				return t1._1()._1().compareTo(t2._1()._1());
			}	
    	}
    };
    
    
    // creating list to print
    List <Tuple2<Tuple2<String, String> , Integer>> listPrint1Temp = new LinkedList<> (pairs1Combined.collect());
    listPrint1Temp.sort(comp);
    
    List <String> listPrint = new LinkedList<>();
    if(listPrint1Temp.size()>0){
    	String prefix = listPrint1Temp.get(0)._1()._1();
    	Integer counter = 0;
    	String tempString = "";
    	for( Tuple2<Tuple2<String, String> , Integer>  t : listPrint1Temp ){
    		if(!prefix.equals("")){
	    		if(prefix.compareTo(t._1()._1())!=0){
	    			listPrint.add(tempString);
	    			tempString="";
	    			prefix = t._1()._1();
					counter = 0;
				}
	    		if(counter < 3){
	    			if(counter == 0){tempString = prefix + " :";}
					tempString = tempString + " "+ t._1()._2();
					counter++;
	    		}
    		}
    	}
    }
    
    // part 2
    
    List <Tuple2<Tuple2<String, String> , Integer>> listPrint2 = new LinkedList<> (pairs2Combined.collect());
    listPrint2.sort(comp);
    if(listPrint2.size()>0){
    	String prefix = listPrint2.get(0)._1()._1();
    	Integer counter = 0;
    	String tempString = "";
    	for( Tuple2<Tuple2<String, String> , Integer>  t : listPrint2 ){
        	if(!prefix.equals("")){
        		if(prefix.compareTo(t._1()._1())!=0){
        			listPrint.add(tempString);
        			tempString="";
        			prefix = t._1()._1();
    				counter = 0;
    			}
        		if(counter < 3){
        			if(counter == 0){tempString = prefix + "$ :";}
    				tempString = tempString + " "+ t._1()._2();
    				counter++;
        		}
    		}
    	}
    }
    
    
    // printing
    Collections.sort(listPrint);
    for( String s : listPrint ){System.out.println(s);}
    
  }
} 