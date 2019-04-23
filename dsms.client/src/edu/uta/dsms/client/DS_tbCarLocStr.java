/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uta.dsms.client;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import edu.uta.dsms.client.comm.*;
import edu.uta.dsms.core.streams.*;
// import edu.uta.dsms.core.streams.DataSource;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.Socket;

/**
 *
 * @author Tim
 */
public class DS_tbCarLocStr {

    public static void main(String args[])
    {
        String name = "tbCarLocStr";

		FileDataStream dstr = new FileDataStream("../../Test/tbCarLocStr.txt");

		DataStreamDefinition dsd = new DataStreamDefinition();

		dsd.addField(name + ".CarID", "number(long)", "0");
		dsd.addField(name + ".Speed", "varchar", "1");
		dsd.addField(name + ".ExpWay", "varchar", "2");
		dsd.addField(name + ".Lane", "varchar", "3");
		dsd.addField(name + ".Dir", "varchar", "4");
		dsd.addField(name + ".X-Pos", "number(long)", "5");
		dsd.addField(name + ".tbSourceTS", "number(long)", "5");
		dsd.addField(name + ".tbSystemTS", "number(long)", "6");

		DataSource ds = new DataSource(name, dsd, dstr);

        System.out.println("Object Serialization to XML *****************");
        System.out.println(DataSourceBuilder.toXML(ds));
        System.out.println("*********************************************");

        try
        {
            DataSourceComm dsc = new DataSourceComm(new StringReader(DataSourceBuilder.toXML(ds)));

            Socket responseSock = dsc.execute(new ServerDefinition("localhost", 8000));

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
