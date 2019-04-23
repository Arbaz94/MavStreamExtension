package edu.uta.dsms.client;

import edu.uta.dsms.client.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Query {

	private Reader _src;

	/**
	 * Initialize a Query object with the Query Source and a Scheduling Strategy.
	 * The Query Source contains the following items:
	 * 1) Stream Definition
	 * 2) QueryPlanObject
	 * 3) Query Name
	 * @param src
	 * @param ss
	 */
	public Query(Reader src) {
		_src = src;
	}
	
	/**
	 * Execute this Query by delivering it to the DSMS Server specified in sd.
	 * The same socket that is used to send the Query will be used to receive
	 * the Query Response.
	 * @param sd
	 * @return
	 */
	public Socket execute(ServerDefinition sd) {
		Socket srvSock = null;
		
		try {
			srvSock = new Socket(sd._hostname, sd._port);
//
            OutputStream srvOs  = srvSock.getOutputStream();

			// tell the server that we're about to send query information
            byte[] outByte = new byte[]{'2','5','5','\0'};

            srvOs.write(outByte);

			// send the XML query
            int ch;
            String xml = "";

            while ((ch = this._src.read()) != -1)
                xml += (char) ch;

			srvOs.write(xml.getBytes());
			srvOs.write('\0');

			// do not close the output stream
			
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
//		catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			try { if (srvSock != null) srvSock.close(); }
//			catch (IOException e1) { }
//			srvSock = null;
//		}
		
		return srvSock;
	}
}
