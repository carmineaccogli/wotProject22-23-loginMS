package it.safesiteguard.ms.loginms_ssguard.restcontrollers;

import it.safesiteguard.ms.loginms_ssguard.domain.User;
import it.safesiteguard.ms.loginms_ssguard.dto.*;
import it.safesiteguard.ms.loginms_ssguard.exceptions.UserNotFoundException;
import it.safesiteguard.ms.loginms_ssguard.mappers.UserMapper;
import it.safesiteguard.ms.loginms_ssguard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/authentication")
public class UserRestController {

    /**
     * Se il ruolo è resposabile della sicurezza e lo User-agent è un app android, allora devo negare l'accesso
     */

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;


    //@PreAuthorize("hasRole('ROLE_Admin')")
    @RequestMapping(value="/registration", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> registration(@Valid @RequestBody RegistrationRequestDTO requestDTO) {

        User userToAdd = userMapper.fromRegistrationDTOToUser(requestDTO);
        User addedUser = userService.addUser(userToAdd);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id",addedUser.getId());
        payload.put("location","/api/authentication/users/"+addedUser.getId());

        return new ResponseEntity<>(
                new ResponseDTO("User successfully registered",payload),
                HttpStatus.CREATED
        );
    }

    /**
     * Endpoint di autenticazione.
     * Restituisce token JWT a client in modo che non debba inviare ripetutamente username e password ad ogni richiesta.
     *
     * @param loginDTO
     * @return Token JWT
     */
    @RequestMapping(value="/authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginDTO loginDTO, @RequestHeader(value = HttpHeaders.USER_AGENT, required = true) String userAgent) throws UsernameNotFoundException {

        User user = userMapper.fromLoginDTOToUser(loginDTO);
        String jwt = userService.createJwtToken(user, userAgent);

        if(jwt == null)
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt));
    }

    /**
     * Endpoint usato dal responsabile della sicurezza per registrare nuovi User con ruoli specifici.
     *
     * @param workerRegistrationDTO
     * @return new User
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MICROSERVICE-COMMUNICATION')")
    @RequestMapping(value="/worker-registration", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> workerRegistration(@Valid @RequestBody WorkerRegistrationDTO workerRegistrationDTO) throws MailException {

        String createdUserID = userService.addWorkerUser(workerRegistrationDTO.getEmail(), workerRegistrationDTO.getRole());
        return ResponseEntity.ok(createdUserID);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SAFETY_MANAGER')")
    @RequestMapping(value="/users/{userID}", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginDTO> getInfoUser(@PathVariable("userID") String userID) throws UserNotFoundException {

        User requestedUser = userService.getUserByID(userID);
        LoginDTO infoUser = userMapper.fromUserToLoginDTO(requestedUser);

        return ResponseEntity.ok(infoUser);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SAFETY_MANAGER')")
    @RequestMapping(value="/users/", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoginDTO>> getAll() {

        List<User> allUsers = userService.getAll();

        if(allUsers.isEmpty())
            return ResponseEntity.noContent().build();

        List<LoginDTO> usersDTO = new ArrayList<>();
        for(User user : allUsers) {
            LoginDTO userDTO = userMapper.fromUserToLoginDTO(user);
            usersDTO.add(userDTO);
        }

        return ResponseEntity.ok(usersDTO);
    }

    /*@RequestMapping(value="/users/macAddresses", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthorizedOperatorDTO>> getMacAddressesPerUsers(@RequestParam(value = "ids", required = true) List<String> usersIDs) {

        List<User> allOperators = userService.getMacAddressesByUsersList(usersIDs);

        if(allOperators.isEmpty())
            return ResponseEntity.noContent().build();

        List<AuthorizedOperatorDTO> allOperatorsDTO = new ArrayList<>();
        for(User user : allOperators) {
            AuthorizedOperatorDTO operatorDTO = userMapper.fromUserToAuthorizedOperatorDTO(user);
            allOperatorsDTO.add(operatorDTO);
        }

        return ResponseEntity.ok(allOperatorsDTO);
    }*/

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MICROSERVICE-COMMUNICATION')")
    @RequestMapping(value="/users/macAddresses", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthorizedOperatorDTO>> getUsersMacAddresses(){
        List<User> allOperators = userService.getAllDriversMacAddresses();

        if(allOperators.isEmpty())
            return ResponseEntity.noContent().build();

        List<AuthorizedOperatorDTO> allOperatorsDTO = new ArrayList<>();
        for(User user : allOperators) {
            AuthorizedOperatorDTO operatorDTO = userMapper.fromUserToAuthorizedOperatorDTO(user);
            allOperatorsDTO.add(operatorDTO);
        }

        return ResponseEntity.ok(allOperatorsDTO);
    }


}
