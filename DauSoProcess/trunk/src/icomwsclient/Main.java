
package icomwsclient;


import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.DeserializerFactory;
import org.apache.axis.Constants;
import org.apache.axis.encoding.ser.ElementDeserializer;
import org.apache.axis.encoding.ser.ElementDeserializerFactory;
import org.apache.axis.encoding.ser.ElementSerializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.BaseType;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.tempuri.IcomLocator;
import org.tempuri.IcomSoap;
import org.tempuri.IcomSoapStub;
import org.w3c.dom.Element;

public class Main {

  
    public static void main(String[] args) throws Exception {
      Main main = new Main();
		main.testicom();
    }

	public void testicom() throws Exception {

		String url = "http://smsservice.maromedia.vn/icom.asmx";
		URL serviceURL = new URL(url);

		IcomLocator locator = new IcomLocator();
		IcomSoap icom = locator.geticomSoap(serviceURL);


		String moid = "1958905";
		String moseq = "701572713";
		String src = "8497292911";
		String dest = "8051";
		String cmdcode = "MRE";
		String msgbody = "MRE tu test 22/03 from java new";
		String username = "icom";
		String password = "icommaromedia";

		int result = icom.receiveMO(moid, moseq, src, dest, cmdcode, msgbody, username, password);
		System.out.println("result=" + result);

	}

}
