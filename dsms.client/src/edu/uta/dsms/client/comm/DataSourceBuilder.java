
// making dummy change sdas
/**
 *
 * Class: DataSourceBuilder
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

import edu.uta.dsms.client.DataSourceComm;
import edu.uta.dsms.core.query.*;
import edu.uta.dsms.core.streams.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;


/**
 * Generally, the static method toXML should be used to generate a serialized
 * version of a DataSource object.  This object can then be sent to the dsms server
 * and deserialized by the DataSourceParser method
 *
 * The query can also be saved in a readable format on the client side for re-use.
 *
 * @author Tim Dockins (timothy.dockins@mavs.uta.edu)
 */
public class DataSourceBuilder
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
        DS_Definition.initQueryXStream(xstream);
        return xstream;
    }

    public static String toXML(DataSource ds)
    {
        XStream xstream = new XStream();
        DS_Definition.initQueryXStream(xstream);
        return(xstream.toXML(ds));
    }

}

