package com.github.jonataslaet.laetcatalog.services;

import com.github.jonataslaet.laetcatalog.controllers.dtos.RoleDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.UserDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.UserInsertDTO;
import com.github.jonataslaet.laetcatalog.controllers.dtos.UserUpdateDTO;
import com.github.jonataslaet.laetcatalog.entities.Role;
import com.github.jonataslaet.laetcatalog.entities.User;
import com.github.jonataslaet.laetcatalog.entities.UserDetailsProjection;
import com.github.jonataslaet.laetcatalog.repositories.RoleRepository;
import com.github.jonataslaet.laetcatalog.repositories.UserRepository;
import com.github.jonataslaet.laetcatalog.services.exceptions.DatabaseException;
import com.github.jonataslaet.laetcatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<UserDetailsProjection> userDetailsProjections =
                userRepository.searchUserAndRolesByEmail(email);
        if (userDetailsProjections.isEmpty()) {
            throw new UsernameNotFoundException("User not found for email = " + email);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(userDetailsProjections.get(0).getPassword());
        for (UserDetailsProjection userDetailsProjection :
                userDetailsProjections) {
            user.addRole(new Role(userDetailsProjection.getRoleId(), userDetailsProjection.getAuthority()));
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> optionalProduct = userRepository.findById(id);
        User user = optionalProduct.orElseThrow(() -> new ResourceNotFoundException("User not found for id = " + id));
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO userDTO) {
        User user = new User();
        setUserFromDTO(user, userDTO);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO userDTO) {
        try {
            User user = userRepository.getReferenceById(id);
            setUserFromDTO(user, userDTO);
            user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
            user = userRepository.save(user);
            return new UserDTO(user);
        } catch (EntityNotFoundException entityNotFoundException){
            throw new ResourceNotFoundException("User not found for id = " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(("Resource not found"));
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new DatabaseException("Integrity Violation");
        }
    }

    private void setUserFromDTO(User user, UserDTO userDTO) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.getRoles().clear();
        for (RoleDTO roleDTO: userDTO.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            user.getRoles().add(role);
        }
    }
}
