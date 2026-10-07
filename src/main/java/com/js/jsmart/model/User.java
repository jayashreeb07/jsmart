package com.js.jsmart.model;

import java.time.LocalDateTime;

/** User entity (POJO). Password stored only as BCrypt hash. */
public class User {
  private long id;
  private String email;
  private String passwordHash;
  private String fullName;
  private Role role;
  private LocalDateTime createdAt;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
