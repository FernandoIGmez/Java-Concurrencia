package pecl3_2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
public class AtenderPeticion implements Runnable {

    private DataInputStream entrada;
    private DataOutputStream salida;
    private int num;
    Socket conexion;
    Parque parque;
    JTextField jt;

    public AtenderPeticion(DataInputStream entrada, DataOutputStream salida, int num, Socket conexion, Parque parque, JTextField jt) {
        this.entrada = entrada;
        this.salida = salida;
        this.num = num;
        this.conexion = conexion;
        this.jt = jt;
        this.parque = parque;

    }

    //2º constructor con menos parametros
    public AtenderPeticion(Socket conexion, Parque parque, JTextField jt) {
        // this.num = num;
        this.conexion = conexion;
        this.jt = jt;
        this.parque = parque;
    }

    public void run() {
        try {
            parque.añadirConexion(jt);
            boolean a = true;

            while (a) {
                entrada = new DataInputStream(conexion.getInputStream());  // Abrimos los canales de E/S
                salida = new DataOutputStream(conexion.getOutputStream());
                String mensaje =(String) entrada.readUTF().trim();    //Leemos el mensaje del cliente 
                //////////
                
                if (mensaje.equals("Detener")) {
                    parque.pararParque();
                } else if (mensaje.equals("Reanudar")) {
                    parque.reanudarParque();
                } else if(mensaje.equals("Ubicacion")){
                    int idBuscar=entrada.readInt();
                    //FUNCIONeS DE BUSQUEDA
                    //
                    String usuario=parque.buscaUsuario(idBuscar);
                    String ubicacion=parque.buscaZona(idBuscar);
                    String actividades=parque.buscaActividades(idBuscar);
                    salida.writeUTF(usuario);//Usuario
                    salida.writeUTF(ubicacion);//Ubicacion
                    salida.writeUTF(actividades);//Actividades
                    
                }else if (mensaje.equals("Menores")){
                    String menores=parque.cuantosNiños();
                    salida.writeUTF(menores);
                    
                }else if (mensaje.equals("Toboganes")){
                    salida.writeUTF(parque.cuantosPasadoToboganA());
                    salida.writeUTF(parque.cuantosPasadoToboganB());
                    salida.writeUTF(parque.cuantosPasadoToboganC());
                }else if (mensaje.equals("Aforo")){
                    salida.writeUTF(parque.aforoVestuarios());
                    salida.writeUTF(parque.aforoTumbonas());
                    salida.writeUTF(parque.aforoPiscinaOlas());
                    salida.writeUTF(parque.aforoPiscinaNiños());
                    salida.writeUTF(parque.aforoPiscinaGrande());
                    salida.writeUTF(parque.aforoToboganes());
                }
                
                else if (mensaje.equals("Cierre")) {
                    a = false;
                    parque.liberarConexion(jt);
                    entrada.close();
                    salida.close();
                    conexion.close();

                }
            }

        } catch (IOException e) {
            parque.liberarConexion(jt);
        }

    }
}
