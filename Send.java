import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;

class Packet {
    private byte[] payload;
    private byte[] seqNo;
    private byte[] len;

    public Packet(byte[] payload, byte[] seqNo, byte[] len) {

        this.payload = payload;
        this.seqNo = seqNo;
        this.len = len;
    }

}

public class Send {
    public static byte[] convertinttobyte(int seqNo) {
        // Convert Integer to byte ---> 1 int = 4Bytes by using right shift operation

        byte[] intB = new byte[4];
        intB[0] = (byte) ((seqNo >> 24) & 0xff);
        intB[1] = (byte) ((seqNo >> 16) & 0xff);
        intB[2] = (byte) ((seqNo >> 8) & 0xff);
        intB[3] = (byte) ((seqNo >> 0) & 0xff);

        return intB;
    }

    public static byte[] convertshorttobyte(short len) {
        // Convert Short to byte ---> 1 short= 2bytes
        byte[] shortB = new byte[2];
        shortB[0] = (byte) ((len >> 8) & 0xff);
        shortB[1] = (byte) (len & 0xff);

        return shortB;
    }

    public static void main(String[] args) throws IOException {
    if(args.length != 1) {
         System.out.println("Usage: java Send portnumber ");
	      return;
	         }

        ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
        System.out.println("Waiting for a Receiver ...");
        Socket sr = ss.accept();
        OutputStream os = sr.getOutputStream();
        FileInputStream fr = new FileInputStream("/afs/cad.njit.edu/courses/ccs/s22/cs/656/102/d35/A2/test.txt"); // Reading a

        byte[] data = new byte[1500];
        ArrayList<Packet> pack = new ArrayList<Packet>();
        int seqno = 0;
        int len = 0;
        int npackets = 0;
        byte[] temp1 = new byte[1506];
        len = fr.read(data, 0, 1500);


            byte[] seqb = convertinttobyte(seqno);
            byte[] lenb = convertshorttobyte((short) len);

            temp1[0] = seqb[0];
            temp1[1] = seqb[1];
            temp1[2] = seqb[2];
            temp1[3] = seqb[3];
            temp1[4] = lenb[0];
            temp1[5] = lenb[1];

            for (int i = 6; i < len; i++) {

                temp1[i] = data[i - 6];

            }
            
	    
	    len = fr.read(data, 0, 1500);
	    seqno = 1500;
		byte[] temp = new byte[1506];
	    seqb = convertinttobyte(seqno);
		lenb = convertshorttobyte((short) len);

				                temp[0] = seqb[0];
						            temp[1] = seqb[1];
							                temp[2] = seqb[2];
									            temp[3] = seqb[3];
										                temp[4] = lenb[0];
												            temp[5] = lenb[1];

													                for (int i = 6; i < len; i++) {

															                temp[i] = data[i - 6];

																		}
	os.write(temp);
	os.write(temp1);
       os.write(temp);
       os.write(temp1);
       os.write(temp1);
       os.write(temp);
       os.write(temp);
       os.write(temp1);
       os.write(temp1);

        System.out.println("File has been successfully sent");
    }

}
