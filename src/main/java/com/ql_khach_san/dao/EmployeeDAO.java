package com.ql_khach_san.dao;

import com.ql_khach_san.config.DBConnection;
import com.ql_khach_san.model.Employee;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;


public class EmployeeDAO {

    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT employee_id, username, password, full_name, role FROM employee ORDER BY employee_id";
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

    public boolean insert(Employee e) {
        String sql = "INSERT INTO employee(username,password,full_name,role) VALUES(?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getUsername());
            ps.setString(2, e.getPassword());
            ps.setString(3, e.getFullName());
            ps.setString(4, e.getRole());

            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean update(Employee e, boolean updatePassword) {
        String sql = updatePassword
                ? "UPDATE employee SET username=?, password=?, full_name=?, role=? WHERE employee_id=?"
                : "UPDATE employee SET username=?, full_name=?, role=? WHERE employee_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getUsername());

            if (updatePassword) {
                ps.setString(2, e.getPassword());
                ps.setString(3, e.getFullName());
                ps.setString(4, e.getRole());
                ps.setInt(5, e.getEmployeeId());
            } else {
                ps.setString(2, e.getFullName());
                ps.setString(3, e.getRole());
                ps.setInt(4, e.getEmployeeId());
            }

            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM employee WHERE employee_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Employee findByUsername(String username) {
        String sql = "SELECT * FROM employee WHERE username=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Employee getById(int id) {
        String sql = "SELECT * FROM employee WHERE employee_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<Employee> searchByName(String keyword) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employee WHERE LOWER(full_name) LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
