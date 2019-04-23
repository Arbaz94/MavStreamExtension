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
 *
 *  Find if a car crossed Point A also crossed Point B
 */
public class Thesis_Query7
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

            CQ_Select select1 = new CQ_Select(3,"select1","Car_Entry.PrevObjectId == '1' || Car_Entry.PrevObjectId == '58' || Car_Entry.PrevObjectId == '120' || Car_Entry.PrevObjectId == '329' || Car_Entry.PrevObjectId == '409' || Car_Entry.PrevObjectId == '504' || Car_Entry.PrevObjectId == '679'");

            CQ_Project project1= new CQ_Project(20,"project_1","Car_Entry.FrameId,Car_Entry.ObjectId,Car_Entry.PrevObjectId");//,Car_Entry.FrameId,Car_Entry.ObjectId");

            CQ_Project project2= new CQ_Project(20,"project_2","Car_Exit.FrameId,Car_Exit.ObjectId,Car_Exit.PrevObjectId");//,Car_Entry.FrameId,Car_Entry.ObjectId");


            CQ_Select select2 = new CQ_Select(3,"select2","Car_Exit.PrevObjectId == '15' || Car_Exit.PrevObjectId == '217' || Car_Exit.PrevObjectId == '236' || Car_Exit.PrevObjectId == '383' || Car_Exit.PrevObjectId == '235' || Car_Exit.PrevObjectId == '530' || Car_Exit.PrevObjectId == '504' || Car_Exit.PrevObjectId == '695'");



            CQ_Arrable arrable1 = new CQ_Arrable(3,"arrable1","Car_Entry.PrevObjectId","Car_Entry.FrameId");
            CQ_Arrable arrable2 = new CQ_Arrable(4,"arrable2","Car_Exit.PrevObjectId","Car_Exit.FrameId");

            CQ_Filter filter1 = new CQ_Filter(6,"filter1","Car_Entry.PrevObjectId","Car_Entry.FrameId","first");

            CQ_Filter filter2 = new CQ_Filter(6,"filter2","Car_Exit.PrevObjectId","Car_Exit.FrameId","first");

            CQ_OrderBy order2 = new CQ_OrderBy(7,"order2","Car.Exit.PrevObjectId","asc");

            CQ_cJoin cjoin1 = new CQ_cJoin(5,"cjoin1","Car_Entry.PrevObjectId","Car_Entry.FrameId","Car_Exit.PrevObjectId","Car_Exit.FrameId","sMatch(Car_Entry.FV,Car_Exit.FV,'0.2')");

            CQ_sMatch match1 = new CQ_sMatch(5,"match1","Car_Entry.FV","Car_Exit.FV","0.2");

            CQ_Select select_ts=new CQ_Select(10,"select_ts","Car_Exit.tbSourceTS > '10' && Car_Entry.tbSourceTS < '50'");

            CQ_Project project_result=new CQ_Project(8,"project_result","Car_Entry.FrameId,Car_Entry.PrevObjectId,Car_Exit.FrameId,Car_Exit.PrevObjectId");

            arrable1.addInput(Car_Entry);
            arrable2.addInput(Car_Exit);
            cjoin1.addInput(arrable1);
            cjoin1.addInput(arrable2);
            match1.addInput(filter1);
            match1.addInput(filter2);
            filter1.addInput(arrable1);
            filter2.addInput(arrable2);
            project_result.addInput(cjoin1);
            select_ts.addInput(match1);
            project1.addInput(arrable1);
            project2.addInput(arrable2);
            select1.addInput(filter1);
            select2.addInput(filter2);
            order2.addInput(filter2);



            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                            "testQuery",
                            qos,
                            project_result,
                            startTime,
                            endTime,
                            Long.parseLong("0"),Long.parseLong("500"),Long.parseLong("500"),
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
           // System.Exit(-1);
        }
        catch (Throwable t)
        {
            System.err.println("Caught: " + t);
            t.printStackTrace();
        }
    }



}
