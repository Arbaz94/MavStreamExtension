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
public class Thesis_Query4
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
            CQ_Stream Lab_exit = new CQ_Stream(2,"Lab_exit","Lab_exit");

            CQ_Select select1 = new CQ_Select(3,"select1","Lab_entry.PrevObjectId == '1' || Lab_entry.PrevObjectId == '58' || Lab_entry.PrevObjectId == '120' || Lab_entry.PrevObjectId == '329' || Lab_entry.PrevObjectId == '409' || Lab_entry.PrevObjectId == '504' || Lab_entry.PrevObjectId == '679'");

            CQ_Project project1= new CQ_Project(20,"project_1","Lab_entry.FrameId,Lab_entry.ObjectId,Lab_entry.PrevObjectId");//,Lab_entry.FrameId,Lab_entry.ObjectId");

            CQ_Project project2= new CQ_Project(20,"project_2","Lab_exit.FrameId,Lab_exit.ObjectId,Lab_exit.PrevObjectId");//,Lab_entry.FrameId,Lab_entry.ObjectId");


            CQ_Select select2 = new CQ_Select(3,"select2","Lab_exit.PrevObjectId == '15' || Lab_exit.PrevObjectId == '217' || Lab_exit.PrevObjectId == '236' || Lab_exit.PrevObjectId == '383' || Lab_exit.PrevObjectId == '235' || Lab_exit.PrevObjectId == '530' || Lab_exit.PrevObjectId == '504' || Lab_exit.PrevObjectId == '695'");



            CQ_Arrable arrable1 = new CQ_Arrable(3,"arrable1","Lab_entry.PrevObjectId","Lab_entry.FrameId");
            CQ_Arrable arrable2 = new CQ_Arrable(4,"arrable2","Lab_exit.PrevObjectId","Lab_exit.FrameId");

            CQ_Filter filter1 = new CQ_Filter(6,"filter1","Lab_entry.PrevObjectId","Lab_entry.FrameId","first");

            CQ_Filter filter2 = new CQ_Filter(6,"filter2","Lab_exit.PrevObjectId","Lab_exit.FrameId","first");

            CQ_OrderBy order2 = new CQ_OrderBy(7,"order2","Lab.exit.PrevObjectId","asc");

            CQ_cJoin cjoin1 = new CQ_cJoin(5,"cjoin1","Lab_entry.PrevObjectId","Lab_entry.FrameId","Lab_exit.PrevObjectId","Lab_exit.FrameId","sMatch(Lab_entry.FV,Lab_entry.FV,0.0005)");

            CQ_sMatch match1 = new CQ_sMatch(5,"match1","Lab_entry.FV","Lab_exit.FV","0.0005");

            CQ_Select select_ts=new CQ_Select(10,"select_ts","Lab_exit.tbSourceTS > '10' && Lab_entry.tbSourceTS < '50'");

            CQ_Project project_result=new CQ_Project(8,"project_result","Lab_entry.FrameId,Lab_entry.PrevObjectId,Lab_exit.FrameId,Lab_exit.PrevObjectId");

            arrable1.addInput(Lab_entry);
            arrable2.addInput(Lab_exit);
            cjoin1.addInput(arrable1);
            cjoin1.addInput(arrable2);
            match1.addInput(select1);
            match1.addInput(select2);
            filter1.addInput(arrable1);
            filter2.addInput(arrable2);
            project_result.addInput(match1);
            select_ts.addInput(match1);
            project1.addInput(filter1);
            project2.addInput(filter2);
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
            System.exit(-1);
        }
        catch (Throwable t)
        {
            System.err.println("Caught: " + t);
            t.printStackTrace();
        }
    }



}
