/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author FERNANDO
 */
public class Parque {

    private AtomicInteger conexionesSimultaneas = new AtomicInteger(0);
    private ReentrantLock cerrojoPararParque = new ReentrantLock();
    private Condition pararParque = cerrojoPararParque.newCondition();//CONDITION QUE DUERME A LOS HILOS SI parqueparado = true
    private boolean parqueParado = false;

    public ArrayList<Usuario> arrayParejas = new ArrayList<Usuario>();//ARRAYLIST USADO PARA LA SINCRONIZACION DE LAS PAREJAS DE USUARIOS

    private ReentrantLock cerrojoElegirActividad = new ReentrantLock();//CERROJO USADO PARA LA SINCRONIZACION DE LAS PAREJAS DE USUARIOS
    private Condition condicionElegirActvidad = cerrojoElegirActividad.newCondition();

    private Semaphore semaforoEntrada = new Semaphore(100, true);//SEMAFORO QUE PERMITE TENER DENTRO DEL PARQUE A 100 USUARIOS SIMULTANEAMENTE
    private ColaThreads entradaParque;//COLA EN LA QUE SE ALMACENAN LOS USUARIOS QUE QUIEREN ENTRAR AL PARQUE
//Vestuario---------------------------------------------

    Vestuario vestuario;
    private Monitor monitorVestuario = new Monitor(this, 1, 'V');
//Tumbonas----------------------------------------------

    private Tumbonas tumbonas;
    private Monitor monitorTumbonas = new Monitor(this, 2, 'T');

//Piscina_Olas------------------------------------------
    private PiscinaOlas piscinaOlas;
    private Monitor monitorPiscinaOlas = new Monitor(this, 3, 'O');

//Piscina_Niños-----------------------------------------
    PiscinaNiños piscinaNiños;
    private Monitor monitorPiscinaNiños = new Monitor(this, 4, 'N');

//Piscina_Grande_+_Toboganes----------------------------  
    PiscinaGrande piscinaGrande;
    Monitor monitorPiscinaGrande = new Monitor(this, 5, 'G');
    Monitor monitorToboganA = new Monitor(this, 6, 'S', 'A');
    Monitor monitorToboganB = new Monitor(this, 7, 'S', 'B');
    Monitor monitorToboganC = new Monitor(this, 8, 'S', 'C');

//Jtext-y-Jspinner-del-apartado-gráfico-------------------    
    private JTextField jtEntradaParque, jtEntradaVestuario, jtVestuario,
            jtEntradaTumbonas, jtZonaTumbonas, jtEntradaPiscinaOlas, jtZonaPiscinaOlas,
            jtEntradaPiscinaNiños, jtZonaPiscinaNiños, jtZonaEsperaAdultos,
            jtEntradaPiscinaGrande, jtZonaPiscinaGrande,
            jtEntradaToboganA, jtEntradaToboganB, jtEntradaToboganC, jtToboganA, jtToboganB, jtToboganC;

    private JSpinner jsEntradaParque, jsEntradaVestuario, jsVestuario,
            jsEntradaTumbonas, jsZonaTumbonas, jsEntradaPiscinaOlas, jsZonaPiscinaOlas,
            jsEntradaPiscinaNiños, jsZonaPiscinaNiños, jsZonaEsperaAdultos,
            jsEntradaPiscinaGrande, jsZonaPiscinaGrande, jsEntradaToboganA, jsEntradaToboganB, jsEntradaToboganC;

//Constructor-del-parque-usado-en-el-Jframe----------------
    public Parque(JTextField jtEntradaParque, JTextField jtEntradaVestuario, JTextField jtVestuario,
            JSpinner jsEntradaParque, JSpinner jsEntradaVestuario, JSpinner jsVestuario,
            JTextField jtEntradaTumbonas, JSpinner jsEntradaTumbonas, JTextField jtZonaTumbonas, JSpinner jsZonaTumbonas,
            JTextField jtEntradaPiscinaOlas, JTextField jtZonaPiscinaOlas, JSpinner jsEntradaPiscinaOlas, JSpinner jsZonaPiscinaOlas,
            JTextField jtEntradaPiscinaNiños, JTextField jtZonaPiscinaNiños, JTextField jtZonaEsperaAdultos, JSpinner jsEntradaPiscinaNiños, JSpinner jsZonaPiscinaNiños, JSpinner jsZonaEsperaAdultos,
            JTextField jtEntradaPiscinaGrande, JTextField jtZonaPiscinaGrande, JTextField jtEntradaToboganA, JTextField jtEntradaToboganB, JTextField jtEntradaToboganC,
            JSpinner jsEntradaPiscinaGrande, JSpinner jsZonaPiscinaGrande, JSpinner jsEntradaToboganA, JSpinner jsEntradaToboganB, JSpinner jsEntradaToboganC,
            JTextField jtTobogan1, JTextField jtTobogan2, JTextField jtTobogan3,
            JTextField jtMonitorVestuario, JTextField jtMonitorTumbonas, JTextField jtMonitorPisOlas, JTextField jtMonitorPisNiños, JTextField jtMonitorPisGrande, JTextField jtMonitorTA, JTextField jtMonitorTB, JTextField jtMonitorTC) {

        this.jtEntradaParque = jtEntradaParque;

        this.jtEntradaVestuario = jtEntradaVestuario;
        this.jtVestuario = jtVestuario;
        this.jtEntradaTumbonas = jtEntradaTumbonas;
        this.jtZonaTumbonas = jtZonaTumbonas;
        this.jtEntradaPiscinaOlas = jtEntradaPiscinaOlas;
        this.jtZonaPiscinaOlas = jtZonaPiscinaOlas;
        this.jtEntradaPiscinaNiños = jtEntradaPiscinaNiños;
        this.jtZonaPiscinaNiños = jtZonaPiscinaNiños;
        this.jtZonaEsperaAdultos = jtZonaEsperaAdultos;
        this.jtEntradaPiscinaGrande = jtEntradaPiscinaGrande;
        this.jtZonaPiscinaGrande = jtZonaPiscinaGrande;
        this.jtEntradaToboganA = jtEntradaToboganA;
        this.jtEntradaToboganB = jtEntradaToboganB;
        this.jtEntradaToboganC = jtEntradaToboganC;
        this.jtToboganA = jtTobogan1;
        this.jtToboganB = jtTobogan2;
        this.jtToboganC = jtTobogan3;

        this.jsEntradaParque = jsEntradaParque;
        this.jsEntradaVestuario = jsEntradaVestuario;
        this.jsVestuario = jsVestuario;
        this.jsEntradaTumbonas = jsEntradaTumbonas;
        this.jsZonaTumbonas = jsZonaTumbonas;
        this.jsEntradaPiscinaOlas = jsEntradaPiscinaOlas;
        this.jsZonaPiscinaOlas = jsZonaPiscinaOlas;
        this.jsEntradaPiscinaNiños = jsEntradaPiscinaNiños;
        this.jsZonaPiscinaNiños = jsZonaPiscinaNiños;
        this.jsZonaEsperaAdultos = jsZonaEsperaAdultos;
        this.jsEntradaPiscinaGrande = jsEntradaPiscinaGrande;
        this.jsZonaPiscinaGrande = jsZonaPiscinaGrande;
        this.jsEntradaToboganA = jsEntradaToboganA;
        this.jsEntradaToboganB = jsEntradaToboganB;
        this.jsEntradaToboganC = jsEntradaToboganC;

        entradaParque = new ColaThreads(jtEntradaParque, jsEntradaParque);
        this.vestuario = new Vestuario(this, jtEntradaVestuario, jsEntradaVestuario, jtVestuario, jsVestuario);
        tumbonas = new Tumbonas(this, jtEntradaTumbonas, jsEntradaTumbonas, jtZonaTumbonas, jsZonaTumbonas);
        piscinaOlas = new PiscinaOlas(this, jtEntradaPiscinaOlas, jsEntradaPiscinaOlas, jtZonaPiscinaOlas, jsZonaPiscinaOlas);
        piscinaNiños = new PiscinaNiños(this, jtEntradaPiscinaNiños, jsEntradaPiscinaNiños, jtZonaPiscinaNiños, jsZonaPiscinaNiños, jtZonaEsperaAdultos, jsZonaEsperaAdultos);
        piscinaGrande = new PiscinaGrande(this, jtEntradaPiscinaGrande, jsEntradaPiscinaGrande, jtZonaPiscinaGrande, jsZonaPiscinaGrande, jtEntradaToboganA, jsEntradaToboganA, jtEntradaToboganB, jsEntradaToboganB, jtEntradaToboganC, jsEntradaToboganC,
                this.jtToboganA, this.jtToboganB, this.jtToboganC);

        jtMonitorVestuario.setForeground(Color.blue);
        jtMonitorVestuario.setText(monitorVestuario.identificacionMonitor());
        jtMonitorTumbonas.setForeground(Color.blue);
        jtMonitorTumbonas.setText(monitorTumbonas.identificacionMonitor());
        jtMonitorPisOlas.setForeground(Color.blue);
        jtMonitorPisOlas.setText(monitorPiscinaOlas.identificacionMonitor());
        jtMonitorPisNiños.setForeground(Color.blue);
        jtMonitorPisNiños.setText(monitorPiscinaNiños.identificacionMonitor());
        jtMonitorPisGrande.setForeground(Color.blue);
        jtMonitorPisGrande.setText(monitorPiscinaGrande.identificacionMonitor());
        jtMonitorTA.setForeground(Color.blue);
        jtMonitorTA.setText(monitorToboganA.identificacionMonitor());
        jtMonitorTB.setForeground(Color.blue);
        jtMonitorTB.setText(monitorToboganB.identificacionMonitor());
        jtMonitorTC.setForeground(Color.blue);
        jtMonitorTC.setText(monitorToboganC.identificacionMonitor());

        monitorVestuario.start();
        monitorTumbonas.start();
        monitorPiscinaNiños.start();
        monitorPiscinaOlas.start();
        monitorPiscinaGrande.start();
        monitorToboganA.start();
        monitorToboganB.start();
        monitorToboganC.start();

    }

    //ACCIONES USUARIO ENTRAR Y SALIR DEL PARQUE
    public void haRealizadoActividad(Usuario u) {
        u.setActividadesRealizadas(u.getActividadesRealizadas() + 1);
    }
    
    public void entrarParque(Usuario u) {
        entradaParque.meter(u);

        if (u.isAcompañado()) {
            try {
                entradaParque.meter(u.getUsuarioAsociado());
                semaforoEntrada.acquire(2);
            } catch (InterruptedException e) {
            }
            estaParqueCerrado();
            entradaParque.sacar(u);
            entradaParque.sacar(u.getUsuarioAsociado());

        } else {
            try {
                semaforoEntrada.acquire();
            } catch (InterruptedException e) {
            }
            entradaParque.sacar(u);

        }
        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void salirParque(Usuario u) {
        if (u.isAcompañado()) {
            try {
                semaforoEntrada.release(2);

            } catch (Exception e) {
            }
        } else {
            try {
                semaforoEntrada.release();

            } catch (Exception e) {
            }
        }
    }

    //METODOS QUE CONTROLAN EL ESTADO DEL PARQUE, PARANDO LOS HILOS O REANUDANDOLOS//
    public void estaParqueCerrado() { //PARA TODOS LOS HILOS EN EJECUCION SI parqueparado=true
        if (parqueParado) {
            try {
                cerrojoPararParque.lock();
                pararParque.await();
            } catch (InterruptedException e) {
            } finally {
                cerrojoPararParque.unlock();
            }
        }
    }

    public boolean estadoParque() { //INFORMA SI EL PARQUE ESTA PARADO O NO
        return parqueParado;
    }

    public void reanudarParque() {  //REANUDA EL PARQUE DESPERTANDO LOS HILOS DORMIDOS
        parqueParado = false;       
        try {
            cerrojoPararParque.lock();
            pararParque.signalAll();
        } catch (Exception e) {
        } finally {
            cerrojoPararParque.unlock();
        }
    }

    public void pararParque() {     //PONE A true LA CONDICION QUE HACE PARAR LOS HILOS
        parqueParado = true;
    }

    public void añadirConexion(JTextField jt) { //SUMA 1 AL CONTADOR DE CONEXIONES SIMULTANEAS
                                                //Y LO MUESTRA EN EL JTEXT
        conexionesSimultaneas.getAndIncrement();
        jt.setText("" + conexionesSimultaneas.get());
    }

    public void liberarConexion(JTextField jt) {

        conexionesSimultaneas.getAndDecrement();
        jt.setText("" + conexionesSimultaneas.get());

    }

    //METODOS PARA EL ENVIO DE DATOS CLIENTE-SERVIDOR
    public String aforoVestuarios() {   //DEVUELVE LA CANTIDAD DE USUARIOS EN EL VESTUARIO
        int aforo = 0;                  //O ESPERANDO EN ESTE
        ArrayList<Usuario> arraybusqueda;
        arraybusqueda = getVestuario().getEntradaVestuario().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getVestuario().getZonaVestuario().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        return aforo + "";
    }

    public String aforoTumbonas() {     
        int aforo = 0;
        ArrayList<Usuario> arraybusqueda;
        arraybusqueda = getTumbonas().getEntradaTumbonas().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getTumbonas().getZonaTumbonas().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        return aforo + "";
    }

    public String aforoPiscinaOlas() {
        int aforo = 0;
        ArrayList<Usuario> arraybusqueda;
        arraybusqueda = getPiscinaOlas().getEntradaPiscina().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getPiscinaOlas().getZonaPiscina().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        return aforo + "";
    }

    public String aforoPiscinaNiños() {
        int aforo = 0;
        ArrayList<Usuario> arraybusqueda;
        arraybusqueda = getPiscinaNiños().getEntradaPiscina().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getPiscinaNiños().getZonaPiscina().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getPiscinaNiños().getZonaEsperaPiscina().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        return aforo + "";
    }

    public String aforoPiscinaGrande() {
        int aforo = 0;
        ArrayList<Usuario> arraybusqueda;
        arraybusqueda = getPiscinaGrande().getEntradaPiscina().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getPiscinaGrande().getZonaPiscina().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        return aforo + "";
    }

    public String aforoToboganes() {
        int aforo = 0;
        ArrayList<Usuario> arraybusqueda;
        arraybusqueda = getPiscinaGrande().getEntradaToboganA().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getPiscinaGrande().getEntradaToboganC().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        arraybusqueda = getPiscinaGrande().getEntradaToboganB().getLista();
        for (int x = 0; x < arraybusqueda.size(); x++) {
            aforo++;
        }
        if (getPiscinaGrande().getSemaforoToboganA().availablePermits() == 0) {
            aforo++;
        }
        if (getPiscinaGrande().getSemaforoToboganB().availablePermits() == 0) {
            aforo++;
        }
        if (getPiscinaGrande().getSemaforoToboganC().availablePermits() == 0) {
            aforo++;
        }
        return aforo + "";
    }

    public String buscaUsuario(int idUsuario) { //BUSCA AL USUARIO POR LAS ZONAS PARA VER SI ESTA O NO
        ArrayList<Usuario> arraybusqueda;       //SI LO ENCUENTRA DEVUELVE SU ID Y SI NO DEVUELVE  "No encontrado"
        
        if (getPiscinaGrande().getUsuarioToboganA() != null) {
            if (getPiscinaGrande().getUsuarioToboganA().getIdUsuario() == idUsuario) {
                return getPiscinaGrande().getUsuarioToboganA().getIDentificacion() + "";
            }
        }
        if (getPiscinaGrande().getUsuarioToboganB() != null) {
            if (getPiscinaGrande().getUsuarioToboganB().getIdUsuario() == idUsuario) {
                return getPiscinaGrande().getUsuarioToboganB().getIDentificacion() + "";
            }
        }
        if (getPiscinaGrande().getUsuarioToboganC() != null) {
            if (getPiscinaGrande().getUsuarioToboganC().getIdUsuario() == idUsuario) {
                return getPiscinaGrande().getUsuarioToboganC().getIDentificacion() + "";
            }

        }
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                arraybusqueda = getVestuario().getEntradaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getVestuario().getZonaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
            } else if (i == 1) {
                arraybusqueda = getTumbonas().getEntradaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getTumbonas().getZonaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
            } else if (i == 2) {
                arraybusqueda = getPiscinaOlas().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getPiscinaOlas().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
            } else if (i == 3) {
                arraybusqueda = getPiscinaNiños().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaEsperaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
            } else if (i == 4) {
                arraybusqueda = getPiscinaGrande().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getPiscinaGrande().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganA().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganC().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganB().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getIDentificacion();
                    }
                }

            }
        }
        
        return "No encontrado";
    }

    public String buscaZona(int idUsuario) { //BUSCA AL USUARIO POR LAS ZONAS Y DEVUELVE EL LUGAR EN  EL QUE ESTA
        ArrayList<Usuario> arraybusqueda;
        if (getPiscinaGrande().getUsuarioToboganA() != null) {
            if (getPiscinaGrande().getUsuarioToboganA().getIdUsuario() == idUsuario) {
                return "Tobogan A";
            }
        }
        if (getPiscinaGrande().getUsuarioToboganB() != null) {
            if (getPiscinaGrande().getUsuarioToboganB().getIdUsuario() == idUsuario) {
                return "Tobogan B";
            }
        }
        if (getPiscinaGrande().getUsuarioToboganC() != null) {
            if (getPiscinaGrande().getUsuarioToboganC().getIdUsuario() == idUsuario) {
                return "Tobogan C";
            }
        }
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                arraybusqueda = getVestuario().getEntradaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Vestuario";
                    }
                }
                arraybusqueda = getVestuario().getZonaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Zona Vestuario";
                    }
                }
            } else if (i == 1) {
                arraybusqueda = getTumbonas().getEntradaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Tumbonas";
                    }
                }
                arraybusqueda = getTumbonas().getZonaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Zona Tumbonas";
                    }
                }
            } else if (i == 2) {
                arraybusqueda = getPiscinaOlas().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Piscina Olas";
                    }
                }
                arraybusqueda = getPiscinaOlas().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Piscina Olas";
                    }
                }
            } else if (i == 3) {
                arraybusqueda = getPiscinaNiños().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Piscina Niños";
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Piscina Niños";
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaEsperaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Zona Espera Piscina N.";
                    }
                }
            } else if (i == 4) {
                arraybusqueda = getPiscinaGrande().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Piscina Grande";
                    }
                }
                arraybusqueda = getPiscinaGrande().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Piscina Grande";
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganA().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Tobogan A";
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganC().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Tobogan C";
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganB().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return "Entrada Tobogan B";
                    }
                }

            }
        }
        
        return "No encontrado";
    }

    public String buscaActividades(int idUsuario) { //BUSCA AL USUARIO POR LAS ZONAS PARA VER SI ESTA O NO Y SI LO ENCUENTRA DEVUELVE LAS ACTIVIDADES QEU HA REALIZADO
        ArrayList<Usuario> arraybusqueda;
        if (getPiscinaGrande().getUsuarioToboganA() != null) {
            if (getPiscinaGrande().getUsuarioToboganA().getIdUsuario() == idUsuario) {
                return getPiscinaGrande().getUsuarioToboganA().getActividadesRealizadas() + "";
            }
        }
        if (getPiscinaGrande().getUsuarioToboganB() != null) {
            if (getPiscinaGrande().getUsuarioToboganB().getIdUsuario() == idUsuario) {
                return getPiscinaGrande().getUsuarioToboganB().getActividadesRealizadas() + "";
            }
        }
        if (getPiscinaGrande().getUsuarioToboganC() != null) {
            if (getPiscinaGrande().getUsuarioToboganC().getIdUsuario() == idUsuario) {
                return getPiscinaGrande().getUsuarioToboganC().getActividadesRealizadas() + "";
            }

        }
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                arraybusqueda = getVestuario().getEntradaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getVestuario().getZonaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
            } else if (i == 1) {
                arraybusqueda = getTumbonas().getEntradaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getTumbonas().getZonaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
            } else if (i == 2) {
                arraybusqueda = getPiscinaOlas().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getPiscinaOlas().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
            } else if (i == 3) {
                arraybusqueda = getPiscinaNiños().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaEsperaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
            } else if (i == 4) {
                arraybusqueda = getPiscinaGrande().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getPiscinaGrande().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganA().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganC().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganB().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (arraybusqueda.get(x).getIdUsuario() == idUsuario) {
                        return arraybusqueda.get(x).getActividadesRealizadas() + "";
                    }
                }

            }
        }
        
        return "No encontrado";
    }

    public String cuantosNiños() { //DEVUELVE LA CANTIDAD DE NIÑOS QUE HAY DENTRO DEL PARQUE
        int numeroMenores = 0;
        ArrayList<Usuario> arraybusqueda;
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                arraybusqueda = getVestuario().getEntradaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getVestuario().getZonaVestuario().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
            } else if (i == 1) {
                arraybusqueda = getTumbonas().getEntradaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getTumbonas().getZonaTumbonas().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
            } else if (i == 2) {
                arraybusqueda = getPiscinaOlas().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getPiscinaOlas().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
            } else if (i == 3) {
                arraybusqueda = getPiscinaNiños().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getPiscinaNiños().getZonaEsperaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
            } else if (i == 4) {
                arraybusqueda = getPiscinaGrande().getEntradaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getPiscinaGrande().getZonaPiscina().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganA().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganC().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
                arraybusqueda = getPiscinaGrande().getEntradaToboganB().getLista();
                for (int x = 0; x < arraybusqueda.size(); x++) {
                    if (!arraybusqueda.get(x).isAdulto()) {
                        numeroMenores++;
                    }
                }
            }
        }
        return numeroMenores + "";
    }

    public String cuantosPasadoToboganA() { //DEVUELVE EL NUMERO DE USUARIOS QUE HAN USADO EL TOBOGAN A
        return getPiscinaGrande().getHanPasadoToboganA().size() + "";
    }

    public String cuantosPasadoToboganB() {
        return getPiscinaGrande().getHanPasadoToboganB().size() + "";
    }

    public String cuantosPasadoToboganC() {
        return getPiscinaGrande().getHanPasadoToboganC().size() + "";
    }

    //METODOS PARA IMPRIMIR POR PANTALLA EN LOS JTEXTFIELD CUANDO LOS JSPINNER SE ACTIVEN//
    
    public void mostrarIDSColaEntrada(int posicion) {   //METODO QUE SE USA CUANDO EL JSPINNER CAMBIA DE VALOR,
        entradaParque.imprimir(posicion);               //PARA IMPRIMIR LOS USUARIOS CORRESPONDIENTES DE UNA LISTA
    }

    public void mostrarIDSColaEntradaVestuarios(int posicion) {
        vestuario.getEntradaVestuario().imprimir(posicion);
    }

    public void mostrarIDSVestuarios(int posicion) {
        vestuario.getZonaVestuario().imprimir(posicion);
    }

    public void mostrarIDSColaTumbonas(int p) {
        tumbonas.getEntradaTumbonas().imprimir(p);
    }

    public void mostrarIDSZonaTumbonas(int p) {
        tumbonas.getZonaTumbonas().imprimir(p);
    }

    public void mostrarIDSColaPiscinaOlas(int p) {
        piscinaOlas.getEntradaPiscina().imprimir(p);
    }

    public void mostrarIDSZonaPiscinaOlas(int p) {
        piscinaOlas.getZonaPiscina().imprimir(p);
    }

    public void mostrarIDSColaPiscinaNiños(int p) {
        piscinaNiños.getEntradaPiscina().imprimir(p);
    }

    public void mostrarIDSZonaPiscinaNiños(int p) {
        piscinaNiños.getZonaPiscina().imprimir(p);
    }

    public void mostrarIDSZonaEsperaPiscinaNiños(int p) {
        piscinaNiños.getZonaEsperaPiscina().imprimir(p);
    }

    public void mostrarIDSColaPiscinaGrande(int p) {
        piscinaGrande.getEntradaPiscina().imprimir(p);
    }

    public void mostrarIDSZonaPiscinaGrande(int p) {
        piscinaGrande.getZonaPiscina().imprimir(p);
    }

    public void mostrarIDSColaToboganA(int p) {
        piscinaGrande.getEntradaToboganA().imprimir(p);
    }

    public void mostrarIDSColaToboganB(int p) {
        piscinaGrande.getEntradaToboganB().imprimir(p);
    }

    public void mostrarIDSColaToboganC(int p) {
        piscinaGrande.getEntradaToboganC().imprimir(p);
    }
    
    //GET DE LAS DISTINTAS ZONAS DEL PARQUE
    public Tumbonas getTumbonas() {
        return tumbonas;
    }

    public Vestuario getVestuario() {
        return vestuario;
    }

    public PiscinaOlas getPiscinaOlas() {
        return piscinaOlas;
    }

    public PiscinaNiños getPiscinaNiños() {
        return piscinaNiños;
    }

    public PiscinaGrande getPiscinaGrande() {
        return piscinaGrande;
    }

//METODOS QUE SINCRONIZAN AL USUARIO Y SU ACOMPAÑANTE PARA ELEGIR LA SIGUIENTE ACTIVIDAD A REALIZAR, SE HACE ANTES DE ENTRAR A LA ACTIVIDAD ELEGIDA// 
    
    //SI ES UN USUARIO SOLO, ELIGE UNA ACTIVIDAD ALEATORIA. SI VA ACOMPAÑADO, SE METE EN UNA LISTA Y DUERME
    //HASTA QUE EL ACOMPAÑANTE ESTE EN LA LISTA, HACIENDO QUE ENTREN EN LA PROXIMA ACTIVIDAD A LA VEZ
    public int elegirSitioUsuario(Usuario u) {
        
        int actividadAleatoria = (int) (Math.random() * 4000) + 1;
        actividadAleatoria = (actividadAleatoria % 7) + 1;
        if (u.isAcompañado()) {
            try {
                meterArrayParejas(u);
                cerrojoElegirActividad.lock();
                u.getUsuarioAsociado().setActividadActual(actividadAleatoria);
                while (!estaMiPareja(u.getIdAcompañante())) {
                    condicionElegirActvidad.await();
                }
                condicionElegirActvidad.signalAll();
                sacarArrayParejas(u);

            } catch (InterruptedException e) {
            } finally {
                cerrojoElegirActividad.unlock();
            }
        }
        return actividadAleatoria;

    }

    public void elegirsitioAcompañante(UsuarioAcompañante u) {
        try {
            meterArrayParejas(u);
            cerrojoElegirActividad.lock();
            while (!estaMiPareja(u.getIdAcompañante())) {
                condicionElegirActvidad.await();
            }
            condicionElegirActvidad.signalAll();
            sacarArrayParejas(u);

        } catch (InterruptedException e) {
        } finally {
            cerrojoElegirActividad.unlock();
        }
    }

    //METODOS AUXILIARES PARA LA SINCRONIZACION DE LAS PAREJAS//
    public synchronized void meterArrayParejas(Usuario u) {
        arrayParejas.add(u);
    }

    public synchronized void sacarArrayParejas(Usuario u) {
        arrayParejas.remove(u);
    }

    public synchronized boolean estaMiPareja(int idPareja) {    //DEVUEVE TRUE SI LA PAREJA ESTA EN LA LISTA
        for (int i = 0; i < arrayParejas.size(); i++) {
            if (arrayParejas.get(i).getIdUsuario() == idPareja) {
                return true;
            }
        }
        return false;
    }
}
