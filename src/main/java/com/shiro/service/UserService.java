package com.shiro.service;

import com.shiro.mapper.PermissionMapper;
import com.shiro.mapper.RoleMapper;
import com.shiro.mapper.UserMapper;
import com.shiro.model.Permission;
import com.shiro.model.PermissionExample;
import com.shiro.model.User;
import com.shiro.model.UserExample;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhenghuasheng on 2017/9/5.17:00
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    /**
     * 根据用户名查找用户
     * @param userName
     * @return
     */
    public User getByUserName(String userName) {
        if (StringUtils.isEmpty(userName)) {
            return null;
        }

        List<User> list = userMapper.selectByUsername(userName);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 根据用户名查找改用户所拥有的角色
     * @param userName
     * @return
     */
    public Set<String> getRoles(String userName) {
        Set<String> roles = new HashSet<>();
        if (StringUtils.isEmpty(userName)) {
            return roles;
        }

        UserExample example = new UserExample();
        example.or().andUsernameEqualTo(userName);
        List<User> list = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return roles;
        }
        User user = list.get(0);
        roles.add(roleMapper.selectByPrimaryKey(user.getRoleId()).getRolename());
        return roles;
    }

    /**
     * 根据用户名查找该用户角色所拥有的权限
     * @param userName
     * @return
     */
    public Set<String> getPerms(String userName) {
        Set<String> perms = new HashSet<>();
        if (StringUtils.isEmpty(userName)) {
            return perms;
        }

        UserExample example = new UserExample();
        example.or().andUsernameEqualTo(userName);
        List<User> list = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return perms;
        }

        PermissionExample permissionExample = new PermissionExample();
        permissionExample.or().andRoleIdEqualTo(list.get(0).getRoleId());
        List<Permission> permissions = permissionMapper.selectByExample(permissionExample);
        for (Permission permission: permissions) {
            perms.add(permission.getPermissionname());
        }
        return perms;
    }
}
