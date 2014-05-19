package scans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//Andrew Baer 2014

/*This Java file takes a user specified range of IP addresses and runs Nmap with that range.
 * The Nmap output is saved to an xml file, which is then parsed for any open ports. A list
 * of all open ports, their associated IP's, and the service running on that port is saved
 * to a csv file.
 */

public class Scans {

	public static void main(String argv[]) {
		FileWriter writer = null;
		try {
			writer = new FileWriter("scansOutput.csv");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			writer.append("IP");
			writer.append(',');
			writer.append("openPort");
			writer.append(',');
			writer.append("service");
			writer.append('\n');

			Scanner sc = new Scanner(System.in);
			System.out.println("Specify IP range");
			String input = (sc).nextLine();
			sc.close();
			Process p = Runtime.getRuntime().exec(
					"cmd /c start nmap " + input + " -oX .xml");
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String s2;
			while ((s2 = stdOut.readLine()) != null) {
				// nothing or print
			}
			System.out.println("made it to writer");

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			File xmlIn = new File(".xml");
			Document doc = docBuilder.parse(xmlIn);

			// normalize text representation
			doc.getDocumentElement().normalize();
		

			NodeList portNodes = doc.getElementsByTagName("port");

			for (int s = 0; s < portNodes.getLength(); s++) {

				Node firstPersonNode = portNodes.item(s);
			
				if (!firstPersonNode.getChildNodes().item(0).getAttributes()
						.getNamedItem("state").getTextContent().equals("open")) {
					
					continue;
				}
				Node addr = firstPersonNode.getParentNode();
				addr = addr.getPreviousSibling().getPreviousSibling()
						.getPreviousSibling().getPreviousSibling();
				String addrString = addr.getAttributes().item(0)
						.getTextContent();

				String portnum = firstPersonNode.getAttributes()
						.getNamedItem("portid").getTextContent();
			

				String service = firstPersonNode.getChildNodes().item(1)
						.getAttributes().getNamedItem("name").getTextContent();

				writer.append(addrString);

				writer.append(",");
				writer.append(portnum);
				writer.append(",");
				writer.append(service);
				writer.append("\n");
			}

		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}// end of main

}