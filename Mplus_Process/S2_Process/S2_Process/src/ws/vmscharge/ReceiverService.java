package ws.vmscharge;

public interface ReceiverService extends javax.xml.rpc.Service {
    public java.lang.String getReceiverAddress();

    public Receiver getReceiver() throws javax.xml.rpc.ServiceException;

    public Receiver getReceiver(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
