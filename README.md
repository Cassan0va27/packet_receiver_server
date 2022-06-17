## Java Server that receives byte data and processes it

 In this project I have developed a packet receiver which will receive the file.
 The program works by receiver connecting to a remote host and fetching a bunch of packets. A packet comprises a header and a payload (data). The receiver extracts the payload from each packet and writes it out to the output file 'data.dat' in the correct order. The receiver prints out packet related information in the order the packets are received, not necessarily their correct order in the file. The receiver closes the connection after receiving the entire file.
 
 
 The structure of a packet is:

    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                        Sequence Number                        |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |           Length              |    Data (up to 1500 bytes)    |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
The sequence number is 32-bit and the packet length is 16-bit, both in network byte order, RFC 1700. The sequence number is similar to a TCP sequence number, RFC 793; it indicates the position of the packet in the file.


The sender may send packets out of order and may send duplicate packets. The sender may place a varying time delay between packets. But the sender will not drop or corrupt any packets. Ths send.java does all these things and thoroughly tests your receiver. I have tried to keep the data in the raw byte format and have avoided the use of in-built functions such as getBytes(), toString() etc. The data is handled purely in raw byte format.
