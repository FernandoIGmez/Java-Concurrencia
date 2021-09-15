package pecl3_2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */





/**
 *
 * @author FERNANDO
 */
public class Hilopeticiones extends Thread{ ///HILO QUE MANEJA EL SOCKET Y LAS PETICIONES DEL PROGRAMA PRINCIPAL
    ExecutorService hilosConexiones;
    Socket conexion;
    ServerSocket servidor;
    DataOutputStream salida;
    DataInputStream entrada;
    int numeroConexion =0;
    JTextField jt;
    Parque p;
   
    
    
    public Hilopeticiones (int numPeticionesSimultaneas, int puertoUtilizado , JTextField jt,Parque p) throws IOException
    {
        hilosConexiones=Executors.newFixedThreadPool(numPeticionesSimultaneas);
        servidor= new ServerSocket(puertoUtilizado);
        this.jt=jt;
        this.p=p;
    }
    public void run(){
        System.out.println("Server abierto....");
        try{
        while (true)
        {
            System.out.println("Server esperando peticion");
            conexion= servidor.accept(); // Esperamos una conexión
            //despues de que acepte el servidor la conexion,se le pasa la tarea al pool de hilos
            numeroConexion++;
          //  jt.setText(""+numeroConexion);
            
      //      AtenderPeticion peticion = new AtenderPeticion(entrada,salida,numeroConexion,conexion,expo, jt);
            AtenderPeticion peticion = new AtenderPeticion(conexion,p, jt);  //PRUEBA ON EL 2º CONSTRUCTOR
                
            hilosConexiones.execute(peticion);
        }}catch (IOException e){}
    }
}
