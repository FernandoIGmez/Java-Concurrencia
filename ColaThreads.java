/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;

import java.util.ArrayList;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author FERNANDO
 */
public class ColaThreads {

    ArrayList<Usuario> lista;
    JTextField tf;
    JSpinner spinner;

    public ColaThreads(JTextField tf, JSpinner s) {
        lista = new ArrayList<Usuario>();
        this.tf = tf;
        this.spinner = s;
    }

    public ColaThreads() {   //CONSTRUCTOR DE PRUEBA PARA EL MAIN SIN INTERFAZ GRAFICA
        lista = new ArrayList<Usuario>();
    }

    public synchronized void meter(Usuario t) {
        lista.add(t);
        imprimir((int) spinner.getValue());

    }

    public synchronized void sacar(Usuario t) {
        lista.remove(t);
        imprimir((int) spinner.getValue());

    }

    public void imprimir(int posicion) {

        String arrayIds[] = arrayIds();
        try {
            int posiciones = arrayIds.length;
            int valorSpinner = (int) (spinner.getValue()) * -1;    //Cambiamos los negtivos a positivos paara que los siguientes ids salgan cuando pulsemos        
            if (valorSpinner >= 0 && valorSpinner < posiciones) {                   //la flecha hacia abajo
                tf.setText(arrayIds[valorSpinner]);         //Si esta entre los limites escribir la posicion correspondiente
            } else if (valorSpinner < 0) {
                tf.setText("vvvvvvvvvvvvvvvvvvvv");
            } else if (valorSpinner >= posiciones) {
                tf.setText("^^^^^^^^^^^^^^^^^^^^");
            }
        } catch (Exception e) {
            tf.setText("excepcion");
        }
    }

    public String[] arrayIds() {        //Funcion que pone 4 IDS en cada posicion del array para luego pasarselo a la interfaz grafica
        int longitudlista = lista.size();
        int capacidadArray;
        if ((longitudlista % 4) == 0) {
            capacidadArray = (int) (longitudlista / 4);
        } else {
            capacidadArray = (int) (longitudlista / 4);
            capacidadArray = capacidadArray + 1;
        }
        String[] ids = new String[capacidadArray];
        int valor = 0;
        for (int i = 0; i < (capacidadArray); i++) {
            String contenido = "";
            int x = 0;
            while (x < 4) {

                if (valor < longitudlista) {
                    contenido += lista.get(valor).getIDentificacion() + " ";
                } else {
                    x = 4;
                }
                valor++;
                x++;
            }
            ids[i] = contenido;
        }
        return ids;
    }

    public synchronized boolean estaprimero(Usuario u) {
        return lista.get(0).equals(u);
    }

    public synchronized boolean contiene(Usuario u) {
        return (lista.contains(u));
    }

    public synchronized int size() {
        return lista.size();
    }

 
    public synchronized Usuario getPosicion(int i) {
        return lista.get(i);

    }

    public synchronized boolean monitorHaMiradoTodos() {
        if (lista.size() == 0) {
            return true;
        } else {
            for (int i = 0; i < lista.size(); i++) //SI SE ENCUENTRA UN ELEMENTO QUE NO HAYA MIRADO DEVUELVE FALSE
            {
                if (!lista.get(i).isMonitorHaAcabado()) {
                    return false;
                }
            }

            return true;
        }
    }

    public ArrayList<Usuario> getLista(){
        return lista;
    }
}
