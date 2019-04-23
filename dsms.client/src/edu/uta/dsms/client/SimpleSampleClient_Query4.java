/**
 * Class: SimpleSampleClient_Query3
 * Original Author: Tim Dockins (timothy.dockins@mavs.uta.edu)
 *
 * Provides a simple sample client mechanism for testing the delivery
 * of a query across the wire.
 *
 * History
 * 2009-04-06: [td] initial commit
 */

package edu.uta.dsms.client;

import edu.uta.dsms.client.Query;
import edu.uta.dsms.core.query.*;
import edu.uta.dsms.client.comm.*;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import edu.uta.dsms.core.constants.SchedulingStrategy;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Tim
 */
public class SimpleSampleClient_Query4
{

    public static void main(String args[])
    {
        // point filename to a well-formed xml file
        // ensure that the dtd is available.
        try
        {
            // Setup QOS by creating a set of tuples arraylists, one
            // for each type of QOS policy, adding a new Tuple to each

            ArrayList<CQ_Tuple> tuples1 = new ArrayList<CQ_Tuple>();
            ArrayList<CQ_Tuple> tuples2 = new ArrayList<CQ_Tuple>();
            ArrayList<CQ_Tuple> tuples3 = new ArrayList<CQ_Tuple>();

            tuples1.add(new CQ_Tuple("1,0.01"));
            tuples2.add(new CQ_Tuple("1,0.2"));
            tuples3.add(new CQ_Tuple("1,0.3"));

            ArrayList<CQ_QOS> qos = new ArrayList<CQ_QOS>();

            qos.add(new CQ_QOS("latency",    "ms", 1, 10, tuples1));
            qos.add(new CQ_QOS("memory",     "ms", 1, 10, tuples2));
            qos.add(new CQ_QOS("throughput", "ms", 1, 10, tuples3));

            // setup operators and streams
            CQ_Stream tbVideoFrames = new CQ_Stream(3, "tbVideoFramesTupleBased", "tbVideoFramesTupleBased");

            CQ_Project  root     = new CQ_Project(0, "project", "tbVideoFramesTupleBased.FrameNo,tbVideoFramesTupleBased.ObjectId");

            CQ_Select   select1  = new CQ_Select(1, "select", "tbVideoFramesTupleBased.FrameNo < '50' || tbVideoFramesTupleBased.FrameNo > '150'");

            CQ_GroupBy  groupBy1 = new CQ_GroupBy(2,
                                        "groupBy",
                                        "tbVideoFramesTupleBased.FrameNo",
                                        "arrable",
                                        "tbVideoFramesTupleBased.FrameNo",
                                        "tbVideoFramesTupleBased.FrameNo");
            
            // add appropriate inputs to the correct operators
            
           /*root.addInput(groupBy1);
           groupBy1.addInput(select1);
           // groupBy1.addInput(root);
          //root.addInput(select1);
            select1.addInput(tbVideoFrames);
*/
            
            groupBy1.addInput(tbVideoFrames);
            select1.addInput(tbVideoFrames);//(groupBy1);
            root.addInput(select1);
            long startTime = 0L;
            long endTime = 10000L;

            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                        "testQuery",
                        qos,
                        select1,
                        startTime,
                        endTime,
                        Long.parseLong("0"),Long.parseLong("500"),Long.parseLong("500"),
                        SchedulingStrategy.SegmentSS.toString(),
                        "null",
                        10));

            // take what was read and serialize it back to XML
            System.out.println("Object Serialization to XML *****************");
            System.out.println(QueryBuilder.toXML(cq));
            System.out.println("*********************************************");

            Query q = new Query(new StringReader(QueryBuilder.toXML(cq)));
            Socket responseSock = q.execute(new ServerDefinition("localhost", 8000));

            ObjectInputStream ois = new ObjectInputStream(responseSock.getInputStream());

            while (true)
            {
				String resp = (String)ois.readObject();
				System.out.print(resp);
				if ("endQuery".equalsIgnoreCase(resp.trim()))
					break;
			}

			ois.close();
			responseSock.close();

        }
        catch (CannotResolveClassException crce)
        {
            System.err.println(crce.getMessage());
            crce.printStackTrace();
            System.exit(-1);
        }
        catch (Throwable t)
        {
			System.err.println("Caught: " + t);
			t.printStackTrace();
        }
    }



}
