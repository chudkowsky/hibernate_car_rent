package org.example.dao.hibernate;

import org.example.dao.IVehicleRepository;
import org.example.model.User;
import org.example.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collection;
import java.util.List;

public class VehicleDAO implements IVehicleRepository {
    private static VehicleDAO instance;
    private SessionFactory sessionFactory;

    private VehicleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static VehicleDAO getInstance(SessionFactory sessionFactory) {
        if (instance == null) {
            instance = new VehicleDAO(sessionFactory);
        }
        return instance;
    }

    @Override
    public boolean rentVehicle(String plate, String login) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            User user = session.get(User.class, login);
            Vehicle vehicle = session.get(Vehicle.class, plate);

            if (user != null && vehicle != null && user.getVehicle() == null) {
                vehicle.setUser(user);
                vehicle.setRent(true);
                user.setVehicle(vehicle);

                session.saveOrUpdate(user);
                session.saveOrUpdate(vehicle);

                transaction.commit();
                return true;
            } else {
                if (transaction != null) {
                    transaction.rollback();
                }
                return false;
            }
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(vehicle);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean removeVehicle(String plate) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Vehicle vehicle = session.get(Vehicle.class, plate);
        session.delete(vehicle);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public Vehicle getVehicle(String plate) {
        Session session = sessionFactory.openSession();
        try {
            Vehicle vehicle = session.get(Vehicle.class, plate);
            return vehicle;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean returnVehicle(String plate,String login) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        User user = session.get(User.class, login);
        Vehicle vehicle = session.get(Vehicle.class, plate);
        vehicle.setUser(null);
        vehicle.setRent(false);
        user.setVehicle(null);
        session.saveOrUpdate(user);
        session.saveOrUpdate(vehicle);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public Collection<Vehicle> getVehicles() {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("from Vehicle");
        List<Vehicle> vehicles = query.list();
        session.close();
        return vehicles;
    }
}