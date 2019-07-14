package dmsistemas.com.dao;

import dmsistemas.com.model.Factura;
import dmsistemas.com.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FacturaDaoImp implements FacturaDao {

    @Override
    public List<Factura> listaFacturas(String id) {
        List<Factura> lista = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        String hql = "FROM Factura WHERE uuid='" + id + "'";
        try {
            lista = session.createQuery(hql).list();
            t.commit();
            session.close();
        } catch (HibernateException e) {
            t.rollback();
            System.err.println(e.getMessage());
        }

        return lista;
    }

    @Override
    public void updateEstatusFactura(Factura factura) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        String hql = "UPDATE Factura SET estatusSat= '" + factura.getEstatusSat() + "' WHERE id='" + factura.getId() + "' AND uuid='" + factura.getUuid() + "'";
        Query q = session.createQuery(hql);
        try {
            q.executeUpdate();
            t.commit();
            session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            t.rollback();
        }
//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            session.beginTransaction();
//            session.update(factura);
//            session.getTransaction().commit();
//        } catch (HibernateException e) {
//            System.out.println(e.getMessage());
//            session.getTransaction().rollback();
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
    }

}
