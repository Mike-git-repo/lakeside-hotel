package com.lakesidehotel.service;

import com.lakesidehotel.model.Role;
import com.lakesidehotel.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {

    List<Role> getRoles();

    Role createRole(Role theRole);

    void deleteRole(Long id);

    Role findByName(String name);

    User removeUserFromRole(Long userId, long roleId);

    User assignRoletoUser(Long userId, long roleId);

    Role removeAllUsersFromRole(Long roleId);

}

