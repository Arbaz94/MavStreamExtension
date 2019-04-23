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
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Mayur
 */
public class SimpleSample_Multi_Dim_Join
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
            
            CQ_Stream tbVideoRel = new CQ_Stream(5, "tbMultiDimenionData", "tbMultiDimenionData");
            CQ_Stream tbVideoRel2 = new CQ_Stream(6, "tbMultiDimenionData2", "tbMultiDimenionData2");

            CQ_Project root= new CQ_Project(6,"project","tbMultiDimenionData.FrameId,tbMultiDimenionData.ObjectId");//,tbMultiDimenionData2.FrameId,tbMultiDimenionData2.ObjectId");

            CQ_Join  join1 = new CQ_Join(4,
                    "joinStreams",
                    tbVideoRel2,
                    tbVideoRel,
                    "tbMultiDimenionData2.multidimvector == tbMultiDimenionData.multidimvector");
                   // "tbMultiDimenionData2.multidimvector == tbMultiDimenionData.multidimvector");
            
            CQ_Select   select1  = new CQ_Select(1, "select", "tbMultiDimenionData.PrevObjectId == '1' ");

            CQ_Filter filter1 = new CQ_Filter(8,"filter","tbMultiDimenionData.PrevObjectId","tbMultiDimenionData.FrameId","first");

            CQ_Filter filter2 = new CQ_Filter(8,"filter","tbMultiDimenionData2.PrevObjectId","tbMultiDimenionData2.FrameId","last");


            CQ_sMatch match1=new CQ_sMatch(7,"sMatch","tbMultiDimenionData.multidimvector","tbMultiDimenionData2.multidimvector","1.5");
            
            CQ_GroupBy  groupBy1 = new CQ_GroupBy(2,
                    "groupBy",
                    "tbVideoDataItlab.PrevObjectId",
                    "arrable",
                    "tbVideoDataItlab.PrevObjectId",
                    "arrable");

            CQ_Arrable arrable1 = new CQ_Arrable(2,"arrable1","tbMultiDimenionData.PrevObjectId","tbMultiDimenionData.FrameId");

            CQ_Arrable arrable2 = new CQ_Arrable(2,"arrable1","tbMultiDimenionData2.PrevObjectId","tbMultiDimenionData2.FrameId");


            CQ_GroupBy  groupBy2 = new CQ_GroupBy(2,
                    "groupBy",
                    "tbMultiDimenionData.PrevObjectId",
                    "arrable",
                    "tbMultiDimenionData.PrevObjectId",
                    "arrable");

            CQ_GroupBy  groupBy3 = new CQ_GroupBy(2,
                    "groupBy",
                    "tbMultiDimenionData2.PrevObjectId",
                    "arrable",
                    "tbMultiDimenionData2.PrevObjectId",
                    "arrable");
            
           root.addInput(filter1);
           
           groupBy1.addInput(tbVideoRel2);

           match1.addInput(filter1);
           match1.addInput(filter2);

           arrable1.addInput(tbVideoRel);
            arrable2.addInput(tbVideoRel2);

           filter1.addInput(arrable1);
           groupBy2.addInput(tbVideoRel);

           filter2.addInput(arrable2);
            groupBy3.addInput(tbVideoRel2);


           select1.addInput(tbVideoRel);
           
          // select1.addInput(tb);
         
           CQ_ClientQuery cq = new CQ_ClientQuery(
    		   new CQ_ContinuousQuery(
    				   "testQuery",
    				   qos,
    				   root,
    				   startTime,
    				   endTime,
    				   Long.parseLong("0"),Long.parseLong("50"),Long.parseLong("50"),
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
