import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.InetSocketAddress;

public class Recv {
  private Socket socket = null;
  private InputStream inputStream = null;
  private List<Packet> pList = new ArrayList<Packet>();

  public Recv(String host, int port) throws IOException {
    socket = new Socket();
    socket.connect(new InetSocketAddress(host, port));
    System.out.println("connected to " + host + " on port " + port);
  }

  public void close() throws IOException {
    socket.close();
    System.out.println("Connection is closed");
  }

  /* should not need to change this */
  public int run(String fname) {
    try {
      get_pkts();
      order_pkts();
      write_pkts(fname);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

  /*
   * get_pkts: pull all packets off the wire and store in pList
   * this method also prints the packet info stats
   */
  public int get_pkts() throws IOException {

    int npackets = 1; // how many packets read
    byte[] data = new byte[1506];
    int k = -1;
    long totalDelay = 0;
    long time = System.currentTimeMillis();
    /*
     * loop: get all packets and capture stats
     * must use getInputStream.read()
     */
    while ((k = socket.getInputStream().read(data, 0, data.length)) > -1) {
      System.out.print("Pkt " + npackets);
      Packet pack = new Packet(data);
      long s = System.currentTimeMillis();
      time = s - time;
      totalDelay += time;
      npackets++;
      pList.add(pack);
      System.out.println(",    Delay = " + time + " ms");
      time = s;
    } // while (read packets)

    npackets--;
    System.out.print("Total  " + npackets + "  packets / " + Packet.totalbytes + " bytes recd. Total delay = "
        + totalDelay + " ms, ");
    System.out.printf("average = %.2f ms \n", (double) totalDelay / npackets);
    return npackets;

  }

  public void write_pkts(String f) throws Exception {
    // this must call Packet.write() for each Packet
    FileOutputStream out = new FileOutputStream(f, true);
    for (Packet pack : pList) {
      pack.write(out);
    }
    close();
  }

  // put the pList in the correct order
  // and remove duplicates
  public void order_pkts() {

    for (int i = 0; i < pList.size(); i++) {
      for (int j = i + 1; j < pList.size(); j++) {
        if (pList.get(i).compareTo(pList.get(j)) == 0) {
          Packet pack = pList.get(j);
          pList.remove(pack);
          j = i;
        }
      }
    }
    Collections.sort(pList);

  }

  // DO_NOT change main at all! String OK here
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Usage: host  port filename");
      return;
    }

    try {
      Recv recv = new Recv(args[0],
          Integer.parseInt(args[1]));
      recv.run(args[2]);
    } catch (Exception e) {
      e.printStackTrace();
    }
  } // main()

} // class Recv

/* Packet class */
class Packet implements Comparable<Packet> {
  /* DO_NOT change these private fields */
  private byte[] payload;
  private int seqNo;
  private short len;
  private PrintStream tty;
  public static int totalbytes = 0;

  /*
   * this CTOR is used to make a packet from
   * a buffer that came off the wire
   */
  public Packet(byte[] buf) {

    seqNo = get_seqno(buf); // must use only this method
    len = get_len(buf); // must use only this method
    totalbytes += len;
    byte[] temp = new byte[1500];
    int counter = 0;
    for (int i = 6; i < buf.length; i++) {
      temp[counter] = buf[i];
      counter++;
    }

    payload = temp;
    System.out.print("    SEQ " + seqNo);
    System.out.print(",    Len: " + len);

  } // Packet CTOR

  private int get_seqno(byte[] b) {
    int[] b1 = new int[4];
    for (int i = 0; i < b1.length; i++) {
      b1[i] = b[i];
      if (b1[i] < 0) {
        b1[i] += 256;
      }
    }

    return ((b1[0] * 16777216) + (b1[1] * 65536) + (b1[2] * 256) + (b1[3]));
  }

  private short get_len(byte[] b) {

    int[] temp = new int[2];
    temp[0] = b[4];
    if (temp[0] < 0) {
      temp[0] += 256;
    }
    temp[1] = b[5];
    if (temp[1] < 0) {
      temp[1] += 256;
    }

    return (short) ((temp[0]) * 256 + (temp[1]));
  }

  public int compareTo(Packet p) {
    return seqNo - p.seqNo;
  }

  // write this Packet to file: no need to change this
  public void write(FileOutputStream f) throws IOException {

    f.write(payload, 0, len);
  }

}// class Packet
