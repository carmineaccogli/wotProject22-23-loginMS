package it.safesiteguard.ms.loginms_ssguard.service;

import it.safesiteguard.ms.loginms_ssguard.domain.User;
import it.safesiteguard.ms.loginms_ssguard.exceptions.UserNotFoundException;
import it.safesiteguard.ms.loginms_ssguard.repositories.UserRepository;
import it.safesiteguard.ms.loginms_ssguard.security.JwtUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static it.safesiteguard.ms.loginms_ssguard.configuration.SecurityConfig.passwordEncoder;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private EmailService emailService;



    public User addUser(User newUser) {
        return userRepository.save(newUser);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


    /** ESECUZIONE LOGIN
     *  1) Gestione autorizzazione tramite spring
     *  2) Ricerca utente
     *      2.1) Check esistenza
     *  3) Check user-agent: i SAFETY_MANAGER non possono accedere da app, i WORKERS non possono da desktop
     *  4) Refresh macAddress-BLE solo per gli EQUIPMENT_OPERATORS
     *  5) Aggiunta dell'utente autenticato al SecurityContextHolder
     *  6) Generazione token jwt
     *
     * @param  user
     * @param  userAgent
     * @return
     * @throws UsernameNotFoundException
     */

    public String createJwtToken(User user, String userAgent) throws UsernameNotFoundException {

        // 1
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );

        // 2
        Optional<User> optUser = userRepository.findByUsername(authentication.getName());

        // 2.1
        if (!optUser.isPresent()) {
            throw new UsernameNotFoundException("User " + user.getUsername() + "does not exist.");
        }

        // 3
        User validUser = optUser.get();

        if ((userAndroidApp(userAgent) && (!validUser.getRole().equals(User.Role.EQUIPMENT_OPERATOR) && !validUser.getRole().equals(User.Role.GROUND_WORKER))) ||
                (!userAndroidApp(userAgent) && !validUser.getRole().equals(User.Role.SAFETY_MANAGER)))
            return null;


        // 4
        if(user.getMacAddress() != null && validUser.getRole().equals(User.Role.EQUIPMENT_OPERATOR))
            checkMacAddress(validUser, user.getMacAddress());

        // 5
        // Questo è un oggetto al cui sto dicendo che, da questo momento in poi, se sto richiamando la logica di business,
        // questo utente è autenticato.
        // Attenzione che questa autenticazione vale a livello di thread. Significa che se io poi rifaccio una altra chiamata
        // api dopo, il context è svuotato! Insomma: se dopo arriva una chiamata di un utente diverso, il context deve essere
        // svuotato, altrimenti quell'utente avrebbe il context di un altro utente.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6
        final String jwt = jwtUtilities.generateToken(validUser.getUsername(), validUser.getRole().toString());

        return jwt;
    }


    /** Funzione per l'aggiunta delle credenziali di login relative a un lavoratore inserito dal responsabile sicurezza
     * 1. Creazione nuovo utente
     * 2. Generazione e setting username (codice operatore)
     * 3. Generazione e setting password
     * 4. Setting ruolo
     * 5. Salvataggio nel db
     * 6. Invio al lavoratore delle credenziali tramite email
     *
     * @param email
     * @return
     * @throws MailException
     */
    public String addWorkerUser(String email, User.Role role) throws MailException {

        // 1
        User newUser = new User();

        // 2
        newUser.setEmail(email);
        String operatorCode = generateOperatorCode();
        newUser.setUsername(operatorCode);

        // 3
        String password = generatePassword();
        newUser.setPassword(passwordEncoder().encode(password));
        System.out.println("USERNAME: "+operatorCode);
        System.out.println("PASSWORD: "+password);

        // 4
        newUser.setRole(role);

        // 5
        User savedUser = userRepository.save(newUser);

        // 6
        //emailService.sendCredentialsEmail(newUser.getEmail(), newUser.getUsername(), newUser.getPassword());

        return savedUser.getId();
    }


    public User getUserByID(String userID) throws UserNotFoundException {

        Optional<User> optUser = userRepository.findById(userID);

        if (!optUser.isPresent())
            throw new UserNotFoundException();

        return optUser.get();
    }

    /** Funzione per recuperare una lista di indirizzi MAC data una lista di utenti
     * @param usersIDs
     * @return
     */
    public List<User> getMacAddressesByUsersList(List<String> usersIDs) {

        return userRepository.findAllByIdIn(usersIDs);
    }


    public List<User> getAllDriversMacAddresses() {
        return userRepository.findAllByRole(User.Role.EQUIPMENT_OPERATOR);
    }


    /** Funzioni di utilità per la classe service di user **/
    static private String generatePassword() {
        int length = 12; // Lunghezza della stringa desiderata
        final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }
        return randomString.toString();
    }

    static private String generateOperatorCode() {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String numbers = "0123456789";

        String finalCode = generateRandomString(letters, 2) +
                generateRandomString(numbers, 3) +
                generateRandomString(letters, 2);

        return finalCode;
    }

    private static String generateRandomString(String characters, int length) {
        StringBuilder slice = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(characters.length());
            slice.append(characters.charAt(index));
        }
        return slice.toString();
    }

    private void checkMacAddress(User validUser, String newMacAddress) {

        // Check valore macAddress se diverso da quello memorizzato (REFRESH)
        if(validUser.getMacAddress() != null) {
            if (!validUser.getMacAddress().equals(newMacAddress)) {
                validUser.setMacAddress(newMacAddress);
                userRepository.save(validUser);
            }
        }
        // Prima volta setting macAddress
        else {
            validUser.setMacAddress(newMacAddress);
            userRepository.save(validUser);
        }
    }

    private boolean userAndroidApp(String userAgent) {

        if(userAgent != null && userAgent.toLowerCase().startsWith("okhttp"))
            return true;

        return false;
    }
}
