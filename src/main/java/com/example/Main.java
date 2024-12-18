package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {

            ServerSocket ss = new ServerSocket(8080);

            while(true){
                Socket s = ss.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                String firstline = in.readLine();            // prendo la prima stringa

                String[] richiesta = firstline.split(" ");   // splitto la stringa
                String metodi = richiesta[0]; 
                String risorse = richiesta[1]; 
                String versione = richiesta[2]; 

                String headers;
                do {                                // stai nel while finche non finisce gli headers
                    headers = in.readLine();
                } while (!headers.isEmpty());


                if(risorse.endsWith("/") )
                {
                    risorse += "index.html";
                }
                File file = new File("htdocs/" + risorse);                      // apre il file che c'è nella "htdocs"
                
                if (file.isDirectory()) {
                    out.writeBytes("HTTP/1.1 301 Moved Permanently\n");
                    out.writeBytes("Content-Length: 0\n");
                    out.writeBytes("Location: "+ risorse + "/\n");
                    out.writeBytes("\n");
                }else if(file.exists()){   //se il file esiste
                    out.writeBytes("HTTP/1.1 200 OK\n");
                    out.writeBytes("Content-Type: "+ getContentType(file)+ " \n");
                    out.writeBytes("Content-Length: " + file.length() + "\n");
                    out.writeBytes("\n");

                    InputStream input  = new FileInputStream(file);                          //gestire il file
                    byte[] buf = new byte[8192];
                    int n ;
                    while((n=input.read(buf)) != -1){
                        out.write(buf,0,n);
                    }
                    input.close();
                } else{
                    String mess = "NON TROVATO";                                             //se nel URL c'è scritto qualcosa di diverso 
                    out.writeBytes("HTTP/1.1 404 Not Found\n");
                    out.writeBytes("Content-Type: text/html \n");
                    out.writeBytes("Content-Length: " + mess.length() + "\n");
                    out.writeBytes("\n");
                    out.writeBytes(mess);
                }
                s.close();
                
            }     
    }

    private static String getContentType(File f)
    {
        String[] s = f.getName().split("\\.");
        String ext = s[s.length-1];
        switch(ext){
            case "html":
            case"htm":
                return "text/html";
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "css":
                return "text/css";
            case "js" :
                return "application/javascript";
            default : 
                return "";
        }

    }
}
