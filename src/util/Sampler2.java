package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

public class Sampler2 {
	public static void main(String[] args) {
		try {
			FileReader reader = new FileReader("D://Jworkspace/4-5train.csv");
		    BufferedReader br = new BufferedReader(reader);
		    FileWriter writer = new FileWriter("D://Jworkspace/4-5_train50.csv");
		    BufferedWriter bw = new BufferedWriter(writer);
		    String str = null;
		    while((str = br.readLine()) != null) {
		    	String[] items = str.split(",");
		    	if (items[10].equals("0")){
		    		Random random = new Random();
			        int a=random.nextInt(2);
			        if (a != 1) continue;
			        else{
			    		bw.write(str);
			    		bw.newLine();
			        }
		    	}
		    	else{
		    		bw.write(str);
		    		bw.newLine();
		    	}
		    }
		    br.close();
		    bw.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
