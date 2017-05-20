package SicController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Reader {
	Queue<String> queue = new LinkedList<>();
	public static HashMap <String,String> opTab = new HashMap<>();

	public void generateOpTab(String fileName) {
		File file=new File(fileName);
		BufferedReader br = null;
		try {
			FileReader in = new FileReader(file);
			br = new BufferedReader(in);
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split("\\s+");
				if (split.length != 2) {
					throw new RuntimeException();
				}
				String s=Integer.toHexString(Integer.parseInt(split[1]));
				s=s.length()==1?"0"+s:s;
				opTab.put(split[0],s);
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> loadCode(File file) {
		ArrayList<String> arr = new ArrayList<>();
		BufferedReader br = null;
		try {
			FileReader in = new FileReader(file);
			br = new BufferedReader(in);
			String line = br.readLine();
			while (line != null) {
			    line=line.replaceAll("\t","    ");
				if (line.length() != 69) {
					for (int i = line.length(); i < 69; i++) {
						line += " ";

					}
				}

				arr.add(line);
				queue.add(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return arr;
	}

	public String lineComplier() {
		String line = queue.peek();
		queue.remove();
		return line;
	}
}