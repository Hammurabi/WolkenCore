package org.wolkenproject.core;

import org.bouncycastle.util.Arrays;
import org.wolkenproject.core.script.*;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.network.messages.*;
import org.wolkenproject.serialization.SerializationFactory;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Context {
    private static Context instance;

    private Database                database;
    private NetworkParameters       networkParameters;
    private ExecutorService         threadPool;
    private AtomicBoolean           isRunning;
    private IpAddressList           ipAddressList;
    private SerializationFactory    serializationFactory;
    private TransactionPool         transactionPool;
    private Server                  server;
    private Address                 payList[];
    private BlockChain              blockChain;
    private OpcodeRegister virtualMachine;
    private FileService             fileService;

    public Context(FileService service, boolean testNet, Address[] payList) throws WolkenException, IOException {
        Context.instance            = this;
        this.database               = new Database(service.newFile("db"));
        this.networkParameters      = new NetworkParameters(testNet);
        this.threadPool             = Executors.newFixedThreadPool(3);
        this.isRunning              = new AtomicBoolean(true);
        this.ipAddressList          = new IpAddressList(service.newFile("peers"));
        this.serializationFactory   = new SerializationFactory();
        this.transactionPool        = new TransactionPool();
        this.payList                = payList;
        this.fileService            = service;
        this.virtualMachine         = null;

        serializationFactory.registerClass(BlockHeader.class, new BlockHeader());
        serializationFactory.registerClass(Block.class, new Block());
        serializationFactory.registerClass(BlockIndex.class, new BlockIndex());
        serializationFactory.registerClass(Ancestors.class, new Ancestors(new byte[Block.UniqueIdentifierLength]));
        serializationFactory.registerClass(Ancestors.class, new Input(new byte[TransactionI.UniqueIdentifierLength], 0, new byte[0]));
        serializationFactory.registerClass(Ancestors.class, new Output(0, new byte[0]));

        serializationFactory.registerClass(Transaction.class, new Transaction(0, 0, 0, new Input[0], new Output[0]));
        serializationFactory.registerClass(Input.class, new Input(new byte[32], 0, new byte[1]));
        serializationFactory.registerClass(Output.class, new Output(0, new byte[1]));

        serializationFactory.registerClass(NetAddress.class, new NetAddress(InetAddress.getLocalHost(), 0, 0));
        serializationFactory.registerClass(VersionMessage.class, new VersionMessage());
        serializationFactory.registerClass(VerackMessage.class, new VerackMessage());
        serializationFactory.registerClass(VersionInformation.class, new VersionInformation());

        serializationFactory.registerClass(BlockList.class, new BlockList(0, new LinkedHashSet<>(), new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(FailedToRespondMessage.class, new FailedToRespondMessage(0, 0, new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(FoundCommonAncestor.class, new FoundCommonAncestor(new byte[Block.UniqueIdentifierLength], new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(HeaderList.class, new HeaderList(0, new LinkedHashSet<>(), new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(Inv.class, new Inv(0, 0, new LinkedHashSet<>()));
        serializationFactory.registerClass(RequestBlocks.class, new RequestBlocks(0, new LinkedHashSet<>()));
        serializationFactory.registerClass(RequestCommonAncestorChain.class, new RequestCommonAncestorChain(0, new Ancestors(new byte[Block.UniqueIdentifierLength])));
        serializationFactory.registerClass(RequestHeaders.class, new RequestHeaders(0, new LinkedHashSet<>()));
        serializationFactory.registerClass(RequestHeadersBefore.class, new RequestHeadersBefore(0, new byte[Block.UniqueIdentifierLength], 0, new BlockHeader()));
        serializationFactory.registerClass(RequestInv.class, new RequestInv(0));
        serializationFactory.registerClass(RequestTransactions.class, new RequestTransactions(0, new LinkedHashSet<>()));
        serializationFactory.registerClass(TransactionList.class, new TransactionList(0, new LinkedHashSet<>(), new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(AddressList.class, new AddressList(0, new LinkedHashSet<>()));

//        serializationFactory.registerClass(MochaObject.class, new MochaObject());;

        this.server                 = new Server();
        this.blockChain             = new BlockChain();

        virtualMachine.registerOp("halt", "halt process and all sub processes", (proc)->{proc.stopProcess(0);});
        virtualMachine.registerOp("push", new BitFields()
                                                    .addField(2, "arg", (s, v)->{
                                                        switch (s.get()[0]) {
                                                            case 0:
                                                                v.set(6);
                                                                return true;
                                                            case 1:
                                                                v.set(12);
                                                                return true;
                                                            case 2:
                                                                v.set(16);
                                                                return true;
                                                            case 3:
                                                                v.set(24);
                                                                return true;
                                                            default:
                                                                return false;
                                                        }
                                                    })
                                                    , "push an array of size x into the stack.", null);

        virtualMachine.registerOp("bconst", "push an int of size [  8 ] into the stack.", null);
        virtualMachine.registerOp("iconst",  new BitFields()
                                                    .addField(1, "sign")
                                                    .addField(32, "value")
                                                    , "push an int of size [ 32 ] into the stack.", null);
        virtualMachine.registerOp("lconst", "push an int of size [ 64 ] into the stack.", null);

        virtualMachine.registerOp("fconst", "push a fixed float of size [ 32 ] into the stack.", null);
        virtualMachine.registerOp("dconst", "push a fixed float of size [ 64 ] into the stack.", null);

        virtualMachine.registerOp("aconst", "push an address of size [ 200 ] into the stack.", null);
        virtualMachine.registerOp("aaconst", "push an array of max length [ 16 ] bits into the stack.", null);
        virtualMachine.registerOp("aaconstl", "push an array of max length [ 32 ] bits into the stack.", null);

        virtualMachine.registerOp("add", "add two top elements of the stack.", null);
        virtualMachine.registerOp("sub", "sub two top elements of the stack.", null);
        virtualMachine.registerOp("mul", "mul two top elements of the stack.", null);
        virtualMachine.registerOp("div", "div two top elements of the stack.", null);
        virtualMachine.registerOp("mod", "mod two top elements of the stack.", null);

        virtualMachine.registerOp("store", new BitFields()
                                                    .addField(4, "register")
                                                    , "pop x from stack and store it in register", null);
        virtualMachine.registerOp("load", new BitFields()
                                                    .addField(4, "register")
                                                    , "load x from register and push it to stack", null);
        virtualMachine.registerOp("call", new BitFields()
                                                    .addField(4, "arg")
                                                    , "call a function [4:arg].", null);
        virtualMachine.registerOp("jump", new BitFields()
                                                    .addField(16, "position")
                                                    , "jump to position.", null);

        virtualMachine.registerOp("getmember", new BitFields()
                                                    .addField(2, "6/14/22")
                                                    , "set or get (and jump) a jump location.", null);
        virtualMachine.registerOp("setmember", new BitFields()
                                                    .addField(2, "6/14/22")
                                                    , "set or get (and jump) a jump location.", null);


        System.out.println(virtualMachine.opCound());
//        virtualMachine.addOp("push", true, 0, 0, "push x amount of bytes into the stack", null);
//        virtualMachine.addOp("pop", false, 0, 0, "pop item from the stack", null);
    }

    public void shutDown()
    {
        isRunning.set(false);
        server.shutdown();
        try {
            ipAddressList.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Database getDatabase()
    {
        return database;
    }

    public NetworkParameters getNetworkParameters()
    {
        return networkParameters;
    }

    public static Context getInstance()
    {
        return instance;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public IpAddressList getIpAddressList() {
        return ipAddressList;
    }

    public SerializationFactory getSerialFactory() {
        return serializationFactory;
    }

    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    public Address[] getPayList() {
        return payList;
    }

    public Server getServer() {
        return server;
    }

    public BlockChain getBlockChain() {
        return blockChain;
    }
}
