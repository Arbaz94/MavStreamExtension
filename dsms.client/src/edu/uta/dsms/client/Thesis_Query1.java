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

// How many people entered every disjoint 15 seconds?

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
 *
 * How many people entered every disjoint 30 seconds ?
 *
 * Dataset used - it-lab-1.txt
 *
 */
public class Thesis_Query1
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



            CQ_Stream Lab_entry = new CQ_Stream(1,"Lab_entry","Lab_entry");


            CQ_Project project1= new CQ_Project(3,"project_1","Lab_entry.FrameId,Lab_entry.ObjectId,Lab_entry.PrevObjectId,Lab_entry.tbSourceTS,count(Lab_entry.tbSourceTS)");//,Lab_entry.FrameId,Lab_entry.ObjectId");
            CQ_Select select1 = new CQ_Select(13,"select1","Lab_entry.PrevObjectId == '1' ");//|| Lab_entry.PrevObjectId == '120' || Lab_entry.PrevObjectId == '58' || Lab_entry.PrevObjectId == '329' || Lab_entry.PrevObjectId == '409' || Lab_entry.PrevObjectId == '504' || Lab_entry.PrevObjectId == '679'");

            CQ_Arrable arrable1=new CQ_Arrable(7,"arrable1","Lab_entry.PrevObjectId","Lab_entry.FrameId");


            CQ_Filter filter1=new CQ_Filter(9,"Filter1","Lab_entry.PrevObjectId","Lab_entry.FrameId","both");


            CQ_OrderBy order1=new CQ_OrderBy(17,"order1","Lab_entry.tbSourceTS","desc");




            CQ_GroupBy  groupBy1 = new CQ_GroupBy(14,
                    "groupBy1",
                    "all",
                    "count",
                    "Lab_entry.tbSourceTS",
                    "Lab_entry.tbSourceTS");


            project1.addInput(groupBy1);
            arrable1.addInput(Lab_entry);
            order1.addInput(Lab_entry);
            filter1.addInput(arrable1);
            groupBy1.addInput(filter1);
            select1.addInput(Lab_entry);



            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                            "testQuery",
                            qos,
                            project1,
                            startTime,
                            endTime,
                            Long.parseLong("0"),Long.parseLong("30"),Long.parseLong("30"),
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
