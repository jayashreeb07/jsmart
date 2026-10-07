package com.js.jsmart.dto;

import com.js.jsmart.model.User;

/** Safe user DTO - never contains passwordHash. */
public class UserResponseDTO {
  private long id;
  private String email;
  private String fullName;
  private String role;

  public static UserResponseDTO from(User u) {
    UserResponseDTO d = new UserResponseDTO();
    d.id = u.getId();
    d.email = u.getEmail();
    d.fullName = u.getFullName();
    d.role = u.getRole() == null ? null : u.getRole().name();
    return d;
  }

  public long getId() { return id; }
  public String getEmail() { return email; }
  public String getFullName() { return fullName; }
  public String getRole() { return role; }
}
