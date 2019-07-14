package dmsistemas.com.dao;

import dmsistemas.com.model.Factura;
import java.util.List;

public interface FacturaDao {

    public List<Factura> listaFacturas(String id);

    public void updateEstatusFactura(Factura factura);

}
