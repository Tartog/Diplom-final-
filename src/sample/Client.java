package sample;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class Client {

    private static DatagramSocket socket = null;
    private static boolean isExit = false;

    private static byte[] receiveData = new byte[1024];
    private static int port;
    private static ArrayList<String> listAddr = new ArrayList<String>();

    public static String myLogin;
    private static String myID;
    public static String password;
    private static String myPort;
    private static String myIP;

    Sender sender;

    static {
        try {
            myIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static String informationAboutMe;

    private static ArrayList<Integer> listOfPort = new ArrayList<Integer>();
    public static ArrayList<String> availableUsers = new ArrayList<String>();
    public static ArrayList<String> listOfFriends = new ArrayList<String>();
    public static ArrayList<UserInformation> listOfUserInformation = new ArrayList<UserInformation>();

    public static BigInteger publicKeyB;
    public static int flag = 0;
    public static String tmp = "";
    public static String message;
    public static int flag2 = 0;
    public static String log = "";

    public Client() throws Exception {

        while(flag == 0)
        {
            Main.myThread.sleep(500);
        }

        if (flag == 1)
        {
            File file = new File("Список друзей.txt");
            if (!file.exists())
            {
                flag = 2;
            }
            else {
                BufferedReader br = new BufferedReader(new FileReader("Мои данные.txt"));
                String line;
                line = br.readLine();
                myLogin = line;
                line = br.readLine();
                password = line;
                line = br.readLine();
                myID = line;
                String pass = "";
                Stribog hash = new StribogImpl();
                while(!pass.equals(password) || !myLogin.equals(log)) {

                    while (tmp.equals(""))
                    {
                        Main.myThread.sleep(500);
                    }
                    pass = DatatypeConverter.printHexBinary(hash.getHash(tmp.getBytes(), false));
                    if(!pass.equals(password))
                    {
                        tmp = "";
                    }
                }
                flag2 = 1;
                password = tmp;
                line = br.readLine();
                port = Integer.parseInt(line);
                line = br.readLine();
                myPort = line;
                informationAboutMe = "/" + myIP + "/" + myPort + "/" + myLogin + "/" + myID + "/";
            }
        }
        if (flag == 2)
        {
            File file = new File( "Мои данные.txt");
            if (!file.exists())
                file.createNewFile();

            FileWriter writer = new FileWriter("Мои данные.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            while(myLogin == null && password == null)
            {
                Main.myThread.sleep(500);
            }
            bufferedWriter.write(myLogin + "\n");
            Stribog hash = new StribogImpl();
            String pass = DatatypeConverter.printHexBinary(hash.getHash(password.getBytes(), false));
            bufferedWriter.write(pass + "\n");
            String h = password + myLogin + myIP;
            byte[] t = h.getBytes();
            myID = DatatypeConverter.printHexBinary(hash.getHash(t, false)).substring(0, 5);
            bufferedWriter.write(myID + "\n");
            port = 4445;
            bufferedWriter.write(port + "\n");
            myPort = "5555";
            bufferedWriter.write(myPort + "\n");
            bufferedWriter.close();
            informationAboutMe = "/" + myIP + "/" + myPort + "/" + myLogin + "/" + myID + "/";
        }

        listOfPort.add(4445);

        File file = new File("Список друзей.txt");
        if (!file.exists())
            file.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader("Список друзей.txt"));
        String line;
        while ((line = br.readLine()) != null)
        {
            listOfFriends.add(line);
        }

        for (int i = 0;i < listOfFriends.size();i++)
        {
            UserInformation inf = new UserInformation(listOfFriends.get(i));
            listOfUserInformation.add(inf);
        }

        broadcast test = new broadcast(informationAboutMe, listAllBroadcastAddresses().get(0));
        test.start();

        broadcastListener test2 = new broadcastListener();
        test2.start();

        Listener listener = new Listener(myPort, listOfUserInformation);
    }

    public void showAvailableUsers()
    {
        if(availableUsers.isEmpty())
        {
            System.out.println("Доступных пользователей нет");
            return;
        }
        System.out.println("Список доступных пользователей: ");
        for(int i = 0;i < availableUsers.size();i++)
        {
            System.out.println(i + 1 + "\t" + availableUsers.get(i));
        }
    }

    public void showFriends()
    {
        if(listOfFriends.isEmpty())
        {
            System.out.println("В списке друзей нет пользователей !");
            return;
        }
        System.out.println("Список друзей: ");
        for(int i = 0;i < listOfFriends.size();i++)
        {
            System.out.println(i + 1 + "\t" + listOfFriends.get(i));
        }
    }

    public static String decryptorHistory(String message)
    {
        String pas = "";
        for(int i = 0;i < 7;i++)
        {
            pas = pas + password;
        }
        Kuznechik kuznechik = new Kuznechik(pas.substring(0, 32));
        return kuznechik.decr(message);
    }

    public void addFriend(int userIndex) throws Exception {
        userIndex++;
        if(userIndex > 0 || userIndex <= availableUsers.size()) {
            if (listOfFriends.contains(availableUsers.get(userIndex - 1))) {
                return;
            }
            else
            {
                for(int i = 0;i < listOfFriends.size();i++)
                {
                    if(listOfFriends.get(i).equals(availableUsers.get(userIndex - 1).substring(0,
                            availableUsers.get(userIndex - 1).lastIndexOf("/") + 1)))
                    {
                        return;
                    }
                    else
                    {
                        System.out.println(listOfFriends.get(i));
                        System.out.println(availableUsers.get(userIndex - 1));
                        System.out.println(listOfFriends.get(i).length() + "\t" + availableUsers.get(userIndex - 1).length());
                    }
                }
                KeyExchange keyExchange = new KeyExchange(availableUsers.get(userIndex - 1),
                        informationAboutMe);
                keyExchange.sendK(informationAboutMe);
                Thread.sleep(2500);
                keyExchange.genSecret(publicKeyB);
                publicKeyB = null;

                listOfFriends.add(availableUsers.get(userIndex - 1));

                FileWriter writer = new FileWriter("Список друзей.txt", true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(availableUsers.get(userIndex - 1) + "\n");
                bufferedWriter.close();

                if(listOfUserInformation.size() == 0)
                {
                    UserInformation inf = new UserInformation(listOfFriends.get(0));
                    listOfUserInformation.add(inf);
                }
                else{
                    UserInformation inf = new UserInformation(listOfFriends.get(listOfFriends.size() - 1));
                    listOfUserInformation.add(inf);
                }

                File file = new File(listOfUserInformation.get(listOfUserInformation.size() - 1).IDFriend +
                        ".txt");
                file.createNewFile();
            }
        }
        else
        {
            return;
        }
    }

    public void writeMessage(String userFriend) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader
                (listOfUserInformation.get(Integer.parseInt(userFriend) - 1).IDFriend + ".txt"));
        String line;
        while ((line = br.readLine()) != null)
        {
            System.out.println(decryptorHistory(line));
        }
        sender = new Sender(listOfUserInformation.get(Integer.parseInt(userFriend) - 1).IPFriend,
                listOfUserInformation.get(Integer.parseInt(userFriend) - 1).portFriend, myID,
                listOfUserInformation.get(Integer.parseInt(userFriend) - 1).IDFriend, myLogin, password);
    }

    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }
            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

    public static class broadcast extends Thread
    {
        private String broadcastMessage;
        private InetAddress address;
        public broadcast(String broadcastMessage, InetAddress address)
        {
            this.broadcastMessage = broadcastMessage;
            this.address = address;
        }
        public void run()
        {
            while(true) {
                try {
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    byte[] buffer = broadcastMessage.getBytes();
                    for(int i = 0;i < listOfPort.size();i++) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, listOfPort.get(i));
                        socket.send(packet);
                    }
                    socket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class broadcastListener extends Thread
    {
        private static DatagramSocket serverSocket;
        public broadcastListener() throws SocketException {
            serverSocket = new DatagramSocket(port);
        }
        public void run()
        {
            while (!isExit) {
                try {
                    while (true) {
                        receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        String message = new String(receivePacket.getData());
                        if(!informationAboutMe.equals(message.substring(0, informationAboutMe.length())) &&
                                !availableUsers.contains(message)) {
                            for(int i = 0;i < listOfFriends.size();i++)
                            {
                                UserInformation us1 = new UserInformation(listOfFriends.get(i));
                                UserInformation us2 = new UserInformation(message);
                                if(us1.IDFriend.equals(us2.IDFriend) &&
                                        !us1.IPFriend.equals(us2.IPFriend))
                                {
                                    String oldInf = listOfFriends.get(i);
                                    String newInf = us2.toString();

                                    StringBuilder sb = new StringBuilder();

                                    File file = new File("Список друзей.txt");
                                    try(
                                            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                                        String strLine;
                                        while ((strLine = br.readLine())!=null){
                                            sb.append(strLine.replace(oldInf, newInf)).append("\r\n");

                                        }
                                    }
                                    try(FileWriter fileWriter = new FileWriter(file)){
                                        fileWriter.write(sb.toString());
                                    }
                                    listOfFriends.set(i, message);
                                }
                                else if(us1.IPFriend.equals(us2.IPFriend) &&
                                        !us1.IDFriend.equals(us2.IDFriend))
                                {
                                    String oldInf = listOfFriends.get(i);
                                    us1.IPFriend = "0.0.0.0";
                                    String newInf = us1.toString();
                                    StringBuilder sb = new StringBuilder();
                                    File file = new File("Список друзей.txt");
                                    try(
                                            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                                        String strLine;
                                        while ((strLine = br.readLine())!=null){
                                            sb.append(strLine.replace(oldInf, newInf)).append('\n');
                                        }
                                    }
                                    try(FileWriter fileWriter = new FileWriter(file)){
                                        System.out.println(oldInf);
                                        System.out.println(newInf);
                                        fileWriter.write(sb.toString());
                                    }
                                    listOfFriends.set(i, message);
                                    break;
                                }
                            }
                            availableUsers.add(message);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static class UserInformation
    {
        public String IPFriend;
        public String portFriend;
        public String loginFriend;
        public String IDFriend;
        public UserInformation(String information)
        {
            int a = 1;
            for(int i = 0;i < information.length();i++)
            {
                if (information.charAt(i) == '/' && i != 0)
                {
                    if(IPFriend == null)
                    {
                        IPFriend = information.substring(a, i);
                    }
                    else if (portFriend == null) {
                        portFriend = information.substring(a, i);
                    } else if (loginFriend == null) {
                        loginFriend = information.substring(a, i);
                    } else if (IDFriend == null) {
                        IDFriend = information.substring(a, i);
                    }
                    a += i - a + 1;
                }
            }
        }
        public void print()
        {
            System.out.println("IP - " + IPFriend);
            System.out.println("порт - " + portFriend);
            System.out.println("логин - " + loginFriend);
            System.out.println("ID - " + IDFriend);
        }
        public String toString()
        {
            return "/" + IPFriend + "/" + portFriend + "/" + loginFriend + "/" + IDFriend + "/";
        }
    }

    public static class Listener {
        public Listener(String myPort, ArrayList<Client.UserInformation> listOfUserInformation) throws IOException {

            int portNumber = Integer.parseInt(myPort);
            ServerSocket serverSocket;
            ArrayList<clientThread> users = new ArrayList<>();
            serverSocket = new ServerSocket(portNumber);
            class AcceptThread extends Thread {
                public void run() {
                    while (true) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            clientThread cliThread = new clientThread(clientSocket, listOfUserInformation);
                            synchronized (users)
                            {
                                users.add(cliThread);
                            }
                            cliThread.start();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
            }
            AcceptThread at = new AcceptThread();
            at.start();
        }

        static class clientThread extends Thread {
            public static BigInteger publicK;
            Socket socket;
            PrintWriter out;
            BufferedReader in;
            String input;
            boolean newMessage = false;
            ArrayList<Client.UserInformation> list;

            clientThread(Socket s, ArrayList<Client.UserInformation> listOfUserInformation) throws IOException {
                socket = s;
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                list = listOfUserInformation;
            }

            public void run() {
                try {
                    while (true) {
                        input = in.readLine();
                        newMessage = true;
                        if (input != null) {
                            if (input.contains("prime")) {

                                String tmp = input.substring(0, input.lastIndexOf("/") + 1);

                                listOfFriends.add(tmp);

                                FileWriter writer = new FileWriter("Список друзей.txt", true);
                                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                                bufferedWriter.write(input.substring(0, input.lastIndexOf("/") + 1) + "\n");
                                bufferedWriter.close();

                                DiffieHellman diffieHellman = new DiffieHellman(
                                        new BigInteger(input.substring(input.lastIndexOf("/") + 6,
                                                input.indexOf("key"))),
                                        new BigInteger(input.substring(input.indexOf("y") + 1)));

                                diffieHellman.genSecretKey(input.substring(0, input.lastIndexOf("/") + 1), informationAboutMe);
                            }
                            else if (input.contains("answer"))
                            {
                                Client.publicKeyB = new BigInteger(input.substring(6));
                                DiffieHellman.publicB = new BigInteger(input.substring(6));
                                publicK = new BigInteger(input.substring(6));
                            }
                            else {
                                String temp = decryptorKuz(input.substring(5), input.substring(0, 5));
                                System.out.println(temp);
                                FileWriter writer = new FileWriter(input.substring(0, 5) + ".txt", true);
                                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                                String name = "";
                                for (int i = 0; i < list.size(); i++) {
                                    if (input.substring(0, 5).equals(list.get(i).IDFriend)) {
                                        name = list.get(i).loginFriend;
                                        break;
                                    }
                                }
                                message = name + "\t" + temp + "\n";
                                bufferedWriter.write(encryptKuz(name + "\t" + temp) + "\n");
                                bufferedWriter.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public String decryptorKuz(String message, String IDFriend) throws IOException {
                BufferedReader reader = new BufferedReader(
                        new FileReader("keys.txt"));
                String line = "";
                String keyTest = "";
                while (!line.contains(IDFriend))
                {
                    line = reader.readLine();

                    keyTest = line.substring(6, 38);
                }
                Kuznechik kuznechik = new Kuznechik(keyTest);

                Stribog hash = new StribogImpl();
                String res = kuznechik.decr(message.substring(128));
                int t = res.length() - 1;
                while(res.toCharArray()[t] == '\0')
                {
                    res = res.substring(0, res.length() - 1);
                    t--;
                }

                if(DatatypeConverter.printHexBinary(hash.getHash(keyTest.getBytes(), false)).equals(message.substring(0, 64)) &&
                        DatatypeConverter.printHexBinary(hash.getHash(res.getBytes(), false)).equals(message.substring(64, 128)))
                {
                    return kuznechik.decr(message.substring(128));
                }
                else
                {
                    System.exit(-1);
                    return "";
                }
            }

            public String encryptKuz(String message)
            {
                String pas = "";
                for(int i = 0;i < 7;i++)
                {
                    pas += password;
                }
                Kuznechik kuznechik = new Kuznechik(pas.substring(0, 32));
                return kuznechik.encr(message);
            }
        }
    }
}