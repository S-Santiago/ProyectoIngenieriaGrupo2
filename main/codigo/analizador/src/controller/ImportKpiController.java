package controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.LineaPedido;
import persistence.CsvImporter;

public class ImportKpiController {
    private final ExploradorController exploradorController = ExploradorController.getInstance();
    private final CsvImporter csvImporter = new CsvImporter();

    public void importar(String s){
     List<LineaPedido>list=csvImporter.importCSVLineaPedidos(s);
        exploradorController.setPedidos(list);
       }    
        public Map<String,BigDecimal> calcularKPIMensualFacturacion(String opcion){
            List<LineaPedido> lineaPedidosFiltrado=exploradorController.filtrarPedidosPorCategoria(opcion);
            Map<String,BigDecimal> kpiMensual=new TreeMap<>();
            for(LineaPedido p:lineaPedidosFiltrado){
                java.time.LocalDate fechaDePedido=java.time.LocalDate.parse(p.getFechaPedido(),exploradorController.getDateFormatter());
                int mes=fechaDePedido.getMonthValue();
                String mesKey=String.format("%02d",mes);
                BigDecimal facturacion=valorMonetario(p.getPrecioVentaUnitario(), p.getUnidades());
                if(kpiMensual.containsKey(mesKey)){
                    kpiMensual.put(mesKey, kpiMensual.get(mesKey).add(facturacion));
                }else{
                    kpiMensual.put(mesKey, facturacion);
                }
            }
            return kpiMensual;
        }
        public Map<String,BigDecimal> calcularKPIMensualMargen(String opcion){
            List<LineaPedido> lineaPedidosFiltrado=exploradorController.filtrarPedidosPorCategoria(opcion);
            Map<String,BigDecimal> kpiMensual=new TreeMap<>();
            for(LineaPedido p:lineaPedidosFiltrado){
                java.time.LocalDate fechaDePedido=java.time.LocalDate.parse(p.getFechaPedido(),exploradorController.getDateFormatter());
                int mes=fechaDePedido.getMonthValue();
                String mesKey=String.format("%02d",mes);
                BigDecimal margen=valorMonetario(p.getPrecioVentaUnitario(), p.getUnidades())
                        .subtract(valorMonetario(p.getCosteUnitario(), p.getUnidades()));
                if(kpiMensual.containsKey(mesKey)){
                    kpiMensual.put(mesKey, kpiMensual.get(mesKey).add(margen));
                }else{
                    kpiMensual.put(mesKey, margen);
                }
            }
            return kpiMensual;
        }

        private BigDecimal valorMonetario(BigDecimal valorUnitario, Integer unidades) {
            BigDecimal valor = valorUnitario == null ? BigDecimal.ZERO : valorUnitario;
            BigDecimal cantidad = unidades == null ? BigDecimal.ZERO : BigDecimal.valueOf(unidades.longValue());
            return valor.multiply(cantidad);
        }
}