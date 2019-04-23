package edu.uta.dsms.client.unused;

//package edu.uta.dsms.client;
//import edu.uta.dsms.core.constants.SchedulingStrategy;
//import java.io.*;
//import java.net.*;
//import java.util.*;
//
//public class SampleClient
//{
//	public static void main(String[] args)
//	{
//		try {
//			FileInputStream fis = new FileInputStream("..//..//Test//query3.dat");
//			Query q = new Query(fis);
//			Socket responseSock = q.execute(new ServerDefinition("localhost", 8000));
//
//			ObjectInputStream ois = new ObjectInputStream(responseSock.getInputStream());
//			while (true) {
//				String resp = (String)ois.readObject();
//				System.out.print(resp);
//				if ("endQuery".equalsIgnoreCase(resp.trim()))
//					break;
//			}
//
//			ois.close();
//			responseSock.close();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Unexpected exception encountered");
//		}
//	}
//}
