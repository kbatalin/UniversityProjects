package net.kir55rus.portforwarder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by kir55rus on 11.01.17.
 */
public class PortForwarder {
    private int socketsCount = 0;

    private static int BUFFER_SIZE = 1024;

    private SocketAddress destAddress;
    private ServerSocketChannel mainSocket;
    private Selector selector;

    public static void main(String[] args) {
        if(args.length < 3) {
            System.err.println("Need args: lport rhost rport");
            return;
        }

        new PortForwarder().run(args[0], args[1], args[2]);
    }

    private boolean init(String lport, String rhost, String rport) {
        try {
            InetSocketAddress local = new InetSocketAddress("", Integer.parseInt(lport));
            InetSocketAddress dest = new InetSocketAddress(rhost, Integer.parseInt(rport));

            if (local.isUnresolved() || dest.isUnresolved()) {
                throw new IllegalArgumentException("Can't resolve names");
            }
            destAddress = dest;

            mainSocket = ServerSocketChannel.open().bind(local);
            mainSocket.configureBlocking(false);

            selector = Selector.open();
            mainSocket.register(selector, SelectionKey.OP_ACCEPT, new AcceptHandler());
            socketsCount = 1;

        } catch (IllegalArgumentException e) {
            System.err.println("Bad args: ".concat(e.getMessage()));
            return false;
        } catch (IOException e) {
            System.err.println("Can't open: ".concat(e.getMessage()));
            return false;
        }

        return true;
    }

    public void run(String lport, String rhost, String rport) {
        if (!init(lport, rhost, rport)) {
            return;
        }

        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    if (!key.isValid()) {
                        it.remove();
                        continue;
                    }

                    Object attachment = key.attachment();
                    if (!(attachment instanceof Handler)) {
                        it.remove();
                        continue;
                    }

                    Handler handler = (Handler) attachment;
                    handler.handle(key.readyOps());

                    it.remove();
                }

                System.out.println("Sockets count: " + socketsCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }

    private static void log(SocketChannel client, String msg) {
        System.out.println("Channel #".concat(String.valueOf(client.hashCode())).concat(": ").concat(msg));
    }

    private interface Handler {
        void handle(int ops);
    }

    private class AcceptHandler implements Handler {
        @Override
        public void handle(int ops) {
            log("Accept handler");

            if((ops & SelectionKey.OP_ACCEPT) == 0) {
                log("Bad ops");
                return;
            }

            try {
                SocketChannel client = mainSocket.accept();
                client.configureBlocking(false);
                log(client, "was accepted");
                ++socketsCount;

                SocketChannel server = SocketChannel.open();
                server.configureBlocking(false);
                server.connect(destAddress);
                server.register(selector, SelectionKey.OP_CONNECT, new ConnectHandler(client, server));
                log(server, "Try connect");
            } catch (IOException e) {
                log("Can't accept: ".concat(e.getMessage()));
            }
        }
    }

    private class ConnectHandler implements Handler {
        private SocketChannel client;
        private SocketChannel server;

        public ConnectHandler(SocketChannel client, SocketChannel server) {
            this.client = client;
            this.server = server;
        }

        @Override
        public void handle(int ops) {
            log(server, "Connect handler");

            if((ops & SelectionKey.OP_CONNECT) == 0) {
                log("Bad ops");
                return;
            }

            try {
                if (!server.finishConnect()) {
                    log(server, "Not connected");
                    closeClient();
                    return;
                }
                ++socketsCount;

                ByteBuffer clientBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                ByteBuffer serverBuffer = ByteBuffer.allocate(BUFFER_SIZE);

                SelectionKey serverKey = server.register(selector, SelectionKey.OP_CONNECT);
                SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                ConnectionInfo serverConnection = new ConnectionInfo(server, serverBuffer, serverKey);
                ConnectionInfo clientConnection = new ConnectionInfo(client, clientBuffer, clientKey);

                serverKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                serverKey.attach(new RWHandler(serverConnection, clientConnection));
                clientKey.attach(new RWHandler(clientConnection, serverConnection));

                log(server, "Added in selector");
                log(client, "Added in selector");

            } catch (IOException e) {
                log(server, "Can't connect: ".concat(e.getMessage()));
                closeClient();
            }
        }

        private void closeClient() {
            try {
                client.close();
                --socketsCount;
            } catch (IOException e) {
                log("Can't close client");
            }
        }
    }

    private class ConnectionInfo {
        public SocketChannel channel;
        public ByteBuffer buffer;
        public SelectionKey selectionKey;
        public boolean readAvailable;
        public boolean writeAvailable;

        public ConnectionInfo(SocketChannel channel, ByteBuffer buffer, SelectionKey selectionKey) {
            this.channel = channel;
            this.buffer = buffer;
            this.selectionKey = selectionKey;
            readAvailable = true;
            writeAvailable = true;
        }
    }

    private class RWHandler implements Handler {
        private ConnectionInfo myConnection;
        private ConnectionInfo otherConnection;

        public RWHandler(ConnectionInfo myConnection, ConnectionInfo otherConnection) {
            this.myConnection = myConnection;
            this.otherConnection = otherConnection;
        }

        @Override
        public void handle(int ops) {
            log(myConnection.channel, "RWHandler");

            if((ops & SelectionKey.OP_READ) != 0) {
                readHandler();
            } else if((ops & SelectionKey.OP_WRITE) != 0) {
                writeHandler();
            } else {
                log(myConnection.channel, "Bad ops");
            }
        }

        private void myLog(String msg) {
            log(myConnection.channel, msg);
        }

        private void pause(ConnectionInfo connectionInfo, int op) {
            log(connectionInfo.channel, "Pause");
            connectionInfo.selectionKey.interestOps(connectionInfo.selectionKey.interestOps() & ~op);
        }

        private void resume(ConnectionInfo connectionInfo, int op) {
            log(connectionInfo.channel, "Resume");
            connectionInfo.selectionKey.interestOps(connectionInfo.selectionKey.interestOps() | op);
        }

        private void readHandler() {
            myLog("Read");

            try {
                if (!otherConnection.writeAvailable) {
                    myLog("Other can't write");
                    myConnection.readAvailable = false;
                    myConnection.channel.shutdownInput();
                    pause(myConnection, SelectionKey.OP_READ);
                    checkClose();
                    return;
                }

                resume(otherConnection, SelectionKey.OP_WRITE);

                int n = myConnection.channel.read(myConnection.buffer);
                myLog("Read: " + n + " bytes");

                if (n == -1) {
                    if (!myConnection.buffer.hasRemaining()) {
                        otherConnection.writeAvailable = false;
                    }

                    myConnection.readAvailable = false;
                    myConnection.channel.shutdownInput();
                    pause(myConnection, SelectionKey.OP_READ);
                }

            } catch (IOException e) {
                log("Can't read: " + e.getMessage());
                myConnection.readAvailable = false;
                pause(myConnection, SelectionKey.OP_READ);
                checkClose();
            }
        }

        private void writeHandler() {
            log(myConnection.channel, "Write");

            try {
                resume(otherConnection, SelectionKey.OP_READ);

                otherConnection.buffer.flip();
                int n = myConnection.channel.write(otherConnection.buffer);
                myLog("Write: " + n + " bytes");

                if (!otherConnection.buffer.hasRemaining()) {
                    pause(myConnection, SelectionKey.OP_WRITE);

                    if (!otherConnection.readAvailable) {
                        myLog("End buffer");
                        myConnection.writeAvailable = false;
                        myConnection.channel.shutdownOutput();
                        checkClose();
                    }
                }

                otherConnection.buffer.compact();

            } catch (IOException e) {
                myLog("Can't write" + e.getMessage());
                myConnection.writeAvailable = false;
                otherConnection.readAvailable = false;
                pause(myConnection, SelectionKey.OP_WRITE);
                checkClose();
            }
        }

        private void checkClose() {
            if (myConnection.readAvailable || myConnection.writeAvailable || otherConnection.readAvailable || otherConnection.writeAvailable) {
                return;
            }

            try {
                myConnection.selectionKey.cancel();
                myConnection.channel.close();
                otherConnection.selectionKey.cancel();
                otherConnection.channel.close();
            } catch (IOException e) {
                log("Can't close channels: " + e.getMessage());
            }

            log(myConnection.channel, "closed");
            log(otherConnection.channel, "closed");

            socketsCount -= 2;
        }
    }
}
