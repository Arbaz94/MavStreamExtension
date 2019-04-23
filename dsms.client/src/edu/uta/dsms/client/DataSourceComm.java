/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uta.dsms.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Tim
 */
public class DataSourceComm
{
    private Reader _src;

    public DataSourceComm(Reader src)
    {
        _src = src;
    }

    public Socket execute(ServerDefinition sd)
    {
        Socket srvSock = null;

  		try
        {
			srvSock = new Socket(sd._hostname, sd._port);

            OutputStream srvOs  = srvSock.getOutputStream();

			// tell the server that we're about to send query information
            byte[] outByte = new byte[]{'1','2','8','\0'};

            srvOs.write(outByte);

			// send the XML query
            int ch;
            String xml = "";

            while ((ch = this._src.read()) != -1)
                xml += (char) ch;

			srvOs.write(xml.getBytes());
			srvOs.write('\0');

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
