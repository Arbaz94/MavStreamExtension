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
public class test_sift_query
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

            CQ_Stream test_stream1 = new CQ_Stream(1,"tbSiftData","tbSiftData");
            CQ_Stream test_stream2 = new CQ_Stream(2,"tbSiftData2","tbSiftData2");

            CQ_Stream Lab_entry = new CQ_Stream(1,"Lab_entry","Lab_entry");
            CQ_Stream Lab_exit = new CQ_Stream(2,"Lab_exit","Lab_exit");

            CQ_Project project1= new CQ_Project(3,"project_1","tbSiftData.FrameId,tbSiftData.PrevObjectId,tbSiftData.FV");
            CQ_Project project2= new CQ_Project(4,"project_2","tbSiftData2.PrevObjectId,tbSiftData2.FV");

            CQ_Project project3=new CQ_Project(5,"project_3","tbSiftData.FrameId,tbSiftData.PrevObjectId,tbSiftData2.FrameId,tbSiftData2.PrevObjectId");

            CQ_sMatch match1=new CQ_sMatch(5,"match1","tbSiftData.FV","tbSiftData2.FV","0.4");

            CQ_Join join1=new CQ_Join(6,"join1","tbSiftData.FrameId == tbSiftData2.FrameId && sMatch(tbSiftData.FV,tbSiftData2.FV,0.4)");

            CQ_Arrable arrable1=new CQ_Arrable(7,"arrable1","tbSiftData.PrevObjectId","tbSiftData.FrameId");

            CQ_Arrable arrable2=new CQ_Arrable(8,"arrable2","tbSiftData2.PrevObjectId","tbSiftData2.FrameId");

            CQ_Filter filter1=new CQ_Filter(9,"Filter1","tbSiftData.PrevObjectId","tbSiftData.FrameId","last");


            CQ_Filter filter2=new CQ_Filter(10,"Filter2","tbSiftData2.PrevObjectId","tbSiftData2.FrameId","last");

            CQ_sMatch match2=new CQ_sMatch(11,"match1","tbSiftData.PrevObjectId","tbSiftData2.PrevObjectId","0.4");

            CQ_cJoin cjoin1=new CQ_cJoin(12,"cjoin1","tbSiftData.PrevObjectId","tbSiftData.FrameId","tbSiftData2.PrevObjectId","tbSiftData2.FrameId","tbSiftData.FrameId == tbSiftData2.FrameId && sMatch(tbSiftData.FV,tbSiftData2.FV,0.2)");


            project1.addInput(Lab_entry);
            project2.addInput(Lab_exit);
            ArrayList arr=new ArrayList();
            ArrayList arr2=new ArrayList();
            arr.add(filter1);
            arr.add(filter2);
            arr2.add(arrable1);
            arr2.add(arrable2);
            match1.setInputs(arr);
            project3.addInput(cjoin1);
            join1.setInputs(arr);
            arrable1.addInput(test_stream1);
            arrable2.addInput(test_stream2);
            filter1.addInput(arrable1);
            filter2.addInput(arrable2);
            match2.setInputs(arr);
            cjoin1.setInputs(arr2);


            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                            "testQuery",
                            qos,
                            project1,
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
