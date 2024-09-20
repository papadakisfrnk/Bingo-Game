/*
Papadakis Fragkiskos
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Listener extends Remote {
    //interface gia tin epikoinwnia tou BingoServer me tous Client (Callback)

    public void sendRandom(int number) throws RemoteException;

    public void thereIsWinner(Player p) throws RemoteException;

    public void lotaryOver() throws RemoteException;
}
