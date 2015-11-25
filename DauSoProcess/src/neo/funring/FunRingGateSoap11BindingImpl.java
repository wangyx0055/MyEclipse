/**
 * FunRingGateSoap11BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package neo.funring;

public class FunRingGateSoap11BindingImpl implements neo.funring.FunRingGatePortType{
    public java.lang.Integer presentTone(java.lang.String myUsr, java.lang.String myPwd, java.lang.String fromUser, java.lang.String toUser, java.lang.String toneCode) throws java.rmi.RemoteException {
        
    	return null;
    }

    public java.lang.String listen(java.lang.String myUsr, java.lang.String myPwd, java.lang.String toneCode) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.Integer subcribe(java.lang.String myUsr, java.lang.String myPwd, java.lang.String msisdn) throws java.rmi.RemoteException {
        return null;
    }

    public vms.funring.ToneProvide.xsd.QueryToneResp queryToneByStatus(java.lang.String myUsr, java.lang.String myPwd, java.lang.String toneCode, java.lang.String status) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String doCommand(java.lang.String command, java.lang.String param) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.Integer orderTone(java.lang.String myUsr, java.lang.String myPwd, java.lang.String msisdn, java.lang.String toneCode) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String getUserStatus(java.lang.String myUsr, java.lang.String myPwd, java.lang.String msisdn) throws java.rmi.RemoteException {
    	FunRingGateSoap11BindingStub bind =null;
    	try{
    	    
    	FunRingGateLocator locator = new FunRingGateLocator();
    	 bind =(FunRingGateSoap11BindingStub) locator.getFunRingGateHttpSoap11Endpoint();
    	 return bind.getUserStatus(myUsr, myPwd, msisdn);
    }catch (Exception e) {
        e.printStackTrace();
    }
    	return null;
    }

}
