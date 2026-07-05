package vigiaurbano.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import vigiaurbano.model.Autoridad;

/**
 * Catálogo de las estaciones oficiales de Manizales.
 */
public class EstacionesManizales {

    public static List<Autoridad> crearCuentas() {
        List<Autoridad> cuentas = new ArrayList<>();

        // CAIs de la Policía Nacional en Manizales
        Autoridad cai1 = new Autoridad("POL-01", "CAI Centro", "cai.centro@policia.gov.co", "Centro2024", "Patrullero", "CAI Centro");
        cai1.setOrganismo("Policía Nacional");
        cuentas.add(cai1);

        Autoridad cai2 = new Autoridad("POL-02", "CAI Cable", "cai.cable@policia.gov.co", "Cable2024", "Patrullero", "CAI Cable");
        cai2.setOrganismo("Policía Nacional");
        cuentas.add(cai2);

        Autoridad cai3 = new Autoridad("POL-03", "CAI Palogrande", "cai.palogrande@policia.gov.co", "Palogrande2024", "Patrullero", "CAI Palogrande");
        cai3.setOrganismo("Policía Nacional");
        cuentas.add(cai3);

        Autoridad cai4 = new Autoridad("POL-04", "CAI San José", "cai.sanjose@policia.gov.co", "SanJose2024", "Patrullero", "CAI San José");
        cai4.setOrganismo("Policía Nacional");
        cuentas.add(cai4);

        Autoridad cai5 = new Autoridad("POL-05", "CAI La Enea", "cai.laenea@policia.gov.co", "Enea2024", "Patrullero", "CAI La Enea");
        cai5.setOrganismo("Policía Nacional");
        cuentas.add(cai5);

        Autoridad cai6 = new Autoridad("POL-06", "CAI Chipre", "cai.chipre@policia.gov.co", "Chipre2024", "Patrullero", "CAI Chipre");
        cai6.setOrganismo("Policía Nacional");
        cuentas.add(cai6);

        Autoridad cai7 = new Autoridad("POL-07", "CAI Villahermosa", "cai.villahermosa@policia.gov.co", "Villa2024", "Patrullero", "CAI Villahermosa");
        cai7.setOrganismo("Policía Nacional");
        cuentas.add(cai7);

        Autoridad cai8 = new Autoridad("POL-08", "CAI Fátima", "cai.fatima@policia.gov.co", "Fatima2024", "Patrullero", "CAI Fátima");
        cai8.setOrganismo("Policía Nacional");
        cuentas.add(cai8);

        // Estaciones del Cuerpo de Bomberos de Manizales
        Autoridad bom1 = new Autoridad("BOM-01", "Estación Central", "estacion.central@bomberos.gov.co", "Central2024", "Comandante", "Estación Central");
        bom1.setOrganismo("Bomberos");
        cuentas.add(bom1);

        Autoridad bom2 = new Autoridad("BOM-02", "Estación La Enea", "estacion.enea@bomberos.gov.co", "EneaB2024", "Teniente", "Estación La Enea");
        bom2.setOrganismo("Bomberos");
        cuentas.add(bom2);

        Autoridad bom3 = new Autoridad("BOM-03", "Estación Maltería", "estacion.malteria@bomberos.gov.co", "Malteria2024", "Teniente", "Estación Maltería");
        bom3.setOrganismo("Bomberos");
        cuentas.add(bom3);

        return cuentas;
    }

    public static void sembrar(GestorUsuarios gestor) throws IOException {
        boolean huboCambios = false;

        for (Autoridad estacion : crearCuentas()) {
            if (gestor.buscarPorEmail(estacion.getEmail()) == null) {
                gestor.registrarUsuario(estacion); //  Usar registrarUsuario en lugar de agregarUsuario
                huboCambios = true;
            }
        }

        if (huboCambios) {
            gestor.guardarUsuarios();
            System.out.println(">> Estaciones de Manizales sembradas correctamente.");
        }
    }
}