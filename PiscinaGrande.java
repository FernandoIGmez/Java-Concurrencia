/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author FERNANDO
 */
public class PiscinaGrande {
    private Usuario usuarioToboganA=null;//VARIABLE QUE ALMACENA EL USUARIO QUE SE ENCUENTRA EN EL TOBOGAN 
    private Usuario usuarioToboganB=null;//SE UTILIZAN UNICAMENTE PARA BUSCAR UN USUARIO ESPECIFICO
    private Usuario usuarioToboganC=null;
    //CERROJO QUE PONE A DORMIR A LOS USUARIOS DURANTE UN TIEMPO LIMITE O HASTA QUE EL MONITOR LES ECHA DEL AGUA
    private ArrayList<Usuario> hanPasadoToboganA = new ArrayList<Usuario>();    //LISTAS QUE GUARDAN TODOS LOS USUARIOS QUE HAN PASADO POR EL TOBOGAN
    private ArrayList<Usuario> hanPasadoToboganB = new ArrayList<Usuario>();
    private ArrayList<Usuario> hanPasadoToboganC = new ArrayList<Usuario>();
    
   
    private ColaThreads entradaPiscina, zonaPiscina;

    private JTextField jtToboganA, jtToboganB, jtToboganC;

    private ColaThreads entradaToboganA;
    private ColaThreads entradaToboganB;
    private ColaThreads entradaToboganC;
    private Semaphore semaforoToboganA = new Semaphore(1, true);
    private Semaphore semaforoToboganB = new Semaphore(1, true);
    private Semaphore semaforoToboganC = new Semaphore(1, true);
    
    private ReentrantLock cerrojoToboganA = new ReentrantLock();
    private Condition despertarUsuariosToboganA = cerrojoToboganA.newCondition();
    private Condition despertarMonitorToboganA = cerrojoToboganA.newCondition();
    private Semaphore semaforoPiscina = new Semaphore(50, true);
    
    private Parque p;
    private ReentrantLock cerrojoPiscina = new ReentrantLock();
    private Condition despertarUsuarios = cerrojoPiscina.newCondition();
    private Condition despertarMonitor = cerrojoPiscina.newCondition();

    private AtomicBoolean usuariosColaMirados = new AtomicBoolean(true); //Cuando todos los ususarios de la cola hayan sido vistos por el monitor se pone a true

    private AtomicBoolean usuariosToboganAmirados = new AtomicBoolean(true);//Cuando todos los ususarios de la cola del tobogan A hayan sido vistos por el monitor se pone a true
   

    private AtomicBoolean usuariosToboganBmirados = new AtomicBoolean(true);//Cuando todos los ususarios de la cola del tobogan B hayan sido vistos por el monitor se pone a true
    private ReentrantLock cerrojoToboganB = new ReentrantLock();
    private Condition despertarUsuariosToboganB = cerrojoToboganB.newCondition();
    private Condition despertarMonitorToboganB = cerrojoToboganB.newCondition();

    private AtomicBoolean usuariosToboganCmirados = new AtomicBoolean(true);//Cuando todos los ususarios de la cola del tobogan C hayan sido vistos por el monitor se pone a true
    private ReentrantLock cerrojoToboganC = new ReentrantLock();
    private Condition despertarUsuariosToboganC = cerrojoToboganC.newCondition();
    private Condition despertarMonitorToboganC = cerrojoToboganC.newCondition();

    public PiscinaGrande(Parque p, JTextField tfCola, JSpinner jsCola, JTextField tfZona, JSpinner jsZona, JTextField tfColaA, JSpinner jsColaA, JTextField tfColaB, JSpinner jsColaB, JTextField tfColaC, JSpinner jsColaC,
            JTextField jtToboganA, JTextField jtToboganB, JTextField jtToboganC) {
        this.p = p;
        entradaPiscina = new ColaThreads(tfCola, jsCola);
        zonaPiscina = new ColaThreads(tfZona, jsZona);
        entradaToboganA = new ColaThreads(tfColaA, jsColaA);
        entradaToboganB = new ColaThreads(tfColaB, jsColaB);
        entradaToboganC = new ColaThreads(tfColaC, jsColaC);
        this.jtToboganA = jtToboganA;
        this.jtToboganB = jtToboganB;
        this.jtToboganC = jtToboganC;
    }

    public void usuarioEntraPiscina(Usuario u) {      
        int tiempoActividad = (int) (Math.random() * 2) + 3;
        tiempoActividad *= 1000;
        try {
            entradaPiscina.meter(u);
            if (u.isAcompañado()) {
                entradaPiscina.meter(u.getUsuarioAsociado());
            }
            p.estaParqueCerrado();
            usuariosColaMirados.compareAndExchange(true, false);
            cerrojoPiscina.lock();
            despertarMonitor.signal();
            while (!u.isMonitorHaAcabado()) {
                despertarUsuarios.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoPiscina.unlock();
        }

        try {

            if (u.isAcompañado()) {
                semaforoPiscina.acquire(2);
                entradaPiscina.sacar(u);
                entradaPiscina.sacar(u.getUsuarioAsociado());
                zonaPiscina.meter(u);
                zonaPiscina.meter(u.getUsuarioAsociado());
            
            } else {
                semaforoPiscina.acquire();
                entradaPiscina.sacar(u);
                zonaPiscina.meter(u);
              
            }
            p.estaParqueCerrado();
        } catch (InterruptedException e) {
        }

        try {    //DESPUES DE ENTRAR A LA PISCINA HAY QUE DESPERTAR AL MONITOR POR SI NO HAY HUECOS LIBRES Y TIENE QUE ECHAR A ALGUIEN
            cerrojoPiscina.lock();
            despertarMonitor.signal();
        } catch (Exception e) {
        } finally {
            cerrojoPiscina.unlock();
        }
        try {
                p.haRealizadoActividad(u);
                Thread.sleep(tiempoActividad);
               

        } catch (InterruptedException e) {
            System.out.println("Usuario echado por el monitor");

        } finally {
                Thread.interrupted();
        }

        try {
            p.estaParqueCerrado();
            zonaPiscina.sacar(u);
            if (u.isAcompañado()) {
                zonaPiscina.sacar(u.getUsuarioAsociado());
                semaforoPiscina.release(2);
            } else {
                semaforoPiscina.release();
            }
        } catch (Exception e) {
        }
        try {
            cerrojoPiscina.lock();
            despertarMonitor.signalAll();
        } catch (Exception e) {
        } finally {
            cerrojoPiscina.unlock();
        }
        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void usuarioAsociadoEntraPiscina(UsuarioAcompañante u) {
        try {
            cerrojoPiscina.lock();
            {
                while (!u.isMonitorHaAcabado()) {
                    despertarUsuarios.await();
                }
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoPiscina.unlock();
        }
            p.estaParqueCerrado();
        try {
            if (!u.isInterrupted()) {
                p.haRealizadoActividad(u);
                Thread.sleep(u.getTiempoActividad());
            }
               Thread.sleep(u.getTiempoActividad());
            
        } catch (InterruptedException e) {
            System.out.println("Usuario asociado interrumpido");
        } finally {
          
               Thread.interrupted();                  //LIMPIAMOS EL ESTADO INTERRUPTED POR SI SE HA INTERRUMPIDO EL HILO
           
           u.setMonitorHaAcabado(false);
            u.setPuedePasar(false);
        }
    }

    public void monitorPiscina() {
        p.estaParqueCerrado();
        try {
            cerrojoPiscina.lock();
            //DUERME MIENTRAS QUE LA COLA DE ENTRADA ESTE VACIA 
            //O CUANDO ESTE LLENA LA COLA Y NO SE PUEDA ACCEDER A LA PISCINA
            while (entradaPiscina.size() == 0 || (!(entradaPiscina.size() > 0) && !(semaforoPiscina.availablePermits() == 0))) {
                
                despertarMonitor.await();
   
            }
        } catch (InterruptedException ex) {
        } finally {
            cerrojoPiscina.unlock();
        }
                try{
                    
                if (semaforoPiscina.availablePermits() == 0 && entradaPiscina.size() > 0 ) {
                    int usuariosPiscina;
                    if (zonaPiscina.size() > 0) {
                        usuariosPiscina = (zonaPiscina.size() - 1);
                    } else {
                        
                        usuariosPiscina = 0;
                    }
                    int personaAleatoria = (int) (Math.random() *1000);
                    personaAleatoria = (personaAleatoria%40)+9;
                    Usuario usuarioElegido = zonaPiscina.getPosicion(personaAleatoria);
                    if (usuarioElegido.isAcompañado()) {
                        for (int z = 0; z < zonaPiscina.size(); z++) {
                            if (zonaPiscina.getPosicion(z).getIdUsuario() == usuarioElegido.getIdAcompañante()) {
                                int tiempoaleat=(int)(Math.random()*5)+5;
                                Thread.sleep(tiempoaleat*100);
                                usuarioElegido.interrupt();
                                if (!usuarioElegido.isAdulto()){
                                    usuarioElegido.getUsuarioAsociado().interrupt();
                                }
                                else{
                                    usuarioElegido.getUsuarioAcompañado().interrupt();
                                }
                                z = 400;
                            }

                        }
                    } else {
                        usuarioElegido.interrupt();

                    }
                    
                }
                }catch (Exception e){
                    System.out.println("EXCEPCION AL ECHAR USUARIO");}
        try {
            for (int i = 0; i < entradaPiscina.size(); i++) {
                    Usuario u = entradaPiscina.getPosicion(i);                               
                if (u.isAcompañado()) {
                    if (!u.isMonitorHaAcabado() && i < 400) {

                        try {
                            
                            Thread.sleep(500);
                            
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(true);
                        if (u.getEdad() < 18) {
                            u.getUsuarioAsociado().setMonitorHaAcabado(true);
                            u.getUsuarioAsociado().setPuedePasar(true);
                        }
                        i = 400;

                    }
                } else {
                    if (!u.isMonitorHaAcabado() && i < 400) {
                        Thread.sleep(500);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(true);
                        i = 400;

                    }
                }
            
                if (entradaPiscina.monitorHaMiradoTodos()) {
                    usuariosColaMirados.compareAndExchange(false, true);
                    i = 400;
                }

            }
            cerrojoPiscina.lock();

            despertarUsuarios.signalAll();
        } catch (InterruptedException ex) {
        } finally {

            cerrojoPiscina.unlock();
        }

    }
//////////////////////////////////////////ACCIONES USUARIOS Y MONITORES TOBOGAN A//////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void usuarioEntraToboganA(Usuario u) {
        int tiempoActividad = (int) (Math.random()) + 2;
        tiempoActividad *= 1000;
        try {
            entradaToboganA.meter(u);
            if (u.isAcompañado()) {
                entradaToboganA.meter(u.getUsuarioAsociado());
            }
            p.estaParqueCerrado();
            usuariosToboganAmirados.compareAndExchange(true, false);
            cerrojoToboganA.lock();
            despertarMonitorToboganA.signal();
            while (!u.isMonitorHaAcabado()) {

                despertarUsuariosToboganA.await();
            }
            
        } catch (InterruptedException e) {
        } finally {
            cerrojoToboganA.unlock();
        }
        p.estaParqueCerrado();
        try {
            //SI ESTA ACOMPAÑADO NO PUEDE ENTRAR AL TOBOGAN
            
            if (u.isAcompañado()) {
               
                entradaToboganA.sacar(u);
                entradaToboganA.sacar(u.getUsuarioAsociado());

            } else {//SI NO ESTA ACOMPAÑADO
                if (u.isPuedePasar()) {   //SI EL MONITOR DECIDE QUE EL USUARIO PUEDE PASAR
                    semaforoPiscina.acquire();  //NO PUEDE USAR EL TOBOGAN SI NO HAY HUECO EN LA PISCINA
                    semaforoToboganA.acquire();
                    usuarioToboganA=u;
                    entradaToboganA.sacar(u);
                    jtToboganA.setText(u.getIDentificacion());
                    if(!hanPasadoToboganA.contains(u))hanPasadoToboganA.add(u);
                    try {
                        p.haRealizadoActividad(u);
                        Thread.sleep(tiempoActividad);
                        p.estaParqueCerrado();
                        jtToboganA.setText("");
                        usuarioToboganA=null;
                        semaforoToboganA.release();

                    } catch (InterruptedException e) {
                    }
                    zonaPiscina.meter(u);   //LLEGA A LA PISCINA 

                    try {    //DESPUES DE ENTRAR A LA PISCINA HAY QUE DESPERTAR AL MONITOR DE LA PISCINA 
                             //POR SI NO HAY HUECOS LIBRES Y TIENE QUE ECHAR A ALGUIEN
                        cerrojoPiscina.lock();
                        despertarMonitor.signal();
                    } catch (Exception e) {
                    } finally {
                        cerrojoPiscina.unlock();    //SALE DE LA PISCINA
                    }
                    zonaPiscina.sacar(u);
                    try {
                        semaforoPiscina.release();
                    } catch (Exception e) {
                    }
                } else {   //SI NO PUEDE PASAR SALE DE LA COLA
                    p.estaParqueCerrado();
                    entradaToboganA.sacar(u);
                }
            }
            cerrojoToboganA.lock();
            despertarMonitorToboganA.signal();
        } catch (InterruptedException e) {

        } finally {
            cerrojoToboganA.unlock();
        }

        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void usuarioAsociadoEntraToboganA(UsuarioAcompañante u) {
        try {
            cerrojoToboganA.lock();
            while (!u.isMonitorHaAcabado()) {
                despertarUsuariosToboganA.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoToboganA.unlock();
            p.estaParqueCerrado();
            u.setMonitorHaAcabado(false);
            u.setPuedePasar(false);
        }
    }

    public void monitorToboganA() {
         p.estaParqueCerrado();
        try {
            cerrojoToboganA.lock();
            //DUERME MIENTRAS QUE LA COLA DE ENTRADA ESTE VACIA O SE HAYA VISTO A TODOS LOS USUARIOS DE LA COLA
 
            while (entradaToboganA.size() <= 0 || usuariosToboganAmirados.get()) {
                despertarMonitorToboganA.await();
            }
        } catch (InterruptedException ex) {
        } finally {
            cerrojoToboganA.unlock();
        }

        try {
            for (int i = 0; i < entradaToboganA.size(); i++) {
                Usuario u = entradaToboganA.getPosicion(i);
                if (u.isAcompañado()) {
                    if (!u.isMonitorHaAcabado() && i < 400) {
                        int mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(false);
                        if (u.getEdad() < 18) {
                            u.getUsuarioAsociado().setMonitorHaAcabado(true);
                            u.getUsuarioAsociado().setPuedePasar(false);
                        }
                        i = 400;

                    }
                } else {
                    if (!u.isMonitorHaAcabado() && i < 400) {

                        int mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);
                        if (u.getEdad() < 15 && u.getEdad() > 10) { //SI TIENE ENTRE 11 Y 14 AÑOS PUEDE PASAR AL TOBOGAN 'A'                       
                            u.setPuedePasar(true);
                        } else {
                            u.setPuedePasar(false);
                        }
                        i = 400;

                    }
                }
                if (entradaToboganA.monitorHaMiradoTodos()) {
                    usuariosToboganAmirados.compareAndExchange(false, true);
                    i = 400;
                }

            }
            cerrojoToboganA.lock();
            despertarUsuariosToboganA.signalAll();
        } catch (InterruptedException ex) {
        } finally {
            cerrojoToboganA.unlock();
        }
    }
//////////////////////////////////////////ACCIONES USUARIOS Y MONITORES TOBOGAN B//////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void usuarioEntraToboganB(Usuario u) {
        int tiempoActividad = (int) (Math.random()) + 2;
        tiempoActividad *= 1000;
        try {
            entradaToboganB.meter(u);
            if (u.isAcompañado()) {
                entradaToboganB.meter(u.getUsuarioAsociado());
            }
                p.estaParqueCerrado();
            usuariosToboganBmirados.compareAndExchange(true, false);
            cerrojoToboganB.lock();
            despertarMonitorToboganB.signal();
            while (!u.isMonitorHaAcabado()) {

                despertarUsuariosToboganB.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoToboganB.unlock();
        }
            p.estaParqueCerrado();

        try {
            //SI ESTA ACOMPAÑADO NO PUEDE ENTRAR AL TOBOGAN
            if (u.isAcompañado()) {
                entradaToboganB.sacar(u);
                entradaToboganB.sacar(u.getUsuarioAsociado());

            } else {//SI NO ESTA ACOMPAÑADO
                if (u.isPuedePasar()) {   //SI EL MONITOR DECIDE QUE EL USUARIO PUEDE PASAR
                    semaforoPiscina.acquire();  //NO PUEDE USAR EL TOBOGAN SI NO HAY HUECO EN LA PISCINA
                    semaforoToboganB.acquire();
                    usuarioToboganB=u;
                    entradaToboganB.sacar(u);
                    jtToboganB.setText(u.getIDentificacion());
                    if(!hanPasadoToboganB.contains(u))hanPasadoToboganB.add(u);
                    try {
                        p.haRealizadoActividad(u);
                        Thread.sleep(tiempoActividad);
                        p.estaParqueCerrado();
                        jtToboganB.setText("");
                        usuarioToboganB=null;
                        semaforoToboganB.release();

                    } catch (InterruptedException e) {
                    }
                    zonaPiscina.meter(u);   //LLEGA A LA PISCINA 

                    try {    //DESPUES DE ENTRAR A LA PISCINA HAY QUE DESPERTAR AL MONITOR DE LA PISCINA 
                             //POR SI NO HAY HUECOS LIBRES Y TIENE QUE ECHAR A ALGUIEN
                        cerrojoPiscina.lock();
                        despertarMonitor.signal();
                    } catch (Exception e) {
                    } finally {
                        cerrojoPiscina.unlock();    //SALE DE LA PISCINA
                    }
                    zonaPiscina.sacar(u);
                    try {
                        semaforoPiscina.release();
                    } catch (Exception e) {
                    }
                } else {   //SI NO PUEDE PASAR SALE DE LA COLA
                    p.estaParqueCerrado();
                    entradaToboganB.sacar(u);
                }
            }
            cerrojoToboganB.lock();
            despertarMonitorToboganB.signal();
        } catch (InterruptedException e) {

        } finally {
            cerrojoToboganB.unlock();
        }

        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void usuarioAsociadoEntraToboganB(UsuarioAcompañante u) {
        try {
            cerrojoToboganB.lock();
            while (!u.isMonitorHaAcabado()) {
                despertarUsuariosToboganB.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoToboganB.unlock();
            p.estaParqueCerrado();
            u.setMonitorHaAcabado(false);
            u.setPuedePasar(false);
        }
    }

    public void monitorToboganB() {
         p.estaParqueCerrado();
        try {
            cerrojoToboganB.lock();
            //DUERME MIENTRAS QUE LA COLA DE ENTRADA ESTE VACIA O SE HAYA VISTO A TODOS LOS USUARIOS DE LA COLA
 
            while (entradaToboganB.size() <= 0 || usuariosToboganBmirados.get()) {
                despertarMonitorToboganB.await();
            }
        } catch (InterruptedException ex) {
        } finally {
            cerrojoToboganB.unlock();
        }

        try {
            for (int i = 0; i < entradaToboganB.size(); i++) {
                Usuario u = entradaToboganB.getPosicion(i);
                if (u.isAcompañado()) {
                    if (!u.isMonitorHaAcabado() && i < 400) {
                        int mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(false);
                        if (u.getEdad() < 18) {
                            u.getUsuarioAsociado().setMonitorHaAcabado(true);
                            u.getUsuarioAsociado().setPuedePasar(false);
                        }
                        i = 400;

                    }
                } else {
                    if (!u.isMonitorHaAcabado() && i < 400) {

                        int mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);
                        if (u.getEdad() < 18 && u.getEdad() > 14) { //SI TIENE ENTRE 15 Y 17 AÑOS PUEDE PASAR AL TOBOGAN 'B'                       
                            u.setPuedePasar(true);
                        } else {
                            u.setPuedePasar(false);
                        }
                        i = 400;

                    }
                }
                if (entradaToboganB.monitorHaMiradoTodos()) {
                    usuariosToboganBmirados.compareAndExchange(false, true);
                    i = 400;
                }

            }
            cerrojoToboganB.lock();
            despertarUsuariosToboganB.signalAll();
        } catch (InterruptedException ex) {
        } finally {
            cerrojoToboganB.unlock();
        }
    }
//////////////////////////////////////////ACCIONES USUARIOS Y MONITORES TOBOGAN C//////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    public void usuarioEntraToboganC(Usuario u) {
        int tiempoActividad = (int) (Math.random()) + 2;
        tiempoActividad *= 1000;
        try {
            entradaToboganC.meter(u);
            if (u.isAcompañado()) {
                entradaToboganC.meter(u.getUsuarioAsociado());
            }
            p.estaParqueCerrado();
            usuariosToboganCmirados.compareAndExchange(true, false);
            cerrojoToboganC.lock();
            despertarMonitorToboganC.signal();
            while (!u.isMonitorHaAcabado()) {

                despertarUsuariosToboganC.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoToboganC.unlock();
        }
            p.estaParqueCerrado();

        try {
            //SI ESTA ACOMPAÑADO NO PUEDE ENTRAR AL TOBOGAN
            if (u.isAcompañado()) {
            
                entradaToboganC.sacar(u);
                entradaToboganC.sacar(u.getUsuarioAsociado());

            } else {//SI NO ESTA ACOMPAÑADO
                if (u.isPuedePasar()) {   //SI EL MONITOR DECIDE QUE EL USUARIO PUEDE PASAR
                    semaforoPiscina.acquire();  //NO PUEDE USAR EL TOBOGAN SI NO HAY HUECO EN LA PISCINA
                    semaforoToboganC.acquire();
                    usuarioToboganC=u;
                    entradaToboganC.sacar(u);
                    jtToboganC.setText(u.getIDentificacion());
                    if(!hanPasadoToboganC.contains(u))hanPasadoToboganC.add(u);
                    try {
                        p.haRealizadoActividad(u);
                        Thread.sleep(tiempoActividad);
                        p.estaParqueCerrado();
                        jtToboganC.setText("");
                        usuarioToboganC=null;
                        semaforoToboganC.release();

                    } catch (InterruptedException e) {
                    }
                    zonaPiscina.meter(u);   //LLEGA A LA PISCINA 

                    try {    //DESPUES DE ENTRAR A LA PISCINA HAY QUE DESPERTAR AL MONITOR DE LA PISCINA 
                             //POR SI NO HAY HUECOS LIBRES Y TIENE QUE ECHAR A ALGUIEN
                        cerrojoPiscina.lock();
                        despertarMonitor.signal();
                    } catch (Exception e) {
                    } finally {
                        cerrojoPiscina.unlock();    //SALE DE LA PISCINA
                    }
                    zonaPiscina.sacar(u);
                    try {
                        semaforoPiscina.release();
                    } catch (Exception e) {
                    }
                } else {   //SI NO PUEDE PASAR SALE DE LA COLA
                        p.estaParqueCerrado();
                    entradaToboganC.sacar(u);
                }
            }
            cerrojoToboganC.lock();
            despertarMonitorToboganC.signal();
        } catch (InterruptedException e) {

        } finally {
            cerrojoToboganC.unlock();
        }

        u.setMonitorHaAcabado(false);
        u.setPuedePasar(false);
    }

    public void usuarioAsociadoEntraToboganC(UsuarioAcompañante u) {
        try {
            cerrojoToboganC.lock();
            while (!u.isMonitorHaAcabado()) {
                despertarUsuariosToboganC.await();
            }
        } catch (InterruptedException e) {
        } finally {
            cerrojoToboganC.unlock();
            p.estaParqueCerrado();
            u.setMonitorHaAcabado(false);
            u.setPuedePasar(false);
        }
    }

    public void monitorToboganC() {
         p.estaParqueCerrado();
        try {
            cerrojoToboganC.lock();
            //DUERME MIENTRAS QUE LA COLA DE ENTRADA ESTE VACIA O SE HAYA VISTO A TODOS LOS USUARIOS DE LA COLA
            //O CUANDO ESTE VACIA  
            while (entradaToboganC.size() <= 0 || usuariosToboganCmirados.get()) {
                despertarMonitorToboganC.await();
            }
        } catch (InterruptedException ex) {
        } finally {
            cerrojoToboganC.unlock();
        }

        try {
            for (int i = 0; i < entradaToboganC.size(); i++) {
                Usuario u = entradaToboganC.getPosicion(i);
                if (u.isAcompañado()) {
                    if (!u.isMonitorHaAcabado() && i < 400) {
                        int mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(false);
                        if (u.getEdad() < 18) {
                            u.getUsuarioAsociado().setMonitorHaAcabado(true);
                            u.getUsuarioAsociado().setPuedePasar(false);
                        }
                        i = 400;

                    }
                } else {
                    if (!u.isMonitorHaAcabado() && i < 400) {

                        int mirarEdad = (int) (Math.random()) + 4;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);
                        if (u.getEdad() >17) { //SI TIENE 18 AÑOS O MAS PUEDE PASAR AL TOBOGAN 'C'                       
                            u.setPuedePasar(true);
                        } else {
                            u.setPuedePasar(false);
                        }
                        i = 400;

                    }
                }
                if (entradaToboganC.monitorHaMiradoTodos()) {
                    usuariosToboganCmirados.compareAndExchange(false, true);
                    i = 400;
                }

            }
            cerrojoToboganC.lock();
            despertarUsuariosToboganC.signalAll();
        } catch (InterruptedException ex) {
        } finally {
            cerrojoToboganC.unlock();
        }
    }
///////////////////////////////////       FIN ACCIONES DE LOS TOBOGANES          //////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public ColaThreads getEntradaPiscina() {
        return entradaPiscina;
    }

    public ColaThreads getZonaPiscina() {
        return zonaPiscina;
    }

    public ColaThreads getEntradaToboganA() {
        return entradaToboganA;
    }

    public ColaThreads getEntradaToboganB() {
        return entradaToboganB;
    }

    public ColaThreads getEntradaToboganC() {
        return entradaToboganC;
    }    

    public ArrayList<Usuario> getHanPasadoToboganA() {
        return hanPasadoToboganA;
    }

    public ArrayList<Usuario> getHanPasadoToboganB() {
        return hanPasadoToboganB;
    }

    public ArrayList<Usuario> getHanPasadoToboganC() {
        return hanPasadoToboganC;
    }

    public Semaphore getSemaforoToboganA() {
        return semaforoToboganA;
    }

    public Semaphore getSemaforoToboganB() {
        return semaforoToboganB;
    }

    public Semaphore getSemaforoToboganC() {
        return semaforoToboganC;
    }

    public Usuario getUsuarioToboganA() {
        return usuarioToboganA;
    }

    public Usuario getUsuarioToboganB() {
        return usuarioToboganB;
    }

    public Usuario getUsuarioToboganC() {
        return usuarioToboganC;
    }
    
    public void verPuedenPasar() {
        String contenido = "";
        for (int i = 0; i < entradaPiscina.size(); i++) {
            contenido += ">" + entradaPiscina.getPosicion(i).getIDentificacion() + "-";
            contenido += entradaPiscina.getPosicion(i).isMonitorHaAcabado() + "< ";
        }
        System.out.println(contenido);
        System.out.println(usuariosColaMirados.get());
        System.out.println(semaforoPiscina.availablePermits());
    }
}
