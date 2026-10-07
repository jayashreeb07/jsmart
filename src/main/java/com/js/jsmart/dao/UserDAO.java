package com.js.jsmart.dao;

import com.js.jsmart.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** DAO for users. All SQL uses PreparedStatement. */
public interface UserDAO {
  /** Find by email. */
  Optional<User> findByEmail(String email) throws SQLException;
  /** Find by id. */
  Optional<User> findById(long id) throws SQLException;
  /** Insert user, returns generated id. */
  long insert(User user) throws SQLException;
  /** List all users (admin). */
  List<User> findAll() throws SQLException;
  /** Count users. */
  long count() throws SQLException;
}
