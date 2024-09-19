import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import javax.imageio.ImageIO;

public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int portNo;

    public Server(int portNo){
        this.portNo=portNo;
    }

    private void processConnection() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

        //*** Application Protocol *****
        String buffer = in.readLine();
        while(buffer.length() != 0 ){
            System.out.println(buffer);
           //out.println("From Server: " + buffer);
           if(buffer.contains("home.html")){
           File doc = new File("assignment-1-Zander01A-1-main\\docroot\\home.html");
            BufferedReader br = new BufferedReader(new FileReader(doc));
            BufferedImage bi = ImageIO.read(new File("assignment-1-Zander01A-1-main//docroot//images//assign2-screen.png"));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", outputStream);
            String encodedImage = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        out.printf("HTTP/1.1 200 OK\n");
        out.printf("Transfer-Encoding: chunked");
        out.printf("Content-Length:  150596\n");
        out.printf("Content-Type: text/css,text/html,text/html\n");
        out.printf("Accept-Ranges: bytes\n\n");
            String line =br.readLine();
            while((line != null)){ 
            if (line.contains("<img")) {
                    out.println("<img src=assignment-1-Zander01A-1-main//docroot//images//assign2-screen.png;base64," +  encodedImage  + " alt=screen>\n");
          
                } else {
                out.println(line + "\n");    
            }
            line = br.readLine();
            }
            br.close();
           }
            buffer = in.readLine();
        }
        in.close();
        out.close();
    }

    public void run() throws IOException{
        boolean running = true;
       
        serverSocket = new ServerSocket(portNo);
        System.out.printf("Listen on Port: %d\n",portNo);
        while(running){
            clientSocket = serverSocket.accept();
            //** Application Protocol
            processConnection();
            clientSocket.close();
        }
        serverSocket.close();
    }
    public static void main(String[] args0) throws IOException{
        Server server = new Server(8080);
        server.run();
    }
}
