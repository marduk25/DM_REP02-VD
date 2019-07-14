package dmsistemas.com.dao;

import dmsistemas.com.model.FacturaGastos;
import dmsistemas.com.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FacturaGastosDaoImp implements FacturaGastosDao {

    @Override
    public String listaFacturaGastos(String id) {
        String uuid = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        try {
            uuid = session.createQuery("FROM Factura WHERE uuid='" + id + "'").toString();
            t.commit();
            session.close();
        } catch (HibernateException e) {
            t.rollback();
            System.err.println(e.getMessage());
        }
        return uuid;
    }

    @Override
    public void updateEstatusFaturaGastos(FacturaGastos factura) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
