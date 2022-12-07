package com.example.demo.services;

import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;

import static com.example.demo.database.DbConnection.getConnection;

@Service
public class MeasurementService {
    public boolean find(byte[] deviceId, Timestamp timestamp){
        try {
            var selectStatement = getConnection().prepareStatement(
                    "select * from measurement where device_id = ? and timestamp = ?");
            selectStatement.setBytes(1, deviceId);
            selectStatement.setTimestamp(2, timestamp);
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public int consumption(byte[] deviceId, Timestamp timestamp){
        try {
            var selectStatement = getConnection().prepareStatement(
                    "select consumption from measurement where device_id = ? and timestamp = ?");
            selectStatement.setBytes(1, deviceId);
            selectStatement.setTimestamp(2, timestamp);
            var resultSet = selectStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
            else throw new RuntimeException("Measurement not found!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public void update(byte[] deviceId, Timestamp timestamp, int consumption){
        try {
            var updateStatement = getConnection().prepareStatement(
                    "update measurement set consumption = consumption + ? where device_id = ? and timestamp = ?");
            updateStatement.setInt(1, consumption);
            updateStatement.setBytes(2, deviceId);
            updateStatement.setTimestamp(3, timestamp);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void create(byte[] deviceId, Timestamp timestamp, int consumption){
        try {
            var createStatement = getConnection().prepareStatement(
                    "insert into measurement(device_id, timestamp, consumption) values (?, ?, ?)");
            createStatement.setBytes(1, deviceId);
            createStatement.setTimestamp(2, timestamp);
            createStatement.setInt(3, consumption);
            createStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
