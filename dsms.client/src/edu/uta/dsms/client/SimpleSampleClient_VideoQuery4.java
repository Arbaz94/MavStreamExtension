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
public class SimpleSampleClient_VideoQuery4
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
            
            CQ_Stream tbVideoRel = new CQ_Stream(5, "tbVideoDataItlab", "tbVideoDataItlab");
            CQ_Stream tbVideoRel2 = new CQ_Stream(6, "tbVideoDataParking", "tbVideoDataParking");
            CQ_Stream tbVideoRel3 = new CQ_Stream(5, "tbVideoTest1", "tbVideoTest1");
            CQ_Stream tbVideoRel4 = new CQ_Stream(8, "tbVideoTest2", "tbVideoTest2");
            CQ_Stream tbVideoRel5 = new CQ_Stream(6, "tbVideoDataParking", "tbVideoDataParking");

            CQ_Project root= new CQ_Project(6,"project","tbVideoTest1.PrevObjectId,tbVideoTest1.FrameId");//,tbVideoTest1.BoundingBox");//Lab.tbSourceTS,,Park.tbSourceTS");

            CQ_Project root_2= new CQ_Project(6,"project2","tbVideoTest2.PrevObjectId,tbVideoTest2.FrameId");//Lab.tbSourceTS,,Park.tbSourceTS");


            CQ_Project project1= new CQ_Project(6,"project1","t1.PrevObjectId,tbMovement");//,tbVideoTest1.ObjectId,tbVideoTest1.PrevObjectId");//Lab.tbSourceTS,,Park.tbSourceTS");

            CQ_Select   select1  = new CQ_Select(1, "tbVideoTest1","tbVideoTest1", "tbVideoTest1.FrameId == '16'");
            CQ_Select   select2  = new CQ_Select(2, "tbVideoTest2","tbVideoTest2", "tbVideoTest2.FrameId == '1'");
            CQ_Select   select3  = new CQ_Select(1, "selectPark1","selectPark1", "t2.tbSourceTS <= '20'");

            CQ_OrderBy order1 = new CQ_OrderBy(7,"order","tbVideoDataItlab.PrevObjectId","asc");

            CQ_Join  join1 = new CQ_Join(4,
                    "joinStreams",
                    "tbVideoTest1.PrevObjectId == tbVideoTest2.PrevObjectId");// && t1.PrevObjectId < t2.FrameId &&  tbVideoDataParking.PrevObjectId < tbVideoDataItlab.FrameId");

            CQ_Join  join2 = new CQ_Join(4,
                    "joinStreams",
                    "tbVideoDataParking.FrameId == tbVideoDataItlab.FrameId");// &&  tbVideoDataParking.PrevObjectId == tbVideoDataItlab.PrevObjectId");// && tbVideoDataParking.PrevObjectId == tbVideoDataParking.PrevObjectId");


            CQ_GroupBy  groupBy1 = new CQ_GroupBy(2,
                    "groupBy",
                    "tbVideoDataItlab.PrevObjectId",
                    "arrable",
                    "tbVideoDataItlab.PrevObjectId",
                    "arrable");

            CQ_OrderBy orderBy3 = new CQ_OrderBy(3,"orderby","tbVideoTest1.FrameId","desc");

            CQ_GroupBy  groupBy2 = new CQ_GroupBy(2,
                    "groupBy",
                    "tbVideoTest1.PrevObjectId",
                    "arrable",
                    "tbVideoTest1.PrevObjectId",
                    "tbVideoTest1.PrevObjectId");

            CQ_GroupBy  groupBy3 = new CQ_GroupBy(2,
                    "groupBy",
                    "tbVideoTest2.PrevObjectId",
                    "arrable",
                    "tbVideoTest2.PrevObjectId",
                    "tbVideoTest2.PrevObjectId");

            CQ_Filter filter1 = new CQ_Filter(8,"filter","tbVideoDataItlab.PrevObjectId","tbVideoDataItlab.FrameId","flast");

            CQ_Filter filter2 = new CQ_Filter(8,"filter2","tbVideoTest1.PrevObjectId","tbVideoTest1.FrameId","first");

            CQ_Filter filter3 = new CQ_Filter(8,"filter3","tbVideoTest2.PrevObjectId","tbVideoTest2.FrameId","first");

            CQ_Direction direct1 = new CQ_Direction(9,"movement","t1.PrevObjectId","t1.BoundingBox");

            CQ_Arrable arrable1= new CQ_Arrable(10,"arrable","tbVideoTest1.PrevObjectId","tbVideoTest1.PrevObjectId");
            CQ_Arrable arrable2= new CQ_Arrable(10,"arrable","tbVideoTest2.PrevObjectId","tbVideoTest2.PrevObjectId");

            ArrayList<CQ_Stream> arr=new ArrayList<CQ_Stream>();
            ArrayList<CQ_Stream> arr2=new ArrayList<CQ_Stream>();


            root.addInput(filter2);
            root_2.addInput(tbVideoRel4);
            arr.add(filter2);
            arr.add(filter3);

            arr2.add(tbVideoRel2);
            arr2.add(tbVideoRel);

            join1.addInput(filter2);
            join1.addInput(filter3);


           select1.addInput(tbVideoRel3);
           select2.addInput(tbVideoRel4);
           select3.addInput(tbVideoRel4);
           //join1.setInputs(arr);
           join2.setInputs(arr2);
           order1.addInput(filter1);
           groupBy1.addInput(tbVideoRel);
           project1.addInput(direct1);
           filter1.addInput(groupBy1);
           groupBy2.addInput(tbVideoRel3);

            groupBy3.addInput(tbVideoRel4);
           filter2.addInput(arrable1);
           filter3.addInput(arrable2);
           orderBy3.addInput(filter2);
           direct1.addInput(arrable1);

           arrable1.addInput(tbVideoRel3);
           arrable2.addInput(tbVideoRel4);
         
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
