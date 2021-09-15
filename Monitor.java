/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3_2;
/**
 *
 * @author FERNANDO
 */
public class Monitor extends Thread {

    Parque p;
    int identificacion;
    char actividad;
    char tobogan = 'X';

    public Monitor(Parque pa, int id, char ac, char tob) {
        p = pa;
        identificacion = id;
        actividad = ac;
        tobogan = tob;
    }

    public Monitor(Parque pa, int id, char ac) {
        p = pa;
        identificacion = id;
        actividad = ac;

    }

    public void run() {
        switch (actividad) {
            case 'V':
                try {
                while (true) {
                    p.getVestuario().monitorVestuario();
                }
            } catch (Exception e) {
            }
            break;
            case 'T':
                try {
                while (true) {
                    p.getTumbonas().monitorTumbonas();
                }
            } catch (Exception e) {
            }
            break;
            case 'O':
                try {
                while (true) {
                    p.getPiscinaOlas().monitorPiscina();
                }
            } catch (Exception e) {
            }
            break;
            case 'N':
                try {
                while (true) {
                    p.getPiscinaNiños().monitorPiscina();
                }
            } catch (Exception e) {
            }
            break;
            case 'G':
                try {
                while (true) {
                    p.getPiscinaGrande().monitorPiscina();
                }
            } catch (Exception e) {
            }
            break;
            case 'S':
                switch (tobogan) {
                    case 'A':
                try {
                        while (true) {
                            p.getPiscinaGrande().monitorToboganA();
                        }
                    } catch (Exception e) {
                    }
                    break;
                    case 'B':
                try {
                        while (true) {
                            p.getPiscinaGrande().monitorToboganB();
                        }
                    } catch (Exception e) {
                    }
                    break;
                    case 'C':
                try {
                        while (true) {
                            p.getPiscinaGrande().monitorToboganC();
                        }
                    } catch (Exception e) {
                    }
                    break;
                    default:
                        break;
                }

                break;
            default:
                break;
        }

    }

    public String identificacionMonitor() {
        return ("MON-" + identificacion);
    }
}
