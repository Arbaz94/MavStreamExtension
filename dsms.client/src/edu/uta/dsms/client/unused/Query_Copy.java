/*
package edu.uta.dsms.client.unused;

import edu.uta.dsms.client.*;
import edu.uta.dsms.core.constants.SchedulingStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

//import streammanager.QueryPlanObject;

public class Query_Copy {

	public static final String SCHEDULING_STRATEGY_TAG = "SSTAG";
	public static final String QPO_TAG = "QPOTAG";
	public static final String QUERY_NAME_TAG = "QNTAG";

	private InputStream _src;
	private SchedulingStrategy _ss;
	
	/**
	 * Initialize a Query object with the Query Source and a Scheduling Strategy.
	 * The Query Source contains the following items:
	 * 1) Stream Definition
	 * 2) QueryPlanObject
	 * 3) Query Name
	 * @param src
	 * @param ss
	 */
/*	public Query_Copy(InputStream src, SchedulingStrategy ss) {
		_src = src;
		_ss = ss;
	}
	
	/**
	 * Execute this Query by delivering it to the DSMS Server specified in sd.
	 * The same socket that is used to send the Query will be used to receive
	 * the Query Response.
	 * @param sd
	 * @return
	 */
/*	public Socket execute(ServerDefinition sd) {
		Socket srvSock = null;
		
		try {
			srvSock = new Socket(sd._hostname, sd._port);
			ObjectInputStream ois = new ObjectInputStream(_src);
			ObjectOutputStream srvOos = new ObjectOutputStream(srvSock.getOutputStream());			

			// tell the server that we're about to send query information
			srvOos.writeObject(new Integer(255));
			
			// send the Scheduling Strategy
			srvOos.writeObject(Query_Copy.SCHEDULING_STRATEGY_TAG);
			srvOos.writeObject(new Integer(_ss.getType()));
						
			// send the QueryPlanObject
//			QueryPlanObject qpo = (QueryPlanObject)ois.readObject();
//			srvOos.writeObject(Query.QPO_TAG);
//			srvOos.writeObject(qpo);
			
			// send the Query Name
			String name = (String)ois.readObject();
			srvOos.writeObject(Query_Copy.QUERY_NAME_TAG);
			srvOos.writeObject(name);
			
			// do not close the output stream
			// do close the input stream
			ois.close();
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try { if (srvSock != null) srvSock.close(); }
			catch (IOException e1) { }
			srvSock = null;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try { if (srvSock != null) srvSock.close(); }
			catch (IOException e1) { }
			srvSock = null;
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try { if (srvSock != null) srvSock.close(); }
			catch (IOException e1) { }
			srvSock = null;
		}
		
		return srvSock;
	}
}
*/