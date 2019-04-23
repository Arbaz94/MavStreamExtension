/**
 * Class: ClientQuery3
 * Original Author: Tim Dockins (timothy.dockins@mavs.uta.edu)
 *
 * Provides a simple sample client mechanism for testing the delivery
 * of a query across the wire.
 *
 * History
 * 2009-04-06: [td] initial commit
 */

package edu.uta.dsms.client;

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
public class ClientQuery3
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

            tuples1.add(new CQ_Tuple("1,1"));
            tuples2.add(new CQ_Tuple("1,2"));
            tuples3.add(new CQ_Tuple("1,3"));

            ArrayList<CQ_QOS> qos = new ArrayList<CQ_QOS>();

            qos.add(new CQ_QOS("latency",    "ms", 1, 10, tuples1));
            qos.add(new CQ_QOS("memory",     "ms", 1, 10, tuples2));
            qos.add(new CQ_QOS("throughput", "ms", 1, 10, tuples3));

            // setup operators and streams
            CQ_Stream tbMotionDetection = new CQ_Stream(5, "tbMotionDetection", "left");
            CQ_Stream tbDetectLight = new CQ_Stream(4, "tbDetectLight", "right");


            CQ_Join     join1 = new CQ_Join(3,
                                        "joinStreams",
                                        tbMotionDetection,
                                        tbDetectLight,
                                        "RoomNumber == RoomNumber");

            CQ_Select   select1  = new CQ_Select(2, "selectTuples", "left.LightStatus == 'ON' && right.Motion == 'ON'");

            CQ_Aggregate count1 = new CQ_Aggregate(1, "countRoomNumber", "COUNT", "left.RoomNumber", "NumberOfActiveRooms");

            CQ_Project  root     = new CQ_Project(0, "projectFields", "NumberOfActiveRooms");

            
            // add appropriate inputs to the correct operators

            select1.addInput(join1);
            count1.addInput(select1);
            root.addInput(count1);

            long startTime = 0;
            long endTime = 2000000;

            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                        "testQuery",
                        qos,
                        root,
                        startTime,
                        endTime,
                        Long.parseLong("100000"),Long.parseLong("100000"),Long.parseLong("100000"),
                        SchedulingStrategy.RoundRobinSS.toString(),
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
