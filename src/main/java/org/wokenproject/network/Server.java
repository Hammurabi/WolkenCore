package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.network.messages.VersionMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable {
    private ServerSocket    socket;
    private Set<Node>       connectedNodes;

    public Server() throws IOException {
        socket = new ServerSocket(Context.getInstance().getNetworkParameters().getPort());
        connectedNodes = Collections.synchronizedSet(new LinkedHashSet<>());
        connectToNodes(Context.getInstance().getIpAddressList().getAddresses());
        Context.getInstance().getThreadPool().execute(this::listenForIncomingConnections);
    }

    public boolean connectToNodes(Queue<NetAddress> addresses)
    {
        int connections = 0;

        for (NetAddress address : addresses)
        {
            try {
                Socket socket = new Socket(address.getAddress(), address.getPort());
                Node node = new Node(socket);
                connectedNodes.add(node);

                node.sendMessage(new VersionMessage(Context.getInstance().getNetworkParameters().getVersion(), new VersionInformation(Context.getInstance().getNetworkParameters().getVersion(), VersionInformation.Flags.AllServices, System.currentTimeMillis(), getNetAddress(), address, 0)));

                if (++ connections == Context.getInstance().getNetworkParameters().getMaxAllowedOutboundConnections())
                {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void listenForIncomingConnections()
    {
        Socket incoming = null;
        while (Context.getInstance().isRunning())
        {
            try {
                incoming = socket.accept();
                if (connectedNodes.size() < (Context.getInstance().getNetworkParameters().getMaxAllowedInboundConnections() + Context.getInstance().getNetworkParameters().getMaxAllowedOutboundConnections()))
                {
                    connectedNodes.add(new Node(incoming));
                }
                else
                {
                    incoming.close();
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void run() {
        // we don't need to start checks right away
        long lastCheck = System.currentTimeMillis();
        while (Context.getInstance().isRunning())
        {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastCheck >= 30_000)
            {
                runMaintenanceChecks();
                lastCheck = currentTime;
            }

            for (Node node : connectedNodes)
            {
                CachedMessage message = node.listenForMessage();
                if (!message.isSpam())
                {
                    if (!node.hasPerformedHandshake() && !message.isHandshake())
                    {
                        // ignore message
                        continue;
                    }

                    message.getMessage().executePayload(this, node);
                }
            }

            for (Node node : connectedNodes)
            {
                node.flush();
            }
        }
    }

    private void runMaintenanceChecks() {
        Iterator<Node> nodeIterator = connectedNodes.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            boolean shouldDisconnect = false;
            boolean isSpammy = false;

            if (node.timeSinceConnected() >= Context.getInstance().getNetworkParameters().getHandshakeTimeout() && !node.hasPerformedHandshake())
            {
                shouldDisconnect = true;
            }

            if (node.getTotalErrorCount() > Context.getInstance().getNetworkParameters().getMaxNetworkErrors()) {
                shouldDisconnect = true;
            }

            if (node.getSpamAverage() > Context.getInstance().getNetworkParameters().getMessageSpamThreshold()) {
                shouldDisconnect = true;
                isSpammy = true;
            }

            if (isSpammy) {
                NetAddress address = Context.getInstance().getIpAddressList().getAddress(node.getInetAddress());
                if (address != null)
                {
                    address.setSpamAverage(node.getSpamAverage());
                }
            }

            if (shouldDisconnect) {
                nodeIterator.remove();
                try {
                    node.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!shouldDisconnect && !isSpammy)
            {
                if (node.getMessageCache().inboundCacheSize() > Context.getInstance().getNetworkParameters().getMaxCacheSize())
                {
                    NetAddress address = Context.getInstance().getIpAddressList().getAddress(node.getInetAddress());
                    if (address != null)
                    {
                        address.setSpamAverage(node.getSpamAverage());
                    }
                    node.getMessageCache().clearInboundCache();
                }

                node.getMessageCache().clearOutboundCache();
            }
        }
    }

    public void shutdown() {
        Iterator<Node> nodeIterator = connectedNodes.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            try {
                node.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public NetAddress getNetAddress() {
        return new NetAddress(socket.getInetAddress(), socket.getLocalPort());
    }
}
