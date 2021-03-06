//making changes sdas testing git
// making changes as demo in lab
//adding xyz
//adding abc
/**
 *
 * Class: QueryBuilder
 * Original Author: Tim Dockins (timothy.dockins@mavs.uta.edu)
 *
 * The purpose of this class is to read a client query, in XML format,
 * and convert it to a QueryPlanObject which may then be serialized,
 * sent to the DSMS server, deserialized and instantianted there.
 *
 * History:
 * 2009-04-07 [td] modified main (test code) so that the corrected form of qos was a collection
 */

package edu.uta.dsms.client.comm;

import edu.uta.dsms.core.query.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import java.util.ArrayList;
import java.util.Date;


/**
 * The QueryBuilder class may be tested by simply executing the class.
 * The code for the deserialization test is an example of the procedure
 * used for utilizing this code.
 *
 * Generally, the static method toXML should be used to generate a serialized
 * version of a CQ_ClientQuery object.  This object can then be sent to the dsms server
 * and deserialized by the QueryParser method
 *
 * The query can also be saved in a readable format on the client side for re-use.
 *
 * @author Tim Dockins (timothy.dockins@mavs.uta.edu)
 */
public class QueryBuilder
{

    /**
     * buildDom4jXStreamForRead
     * This method builds an XStream using the Dom4J api for manipulating
     * xml.  More information on Dom4j can be found at Sourceforge
     * http://sourceforge.net/projects/dom4j/
     * @return
     */
    public static XStream buildDom4jXStreamForRead() 
    {
        Dom4JDriver dom4jDriver = new Dom4JDriver();
        XStream xstream = new XStream(dom4jDriver);
        CQ_QueryDefinition.initQueryXStream(xstream);
        return xstream;
    }

   
//    public static CQ_ClientQuery loadClientQuery(String filename)
//            throws Throwable
//    {
//        XStream xstream = buildDom4jXStreamForRead();
//        FileReader fread = new FileReader(filename);
//        CQ_ClientQuery clientQuery = (CQ_ClientQuery) xstream.fromXML(fread);
//
//        return clientQuery;
//    }
//
//    public static CQ_ClientQuery loadClientQuery(InputStream input)
//            throws Throwable
//    {
//        XStream xstream = buildDom4jXStreamForRead();
//        CQ_ClientQuery clientQuery = (CQ_ClientQuery) xstream.fromXML(input);
//
//        return (clientQuery);
//    }

    public static String toXML(CQ_ClientQuery clientQuery)
    {
        XStream xstream = new XStream();
        CQ_QueryDefinition.initQueryXStream(xstream);
        return(xstream.toXML(clientQuery));
    }

    /*
     * The main function provide a unit test for this class and the CQ_ClientQuery
     * subsystem in general.
     */
    public static void main(String args[])
    {
        // point filename to a well-formed xml file
        // ensure that the dtd is available.
        try
        {
            // Setup QOS
            ArrayList<CQ_QOS> qos = new ArrayList<CQ_QOS>();

            ArrayList<CQ_Tuple> tuples = new ArrayList<CQ_Tuple>();
            CQ_Tuple t1 = new CQ_Tuple("1,1");
            qos.add(new CQ_QOS("latency","ms",1,10,tuples));
            qos.add(new CQ_QOS("memory", "ms", 1, 10, tuples));
            qos.add(new CQ_QOS("througput", "ms", 1, 10, tuples));

            tuples.add(t1);

            // setup operators and streams
            CQ_Project root = new CQ_Project(0, "sampleProject", "field1,field2,field3");
            CQ_Select select1 = new CQ_Select(1, "sampleSelect", "field1 > 30");
            CQ_Stream stream1 = new CQ_Stream(2, "sampleStream", "sampleStream");
            select1.addInput(stream1);
            root.addInput(select1);

            CQ_QOS qos2 = new CQ_QOS();
            qos2.setTuples(tuples);

            Date date = new Date();

            CQ_ClientQuery cq = new CQ_ClientQuery(
                    new CQ_ContinuousQuery(
                        "testQuery",
                        qos,
                        root,
                        date.getTime(),
                        date.getTime() + (30 * 60000),
                        Long.parseLong("10000"),Long.parseLong("10000"),Long.parseLong("10000"),
                        "strategy",
                        "null",                 // ARGH!!!!
                        10));
            

            // take what was read and serialize it back to XML
            System.out.println("Object Serialization to XML *****************");
            System.out.println(QueryBuilder.toXML(cq));
            System.out.println("*********************************************");
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

