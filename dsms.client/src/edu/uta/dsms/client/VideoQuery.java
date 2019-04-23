/**
 * Class: SimpleSampleClient_VideoQuery1
 * Original Author: Mayur Arora (mayur.arora@mavs.uta.edu)
 *
 * Provides a simple sample client mechanism for testing queries on
 * the relational output of a video
 *
 * History
 * 2017-03-18: [td] initial commit
 */

package edu.uta.dsms.client;

import edu.uta.dsms.client.Query;
import edu.uta.dsms.core.query.*;
import edu.uta.dsms.client.comm.*;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import edu.uta.dsms.core.constants.SchedulingStrategy;
import edu.uta.dsms.core.streams.DataItem;

import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;

import java.util.Vector;

/**
 *
 * @author Mayur
 */
public class VideoQuery
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

            long startTime = 0L;
            long endTime = 10000L;

            ArrayList<CQ_QOS> qos = new ArrayList<CQ_QOS>();

            qos.add(new CQ_QOS("latency",    "ms", 1, 10, tuples1));
            qos.add(new CQ_QOS("memory",     "ms", 1, 10, tuples2));
            qos.add(new CQ_QOS("throughput", "ms", 1, 10, tuples3));

            // setup operators and streams

           CQ_Stream test_stream1 = new CQ_Stream(1,"tbVideoTest1","tbVideoTest1");
           CQ_Stream test_stream2 = new CQ_Stream(2,"tbVideoTest2","tbVideoTest2");

           CQ_Project project1= new CQ_Project(3,"project_1","tbVideoTest1.BoundingBox");
            CQ_Project project2= new CQ_Project(4,"project_4","tbVideoTest2.BoundingBox");

            CQ_Select select1= new CQ_Select(5,"select_1","tbVideoTest1.PrevObjectId == '1'");
            CQ_Select select2= new CQ_Select(6,"select_2","tbVideoTest2.PrevObjectId == '1'");

            CQ_Join join1=new CQ_Join(7,"join_1","tbVideoTest1.PrevObjectId == tbVideoTest2.PrevObjectId");

            CQ_Arrable arrable1=new CQ_Arrable(8,"arrable_1","tbVideoTest1.PrevObjectId","tbVideoTest1.FrameId");

            CQ_Arrable arrable2=new CQ_Arrable(9,"arrable_2","tbVideoTest2.PrevObjectId","tbVideoTest2.FrameId");

            CQ_Filter filter1=new CQ_Filter(10,"filter_1","tbVideoTest1.PrevObjectId","tbVideoTest1.FrameId","first");

            CQ_Filter filter2=new CQ_Filter(11,"filter_2","tbVideoTest2.PrevObjectId","tbVideoTest2.FrameId","first");

            CQ_cJoin cjoin1=new CQ_cJoin(12,"cjoin1","tbVideoTest1.PrevObjectId","tbVideoTest1.PrevObjectId","tbVideoTest2.PrevObjectId","tbVideoTest2.PrevObjectId","tbVideoTest1.BoundingBox == tbVideoTest2.BoundingBox");


            project1.addInput(filter1);
            project2.addInput(filter2);
            select1.addInput(test_stream1);
            select2.addInput(test_stream2);
            ArrayList arr=new ArrayList();
            ArrayList arr2=new ArrayList();
            arr.add(filter1);
            arr.add(filter2);
            arr2.add(test_stream1);
            arr2.add(test_stream2);
            join1.setInputs(arr2);
            arrable1.addInput(test_stream1);
            arrable2.addInput(test_stream2);
            filter1.addInput(arrable1);
            filter2.addInput(arrable2);
            cjoin1.setInputs(arr2);




            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                            "testQuery",
                            qos,
                            join1,
                            startTime,
                            endTime,
                            Long.parseLong("100"),Long.parseLong("100"),Long.parseLong("100"),
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
