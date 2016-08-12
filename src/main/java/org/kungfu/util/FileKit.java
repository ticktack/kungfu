package org.kungfu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileKit {
	
	public static void wirteToFile(String filePath, String fileName, String content) {
		File dir = new File(filePath);
		if (!dir.exists())
			dir.mkdirs();
		FileWriter fw = null;
		String target = filePath + fileName;
		try {
			fw = new FileWriter(target);
			fw.write(content);
		}
		catch(IOException e) {
			e.getStackTrace();
		}
		finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	 public static StringBuffer readFile(String pathname){
	        InputStreamReader read = null;
	        BufferedReader reader = null;
	        StringBuffer sb = new StringBuffer();
	        try{
	            File file = new File(pathname);
	            if(!file.exists()){
	            	throw new IllegalArgumentException("cant't exists file: " + pathname);
	            }
	            read = new InputStreamReader (new FileInputStream(file),"UTF-8");
	            reader=new BufferedReader(read);
	            String line;
	            while((line = reader.readLine())!=null){
	                sb.append(line).append("\n");
	            }
	        }catch(Exception e){
	        	throw new IllegalArgumentException(e.getMessage());
	        }finally{
	            try {
	                if(reader!=null)
	                    reader.close();
	                if(read!=null)
	                    read.close();
	            } catch (IOException e) {
	            	throw new IllegalArgumentException(e.getMessage());
	            }
	        }
	        return sb;
	    }
	    
}
