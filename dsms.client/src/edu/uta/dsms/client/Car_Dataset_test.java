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
public class Car_Dataset_test
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


            CQ_Stream Car_Entry = new CQ_Stream(1,"Car_Entry","Car_Entry");
            CQ_Stream Car_Exit = new CQ_Stream(2,"Car_Exit","Car_Exit");

            CQ_Project project1= new CQ_Project(3,"project_1","Car_Entry.FrameId,Car_Entry.ObjectId,Car_Entry.PrevObjectId");//,Lab_entry.FrameId,Lab_entry.ObjectId");
            CQ_Project project2= new CQ_Project(4,"project_2","Car_Exit.FrameId,Car_Exit.ObjectId,Car_Exit.PrevObjectId");

            CQ_Arrable arrable1= new CQ_Arrable(5,"arrable1","Car_Entry.PrevObjectId","Car_Entry.FrameId");

            CQ_Arrable arrable2= new CQ_Arrable(6,"arrable2","Car_Exit.PrevObjectId","Car_Exit.FrameId");

            CQ_Filter filter1 = new CQ_Filter(7,"filter1","Car_Entry.PrevObjectId","Car_Entry.FrameId","first");

            CQ_Filter filter2 = new CQ_Filter(8,"filter2","Car_Exit.PrevObjectId","Car_Exit.FrameId","first");

            CQ_sMatch match1 = new CQ_sMatch(9,"match1","Car_Entry.FV","Car_Exit.FV","0.2");

            CQ_cJoin cjoin1 = new CQ_cJoin(10,"cjoin1","Car_Entry.PrevObjectId","Car_Entry.FrameId","Car_Exit.PrevObjectId","Car_Exit.FrameId", "sMatch(Car_Entry.FV,Car_Exit.FV,0.2)");

            CQ_Join join1=new CQ_Join(11,"join1","Car_Entry.PrevObjectId == Car_Exit.PrevObjectId");

            project1.addInput(Car_Entry);
            project2.addInput(Car_Exit);
            arrable1.addInput(Car_Entry);
            arrable2.addInput(Car_Exit);
            filter1.addInput(arrable1);
            filter2.addInput(arrable2);
            match1.addInput(filter1);
            match1.addInput(filter2);
            join1.addInput(filter1);
            join1.addInput(filter2);

            cjoin1.addInput(arrable1);
            cjoin1.addInput(arrable2);



            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                            "testQuery",
                            qos,
                            project1,
                            startTime,
                            endTime,
                            Long.parseLong("0"),Long.parseLong("5000"),Long.parseLong("5000"),
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
