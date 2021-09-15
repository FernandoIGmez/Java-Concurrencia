/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;
import java.util.ArrayList;

/**
 *
 * @author FERNANDO
 */
public class Usuario extends Thread {

    private boolean acompañanteHaAcabado = false;

    private boolean estaDentroActividad = true;//SE UTILIZA PARA SINCRONIZAR AL ACOMPAÑANTE Y SU USUARIO ASOCIADO MENTRAS ELIGEN ACTIVIDAD

    private boolean monitorHaAcabado = false;
    private boolean puedePasar = false;
    private boolean adulto;
    private boolean acompañado;
    private boolean tieneAcompañantePiscinaOlas = false;
    private int id;
    private int edad;
    private ArrayList<String> listaActividades = new ArrayList<String>();
    private int actividadesARealizar = 0;
    private int actividadesRealizadas=0;

    private int idAcompañante;
    Parque parque;
    UsuarioAcompañante usuarioAcompañante;
    int actividadActual = 0;

    public Usuario(int id, Parque parque) {   //Constructor de un usuario normal
        this.id = id;
        this.parque = parque;
        edad = (int) (Math.random() * 49) + 1;
        
        actividadesARealizar = (int) (Math.random() * 10) + 5;
        if (edad < 11) {
            adulto = false;
            acompañado = true;
            tieneAcompañantePiscinaOlas = true;
        } else if (edad < 18 && edad > 10) { // si tiene entre 11 y 17 años es niño pero no va acompañado
            adulto = false;
            acompañado = false;
        } else if (edad >= 18) {
            adulto = true;
            acompañado = false;
        }

    }

    public Usuario(int id, int edadAcompañante, int idAcompañante, Parque parque) { //Constructor del usuario acompañante   //NO USAR
        this.id = id;                                                                      //--------------------------
        this.parque = parque;
        edad = (int) (Math.random() * 32) + 18;
        adulto = true;
        acompañado = true;
        this.idAcompañante = idAcompañante;
    }

    public Usuario(UsuarioAcompañante usuarioAcompañante, Parque parque) {
        this.usuarioAcompañante = usuarioAcompañante;
        this.parque = parque;
        this.id = usuarioAcompañante.getIdUsuario() + 1;
        this.actividadesARealizar = usuarioAcompañante.getActividadesARealizar();
    }

    public void run() {
        parque.entrarParque(this);
        parque.elegirSitioUsuario(this);
        parque.getVestuario().entraVestuario(this);
       
       while (actividadesRealizadas<actividadesARealizar){  
       try {
              
                int numeroaleat=parque.elegirSitioUsuario(this);

                if (numeroaleat == 1) {
                     parque.getPiscinaNiños().entraPiscina(this);
                } else if (numeroaleat == 2) {
                    parque.getPiscinaOlas().entraPiscinaOLAS(this);                   
                } else if (numeroaleat == 3) {
                    parque.getPiscinaGrande().usuarioEntraPiscina(this);
                } else if (numeroaleat == 4) {
                    parque.getTumbonas().usuarioTumbonas(this);                    
                    
                } else if (numeroaleat == 5) {
                    parque.getPiscinaGrande().usuarioEntraToboganA(this);
                
                } else if (numeroaleat == 6) {
                    parque.getPiscinaGrande().usuarioEntraToboganB(this);
                
                } else if (numeroaleat == 7) {
                   parque.getPiscinaGrande().usuarioEntraToboganC(this);                  
               
                } 
            } catch (Exception e) {
                System.out.println("EXCEPCION EN run() USUARIO");
            }
        }
        parque.getVestuario().entraVestuario(this);
        parque.salirParque(this);
    }

    public String getActividades() {     //Devuelve un string con todas las actividades realizadas por el usuario
        String actividades = "" + listaActividades.get(0);

        for (int i = 1; i < listaActividades.size(); i++) {
            actividades += listaActividades.get(i);
        }
        return actividades;
    }

    public String getIDentificacion() {  //Devueve el ID en forma de String de un Usuario    ID-
        String ident = "";
        if (acompañado) {
            ident = "ID" + id + "-" + edad + "-" + idAcompañante;
        } else {
            ident = "ID" + id + "-" + edad;
        }
        return ident;
    }
    public Usuario getUsuarioAcompañado(){
        return this;
    }
    public int getEdad() {
        return edad;
    }

    public boolean isAdulto() {
        return adulto;
    }

    public boolean isAcompañado() {
        return acompañado;
    }

    public int getIdUsuario() {
        return id;
    }

    public ArrayList<String> getListaActividades() {
        return listaActividades;
    }

    public int getActividadesARealizar() {
        return actividadesARealizar;
    }

    public int getIdAcompañante() {
        return idAcompañante;
    }

    public UsuarioAcompañante getUsuarioAsociado() {
        return usuarioAcompañante;
    }

    public boolean isEstaDentroActividad() {
        return estaDentroActividad;
    }

    public boolean isMonitorHaAcabado() {
        return monitorHaAcabado;
    }

    public boolean isPuedePasar() {
        return puedePasar;
    }

    public boolean isTieneAcompañantePiscinaOlas() {
        return tieneAcompañantePiscinaOlas;
    }

    public void setIdAcompañante(int idAcompañante) {
        this.idAcompañante = idAcompañante;
    }

    public void setUsuarioAcompañante(UsuarioAcompañante usuarioAsociado) {
        this.usuarioAcompañante = usuarioAsociado;
    }

    public void setEstaDentroActividad(boolean estaDentroActividad) {
        this.estaDentroActividad = estaDentroActividad;
    }

    public void setMonitorHaAcabado(boolean monitorHaAcabado) {
        this.monitorHaAcabado = monitorHaAcabado;
    }

    public void setPuedePasar(boolean puedePasar) {
        this.puedePasar = puedePasar;
    }

    public void setActividadActual(int actividadActual) {
        this.actividadActual = actividadActual;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setAdulto(boolean adulto) {
        this.adulto = adulto;
    }

    public void setAcompañado(boolean acompañado) {
        this.acompañado = acompañado;
    }

    public void setTieneAcompañantePiscinaOlas(boolean tieneAcompañantePiscinaOlas) {
        this.tieneAcompañantePiscinaOlas = tieneAcompañantePiscinaOlas;
    }

    public void setActividadesARealizar(int actividadesARealizar) {
        this.actividadesARealizar = actividadesARealizar;
    }

    public boolean isAcompañanteHaAcabado() {
        return acompañanteHaAcabado;
    }

    public void setAcompañanteHaAcabado(boolean acompañanteHaAcabado) {
        this.acompañanteHaAcabado = acompañanteHaAcabado;
    }

    public int getActividadesRealizadas() {
        return actividadesRealizadas;
    }

    public void setActividadesRealizadas(int actividadesRealizadas) {
        this.actividadesRealizadas = actividadesRealizadas;
    }

   
    
}
