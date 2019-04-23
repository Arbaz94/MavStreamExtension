/**
 * Class: SimpleSampleClient_VideoQuery1
 * Original Author: Mayur Arora (mayur.arora@mavs.uta.edu)
 *
 * Find the direction of the moving Person
 *
 *
 * Data set used - ERB_DIRECTION.txt
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
public class Thesis_Query3
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



            CQ_Stream ERB_DIRECTION = new CQ_Stream(1,"ERB_DIRECTION","ERB_DIRECTION");


            CQ_Project project1= new CQ_Project(3,"project_1","ERB_DIRECTION.FrameId,ERB_DIRECTION.ObjectId,ERB_DIRECTION.PrevObjectId,ERB_DIRECTION.BB");//,Lab_entry.FrameId,Lab_entry.ObjectId");
           // CQ_Select select1 = new CQ_Select(13,"select1","sMatch(Lab_entry.FV,'[[0.000000,0.000000,0.000000,0.000000,0.000000,0.000032,0.000000,0.000096,0.000193,0.000128,0.000128,0.000513,0.000257,0.001059,0.001861,0.001893,0.002503,0.003626,0.004171,0.004974,0.006386,0.007124,0.008921,0.010653,0.017007,0.010140,0.010076,0.009691,0.011006,0.011006,0.011712,0.011295,0.011744,0.011167,0.010782,0.010814,0.012033,0.011167,0.012707,0.015049,0.013670,0.014183,0.013252,0.014985,0.013541,0.013156,0.011488,0.011391,0.012867,0.011808,0.011391,0.011873,0.010942,0.011873,0.009466,0.008439,0.008311,0.008086,0.007027,0.008439,0.008568,0.007348,0.006835,0.007701,0.006289,0.007477,0.008311,0.007092,0.006610,0.007477,0.005808,0.007284,0.007894,0.008600,0.006674,0.007701,0.007669,0.006033,0.006546,0.006674,0.007316,0.006995,0.006482,0.006482,0.006353,0.006129,0.006418,0.006065,0.006033,0.005904,0.007637,0.007797,0.006771,0.005968,0.008183,0.006450,0.007894,0.007316,0.006835,0.007316,0.006450,0.005968,0.006129,0.006065,0.004364,0.004075,0.004107,0.004364,0.005712,0.004685,0.005776,0.004781,0.003498,0.003305,0.003241,0.002984,0.004139,0.003851,0.003851,0.003530,0.003369,0.002728,0.001957,0.002439,0.002535,0.002920,0.002599,0.002888,0.002792,0.001925,0.002824,0.002663,0.002375,0.002278,0.002118,0.001765,0.002375,0.001925,0.001861,0.001925,0.001669,0.001316,0.001508,0.001989,0.001316,0.001476,0.001380,0.001316,0.001187,0.001348,0.001540,0.001348,0.001284,0.001348,0.001444,0.001669,0.001476,0.001701,0.001187,0.001380,0.000995,0.000738,0.000834,0.000578,0.000802,0.001027,0.000738,0.000802,0.000898,0.000770,0.000834,0.000674,0.000674,0.000898,0.000802,0.000706,0.000866,0.000995,0.000866,0.000738,0.000834,0.000706,0.000642,0.000578,0.000802,0.000513,0.000738,0.000898,0.001380,0.001091,0.001123,0.000898,0.000770,0.000898,0.000963,0.001316,0.001380,0.001091,0.001380,0.001123,0.000995,0.000995,0.000610,0.000898,0.000898,0.000866,0.000898,0.000866,0.000802,0.000674,0.000931,0.000738,0.000963,0.000578,0.000578,0.000513,0.000449,0.000385,0.000449,0.000834,0.000417,0.000866,0.000481,0.000578,0.000449,0.000385,0.000513,0.000417,0.000513,0.000449,0.000513,0.000546,0.000449,0.000674,0.000353,0.000289,0.000546,0.000449,0.000385,0.000289,0.000128,0.000289,0.000578,0.000578,0.000834,0.000481,0.000546,0.000738,0.000834,0.001059,0.001123,0.002342,0.001701,0.001733,0.001476,0.008664],[0.000032,0.000000,0.000000,0.000000,0.000032,0.000096,0.000096,0.000064,0.000353,0.000385,0.000449,0.000610,0.001412,0.001412,0.002471,0.003209,0.004685,0.005583,0.007316,0.008247,0.015338,0.015017,0.010461,0.010878,0.009755,0.011776,0.011905,0.012867,0.013830,0.014183,0.014729,0.014632,0.014729,0.014376,0.013830,0.014536,0.018611,0.016205,0.016012,0.016558,0.016911,0.020344,0.015787,0.014311,0.013894,0.011584,0.010814,0.009691,0.009466,0.008824,0.009530,0.009306,0.009691,0.009081,0.009915,0.007541,0.007926,0.008568,0.008503,0.007990,0.008183,0.006995,0.007059,0.008022,0.007701,0.007958,0.006739,0.006899,0.007477,0.006129,0.006225,0.007252,0.007220,0.007765,0.007444,0.007477,0.006482,0.006642,0.007637,0.006931,0.008054,0.007412,0.008439,0.009402,0.007765,0.008792,0.008568,0.008568,0.007990,0.007284,0.005615,0.005551,0.005776,0.004910,0.005519,0.006129,0.005680,0.005166,0.004910,0.003786,0.003401,0.002375,0.002695,0.002535,0.003241,0.003337,0.003016,0.002567,0.002728,0.003530,0.002824,0.002888,0.002567,0.002599,0.002054,0.002214,0.002631,0.002567,0.002150,0.002246,0.001797,0.002695,0.002150,0.001572,0.001893,0.001123,0.001957,0.001476,0.001284,0.001187,0.001669,0.001701,0.001861,0.001572,0.001284,0.001765,0.001797,0.001669,0.001251,0.001444,0.001219,0.001508,0.001284,0.001059,0.001091,0.001251,0.000995,0.001316,0.001155,0.001219,0.000546,0.001027,0.000834,0.001059,0.000738,0.001059,0.000866,0.000931,0.001412,0.001123,0.001155,0.000898,0.000674,0.000995,0.000738,0.000802,0.000995,0.001187,0.001219,0.001733,0.001348,0.001540,0.001316,0.001412,0.001155,0.001219,0.001540,0.001091,0.001251,0.000963,0.000738,0.000738,0.000834,0.000738,0.000802,0.000931,0.001027,0.000898,0.000898,0.000770,0.000834,0.001091,0.000578,0.000674,0.001187,0.000898,0.000834,0.000449,0.000610,0.000417,0.000642,0.000898,0.000770,0.000513,0.000353,0.000674,0.000449,0.000674,0.000546,0.000417,0.000546,0.000738,0.000449,0.000481,0.000866,0.000353,0.000546,0.000321,0.000353,0.000417,0.000289,0.000321,0.000546,0.000417,0.000417,0.000353,0.000353,0.000321,0.000289,0.000385,0.000449,0.000417,0.000289,0.000289,0.000385,0.000449,0.000353,0.000610,0.000289,0.000321,0.000578,0.000546,0.000417,0.000417,0.000642,0.000321,0.000642,0.000963,0.001091,0.001027,0.001508,0.001637,0.002278,0.002086,0.001123,0.002086],[0.011038,0.002150,0.002439,0.003273,0.004011,0.004845,0.006289,0.007477,0.007637,0.007990,0.008985,0.010076,0.014247,0.013156,0.016044,0.014504,0.015980,0.018996,0.016750,0.019638,0.020793,0.022077,0.021628,0.022173,0.021403,0.021916,0.021820,0.020729,0.021371,0.020601,0.021210,0.019125,0.018996,0.018162,0.018226,0.018322,0.018900,0.016493,0.017296,0.017841,0.015467,0.016140,0.011552,0.010236,0.011103,0.010365,0.009274,0.008824,0.007926,0.006995,0.006963,0.006867,0.006835,0.005712,0.005776,0.005744,0.005134,0.005872,0.005359,0.005198,0.005102,0.004845,0.004268,0.004236,0.003947,0.004107,0.004171,0.004236,0.004268,0.004845,0.003433,0.003979,0.003658,0.003754,0.003401,0.003626,0.003177,0.003080,0.002856,0.003658,0.003113,0.002888,0.003466,0.003241,0.002695,0.002663,0.002695,0.002888,0.002599,0.002342,0.002246,0.002984,0.001669,0.001861,0.002054,0.002310,0.001989,0.001604,0.002214,0.001604,0.001861,0.001669,0.002054,0.001765,0.001572,0.001925,0.001669,0.001701,0.001989,0.001669,0.001604,0.001701,0.001284,0.001251,0.001412,0.001412,0.001508,0.001316,0.001412,0.001412,0.001540,0.001508,0.000898,0.000898,0.001155,0.001155,0.001284,0.000834,0.000931,0.001797,0.001123,0.001155,0.001348,0.001412,0.001187,0.000995,0.000931,0.000738,0.001027,0.000995,0.000898,0.000995,0.000931,0.000866,0.001059,0.000770,0.000931,0.000834,0.000770,0.000546,0.000674,0.000642,0.000866,0.000578,0.000866,0.000449,0.000706,0.000481,0.000706,0.000449,0.000802,0.000385,0.000546,0.000353,0.000257,0.000385,0.000353,0.000481,0.000160,0.000321,0.000481,0.000225,0.000128,0.000417,0.000321,0.000353,0.000481,0.000321,0.000225,0.000449,0.000257,0.000225,0.000417,0.000289,0.000160,0.000225,0.000225,0.000257,0.000257,0.000064,0.000225,0.000160,0.000193,0.000257,0.000225,0.000193,0.000225,0.000193,0.000193,0.000321,0.000225,0.000225,0.000064,0.000225,0.000096,0.000289,0.000096,0.000096,0.000225,0.000193,0.000160,0.000160,0.000321,0.000096,0.000193,0.000289,0.000064,0.000225,0.000128,0.000128,0.000225,0.000225,0.000289,0.000257,0.000160,0.000225,0.000225,0.000289,0.000225,0.000289,0.000225,0.000193,0.000289,0.000128,0.000417,0.000128,0.000321,0.000449,0.000289,0.000610,0.000193,0.000257,0.000096,0.000481,0.000481,0.000417,0.000578,0.000802,0.000866,0.000578,0.000610,0.000546,0.000449,0.000578,0.000289,0.001989]]',0.01 ");//|| Lab_entry.PrevObjectId == '120' || Lab_entry.PrevObjectId == '58' || Lab_entry.PrevObjectId == '329' || Lab_entry.PrevObjectId == '409' || Lab_entry.PrevObjectId == '504' || Lab_entry.PrevObjectId == '679'");

            CQ_Arrable arrable1=new CQ_Arrable(7,"arrable1","ERB_DIRECTION.PrevObjectId","ERB_DIRECTION.PrevObjectId");


            CQ_Direction direct1=new CQ_Direction(8,"direct1","ERB_DIRECTION.PrevObjectId","ERB_DIRECTION.BB");

            CQ_OrderBy order1=new CQ_OrderBy(17,"order1","ERB_DIRECTION.tbSourceTS","desc");

            CQ_GroupBy  groupBy1 = new CQ_GroupBy(14,
                    "groupBy1",
                    "Lab_entry.tbSourceTS",
                    "count",
                    "Lab_entry.tbSourceTS",
                    "Lab_entry.tbSourceTS");

            CQ_Project project_result= new CQ_Project(3,"project_result","ERB_DIRECTION.PrevObjectId,tbMovement");//,Lab_entry.FrameId,Lab_entry.ObjectId");



            project1.addInput(direct1);
            arrable1.addInput(ERB_DIRECTION);
            direct1.addInput(arrable1);
            project_result.addInput(order1);
            order1.addInput(direct1);


            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                            "testQuery",
                            qos,
                            project_result,
                            startTime,
                            endTime,
                            Long.parseLong("0"),Long.parseLong("100"),Long.parseLong("100"),
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
