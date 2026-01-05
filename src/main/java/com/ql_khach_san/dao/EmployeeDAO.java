package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Employee;

import java.sql.*;


public class EmployeeDAO {

    public Employee findByUsername(String username) {
        String sql = "SELECT employee_id, username, password, full_name, role FROM employee WHERE username = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Employee(rs.getInt("employee_id"), rs.getString("username"), rs.getString("password"), rs.getString("full_name"), rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Employee getById(int id) {
        String sql = "SELECT employee_id, username, password, full_name, role FROM employee WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Employee(rs.getInt("employee_id"), rs.getString("username"), rs.getString("password"), rs.getString("full_name"), rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Employee e) {
        String sql = "INSERT INTO employee(username, password, full_name, role) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getUsername());
            ps.setString(2, e.getPassword());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getRole());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) e.setEmployeeId(keys.getInt(1)); }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean update(Employee e) {
        String sql = "UPDATE employee SET username = ?, password = ?, full_name = ?, role = ? WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getUsername());
            ps.setString(2, e.getPassword());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getRole());
            ps.setInt(5, e.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT employee_id, username, password, full_name, role FROM employee ORDER BY full_name";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Employee e = new Employee(rs.getInt("employee_id"), rs.getString("username"), rs.getString("password"), rs.getString("full_name"), rs.getString("role"));
                list.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
