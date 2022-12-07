package com.example.demo.services;

import com.example.demo.database.DbConnection;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class DeviceService {
    public int consumption(byte[] deviceId){
        try {
            var getStatement = DbConnection.getConnection().prepareStatement(
                    "select max_consumption from device where id = ?");
            getStatement.setBytes(1, deviceId);
            var resultSet = getStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
            else throw new RuntimeException("Device not found!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
}
