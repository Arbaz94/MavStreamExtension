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
public class Lab_Dataset_test
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

            CQ_Stream test_stream1 = new CQ_Stream(1,"Lab_entry","Lab_entry");
            CQ_Stream test_stream2 = new CQ_Stream(2,"Lab_exit","Lab_exit");
          //  CQ_Stream Lobby_Rentry = new CQ_Stream(2,"Lobby_Rentry","Lobby_Rentry");

            CQ_Stream Lab_entry = new CQ_Stream(1,"Lab_entry","Lab_entry");
            CQ_Stream Lab_exit = new CQ_Stream(2,"Lab_exit","Lab_exit");
            CQ_Stream Lobby_Rentry = new CQ_Stream(2,"Lobby_Rentry","Lobby_Rentry");
            CQ_Stream Lab_entry_exit = new CQ_Stream(2,"Lab_entry_exit","Lab_entry_exit");

            CQ_Project project1= new CQ_Project(3,"project_1","Lab_entry.FrameId,Lab_entry.ObjectId,Lab_entry.PrevObjectId");//,Lab_entry.FrameId,Lab_entry.ObjectId");
            CQ_Project project2= new CQ_Project(4,"project_2","Lab_exit.FrameId,Lab_exit.ObjectId,Lab_exit.PrevObjectId");
            CQ_Project project4= new CQ_Project(16,"project_4","Lobby_Rentry.FrameId,Lobby_Rentry.ObjectId,Lobby_Rentry.PrevObjectId");
            CQ_Project project5= new CQ_Project(16,"project_5","Lab_entry_exit.FrameId,Lab_entry_exit.ObjectId,Lab_entry_exit.PrevObjectId,Lab_entry_exit.tbSourceTS");



            CQ_Project project3=new CQ_Project(5,"project_3","Lab_entry.FrameId,Lab_entry.PrevObjectId,Lab_exit.FrameId,Lab_exit.PrevObjectId");

            CQ_Select select1 = new CQ_Select(13,"select1","Lab_entry.PrevObjectId == '267'");// || Lab_entry.PrevObjectId == '120' || Lab_entry.PrevObjectId == '58' || Lab_entry.PrevObjectId == '329' || Lab_entry.PrevObjectId == '409' || Lab_entry.PrevObjectId == '504' || Lab_entry.PrevObjectId == '679'");


            CQ_Select select2 = new CQ_Select(20,"select2","Lab_exit.PrevObjectId == '15' || Lab_exit.PrevObjectId == '217' || Lab_exit.PrevObjectId == '236' || Lab_exit.PrevObjectId == '383' || Lab_exit.PrevObjectId == '530' || Lab_exit.PrevObjectId == '695' ");


            CQ_sMatch match1=new CQ_sMatch(5,"match1","Lab_entry.FV","Lab_exit.FV","0.00032");

            CQ_Join join1=new CQ_Join(6,"join1","sMatch(Lab_entry.FV,Lab_exit.FV,'0.005') && Lab_exit.ObjectId > Lab_entry.ObjectId");

            CQ_Arrable arrable1=new CQ_Arrable(7,"arrable1","Lab_entry.PrevObjectId","Lab_entry.FrameId");

            CQ_Arrable arrable2=new CQ_Arrable(8,"arrable2","Lab_exit.PrevObjectId","Lab_exit.FrameId");

            CQ_Arrable arrable3=new CQ_Arrable(15,"arrable3","Lobby_Rentry.PrevObjectId","Lobby_Rentry.FrameId");

            CQ_Arrable arrable4=new CQ_Arrable(15,"arrable4","Lab_entry_exit.PrevObjectId","Lab_entry_exit.FrameId");


            CQ_Filter filter1=new CQ_Filter(9,"Filter1","Lab_entry.PrevObjectId","Lab_entry.FrameId","first");


            CQ_Filter filter2=new CQ_Filter(10,"Filter2","Lab_exit.PrevObjectId","Lab_exit.FrameId","first");

            CQ_Filter filter3=new CQ_Filter(10,"Filter3","Lobby_Rentry.PrevObjectId","Lobby_Rentry.FrameId","both");


            CQ_Filter filter4=new CQ_Filter(10,"Filter4","Lab_entry_exit.PrevObjectId","Lab_entry_exit.FrameId","both");


            CQ_sMatch match2=new CQ_sMatch(11,"match1","Lab_entry.PrevObjectId","Lab_exit.PrevObjectId","0.001");

            CQ_cJoin cjoin1=new CQ_cJoin(12,"cjoin1","Lab_entry.PrevObjectId","Lab_entry.FrameId","Lab_exit.PrevObjectId","Lab_exit.FrameId","Lab_exit.tbSourceTS > Lab_entry.tbSourceTS && sMatch(Lab_entry.FV,Lab_exit.FV,0.0005)");

            CQ_OrderBy order1=new CQ_OrderBy(17,"order1","Lab_entry.PrevObjectId");

            CQ_GroupBy  groupBy1 = new CQ_GroupBy(14,
                    "groupBy",
                    "Lab_entry.PrevObjectId",
                    "count",
                    "Lab_entry.PrevObjectId",
                    "count");

            project1.addInput(arrable1);
            project2.addInput(arrable2);
            ArrayList arr=new ArrayList();
            ArrayList arr2=new ArrayList();
            arr.add(Lab_entry);
            arr.add(Lab_exit);
            arr2.add(arrable1);
            arr2.add(arrable2);
            match1.setInputs(arr);
            project3.addInput(cjoin1);
            join1.setInputs(arr);
            arrable1.addInput(Lab_entry);
            arrable2.addInput(Lab_exit);
            arrable3.addInput(Lobby_Rentry);
            filter1.addInput(arrable1);
            filter2.addInput(arrable2);
            match2.setInputs(arr);
           // cjoin1.setInputs(arr2);
            select1.addInput(Lab_entry);
            select2.addInput(Lab_exit);
            groupBy1.addInput(Lab_entry);
            project4.addInput(filter3);
            order1.addInput(project3);
            filter3.addInput(arrable3);
            arrable4.addInput(Lab_entry_exit);
            filter4.addInput(arrable4);
            project5.addInput(Lab_entry_exit);

            cjoin1.addInput(arrable1);
            cjoin1.addInput(arrable2);


            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                            "testQuery",
                            qos,
                            join1,
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
