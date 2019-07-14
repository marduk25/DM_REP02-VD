package dmsistemas.com.dao;

import dmsistemas.com.model.FacturaGastos;

public interface FacturaGastosDao {

    public String listaFacturaGastos(String id);

    public void updateEstatusFaturaGastos(FacturaGastos factura);

}
