
import java.io.*;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class nfo {
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		File directory = new File("");
		visitAllDirsAndFiles(new File(directory.getCanonicalPath()));

	}
	
    public static void visitAllDirsAndFiles(File dir) {
        File f = dir;
        String[] childrens = f.list();
        //System.out.println(childrens.length);
		for(int j = 0; j < childrens.length; j++) {
			//System.out.println(childrens[j].toString());
			File afile = new File(f, childrens[j]);
			if(afile.isFile()) {
				if(childrens[j].toString().substring(1+childrens[j].toString().lastIndexOf(".")).equals("mp4")) {
					//afile.renameTo(new File(f,childrens[j].substring(0, afile.getName().lastIndexOf("."))+".mp4"));
					File vsmeta = new File(f, childrens[j].toString()+".vsmeta");
					if(!vsmeta.exists()) {
						System.out.println(childrens[j]);
						File nfo = new File(f, childrens[j].toString().substring(0,childrens[j].toString().lastIndexOf("."))+".nfo");
						System.out.println(nfo);
						if(nfo.exists()) {
							try {
								action(nfo,vsmeta);
								
								
							} catch (ParserConfigurationException | SAXException | IOException | NullPointerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
					
					
					}
					if(!destposter.exists()) {
						try {
							copyFileUsingStream(poster,destposter);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				
			} else if (afile.isDirectory()) {
				visitAllDirsAndFiles(afile);
				
			}
		}
	
	public static void action(File nfo,File target) throws ParserConfigurationException, SAXException, IOException, NullPointerException {

        File f = nfo;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(f);
        
		String title,plot,level,date,rate;
	    try {
	        plot = doc.getElementsByTagName("plot").item(0).getFirstChild().getNodeValue();
	    	
	    }catch (NullPointerException e) {
			// TODO Auto-generated catch block
	    	plot = "";
		}

		title = doc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
		
		level = "R-18";
		date = doc.getElementsByTagName("premiered").item(0).getFirstChild().getNodeValue();

	    try {
			rate = doc.getElementsByTagName("rating").item(0).getFirstChild().getNodeValue();
	    	
	    }catch (NullPointerException e) {
			// TODO Auto-generated catch block
			rate = "0";
		}

        NodeList nl = doc.getElementsByTagName("genre");
        String[] cla = new String[nl.getLength()];
        for (int i = 0; i < nl.getLength(); i++) {
            cla[i] = doc.getElementsByTagName("genre").item(i).getFirstChild().getNodeValue();
        }
        nl = doc.getElementsByTagName("name");
        String[] act = new String[nl.getLength()];
        for (int i = 0; i < nl.getLength(); i++) {
            act[i] = doc.getElementsByTagName("name").item(i).getFirstChild().getNodeValue();
        }
        nl = doc.getElementsByTagName("director");
        String[] direc = new String[nl.getLength()];
        for (int i = 0; i < nl.getLength(); i++) {
            direc[i] = doc.getElementsByTagName("director").item(i).getFirstChild().getNodeValue();
        }
		OutputStream output = new FileOutputStream(target.getPath());
	    output.write(8);
	    output.write(1);
	    output.write(18);
	    output.write(SumStrAscii(title));
	    output.write(title.getBytes("UTF-8"));
	    output.write(26);
	    output.write(SumStrAscii(title));
	    output.write(title.getBytes("UTF-8"));
	    output.write(34);
	    output.write(SumStrAscii(title));
	    output.write(title.getBytes("UTF-8"));
	    output.write(40);
	    output.write(220);
	    output.write(15);
	    output.write(50);
	    output.write(SumStrAscii(date));
	    output.write(date.getBytes("UTF-8"));
	    output.write(56);
	    output.write(01);
	    output.write(66);
		int sizes = byteAsciiToChar(SumStrAscii(plot));
		if(sizes<80) {
			output.write(sizes);
			
		} else {
			byte s = (byte) (sizes%128+128);
			output.write(byteAsciiToChar(s));
			output.write(sizes/128);
			
		}
		output.write(plot.getBytes("UTF-8"));
		output.write("J".getBytes("UTF-8"));
		output.write(04);
		output.write("null".getBytes("UTF-8"));
		

		int cals = 0;
		for(String c: cla) {
			cals += 2 + SumStrAscii(c);
		}
		for(String c: act) {
			cals += 2 + SumStrAscii(c);
		}
		for(String c: direc) {
			cals += 2 + SumStrAscii(c);
		}

		output.write("R".getBytes("UTF-8"));
		if(cals<80) {
			output.write(cals);
			
		} else {
			byte s = (byte) (cals%128+128);
			output.write((cals%128+128));
			output.write(cals/128);
			
		}
		for(String c: act) {
			output.write(10);
			output.write(SumStrAscii(c));
			output.write(c.getBytes("UTF-8"));
		}
		for(String c: direc) {
			output.write(18);
			output.write(SumStrAscii(c));
			output.write(c.getBytes("UTF-8"));
		}
		for(String c: cla) {
			output.write(26);
			output.write(SumStrAscii(c));
			output.write(c.getBytes("UTF-8"));
		}
		
		
		output.write("Z".getBytes("UTF-8"));
		output.write(SumStrAscii(level));
		output.write(level.getBytes("UTF-8"));
		output.write(96);//评级
		
		output.write((int)(Double.parseDouble(rate)*10));
		output.flush(); // 把缓存区内容压入文件
	    output.close();
	
	}
	
	public static byte charToByteAscii(char ch){		
		byte byteAscii = (byte)ch;
		
		return byteAscii;
	}
	public static char byteAsciiToChar(int ascii){
		char ch = (char)ascii;
		return ch;
	}
	public static int SumStrAscii(String str) throws UnsupportedEncodingException{
		byte[] bytestr = str.getBytes(StandardCharsets.UTF_8);
		int sum = 0;
		for(int i=0;i<bytestr.length;i++){
			sum++;
		}
		return sum;
	}
}
