package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.featureplatform.dao.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EntityService {

    private final Connection openmldbConnection;

    @Autowired
    public EntityService(Connection openmldbConnection) {
        this.openmldbConnection = openmldbConnection;
    }

    public List<Entity> getEntities() {
        String sql = "SELECT name, primary_keys FROM SYSTEM_FEATURE_PLATFORM.entities";

        ArrayList<Entity> entities = new ArrayList<>();

        try {
            Statement openmldbStatement = openmldbConnection.createStatement();
            openmldbStatement.execute(sql);
            ResultSet result = openmldbStatement.getResultSet();

            while (result.next()) {
                Entity entity = new Entity(result.getString(1), result.getString(2));
                entities.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entities;
    }

    public Entity getEntityByName(String name) {
        try {
            // TODO: Set database before
            String sql = "SELECT name, primary_keys FROM SYSTEM_FEATURE_PLATFORM.entities WHERE name=?";

            PreparedStatement openmldbStatement = openmldbConnection.prepareStatement(sql);
            openmldbStatement.setString(1, name);

            ResultSet result = openmldbStatement.executeQuery();

            if (result.getFetchSize() == 0) {
                System.out.print("Can not get entity with the name: " + name);
                return null;
            } else if (result.getFetchSize() > 1) {
                System.out.print("Get more entities with the same name");
                return null;
            } else {
                while (result.next()) {
                    Entity entity = new Entity(result.getString(1), result.getString(2));
                    return entity;
                }
            }

            openmldbStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addEntity(Entity entity) {
        try {
            // TODO: It would be better to use JDBC prepared statement from connection
            String sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.entities values ('%s', '%s')", entity.getName(), entity.getPrimaryKeys());

            Statement openmldbStatement = openmldbConnection.createStatement();
            openmldbStatement.execute(sql);
            openmldbStatement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteEntity(String name) {
        try {
            // TODO: It would be better to use JDBC prepared statement from connection
            String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.entities WHERE name='%s'", name);

            Statement openmldbStatement = openmldbConnection.createStatement();
            openmldbStatement.execute(sql);
            openmldbStatement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}