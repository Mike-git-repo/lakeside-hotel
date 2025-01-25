package com.lakesidehotel.controller;

import com.lakesidehotel.exception.RoleAlreadyExistException;
import com.lakesidehotel.model.Role;
import com.lakesidehotel.model.User;
import com.lakesidehotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.PanelUI;
import java.util.List;

import static org.springframework.http.HttpStatus.FOUND;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/all")
    public ResponseEntity<List<Role>> getAllRoles(){
        return new ResponseEntity<>(roleService.getRoles(), FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createRole(@RequestBody Role theRole){
        try {
            roleService.createRole(theRole);
            return ResponseEntity.ok("New role created successfully");
        }catch (RoleAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable long roleId){
        roleService.deleteRole(roleId);

    }

    @PostMapping("/remove-all-users/{roleId}")
    public Role removeAllUserFromRole(@PathVariable long roleId) {
        return roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public User removeUserFromRole(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        return roleService.removeUserFromRole(userId,roleId);
    }

    @PostMapping("/assign-user-to-role")
    public User assignRoletoUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        return roleService.assignRoletoUser(userId,roleId);
    }

}
