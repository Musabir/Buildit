package com.rentit.user.domain.repository;

import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.UserTable;
import com.rentit.user.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    List<User> userList;
    @Autowired
    EntityManager em;

    public UserRepositoryImpl() {
        userList = new ArrayList<>();
        userList.add(new User("site", "site","site@site.com", 2));
        userList.add(new User("work", "work","work@work.com", 1));
    }

    @Override
    public User login(User user) {
        int role = -1;
        int pos = -1;
        User result = new User();
        List<UserTable> userTable = new ArrayList<>();
        try {
            userTable = em.createQuery("SELECT p FROM UserTable p WHERE p.username = ?2 and p.password = ?1", UserTable.class)
                    .setParameter(1, user.getPassword())
                    .setParameter(2, user.getUsername())
                    .getResultList();
        }
        catch (Exception e){
            result.setRole(-1);

        }
        if(userTable.size()==0)
            result.setRole(-1);
        else
            result = new User(userTable.get(0).getUsername(),userTable.get(0).getPassword(),userTable.get(0).getEmail(),userTable.get(0).getRole());

//        for(int i=0;i<2;i++){
//            if(userList.get(i).getUsername().equals(user.getUsername())){
//                result = userList.get(i);
//            }
//        }

//            result.setEmail(userTable.getPassword());
//            result.setPassword(userTable.getPassword());
//            result.setUsername(userTable.getUsername());
//            result.setRole(userTable.getRole());
//
//        for (int i = 0; i < userList.size(); i++) {
//            if (user.getUsername().equals(userList.get(i).getUsername())
//                    && user.getPassword().equals(userList.get(i).getPassword())) {
//                pos = i;
//            }
//
//        }
        return result;
    }
}
