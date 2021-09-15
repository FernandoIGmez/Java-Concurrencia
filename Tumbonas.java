/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;
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
public class Tumbonas {

    private ColaThreads entradaTumbonas;
    private ColaThreads zonaTumbonas;
    private Semaphore semaforoTumbonas = new Semaphore(20, false);
    private Parque p;
    private ReentrantLock cerrojoTumbonas = new ReentrantLock();
    private Condition despertarUsuarios = cerrojoTumbonas.newCondition();
    private Condition despertarMonitor = cerrojoTumbonas.newCondition();

    private ReentrantLock cerrojoAcompañante = new ReentrantLock();
    private Condition condicionAcompañante = cerrojoAcompañante.newCondition();
    private AtomicBoolean usuariosColaMirados = new AtomicBoolean(true); //Cuando todos los ususarios de la cola hayan sido vistos por el monitor se pone a true

    public Tumbonas(Parque p, JTextField tfColaT, JSpinner jsColat, JTextField tfZonaT, JSpinner jsZonat) {
        this.p = p;
        entradaTumbonas = new ColaThreads(tfColaT, jsColat);
        zonaTumbonas = new ColaThreads(tfZonaT, jsZonat);
    }

    public void usuarioTumbonas(Usuario u) {

        try {
            entradaTumbonas.meter(u);
            if (u.isAcompañado()) {
                entradaTumbonas.meter(u.getUsuarioAsociado());
            }
            p.estaParqueCerrado();
            usuariosColaMirados.compareAndExchange(true, false);
            cerrojoTumbonas.lock();
            despertarMonitor.signal();
            while (!u.isMonitorHaAcabado()) {           
                despertarUsuarios.await();

            }


        } catch (InterruptedException e) {
        } finally {
            cerrojoTumbonas.unlock();
        }

        try {
            if (u.isPuedePasar()) {
                semaforoTumbonas.acquire();
                entradaTumbonas.sacar(u);
                zonaTumbonas.meter(u);
                p.estaParqueCerrado();
                int tiempoTumbonas = (int) (Math.random() * 2) + 2;
                Thread.sleep(tiempoTumbonas * 1000);
                p.haRealizadoActividad(u);  //SUMAMOS 1 AL CONTADOR DE ACTIVIDADES DEL USUARIO
                zonaTumbonas.sacar(u);
                semaforoTumbonas.release();

            } else {
                entradaTumbonas.sacar(u);
                if (u.isAcompañado()) {
                    entradaTumbonas.sacar(u.getUsuarioAsociado());
                }
                p.estaParqueCerrado();
            }
            try{
                    cerrojoTumbonas.lock();
                    despertarMonitor.signalAll();
                }catch (Exception e){}
                finally{
                    cerrojoTumbonas.unlock();
                }
            u.setMonitorHaAcabado(false);
            u.setPuedePasar(false);
        } catch (InterruptedException e) {
        } finally {
        }
        
    }

    public void usuarioAsociadoTumbonas(UsuarioAcompañante u) {
        cerrojoTumbonas.lock();
        try {

            while (!u.isMonitorHaAcabado()) {
                despertarUsuarios.await();
            }
            u.setMonitorHaAcabado(false);
            u.setPuedePasar(false);

            entradaTumbonas.sacar(u);

            despertarMonitor.signal();//
        } catch (InterruptedException e) {
        } finally {
            cerrojoTumbonas.unlock();
        }

    }

    public void usuarioAsociadoTumbonasV2(UsuarioAcompañante u) {
        try {
            cerrojoAcompañante.lock();
            while (!u.isMonitorHaAcabado()) {
                condicionAcompañante.await();
            }
            
        } catch (InterruptedException e) {
        } finally {
            cerrojoAcompañante.lock();
        }
            p.estaParqueCerrado();
        if (entradaTumbonas.contiene(u)) {
            entradaTumbonas.sacar(u);
        }
    }

    public void monitorTumbonas() {
         p.estaParqueCerrado();
        try {
            cerrojoTumbonas.lock();

            while (entradaTumbonas.size() <= 0 || usuariosColaMirados.get()) {
                despertarUsuarios.signalAll();
                despertarMonitor.await();
            }
        } catch (InterruptedException ex) {
        } finally {
            cerrojoTumbonas.unlock();
        }

        try {
            for (int i = 0; i < entradaTumbonas.size(); i++) {
                Usuario u = entradaTumbonas.getPosicion(i);
                if (u.isAcompañado()) {
                    if (!u.isMonitorHaAcabado() && i < 400) {
                        int mirarEdad = (int) (Math.random() * 4) + 5;
                        Thread.sleep(mirarEdad * 100);
                        mirarEdad = (int) (Math.random() * 4) + 5;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);
                        u.setPuedePasar(false);
                        if(u.getEdad()<18){//PRUEBA MONITOR DESPIERTA AMBOS
                        u.getUsuarioAsociado().setMonitorHaAcabado(true);   
                        u.getUsuarioAsociado().setPuedePasar(false);}
 
                        i = 400;
                    }
                } else {
                    if (!u.isMonitorHaAcabado() && i < 400) {

                        int mirarEdad = (int) (Math.random() * 4) + 5;
                        Thread.sleep(mirarEdad * 100);
                        u.setMonitorHaAcabado(true);

                        if (u.getEdad() >= 15) {
                            u.setPuedePasar(true);
                        } else {
                            u.setPuedePasar(false);
                        }
                        i = 400;

                    }
                }
                if (entradaTumbonas.monitorHaMiradoTodos()) {
                    usuariosColaMirados.compareAndExchange(false, true);
                    i = 400;
                }

                //SI LLEGA AL FINAL DE LA COLA Y VE QUE TODOS ESTAN VISTOS YA, PONE EL BOOLEANO A TRUE

            }
            cerrojoTumbonas.lock();
            despertarUsuarios.signalAll();

        } catch (InterruptedException ex) {
        } finally {
            cerrojoTumbonas.unlock();
        }
    }

    public ColaThreads getEntradaTumbonas() {
        return entradaTumbonas;
    }

    public ColaThreads getZonaTumbonas() {
        return zonaTumbonas;
    }
    public void verPuedenPasar() {
        String contenido = "";
        for (int i = 0; i < entradaTumbonas.size(); i++) {
            contenido += ">" + entradaTumbonas.getPosicion(i).getIDentificacion() + "-";
            contenido += entradaTumbonas.getPosicion(i).isMonitorHaAcabado() + "< ";
        }
        System.out.println(contenido);
        System.out.println(usuariosColaMirados.get());
        System.out.println(semaforoTumbonas.availablePermits());
    }

}
